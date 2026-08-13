package com.ducnnn.blessenger.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.ducnnn.blessenger.navigation.BlessengerScreenDestination
import com.ducnnn.blessenger.components.glassmorphic
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun BlessengerNavBar(currentDestination: NavKey, onTabSelected : (BlessengerScreenDestination) -> Unit) {
    val tabs = listOf(
        BottomTab("Nodes", BlessengerScreenDestination.Nodes),
        BottomTab("Chat", BlessengerScreenDestination.Chat),
        BottomTab("Settings", BlessengerScreenDestination.Settings)
    )
    NavigationBar(
        modifier = Modifier.glassmorphic(),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp

    ) {
        tabs.forEachIndexed { index, tab ->
            NavigationBarItem(
                icon = {Text(text = "A$index")},
                selected = currentDestination == tab.destination,
                onClick = {
                        onTabSelected(tab.destination)
                }
            )
        }
    }
}

data class BottomTab(
    val title : String,
    val destination : BlessengerScreenDestination
)