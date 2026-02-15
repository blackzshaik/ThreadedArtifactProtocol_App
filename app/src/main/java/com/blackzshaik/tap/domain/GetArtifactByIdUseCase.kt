package com.blackzshaik.tap.domain

import com.blackzshaik.tap.model.database.DatabaseRepository
import javax.inject.Inject

class GetArtifactByIdUseCase @Inject constructor(private val databaseRepository: DatabaseRepository){
    suspend operator fun invoke(id: String) = databaseRepository.getArtifactById(id)
}