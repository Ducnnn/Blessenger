package com.ducnnn.blessenger.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.glassmorphic(
    cornerRadius: Dp = 16.dp,
    borderAlpha: Float = 0.2F,
    backgroundAlpha: Float = 0.15f
) : Modifier {
    val shape = RoundedCornerShape(cornerRadius)

    return this
        .clip(shape)
        .background(
            color = Color.White.copy(alpha = backgroundAlpha),
            shape = shape
        )
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = borderAlpha),
                    Color.White.copy(alpha = borderAlpha / 4f)
                )
            ),
            shape = shape
        )
}