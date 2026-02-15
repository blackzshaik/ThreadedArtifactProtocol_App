package com.blackzshaik.tap.ai

import com.blackzshaik.tap.ai.model.PostResponse
import com.blackzshaik.tap.ai.model.Response
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.util.concurrent.TimeUnit

object PostInference {
    private const val POST_SYSTEM_INSTRUCTION = """
You are a helpful AI chat bot.
"""
    suspend operator fun invoke(client: HttpClient, message: String): Response {
        val conversationHistory = mutableListOf<ChatMessage>()
        conversationHistory.add(ChatMessage(Role.SYSTEM.value, POST_SYSTEM_INSTRUCTION))
        conversationHistory.add(ChatMessage(Role.USER.value, message))
        return try {
            client.post(base_url + chatEndPoint) {
                this.contentType(ContentType.Application.Json)
                setBody(ChatRequest(MODEL, "", conversationHistory.toTypedArray()))
                this.timeout {
                    requestTimeoutMillis = 300000
                }
            }.body<ChatCompletion>().let {
                return PostResponse(it.choices.first().message.content)
            }
        }catch (e:Exception){
            Response.ErrorResponse(e.message ?: "Something went wrong")
        }
    }

}