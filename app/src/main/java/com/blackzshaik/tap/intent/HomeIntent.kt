package com.blackzshaik.tap.intent

import com.blackzshaik.tap.model.Artifact

sealed interface HomeIntent {
    data object ShowCreateArtifactDialog: HomeIntent
    data object HideCreateArtifactDialog: HomeIntent

    data class CreateArtifact(val prompt: String) : HomeIntent
    data object GetAllArtifacts : HomeIntent

    data class OnClickArtifact(val artifact: Artifact?): HomeIntent

    data object ResetErrorMessage : HomeIntent
}