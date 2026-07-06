package com.ducnnn.blessenger.permission

data class PermissionState(
    val permission: String,
    val diplayName: String,
    val description: String,
    val isGranted: Boolean,
    val isPermanentlyDenied: Boolean
)

data class PermissionsScreenState(
    val permissions: List<PermissionState> = emptyList(),
    val allGranted: Boolean = false,
    val showWationale: Boolean = false
)