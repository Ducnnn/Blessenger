package com.ducnnn.blessenger.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.ducnnn.blessenger.navigation.BlessengerScreenDestination
import com.ducnnn.blessenger.ui.chat.ChatMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlessengerTopAppBar(
    currentScreen: NavKey,
    selectedMode: ChatMode,
    onClick: (ChatMode) -> Unit
) {
    TopAppBar(
        title = {
            val title = when (currentScreen) {
                is BlessengerScreenDestination.Chat -> "Blessenger"
                is BlessengerScreenDestination.Settings -> "Settings"
                else -> ""
            }
            Text(title)
        },
        navigationIcon = {},
        actions = {
            if (currentScreen == BlessengerScreenDestination.Chat) {
                SelectChatModeDropdown(selectedMode, onClick)
            }
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectChatModeDropdown(selectedMode: ChatMode, onClick: (ChatMode) -> Unit) {
    val options = ChatMode.entries

    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.padding(16.dp)
    ) {
        TextButton(onClick = { expanded = !expanded }) {
            Text(text = selectedMode.value)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(text = mode.value) },
                    onClick = {
                        onClick(mode)
                        expanded = false
                    }
                )
            }
        }
    }

}