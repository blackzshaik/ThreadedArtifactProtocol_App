package com.blackzshaik.tap.domain

import com.blackzshaik.tap.model.datastore.PreferencesRepository
import com.blackzshaik.tap.model.datastore.SERVER_URL
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ServerURLPreferenceUseCase @Inject constructor(private val preferencesRepository: PreferencesRepository)  {
    suspend operator fun invoke(serverUrl: String) {
        preferencesRepository.update(SERVER_URL, serverUrl)
    }

    suspend operator fun invoke(): String {
        return preferencesRepository.serverUrl.first()
    }

}