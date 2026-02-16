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
    private val commentsDepthPreferenceUseCase: CommentsDepthPreferenceUseCase
) {
    suspend operator fun invoke(
        artifactId: String,
        newComment: String,
        selectedComment:Comment? = null,
        commentList: List<Comment> = emptyList()
    ): Artifact {
        val artifact = databaseRepository.getArtifactById(artifactId)
        val commentsDepth = commentsDepthPreferenceUseCase()

        val parentComments = when(commentsDepth) {
            CommentsDepth.MINIMUM -> {
                //single comment without context or replies
                 selectedComment?.let {
                    getAllParentCommentsUseCase(commentList, selectedComment)
                } ?: emptyList()
            }
            CommentsDepth.OPTIMAL -> {
                val replies = selectedComment?.let {
                    getAllParentCommentsUseCase(commentList, selectedComment)
                }

                //only top level comments with replies or current
                commentList.filter { it.parentId == artifact._id }.plus(replies ?: emptyList())
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
        } catch (nse: NoSuchElementException) {
            artifact._id
        }

        val userComment = Comment(
            id = UUID.randomUUID().toString(),
            artifact._id,
            userCommentParentID,
            false,
            Role.USER.value,
            newComment,
            repliedToCommentId = selectedComment?.id,
            repliedToComment = selectedComment?.content
        )
        databaseRepository.insertComment(userComment)

        val response = networkRepository.addComment(artifact, newComment, parentComments)

        val assistantComment = Comment(
            id = UUID.randomUUID().toString(),
            artifact._id,
            userComment.id,
            false,
            Role.ASSISTANT.value,
            response.assistantComment
        )

        databaseRepository.insertComment(assistantComment)
        val updatedArtifact = artifact.copy(
            artifact = response.updatedArtifact ?: artifact.artifact,
            artifactVersion = artifact.artifactVersion.takeIf { response.updatedArtifact != null }
                ?.plus(1.0f) ?: artifact.artifactVersion,
            time = System.currentTimeMillis(),
            commentCount = artifact.commentCount + 1
        )

        updatedArtifact.let {
            databaseRepository.updateArtifact(it)
        }
        databaseRepository.insertArtifactHistory(
            ArtifactHistory(
                _id = UUID.randomUUID().toString(),
                artifactId = artifact._id,
                originalArtifact = response.originalArtifact,
                updatedArtifact = response.updatedArtifact ?: "",
                userCommentId = userComment.id,
                assistantCommentId = assistantComment.id,
                originalArtifactStr = response.originalArtifactStr ?: "",
                replaceArtifactStr = response.replaceArtifactStr ?: "",
                version = updatedArtifact.artifactVersion
            )
        )

        return updatedArtifact
    }
}