package com.blackzshaik.tap.domain

import com.blackzshaik.tap.model.Artifact
import com.blackzshaik.tap.model.ArtifactHistory
import com.blackzshaik.tap.model.NetworkRepository
import com.blackzshaik.tap.model.database.DatabaseRepository
import java.util.UUID
import javax.inject.Inject

class CreateArtifactUseCase @Inject constructor(private val homeRepository: NetworkRepository,
                                                private val databaseRepository: DatabaseRepository
){

    suspend operator fun invoke(prompt:String) : Unit{
        val   artifactResponse = homeRepository.createArtifact(prompt)
        val artifact = Artifact(_id = UUID.randomUUID().toString(),artifact = artifactResponse.artifact, prompt = prompt)
        databaseRepository.insertArtifact(artifact )
        databaseRepository.insertArtifactHistory(ArtifactHistory(
            _id = UUID.randomUUID().toString(),
            artifactId = artifact._id,
            originalArtifact = artifact.artifact,
            updatedArtifact = artifact.artifact,
            version = 1.0f,
            originalArtifactStr = artifact.artifact,
            replaceArtifactStr = artifact.artifact
        ))
        return
    }

}