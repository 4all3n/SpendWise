package com.fallen.spenwise.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    fun clearCredentials() {
        sharedPreferences.edit().apply {
            remove(KEY_EMAIL)
            remove(KEY_PASSWORD)
            remove(KEY_USER_ID)
            remove(KEY_GOOGLE_ID_TOKEN)
            apply()
        }
    }

    fun setRememberMe(remember: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_REMEMBER_ME, remember).apply()
    }

    fun getRememberMe(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
    }

    fun saveCredentials(email: String, password: String) {
        sharedPreferences.edit().apply {
            putString(KEY_EMAIL, email)
            putString(KEY_PASSWORD, password)
            apply()
        }
    }

    fun saveGoogleCredentials(idToken: String) {
        sharedPreferences.edit().putString(KEY_GOOGLE_ID_TOKEN, idToken).apply()
    }

    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getEmail(): String? = sharedPreferences.getString(KEY_EMAIL, null)
    fun getPassword(): String? = sharedPreferences.getString(KEY_PASSWORD, null)

    companion object {
        private const val PREF_NAME = "SpendWisePrefs"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
        private const val KEY_USER_ID = "userId"
        private const val KEY_GOOGLE_ID_TOKEN = "googleIdToken"
        private const val KEY_REMEMBER_ME = "rememberMe"
    }
} 