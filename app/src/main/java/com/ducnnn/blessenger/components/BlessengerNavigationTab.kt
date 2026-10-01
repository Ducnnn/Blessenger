package com.ducnnn.blessenger.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.ducnnn.blessenger.navigation.BlessengerScreenDestination
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ducnnn.blessenger.ui.icons.Graph5
import com.ducnnn.blessenger.ui.icons.Chat
import com.ducnnn.blessenger.ui.icons.Settings

@Composable
fun BlessengerNavBar(
    currentDestination: NavKey,
    onTabSelected: (BlessengerScreenDestination) -> Unit
) {
    val tabs = listOf(
        BottomTab("Nodes", BlessengerScreenDestination.Nodes),
        BottomTab("Chat", BlessengerScreenDestination.Chat),
        BottomTab("Settings", BlessengerScreenDestination.Settings)
    )
    val icons = listOf(
        Graph5,
        Chat,
        Settings
    )
    NavigationBar(
        modifier = Modifier.glassmorphic(),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp

    ) {
        tabs.forEachIndexed { index, tab ->
            NavigationBarItem(

                icon = {
                    Icon(
                        modifier = Modifier.width(24.dp).aspectRatio(0.5f),
                        imageVector = icons[index],
                        contentDescription = "",
                    )
                },
                selected = currentDestination == tab.destination,
                onClick = {
                    onTabSelected(tab.destination)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}

data class BottomTab(
    val title: String,
    val destination: BlessengerScreenDestination
)