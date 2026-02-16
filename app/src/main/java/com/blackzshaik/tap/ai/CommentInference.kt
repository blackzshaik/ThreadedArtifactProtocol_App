package com.blackzshaik.tap.ai

import com.blackzshaik.tap.ai.model.ChatCompletion
import com.blackzshaik.tap.ai.model.ChatMessage
import com.blackzshaik.tap.ai.model.ChatRequest
import com.blackzshaik.tap.ai.model.CommentResponse
import com.blackzshaik.tap.ai.model.Function
import com.blackzshaik.tap.ai.model.ParamProperties
import com.blackzshaik.tap.ai.model.Params
import com.blackzshaik.tap.ai.model.Response
import com.blackzshaik.tap.ai.model.ToolUse
import com.blackzshaik.tap.ai.model.UpdateArtifact
import com.blackzshaik.tap.model.Artifact
import com.blackzshaik.tap.model.Comment
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json


object CommentInference {


    private const val COMMENT_SYSTEM_INSTRUCTION = """
You are an iterative Engine.
You do not just chat. 
You produce deliverable.
You MUST update the artifact only if the user's comment requested a change.
You MUST use tool to update the artifact. If an artifact is updated briefly explain about the changes made as your response.
You don't ask follow up question in the comments, you explain only about the changes. Not all user comments need modification, so assess the input carefully.

User will provide the comment with <comment> tag.
When a user comments, analyze the comment based on the user's requirement update the artifact by always include the same text including the formatting.

Additionally add a comment yourself briefly explaining about the update.

Example scenarios:
1) If the artifact contains a chapter of a story, and the user comments on to update a scene include the entire scene that needs to be updated in the <org> tag along with the result in the <rep> tag
2) In another case if the user just asks a question why a scene ended like that, there is not need to update the artifact, only reply with an explanation.
"""

    val updateArtifactTool = ToolUse(
        type = "function",
        function = Function(
            name = "update_artifact",
            description = "To update/modify part of the artifact",
            parameters = Params(
                type = "object",
                properties = mapOf(
                    "orgStr" to ParamProperties(
                        "string",
                        "Original text present in the Artifact. IMPORTANT: this has to be matched exactly for successful modification, for updates this can be empty based on the context"
                    ),
                    "repStr" to ParamProperties(
                        "string",
                        "Text to be updated in the original artifact, if orgStr is empty, this will be added to the last"
                    )
                ),
                required = arrayOf("repStr")
            )
        )
    )

    suspend operator fun invoke(
        client: HttpClient,
        artifact: Artifact,
        newComment: String,
        commentHistory: List<Comment>
    ): Response {
        val conversationHistory = createConversationHistory(artifact, commentHistory, newComment)
        try {
            val response = client.post(base_url + chatEndPoint) {
                this.contentType(ContentType.Application.Json)
                setBody(
                    ChatRequest(
                        MODEL,
                        "",
                        conversationHistory.toTypedArray(),
                        tools = arrayOf(updateArtifactTool)
                    )
                )
                this.timeout {
                    requestTimeoutMillis = 300000
                }
            }.body<ChatCompletion>()
            return checkAndUseTool(client, conversationHistory, response, artifact, newComment)
                ?: CommentResponse(
                    artifact.artifact,
                    null,
                    newComment,
                    response.choices.first().message.content,
                    null,
                    null
                )
        } catch (e: Exception) {
            return Response.ErrorResponse(e.message ?: "Something went wrong")
        }
    }

    suspend fun checkAndUseTool(
        client: HttpClient,
        conversationHistory: MutableList<ChatMessage>,
        chatCompletion: ChatCompletion,
        artifact: Artifact,
        newComment: String
    ): CommentResponse? {
        val toolCall = try {
            chatCompletion.choices.first().message.tool_calls?.first()
        } catch (e: kotlin.Exception) {
            null
        }
        val args = if (toolCall?.function?.name == "update_artifact")
            toolCall.function.arguments
        else null
        val toolId = toolCall?.id

        val toolUseResponse = if (toolCall != null && args != null && toolId != null) {
            val arg = Json.decodeFromString<UpdateArtifact>(args)

            conversationHistory.add(ChatMessage(Role.ASSISTANT.value, "", arrayOf(toolCall)))
            val updatedArtifact = if (arg.orgStr.isNullOrEmpty()) {
                artifact.artifact + "\n${arg.repStr}"
            } else artifact.artifact.replace(arg.orgStr, arg.repStr)
            val originalArtifactStr = arg.orgStr
            val replaceArtifactStr = arg.repStr

            conversationHistory.add(
                ChatMessage(
                    Role.TOOL.value,
                    "Comment updated successfully",
                    tool_call_id = toolId
                )
            )

            val chatCompletion = client.post(base_url + chatEndPoint) {
                this.contentType(ContentType.Application.Json)
                setBody(
                    ChatRequest(
                        MODEL,
                        "",
                        conversationHistory.toTypedArray(),
                        arrayOf(updateArtifactTool)
                    )
                )
                timeout {
                    requestTimeoutMillis = (1000 * 60) * 5
                }
            }.body<ChatCompletion>()
            CommentResponse(
                artifact.artifact,
                updatedArtifact,
                newComment,
                chatCompletion.choices.first().message.content,
                originalArtifactStr,
                replaceArtifactStr
            )
        } else {
            null
        }

        return toolUseResponse
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
}