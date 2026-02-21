package com.blackzshaik.tap.domain

import com.blackzshaik.tap.model.Comment
import javax.inject.Inject

class CommentToUiCommentMapper @Inject constructor() {
    fun map(straightListOfComments: List<Comment>, _id: String?): List<Comment> {
        val parentComments = try {
            straightListOfComments.filter { it.parentCommentId == _id }
        } catch (nse: NoSuchElementException) {
            null
        }

        return parentComments?.map {
            append(it, straightListOfComments)
        } ?: emptyList()
    }

    private fun append(uiComment: Comment, comment: List<Comment>): Comment {
        uiComment.replyComment = comment.find { it.id == uiComment.replyCommentId }
        if (uiComment.replyComment != null) {
            append(uiComment.replyComment!!, comment)
        }
        return uiComment
    }
}