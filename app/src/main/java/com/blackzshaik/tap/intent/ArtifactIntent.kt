package com.blackzshaik.tap.intent

import com.blackzshaik.tap.model.Artifact
import com.blackzshaik.tap.model.Comment

sealed interface ArtifactIntent{
    data class OnShowArtifact(val data: Artifact): ArtifactIntent
    data class AddComment(val newComment:String): ArtifactIntent

    data class OnClickReplyComment(val assistantComment: Comment): ArtifactIntent
    data object OnClickShowFullScreen: ArtifactIntent
    data class ShowFullScreen(val data: Artifact): ArtifactIntent

    data object OnClickClearReply: ArtifactIntent
}