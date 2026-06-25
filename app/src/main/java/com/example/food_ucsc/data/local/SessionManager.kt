package com.example.food_ucsc.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "food_ucsc_session")

class SessionManager(context: Context) {
    private val appContext = context.applicationContext

    val authTokenFlow: Flow<String?> = appContext.sessionDataStore.data
        .map { prefs -> prefs[KEY_TOKEN] }

    fun saveSession(token: String, userId: Int) {
        runBlocking {
            appContext.sessionDataStore.edit { prefs ->
                prefs[KEY_TOKEN] = token
                prefs[KEY_USER_ID] = userId
            }
        }
    }

    fun clearSession() {
        runBlocking {
            appContext.sessionDataStore.edit { prefs ->
                prefs.remove(KEY_TOKEN)
                prefs.remove(KEY_USER_ID)
            }
        }
    }

    fun getToken(): String? = runBlocking {
        authTokenFlow.first()
    }

    fun getUserId(): Int? {
        val value = runBlocking {
            appContext.sessionDataStore.data.map { prefs -> prefs[KEY_USER_ID] ?: -1 }.first()
        }
        return if (value == -1) null else value
    }

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    private companion object {
        val KEY_TOKEN = stringPreferencesKey("token")
        val KEY_USER_ID = intPreferencesKey("user_id")
    }
}
