package com.ducnnn.blessenger.ui

import android.content.Intent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.ducnnn.blessenger.components.BlessengerNavBar
import com.ducnnn.blessenger.components.BlessengerTopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.ducnnn.blessenger.mesh.MeshService
import com.ducnnn.blessenger.navigation.BlessengerScreenDestination
import com.ducnnn.blessenger.ui.chat.ChatScreen
import com.ducnnn.blessenger.ui.chat.ChatScreenViewModel


@Composable
fun BlessengerScreen() {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(BlessengerScreenDestination.Chat)
    val currentDestination = backStack.last()
    val chatViewModel: ChatScreenViewModel = viewModel()
    LaunchedEffect(Unit) {
        Intent(context, MeshService::class.java).also { intent ->
            intent.action = MeshService.Actions.START.toString()
            ContextCompat.startForegroundService(context, intent)
        }
    }

    Scaffold(
        topBar = {
            BlessengerTopAppBar(
                currentScreen = currentDestination,
                selectedMode = chatViewModel.uiState.collectAsState().value.chatMode,
                onClick = chatViewModel::changeChatMode
            )
        },
        bottomBar = {
            BlessengerNavBar(
                currentDestination = currentDestination,
                onTabSelected = { tabDestination ->
                    if (currentDestination != tabDestination) {
                        backStack.clear()
                        backStack.add(BlessengerScreenDestination.Chat)
                        if (tabDestination != BlessengerScreenDestination.Chat) {
                            backStack.add(tabDestination)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        NavDisplay(
            modifier = Modifier.padding(paddingValues),
            backStack = backStack,
            onBack = { },
            transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
            popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
            predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
            entryProvider = entryProvider {
                entry<BlessengerScreenDestination.Chat> {
                    ChatScreen(chatViewModel)
                }
                entry<BlessengerScreenDestination.Settings> {
                    SettingsScreen()
                }
            }
        )
    }
}


@Composable
fun SettingsScreen() {
    Text(text = "Settings")
}