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
import com.blackzshaik.tap.ai.utils.postChat
import com.blackzshaik.tap.model.Artifact
import com.blackzshaik.tap.model.Comment
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json


object CommentInference {


    private const val COMMENT_SYSTEM_INSTRUCTION = """
You are a Collaborative Revision Engine. You manage a digital artifact based on user feedback.
You have access to the current state of the artifact and a stream of comments.
Analyze the [CURRENT FOCUS COMMENT] in the context of the artifact.
You MUST update the artifact only if the user's comment requires a change.
You MUST use **update_artifact** tool to update the artifact.
Then briefly explain about the changes made as your comment.

You MUST not chat.
Not all user comments need modification to the artifact (eg: Why did you use a loop here? or I like this one better),
on that case you can just reply with an explanation.

User will provide the comment with <comment> tag.

Notes:
Your name: {{aiName}}
User's name: {{userName}}
You can use this during comments and also the user will use it in context.


Example scenarios:
1) If the artifact contains a blog post about certain topic and the user wants to change the tone, update the artifact adapting the tone.
2) In another case if the user just asks a question about the artifact, just add a comment yourself as a response.

RULES:
1) Update the artifact by only calling the tool.
2) Don't give updates as comments.
3) When updating if the text needs to replaced always include the same text including the formatting.
4) Always give a short and concise response as your comment. Treat it as like replying to a thread.
5) If your are unsure whether to update the artifact or not, ask the user for clarification.

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
        serverUrl:String,
        artifact: Artifact,
        newComment: String,
        commentHistory: List<Comment>,
        userName:String = "User",
        assistantName:String = "Assistant"
    ): Response {
        val conversationHistory = createConversationHistory(artifact, commentHistory, newComment)
        try {

            val response = client.postChat<ChatRequest, ChatCompletion>(serverUrl,ChatRequest(
                MODEL,
                "",
                conversationHistory.toTypedArray(),
                tools = arrayOf(updateArtifactTool)
            ))
            if (response.body == null){
                return response.error ?: Response.ErrorResponse("Something went wrong")
            }
            return checkAndUseTool(client, serverUrl,conversationHistory, response.body, artifact, newComment)
                ?: CommentResponse(
                    artifact.artifact,
                    null,
                    newComment,
                    response.body.choices.first().message.content,
                    null,
                    null
                )
        } catch (e: Exception) {
            e.printStackTrace()
            return Response.ErrorResponse(e.message ?: "Something went wrong")
        }
    }

    suspend fun checkAndUseTool(
        client: HttpClient,
        serverUrl:String,
        conversationHistory: MutableList<ChatMessage>,
        chatCompletion: ChatCompletion?,
        artifact: Artifact,
        newComment: String
    ): CommentResponse? {
        if (chatCompletion == null) return null
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
                    "Artifact updated successfully",
                    tool_call_id = toolId
                )
            )

                val toolResponse = client.postChat<ChatRequest, ChatCompletion>( serverUrl,ChatRequest(
                    MODEL,
                    "",
                    conversationHistory.toTypedArray(),
                    arrayOf(updateArtifactTool)
                ))

            if (toolResponse.body == null) return null
            else{
                toolResponse.let {
                    CommentResponse(
                        artifact.artifact,
                        updatedArtifact,
                        newComment,
                        toolResponse.body.choices.first().message.content,
                        originalArtifactStr,
                        replaceArtifactStr
                    )
                }
            }
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