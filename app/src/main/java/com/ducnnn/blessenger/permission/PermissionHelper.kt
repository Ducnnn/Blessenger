package com.ducnnn.blessenger.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionHelper {
    data class PermissionInfo(
        val permission: String,
        val displayName: String,
        val description: String
    )

    fun getRequiredPermissions(): List<PermissionInfo> {
        val permissions = mutableListOf(
            PermissionInfo(
                permission = Manifest.permission.BLUETOOTH_SCAN,
                displayName = "Bluetooth-Scanning",
                description = "finding users not far from you"
            ),
            PermissionInfo(
                permission = Manifest.permission.BLUETOOTH_CONNECT,
                displayName = "Bluetooth-connection",
                description = "allows you to connect to devices which were found"
            ),
            PermissionInfo(
                permission = Manifest.permission.BLUETOOTH_ADVERTISE,
                displayName = "Bluetooth-visibility",
                description = "Allows other users to find you"
            ),
            PermissionInfo(
                permission = Manifest.permission.ACCESS_FINE_LOCATION,
                displayName = "accurate location",
                description = "essential for Bluetooth-scanning"
            ),
            PermissionInfo(
                permission = Manifest.permission.ACCESS_COARSE_LOCATION,
                displayName = "Approximate Location",
                description = "essential for bluetooth-scanning"
            )
        )

        return permissions
    }

    fun getRequiredPermissionStrings(): Array<String> {
        return getRequiredPermissions().map{it -> it.permission}.toTypedArray()
    }

    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun areAllPermissionsGranted(context: Context): Boolean {
        return getRequiredPermissions().all {
            info -> isPermissionGranted(context, info.permission)
        }
    }

    fun getDeniedPermissions(context: Context): List<PermissionInfo> {
        return getRequiredPermissions().filter{
            info -> !isPermissionGranted(context, info.permission)
        }
    }
}

