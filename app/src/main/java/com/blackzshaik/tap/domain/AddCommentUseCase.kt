package com.blackzshaik.tap.domain

import com.blackzshaik.tap.ai.Role
import com.blackzshaik.tap.model.Artifact
import com.blackzshaik.tap.model.ArtifactHistory
import com.blackzshaik.tap.model.Comment
import com.blackzshaik.tap.model.NetworkRepository
import com.blackzshaik.tap.model.database.DatabaseRepository
import com.blackzshaik.tap.utils.CommentsDepth
import java.util.UUID
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val networkRepository: NetworkRepository,
    private val getAllParentCommentsUseCase: GetAllParentCommentsUseCase,
    private val getOptimalCommentsUseCase: GetOptimalCommentsUseCase,
    private val commentsDepthPreferenceUseCase: CommentsDepthPreferenceUseCase
) {
    suspend operator fun invoke(
        artifactId: String,
        newComment: String,
        selectedComment: Comment? = null,
        commentList: List<Comment> = emptyList()
    ): Artifact {
        val originalArtifact = databaseRepository.getArtifactById(artifactId)
        val commentsDepth = commentsDepthPreferenceUseCase()

        val parentComments = when (commentsDepth) {
            CommentsDepth.MINIMUM -> {
                //either single comment main comment or
                //as reply select all parent
                selectedComment?.let {
                    getAllParentCommentsUseCase(commentList, selectedComment) + selectedComment
                } ?: emptyList()
            }

            CommentsDepth.OPTIMAL -> {
                getOptimalCommentsUseCase(artifactId, commentList, selectedComment)
            }

            CommentsDepth.FULL -> {
                //all comments
                commentList
            }
        }


        val userCommentParentID = try {
            when (commentsDepth) {
                CommentsDepth.MINIMUM -> {
                    parentComments.last().id
                }

                else -> {
                    selectedComment?.id ?: artifactId
                }
            }
        } catch (_: NoSuchElementException) {
            originalArtifact._id
        }


        val userCommentId = UUID.randomUUID().toString()
        val assistantCommentId = UUID.randomUUID().toString()

        databaseRepository.updateReplyCommentId(selectedComment?.id, userCommentId)

        val userComment = Comment(
            id = userCommentId,
            originalArtifact._id,
            userCommentParentID,
            false,
            Role.USER.value,
            newComment,
            parentCommentId = selectedComment?.id ?: originalArtifact._id,
            replyCommentId = assistantCommentId,
            repliedToCommentId = selectedComment?.id,
            repliedToComment = selectedComment?.content
        )
        databaseRepository.insertComment(userComment)

        val response = networkRepository.addComment(
            originalArtifact,
            newComment,
            parentComments.sortedBy { it.time })

        val assistantComment = Comment(
            id = assistantCommentId,
            originalArtifact._id,
            userComment.id,
            false,
            Role.ASSISTANT.value,
            response.assistantComment,
            parentCommentId = userCommentId
        )

        databaseRepository.insertComment(assistantComment)
        val updatedArtifact = originalArtifact.copy(
            artifact = response.updatedArtifact ?: originalArtifact.artifact,
            artifactVersion = originalArtifact.artifactVersion.takeIf { response.updatedArtifact != null }
                ?.plus(1.0f) ?: originalArtifact.artifactVersion,
            time = System.currentTimeMillis(),
            commentCount = originalArtifact.commentCount + 1
        )

        databaseRepository.updateArtifact(updatedArtifact)

        if (response.updatedArtifact != null && originalArtifact.artifact != response.updatedArtifact) {
            databaseRepository.insertArtifactHistory(
                ArtifactHistory(
                    _id = UUID.randomUUID().toString(),
                    artifactId = originalArtifact._id,
                    originalArtifact = response.originalArtifact,
                    updatedArtifact = response.updatedArtifact ?: "",
                    userCommentId = userComment.id,
                    assistantCommentId = assistantComment.id,
                    originalArtifactStr = response.originalArtifactStr ?: "",
                    replaceArtifactStr = response.replaceArtifactStr ?: "",
                    version = updatedArtifact.artifactVersion
                )
            )
        }

        return updatedArtifact
    }
}