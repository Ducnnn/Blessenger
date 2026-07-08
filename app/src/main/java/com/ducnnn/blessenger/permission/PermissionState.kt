package com.ducnnn.blessenger.permission

data class PermissionItemState(
    val permission: String,
    val displayName: String,
    val description: String,
    val isGranted: Boolean,
    val isPermanentlyDenied: Boolean
)

data class PermissionsScreenState(
    val permissions: List<PermissionItemState> = emptyList(),
    val allGranted: Boolean = false,
    val showRationale: Boolean = false
)