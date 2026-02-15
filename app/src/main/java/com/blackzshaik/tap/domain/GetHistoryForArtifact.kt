package com.blackzshaik.tap.domain

import com.blackzshaik.tap.model.ArtifactHistory
import com.blackzshaik.tap.model.database.DatabaseRepository
import javax.inject.Inject

class GetHistoryForArtifact @Inject constructor(private val databaseRepository: DatabaseRepository) {
    suspend operator fun invoke(artifactId: String): List<ArtifactHistory> = databaseRepository.getHistoryForArtifact(artifactId)
}