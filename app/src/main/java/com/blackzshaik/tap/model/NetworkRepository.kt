package com.blackzshaik.tap.model

import com.blackzshaik.tap.ai.KtorClient
import com.blackzshaik.tap.ai.model.CommentResponse
import com.blackzshaik.tap.ai.model.PostResponse
import com.blackzshaik.tap.ai.model.Response
import javax.inject.Inject

interface NetworkRepository{
    suspend fun createArtifact(prompt:String): PostResponse
    suspend fun addComment(artifact: Artifact, newComment:String,commentHistory: List<Comment> = emptyList()): CommentResponse
}

class NetworkRepositoryImpl @Inject constructor(val ktorClient: KtorClient): NetworkRepository{

    override suspend fun createArtifact(prompt: String): PostResponse {
        return when (val response = ktorClient.createArtifact(prompt)){
            is PostResponse -> response
            is Response.ErrorResponse -> throw Exception(response.error)
            else -> throw Exception("Something went wrong")
        }
    }

    override suspend fun addComment(
        artifact: Artifact,
        newComment: String,
        commentHistory: List<Comment>
    ): CommentResponse {
        return when (val response = ktorClient.addComment(artifact, newComment,commentHistory)){
            is CommentResponse -> response
            is Response.ErrorResponse -> throw Exception(response.error)
            else -> throw Exception("Something went wrong")
        }
    }

}