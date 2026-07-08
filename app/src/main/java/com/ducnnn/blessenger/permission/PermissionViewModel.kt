package com.ducnnn.blessenger.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.ducnnn.blessenger.permission.PermissionsScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.ducnnn.blessenger.permission.PermissionPreferences
import com.ducnnn.blessenger.permission.PermissionItemState

class PermissionViewModel : ViewModel() {

    private val _state =
        MutableStateFlow(PermissionsScreenState())
    val state: StateFlow<PermissionsScreenState> =
        _state.asStateFlow()

    private var permissionPreferences: PermissionPreferences? = null

    fun initialize(context: Context) {
        permissionPreferences = PermissionPreferences(context)
        refreshPermissionStates(context)
    }

//    init {
//        permissionPreferences = PermissionPreferences(context)
//        refreshPermissionStates(context)
//    }

    fun refreshPermissionStates(context: Context) {
        val prefs = permissionPreferences?: return
        val activity = context as? Activity
        val permissionInfos = PermissionHelper.getRequiredPermissions()
        val permissionStates = permissionInfos.map { info ->
            val isGranted = PermissionHelper.isPermissionGranted(
                context,
                info.permission
            )

            val wasRequested =
                prefs.wasPermissionRequested(info.permission)

            val shouldShowRational = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, info.permission)
            } ?: false

            val isPermanentlyDenied = wasRequested && !isGranted && !shouldShowRational

            PermissionItemState(
                permission = info.permission,
                displayName = info.displayName,
                description = info.description,
                isGranted = isGranted,
                isPermanentlyDenied = isPermanentlyDenied
            )
        }

        _state.update {
            it.copy(
                permissions = permissionStates,
                allGranted = permissionStates.all { p ->
                    p.isGranted
                },
                showRationale = permissionStates.any { p ->
                    p.isPermanentlyDenied
                }
            )
        }
    }


    // called only after getting results of permission request
    fun onPermissionResults(
        context: Context, results:
        Map<String, Boolean>
    ) {
        val prefs = permissionPreferences?: return
        prefs.markAllPermissionsRequested(results.keys.toList())
        refreshPermissionStates(context)
    }

    fun openAppSettings(context: Context) {
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        context.startActivity(intent)
    }


}





