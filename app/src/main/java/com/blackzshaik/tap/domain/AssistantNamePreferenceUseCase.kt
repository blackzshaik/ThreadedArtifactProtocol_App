package com.blackzshaik.tap.domain

import com.blackzshaik.tap.model.datastore.AI_NAME
import com.blackzshaik.tap.model.datastore.PreferencesRepository
import com.blackzshaik.tap.model.datastore.USER_NAME
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AssistantNamePreferenceUseCase @Inject constructor(private val preferencesRepository: PreferencesRepository) {

    suspend operator fun invoke(aiName: String) {
        preferencesRepository.update(AI_NAME, aiName)
    }

    suspend operator fun invoke(): String {
        return preferencesRepository.aiName.first()
    }

}