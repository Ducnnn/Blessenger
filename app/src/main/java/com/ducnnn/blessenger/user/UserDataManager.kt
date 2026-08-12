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

    fun generateHexId(): String {
        val allowedCharacters = ('0'..'9') + ('a'..'f')
        val arr = mutableListOf<Char> ()
        for (i in 1..8) {
            arr.add(allowedCharacters.random())
        }
        return arr.joinToString("")
    }

    fun getSavedId() : String {
        var id = sharedPreferences.getString("user_id", null)

        if (id == null) {
            id = generateHexId()
            saveUserId(id)
        }
        return id
    }


}