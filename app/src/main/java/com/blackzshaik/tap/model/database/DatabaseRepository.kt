package com.blackzshaik.tap.model.database

import com.blackzshaik.tap.model.Artifact
import com.blackzshaik.tap.model.ArtifactHistory
import com.blackzshaik.tap.model.Comment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseRepository @Inject constructor(private val artifactDao: ArtifactDao,
                                             private val commentDao: CommentDao,
                                             private val artifactHistoryDao:ArtifactHistoryDao) {

    suspend fun insertArtifact(artifact: Artifact) {
        artifactDao.insertArtifact(artifact)
    }

    fun getAllArtifacts(): Flow<List<Artifact>> {
        return artifactDao.getAllArtifacts()
    }

    suspend fun getArtifactById(id: String): Artifact {
        return artifactDao.getArtifactById(id)
    }

    suspend fun insertComment(comment: Comment) {
        commentDao.insertComment(comment)
    }

    suspend fun getAllCommentsForArtifact(artifactId: String): Flow<List<Comment>> {
        return commentDao.getCommentsForArtifact(artifactId)
    }

    suspend fun updateArtifact(artifact: Artifact): Int {
        return artifactDao.updateArtifact(artifact)
    }

    suspend fun insertArtifactHistory(artifactHistory: ArtifactHistory) {
        artifactHistoryDao.insertArtifactHistory(artifactHistory)
    }

    suspend fun getHistoryForArtifact(artifactId: String): List<ArtifactHistory> {
        return artifactHistoryDao.getHistoryForArtifact(artifactId)
    }



}