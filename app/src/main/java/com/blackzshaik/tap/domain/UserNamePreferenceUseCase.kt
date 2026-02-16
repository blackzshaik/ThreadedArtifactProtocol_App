package com.blackzshaik.tap.domain

import com.blackzshaik.tap.model.datastore.PreferencesRepository
import com.blackzshaik.tap.model.datastore.USER_NAME
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserNamePreferenceUseCase @Inject constructor(private val preferencesRepository: PreferencesRepository) {

    suspend operator fun invoke(userName: String) {
        preferencesRepository.update(USER_NAME, userName)
    }

    suspend operator fun invoke(): String {
        return preferencesRepository.userName.first()
    }

}