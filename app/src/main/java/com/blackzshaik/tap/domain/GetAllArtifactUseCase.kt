package com.blackzshaik.tap.domain

import com.blackzshaik.tap.model.Artifact
import com.blackzshaik.tap.model.database.DatabaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllArtifactUseCase @Inject constructor(private val databaseRepository: DatabaseRepository) {
    operator fun invoke(): Flow<List<Artifact>>{
        return databaseRepository.getAllArtifacts()
    }
}