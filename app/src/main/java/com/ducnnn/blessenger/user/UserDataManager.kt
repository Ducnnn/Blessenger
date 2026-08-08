package com.ducnnn.blessenger.user

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object UserDataManager {

    private lateinit var sharedPreferences: SharedPreferences
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
    }

    fun saveUserId(userId: String) {
        sharedPreferences.edit {
            putString("user_id", userId)
        }
    }

    fun getSavedId(): String {
        return sharedPreferences.getString("user_id", "sampleId") ?: ""
    }
}