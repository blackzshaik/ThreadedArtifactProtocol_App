package com.blackzshaik.tap.ai

import com.blackzshaik.tap.ai.model.CommentResponse
import com.blackzshaik.tap.ai.model.PostResponse
import com.blackzshaik.tap.ai.model.Response
import com.blackzshaik.tap.model.Artifact
import com.blackzshaik.tap.model.Comment
import com.blackzshaik.tap.model.datastore.PreferencesRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

class KtorClient @Inject constructor(val preferencesRepository: PreferencesRepository){
    val httpClient = HttpClient(){
        install(ContentNegotiation){
            json()
        }
        install(HttpTimeout){
            // Time period in which a client should establish a connection with a server.
            connectTimeoutMillis = 5.minutes.inWholeMilliseconds

            // Maximum time of inactivity between two data packets when exchanging data with a server.
            socketTimeoutMillis = 5.minutes.inWholeMilliseconds

            // Time period required to process an HTTP call (from sending request to receiving response).
            requestTimeoutMillis = 5.minutes.inWholeMilliseconds
        }
    }
    private var serverUrl = ""
    init {
        CoroutineScope(Dispatchers.IO).launch {
            preferencesRepository.serverUrl.collect {
                serverUrl = it
                println(":: serverUrl $serverUrl")
            }
        }
    }


    suspend fun createArtifact(prompt: String): Response {
        return PostInference(httpClient, prompt)
    }

    suspend fun addComment(artifact: Artifact, newComment:String,commentHistory: List<Comment>): Response {
        return CommentInference(httpClient, artifact, newComment,commentHistory)
    }
}