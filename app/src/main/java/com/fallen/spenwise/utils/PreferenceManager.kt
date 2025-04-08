package com.fallen.spenwise.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "SpendWisePrefs"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_EMAIL = "email"
        private const val KEY_PASSWORD = "password"
    }

    fun saveCredentials(email: String, password: String) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_REMEMBER_ME, true)
            putString(KEY_EMAIL, email)
            putString(KEY_PASSWORD, password)
            apply()
        }
    }

    fun clearCredentials() {
        sharedPreferences.edit().apply {
            putBoolean(KEY_REMEMBER_ME, false)
            remove(KEY_EMAIL)
            remove(KEY_PASSWORD)
            apply()
        }
    }

    fun setRememberMe(value: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_REMEMBER_ME, value).apply()
    }

    fun getRememberMe(): Boolean = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
    fun getEmail(): String? = sharedPreferences.getString(KEY_EMAIL, null)
    fun getPassword(): String? = sharedPreferences.getString(KEY_PASSWORD, null)
} 