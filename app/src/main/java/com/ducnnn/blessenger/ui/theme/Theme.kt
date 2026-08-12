package com.ducnnn.blessenger.ui.theme

import com.ducnnn.blessenger.ui.theme.BgBlue
import com.ducnnn.blessenger.ui.theme.BgPurple
import com.ducnnn.blessenger.ui.theme.BgDarkBlue
import com.ducnnn.blessenger.ui.theme.BgLightBlue
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val LiguidGlassColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color.White,

    primaryContainer = Color(0x3390CAF9),
    onPrimaryContainer = Color.White,

    background = Color.Transparent,
    onBackground = Color.White,
    surface = Color.Transparent,
    onSurface = Color.White,
    surfaceVariant = Color(0x33FFFFFF),
    onSurfaceVariant = Color(0xCCFFFFFF),

    error = Color(0xFFEF5350),
    onError = Color.White,
    errorContainer = Color(0x33EF5350),
    onErrorContainer = Color.White,

    outline = Color(0x40FFFFFF),
    outlineVariant = Color(0x20FFFFFF)



    )


@Composable
fun BlessengerTheme(

    content: @Composable () -> Unit
) {


    MaterialTheme(
        colorScheme = LiguidGlassColorScheme,
        typography = Typography,
        content = content
    )
}