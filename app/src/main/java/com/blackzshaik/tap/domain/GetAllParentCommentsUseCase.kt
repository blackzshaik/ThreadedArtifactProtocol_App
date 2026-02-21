package com.blackzshaik.tap.domain

import com.blackzshaik.tap.model.Comment
import javax.inject.Inject

class GetAllParentCommentsUseCase @Inject constructor(){
    operator fun invoke(commentList:List<Comment>,selectedComment: Comment): List<Comment> {
        val mutableList = mutableListOf<Comment>()
        filter(selectedComment,commentList,mutableList)
        return mutableList.reversed()
    }

    private fun filter(selectedComment: Comment, comment: List<Comment>, filteredList: MutableList<Comment>):List<Comment>{
        val parentComment =  comment.find { it.id  == selectedComment.parentCommentId}
        if (parentComment != null){
            filteredList.add(parentComment)
            filter(parentComment,comment,filteredList)
        }
        return filteredList
    }
}
