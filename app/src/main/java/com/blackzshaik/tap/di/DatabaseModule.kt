package com.blackzshaik.tap.di

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.blackzshaik.tap.model.database.TapDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): TapDatabase{
        return Room.databaseBuilder(
            context,
            TapDatabase::class.java,
            "tap_database"
        ).build()
    }

    @Provides
    fun provideArtifactDao(database: TapDatabase) = database.artifactDao()

    @Provides
    fun provideCommentDao(database: TapDatabase) = database.commentDao()

    @Provides
    fun provideArtifactHistoryDao(database: TapDatabase) = database.artifactHistoryDao()
}