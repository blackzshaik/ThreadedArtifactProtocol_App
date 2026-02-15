package com.blackzshaik.tap.intent

sealed interface ArtifactHistoryIntent{
    data class GetById(val artifactId:String, val prompt:String): ArtifactHistoryIntent
    data class ChangeToVersion(val version:Float): ArtifactHistoryIntent
    data object Previous: ArtifactHistoryIntent
    data object Next: ArtifactHistoryIntent
}