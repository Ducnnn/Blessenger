package com.ducnnn.blessenger.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.ducnnn.blessenger.MainScreen
import com.ducnnn.blessenger.ui.BlessengerScreen
import com.ducnnn.blessenger.ui.ChatScreen
import com.ducnnn.blessenger.permission.PermissionScreen


@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(PermissionScreenRoute)

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
                        backStack.add(MainScreenRoute)
                    }
                )
            }
            entry<MainScreenRoute> {
                MainScreen(
                    navigateToChatScreen = {
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