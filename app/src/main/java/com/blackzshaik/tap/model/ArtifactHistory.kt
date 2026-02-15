package com.blackzshaik.tap.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ArtifactHistory(
    @PrimaryKey(autoGenerate = false)
    var _id:String = "",
    var artifactId:String = "",
    var originalArtifact:String = "",
    var updatedArtifact:String = "",
    var time:Long = System.currentTimeMillis(),
    var version: Float = 1.0f,
    var userCommentId:String = "",
    var assistantCommentId:String = "",
    var originalArtifactStr:String = "",
    var replaceArtifactStr:String = ""
)