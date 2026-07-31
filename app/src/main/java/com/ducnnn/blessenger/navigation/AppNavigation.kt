package com.ducnnn.blessenger.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.ducnnn.blessenger.mesh.BleManager
import com.ducnnn.blessenger.ui.BlessengerScreen
import com.ducnnn.blessenger.ui.permission.PermissionScreen
import java.security.Permission


@Composable
fun AppNavigation(startWithPermissionGranted: Boolean = false) {
    val startRoute = if (startWithPermissionGranted) {
        BlessengerScreenRoute
    } else {
        PermissionScreenRoute
    }

    val backStack = rememberNavBackStack(startRoute)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        entryProvider = entryProvider {

            entry<PermissionScreenRoute> {
                PermissionScreen(
                    onAllPermissionsGranted = {
                        backStack.clear()
                        backStack.add(BlessengerScreenRoute)
                    }
                )
            }
            entry<BlessengerScreenRoute> {
                BlessengerScreen()
            }
        }
    )
}