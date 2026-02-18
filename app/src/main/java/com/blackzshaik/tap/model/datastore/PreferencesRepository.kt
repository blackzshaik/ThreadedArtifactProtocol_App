package com.blackzshaik.tap.model.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    val data = context.dataStore.data
    val userName = context.dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: "User"
    }
    val aiName = context.dataStore.data.map { preferences ->
        preferences[AI_NAME] ?: "Assistant"
    }

    val commentsDepth = context.dataStore.data.map { preferences ->
        preferences[COMMENTS_DEPTH] ?: "MINIMUM"
    }

    val serverUrl = context.dataStore.data.map { preferences ->
        preferences[SERVER_URL] ?: "http://127.0.0.1:8080"
    }

    suspend fun update(key: Preferences.Key<String>, value: String){
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

}