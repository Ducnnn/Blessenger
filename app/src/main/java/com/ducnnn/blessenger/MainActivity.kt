package com.ducnnn.blessenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.ducnnn.blessenger.navigation.AppNavigation
import com.ducnnn.blessenger.permission.PermissionHelper
import com.ducnnn.blessenger.ui.theme.BlessengerTheme
import com.ducnnn.blessenger.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlessengerTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    BgLightGreen,
                                    BgBlue,
                                    BgDarkBlue,
                                    BgPurple,
                                    BgDeepPurple,
                                    BgPink
                                )
                            )
                        )
                ) {
                    val allPermissionsGranted =
                        PermissionHelper.areAllPermissionsGranted(this@MainActivity)
                    AppNavigation(startWithPermissionGranted = allPermissionsGranted)
                }
            }
        }
    }
}
