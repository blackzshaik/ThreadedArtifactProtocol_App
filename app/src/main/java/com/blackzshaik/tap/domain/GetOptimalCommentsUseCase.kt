package com.blackzshaik.tap.domain

import com.blackzshaik.tap.model.Comment
import javax.inject.Inject


class GetOptimalCommentsUseCase @Inject constructor(private val getAllParentCommentsUseCase: GetAllParentCommentsUseCase){
    operator fun invoke(artifactId:String,commentList:List<Comment>,selectedComment: Comment?): List<Comment> {

        val optimalComment = mutableListOf<Comment>()
        val replies = selectedComment?.let {
            getAllParentCommentsUseCase(commentList, selectedComment) + selectedComment
        }

        //only top level comments with replies or current
        val parentUserComment = commentList.filter { it.parentCommentId == artifactId }

        parentUserComment.forEach { parent ->
            optimalComment.add(parent)
            commentList.first { it.parentCommentId == parent.id }.let { reply ->
                optimalComment.add(reply)
            }
        }
        replies?.let {
            optimalComment.addAll(it)
        }
        return optimalComment.distinctBy { it.id }
    }
}