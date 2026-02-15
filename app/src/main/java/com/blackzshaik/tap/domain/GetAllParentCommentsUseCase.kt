package com.blackzshaik.tap.domain

import com.blackzshaik.tap.model.Comment
import java.lang.Exception
import javax.inject.Inject

class GetAllParentCommentsUseCase @Inject constructor(){
    operator fun invoke(commentList:List<Comment>,selectedComment: Comment): List<Comment> {
        var hasParent = true
        val mutableList = mutableListOf<Comment>(selectedComment)
        var parentId = selectedComment.parentId

        try {
            while (hasParent) {
                val parentComment = commentList.find { it.id == parentId }
                if (parentComment != null) {
                    mutableList.add(parentComment)
                    parentId = parentComment.parentId
                } else {
                    hasParent = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mutableList.reversed()
    }
}