package com.ducnnn.blessenger.permission

import android.content.Context
import android.content.SharedPreferences

class PermissionPreferences(context: Context) {
    companion object {
        private const val PRESS_NAME =
            "blessenger_permissions"
        private const val KEY_PREFIX_REQUESTED =
            "permission_requested"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(
            PRESS_NAME,
            Context.MODE_PRIVATE)

    fun markPermissionRequested(permission: String) {
        prefs.edit().putBoolean(KEY_PREFIX_REQUESTED
        + permission, true).apply()
    }

    fun wasPermissionRequested(permission: String): Boolean {

        return prefs.getBoolean(KEY_PREFIX_REQUESTED +
        permission, false)
    }

    fun markAllPermissionsRequested(permissions:
    List<String>) {
        val editor = prefs.edit()
        permissions.forEach { permission ->
            editor.putBoolean(KEY_PREFIX_REQUESTED +
            permission, true)
        }
        editor.apply()
    }
}

