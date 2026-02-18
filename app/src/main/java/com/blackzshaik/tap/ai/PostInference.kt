package com.blackzshaik.tap.ai

import com.blackzshaik.tap.ai.model.ChatCompletion
import com.blackzshaik.tap.ai.model.ChatMessage
import com.blackzshaik.tap.ai.model.ChatRequest
import com.blackzshaik.tap.ai.model.PostResponse
import com.blackzshaik.tap.ai.model.Response
import com.blackzshaik.tap.ai.utils.postChat
import io.ktor.client.HttpClient

object PostInference {
    private const val POST_SYSTEM_INSTRUCTION = """
You are an Expert Artifact Generator. Your goal is to convert a User's request into a structured Initial Artifact.
You must NOT chat. 
You must NOT explain yourself.
If the user's request is vague, make a reasonable assumption to generate a valid artifact.
The Artifact should be a high-quality content based on the user's request, keep it short but informative, and honestly fulfils user's request.
"""

    suspend operator fun invoke(client: HttpClient, serverUrl: String, message: String): Response {
        val conversationHistory = mutableListOf<ChatMessage>()
        conversationHistory.add(ChatMessage(Role.SYSTEM.value, POST_SYSTEM_INSTRUCTION))
        conversationHistory.add(ChatMessage(Role.USER.value, message))

        return client.postChat<ChatRequest, ChatCompletion>(
            serverUrl,
            ChatRequest(
                MODEL,
                "",
                conversationHistory.toTypedArray(),
                emptyArray()
            )
        ).let {
            if (it.body != null){
                PostResponse(it.body.choices.first().message.content)
            }else
                it.error ?: Response.ErrorResponse("Something went wrong")
        }

    }


}