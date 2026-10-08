package com.burlaychiki.hakatonapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val sessionKey = stringPreferencesKey("session_id")

    val sessionId: Flow<String?> = dataStore.data.map { it[sessionKey] }

    suspend fun saveSession(id: String) {
        dataStore.edit { it[sessionKey] = id }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}