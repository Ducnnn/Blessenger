package com.ducnnn.blessenger.ui

import android.content.Intent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ducnnn.blessenger.components.BlessengerNavBar
import com.ducnnn.blessenger.components.BlessengerTopAppBar
import com.ducnnn.blessenger.mesh.MeshService
import com.ducnnn.blessenger.navigation.BlessengerScreenDestination
import com.ducnnn.blessenger.ui.chat.ChatScreen
import com.ducnnn.blessenger.ui.chat.ChatScreenViewModel
import com.ducnnn.blessenger.ui.nodes.NodesScreen
import com.ducnnn.blessenger.ui.settings.SettingsScreen


@Composable
fun BlessengerScreen() {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(BlessengerScreenDestination.Chat)
    val currentDestination = backStack.last()
    val chatViewModel: ChatScreenViewModel = viewModel()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
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
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
            popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
            predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<BlessengerScreenDestination.Chat> {
                    ChatScreen(
                        viewModel = chatViewModel,
                        onContactClick = { contactId, contactName ->
                            backStack.openContactChat(
                                contactId,
                                contactName
                            )
                        })
                }
                entry<BlessengerScreenDestination.Settings> {
                    SettingsScreen()
                }
                entry<BlessengerScreenDestination.Nodes> {
                    NodesScreen()
                }
                entry<BlessengerScreenDestination.ChatWithContact> { entry ->
                    ChatScreen(viewModel { ChatScreenViewModel(entry.contactId) })
                }
            }
        )
    }
}

private fun NavBackStack<NavKey>.openContactChat(contactId: String, contactName: String) {
    val dest = BlessengerScreenDestination.ChatWithContact(contactId, contactName)
    remove(dest)
    add(dest)
}

