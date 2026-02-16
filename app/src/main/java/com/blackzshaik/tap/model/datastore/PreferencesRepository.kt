package com.blackzshaik.tap.model.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    val userName = context.dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: "User"
    }
    val aiName = context.dataStore.data.map { preferences ->
        preferences[AI_NAME] ?: "Assistant"
    }

    val commentsDepth = context.dataStore.data.map { preferences ->
        preferences[COMMENTS_DEPTH] ?: "MINIMUM"
    }

    suspend fun update(key: Preferences.Key<String>, value: String){
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

}