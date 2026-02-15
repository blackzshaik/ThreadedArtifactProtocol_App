package com.blackzshaik.tap.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.blackzshaik.tap.model.Artifact
import kotlinx.coroutines.flow.Flow
@Dao
interface ArtifactDao {
    @Insert
    suspend fun insertArtifact(artifact: Artifact)

    @Query("SELECT * FROM artifact ORDER by time DESC")
    fun getAllArtifacts(): Flow<List<Artifact>>

    @Query("SELECT * FROM artifact WHERE _id = :id")
    suspend fun getArtifactById(id: String): Artifact

    @Update
    suspend fun updateArtifact(artifact: Artifact): Int

}
