package com.blackzshaik.tap.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.blackzshaik.tap.model.ArtifactHistory

@Dao
interface ArtifactHistoryDao {
    @Insert
    suspend fun insertArtifactHistory(artifactHistory: ArtifactHistory)

    @Query("SELECT * FROM artifacthistory WHERE artifactId = :artifactId ORDER BY time DESC")
    suspend fun getHistoryForArtifact(artifactId: String): List<ArtifactHistory>

}