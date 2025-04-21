package com.example.rental_recommend.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.rental_recommend.model.User

object UserManager {
    private const val PREF_NAME = "user_preferences"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USERNAME = "username"
    private const val KEY_TOKEN = "token"

    var currentUser by mutableStateOf<User?>(null)
        private set

    fun getInstance(): UserManager = this

    fun saveUserInfo(context: Context, userId: Int, username: String, token: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putString(KEY_TOKEN, token)
            apply()
        }
    }

    fun loadUserInfo(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val userId = prefs.getInt(KEY_USER_ID, -1)
        val username = prefs.getString(KEY_USERNAME, null)
        val token = prefs.getString(KEY_TOKEN, null)

        return if (userId != -1 && username != null && token != null) {
            currentUser = User(
                id = userId,
                username = username
            )
            true
        } else {
            false
        }
    }

    fun clearUserInfo(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
        currentUser = null
    }

    fun getToken(context: Context): String? {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_TOKEN, null)
    }
} 