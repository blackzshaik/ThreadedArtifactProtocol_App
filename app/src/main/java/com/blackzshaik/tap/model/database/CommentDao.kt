package com.blackzshaik.tap.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.blackzshaik.tap.model.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Insert
    suspend fun insertComment(comment: Comment)

    @Query("SELECT * FROM comment WHERE artifactId = :artifactId")
    fun getCommentsForArtifact(artifactId: String): Flow<List<Comment>>

    @Query("UPDATE comment SET replyCommentId = :userCommentId WHERE id = :id")
    fun updateReplyCommentId(id: String?, userCommentId: String)

}