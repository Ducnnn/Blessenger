package com.ducnnn.blessenger.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object MainScreenRoute : NavKey
@Serializable data object BlessengerScreenRoute : NavKey
@Serializable
sealed interface BlessengerScreenDestination : NavKey {
    @Serializable data object Chat : BlessengerScreenDestination
    @Serializable data object Settings : BlessengerScreenDestination
}