package com.example.food_ucsc.data.local

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveSession(token: String, userId: Int) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putInt(KEY_USER_ID, userId)
            .apply()
    }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_USER_ID)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserId(): Int? {
        val value = prefs.getInt(KEY_USER_ID, -1)
        return if (value == -1) null else value
    }

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    private companion object {
        const val PREF_NAME = "food_ucsc_session"
        const val KEY_TOKEN = "token"
        const val KEY_USER_ID = "user_id"
    }
}
