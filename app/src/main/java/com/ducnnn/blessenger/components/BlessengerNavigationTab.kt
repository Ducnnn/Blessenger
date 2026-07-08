package com.ducnnn.blessenger.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.ducnnn.blessenger.navigation.BlessengerScreenDestination


@Composable
fun BlessengerNavBar(currentDestination: NavKey, onTabSelected : (BlessengerScreenDestination) -> Unit) {
    val tabs = listOf(
        BottomTab("Chat", BlessengerScreenDestination.Chat),
        BottomTab("Settings", BlessengerScreenDestination.Settings)
    )
    NavigationBar {
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