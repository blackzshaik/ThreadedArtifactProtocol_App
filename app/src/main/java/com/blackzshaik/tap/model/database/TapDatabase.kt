package com.blackzshaik.tap.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.blackzshaik.tap.model.Artifact
import com.blackzshaik.tap.model.ArtifactHistory
import com.blackzshaik.tap.model.Comment

@Database(entities = [Artifact::class, Comment::class, ArtifactHistory::class], version = 1)
abstract class TapDatabase : RoomDatabase(){

    abstract fun artifactDao(): ArtifactDao

    abstract fun commentDao(): CommentDao

    abstract fun artifactHistoryDao(): ArtifactHistoryDao

}