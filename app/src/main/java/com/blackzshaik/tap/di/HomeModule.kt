package com.blackzshaik.tap.di

import com.blackzshaik.tap.ai.KtorClient
import com.blackzshaik.tap.model.NetworkRepository
import com.blackzshaik.tap.model.NetworkRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object HomeModule {

    @Provides
    fun provideNetworkRepository(ktorClient: KtorClient): NetworkRepository {
        return NetworkRepositoryImpl(ktorClient)
    }
}