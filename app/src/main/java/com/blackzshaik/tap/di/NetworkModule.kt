package com.blackzshaik.tap.di

import com.blackzshaik.tap.ai.KtorClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    fun provideKtorClient(): KtorClient {
        return KtorClient()
    }

}