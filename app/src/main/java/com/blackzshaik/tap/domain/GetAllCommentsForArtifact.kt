package com.blackzshaik.tap.domain

import com.blackzshaik.tap.model.database.DatabaseRepository
import javax.inject.Inject

class GetAllCommentsForArtifact @Inject constructor(private  val databaseRepository: DatabaseRepository) {
    suspend operator fun invoke(id: String) = databaseRepository.getAllCommentsForArtifact(id)

}