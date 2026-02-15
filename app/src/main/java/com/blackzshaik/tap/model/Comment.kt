package com.blackzshaik.tap.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity
data class Comment (
    @PrimaryKey(autoGenerate = false)
    var id:String = "",
    var artifactId:String = "",
    var parentId:String = "",
    var isReply:Boolean = false,
    var role: String = "",
    var content:String = "",
    @Ignore var replyList:List<Comment> = emptyList(),
    var time: Long = System.currentTimeMillis(),
    var repliedToCommentId:String? = null,
    var repliedToComment:String? = null
)