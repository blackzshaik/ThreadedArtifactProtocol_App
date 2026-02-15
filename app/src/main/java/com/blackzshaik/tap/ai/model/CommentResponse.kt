package com.blackzshaik.tap.ai.model

data class CommentResponse(
    val originalArtifact:String,
    val updatedArtifact:String?,
    val userComment:String,
    val assistantComment:String,
    val originalArtifactStr:String?,
    val replaceArtifactStr:String?
) : Response