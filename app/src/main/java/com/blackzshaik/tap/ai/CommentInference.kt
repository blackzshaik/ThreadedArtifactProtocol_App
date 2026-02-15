package com.blackzshaik.tap.ai

import com.blackzshaik.tap.ai.model.CommentResponse
import com.blackzshaik.tap.ai.model.Response
import com.blackzshaik.tap.model.Artifact
import com.blackzshaik.tap.model.Comment
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType




object CommentInference {


        private const val COMMENT_SYSTEM_INSTRUCTION = """
You are an iterative Engine.
You do not just chat. 
You produce deliverable.
You MUST always update the artifact based on user's comment.
You don't ask followup question in the comments, you explain only about the changes. Not all user comments need modification, so assess the input carefully.

User will provide the comment with <comment> tag.
When a user comments, analyze the comment based on the user's requirement update the artifact following the below convention, always include the same text including the formatting.
If modification needed reply in the below format:
<update_artifact>
    <org>EXACT text to be replaced in the artifact</org>
    <rep>UPDATED text to be replace in the artifact</rep>
</update_artifact>

Explanation:
<update_artifact> tag contains the block which has both the original text from the original artifact and the updated text
<org> is the tag for original text, a sentence or a paragraph with exact formatting, you must include the entire part of text that needs to be updated.
<rep> is the updated text that fulfils users's request, 

Additionally add a comment yourself briefly explaining about the update.

Example scenarios:
1) If the artifact contains a chapter of a story, and the user comments on to update a scene include the entire scene that needs to be updated in the <org> tag along with the result in the <rep> tag
2) In another case if the user just asks a question why a scene ended like that, there is not need to update the artifact, only reply with an explanation.
"""

    suspend operator fun invoke(client: HttpClient,
                                artifact: Artifact,
                                newComment:String,
                                commentHistory: List<Comment>): Response {
        val conversationHistory = createConversationHistory( artifact, commentHistory, newComment)
        try {
            val response = client.post(base_url + chatEndPoint) {
                this.contentType(ContentType.Application.Json)
                setBody(ChatRequest(MODEL, "", conversationHistory.toTypedArray()))
                this.timeout {
                    requestTimeoutMillis = 300000
                }
            }.body<ChatCompletion>()
            val content = response.choices.first().message.content
            val (updatedArtifact,originalArtifactStr,replaceArtifactStr) = getUpdateArtifact(content)?.let { comment ->
                val updatedArtifact = artifact.artifact.replace(comment.first, comment.second)
                Triple(updatedArtifact, comment.first, comment.second)
            } ?: Triple(null, null, null)

            val assistantComment = try {
                content.indexOf("</update_artifact>").let {
                    if (it != -1) {
                        content.substring(it + "</update_artifact>".length).removePrefix("\n")
                    } else {
                        content
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                content
            }.replace("<comment>","").replace("</comment>","")

            return CommentResponse(
                artifact.artifact,
                updatedArtifact,
                newComment,
                assistantComment,
                originalArtifactStr,
                replaceArtifactStr
            )
        }catch (e: Exception){
            return Response.ErrorResponse(e.message ?: "Something went wrong")
        }
    }

    private fun createConversationHistory(
        artifact: Artifact,
        commentHistory: List<Comment>,
        newComment: String
    ): MutableList<ChatMessage> {
        val conversationHistory = mutableListOf<ChatMessage>()

        conversationHistory.add(
            ChatMessage(
                Role.SYSTEM.value,
                COMMENT_SYSTEM_INSTRUCTION
            )
        )

        conversationHistory.add(ChatMessage(Role.USER.value, artifact.prompt))
        conversationHistory.add(
            ChatMessage(
                Role.ASSISTANT.value,
                "<artifact>" + artifact.artifact + "</artifact>"
            )
        )
        conversationHistory.addAll(commentHistory.map {
            ChatMessage(it.role, "<comment>" + it.content + "</comment>")
        })
        conversationHistory.add(ChatMessage(Role.USER.value, "<comment>$newComment</comment>"))

        return conversationHistory
    }

    fun getUpdateArtifact(content: String): Pair<String, String>? {
        return try {
            val updateArtifact = content.substring(
                content.indexOf("<update_artifact>"),
                content.indexOf("</update_artifact>") + "</update_artifact>".length
            )
            val original = updateArtifact.substring(
                updateArtifact.indexOf("<org>"),
                updateArtifact.indexOf("</org>") + "</org>".length
            )
            val replace = updateArtifact.substring(
                updateArtifact.indexOf("<rep>"),
                updateArtifact.indexOf("</rep>") + "</rep>".length
            )
            Pair(
                original.removePrefix("<org>").removeSuffix("</org>").trim(),
                replace.removePrefix("<rep>").removeSuffix("</rep>").trim()
            )

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}