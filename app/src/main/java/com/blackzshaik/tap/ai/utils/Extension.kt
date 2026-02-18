package com.blackzshaik.tap.ai.utils

import com.blackzshaik.tap.ai.chatEndPoint
import com.blackzshaik.tap.ai.model.Response
import com.blackzshaik.tap.ai.model.ServerResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.time.Duration.Companion.minutes

suspend inline fun <reified REQUEST,reified RESPONSE: Response> HttpClient.postChat(serverUrl:String, body: REQUEST): ServerResponse<RESPONSE> {
    return try {
        val successResponse = this.post(serverUrl + chatEndPoint) {
            this.contentType(ContentType.Application.Json)
            setBody(body)
            this.timeout {
                requestTimeoutMillis = 5.minutes.inWholeMilliseconds
            }
        }.body<RESPONSE>()
        ServerResponse(body = successResponse, error = null)
    } catch (e: Exception) {
        e.printStackTrace()
        ServerResponse(body = null, error = Response.ErrorResponse(e.message ?: "Something went wrong"))
    }
}