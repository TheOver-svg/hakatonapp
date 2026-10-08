package com.burlaychiki.hakatonapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class ConnectionData(
    val host: String,
    val port: Int,
    val token: String?,
    val pcName: String
)

@Singleton
class ConnectionStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val HOST = stringPreferencesKey("host")
        val PORT = intPreferencesKey("port")
        val TOKEN = stringPreferencesKey("token")
        val PC_NAME = stringPreferencesKey("pc_name")
    }

    val connection: Flow<ConnectionData?> = dataStore.data.map { prefs ->
        val host = prefs[Keys.HOST]
        val port = prefs[Keys.PORT]
        if (host == null || port == null) null
        else ConnectionData(host, port, prefs[Keys.TOKEN], prefs[Keys.PC_NAME] ?: "PC")
    }

    suspend fun saveAddress(host: String, port: Int, pcName: String) {
        dataStore.edit {
            it[Keys.HOST] = host
            it[Keys.PORT] = port
            it[Keys.PC_NAME] = pcName
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { it[Keys.TOKEN] = token }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}