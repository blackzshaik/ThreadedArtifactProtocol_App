package com.blackzshaik.tap.di

import com.blackzshaik.tap.ai.KtorClient
import com.blackzshaik.tap.model.datastore.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    fun provideKtorClient(preferencesRepository: PreferencesRepository): KtorClient {
        return KtorClient(preferencesRepository)
    }

}