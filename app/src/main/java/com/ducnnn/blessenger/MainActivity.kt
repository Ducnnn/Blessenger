package com.ducnnn.blessenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ducnnn.blessenger.navigation.AppNavigation
import com.ducnnn.blessenger.ui.theme.BlessengerTheme
import  com.ducnnn.blessenger.permission.PermissionHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlessengerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val allPermissionsGranted =
                        PermissionHelper.areAllPermissionsGranted(this)
                    AppNavigation(startWithPermissionGranted = allPermissionsGranted)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }
}
