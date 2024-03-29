package com.example.perceive.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

/**
 * A concrete implementation of [UserPreferencesManager] that uses [DataStore] to store
 * user preferences.
 */
class PerceiveUserPreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesManager {

    override val preferencesStream: Flow<PerceiveAppPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            val isAssistantMuted = preferences[DatastoreKeys.IS_ASSISTANT_MUTED] ?: false
            PerceiveAppPreferences(isAssistantMuted = isAssistantMuted)
        }

    override suspend fun setAssistantMutedStatus(isAssistantMuted: Boolean) {
        dataStore.edit { it[DatastoreKeys.IS_ASSISTANT_MUTED] = isAssistantMuted }
    }

    private object DatastoreKeys {
        val IS_ASSISTANT_MUTED = booleanPreferencesKey("is_assistant_muted")
    }
}