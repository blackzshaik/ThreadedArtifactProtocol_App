package com.blackzshaik.tap.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Artifact (
    @PrimaryKey(autoGenerate = false)
    var _id:String = "",
    var title:String ="",
    var prompt:String = "",
    var artifact:String = "",
    @Ignore var commentList:List<Comment> = emptyList(),
    @Ignore var artifactHistory:List<String> = emptyList(),
    var artifactVersion:Float = 1.0f,
    var commentCount:Int = 0,
    var time:Long = System.currentTimeMillis()
)