package com.app.alphabetlauncher.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.max

@Composable
internal fun LauncherBackground(modifier: Modifier = Modifier) {
    val dark = isSystemInDarkTheme()

    Canvas(modifier) {
        val reach = max(size.width, size.height)
        drawRect(
            brush = Brush.verticalGradient(
                if (dark) {
                    listOf(Color(0xFF101E2C), Color(0xFF0A111C), Color(0xFF101925))
                } else {
                    listOf(Color(0xFFF8FBFD), Color(0xFFF1F5F9), Color(0xFFEAF1F6))
                }
            )
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = if (dark) {
                    listOf(Color(0xFF467894).copy(alpha = 0.17f), Color.Transparent)
                } else {
                    listOf(Color(0xFF9ECFE1).copy(alpha = 0.25f), Color.Transparent)
                },
                center = Offset(size.width * 0.05f, size.height * 0.18f),
                radius = reach * 0.65f
            )
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = if (dark) {
                    listOf(Color(0xFF587390).copy(alpha = 0.11f), Color.Transparent)
                } else {
                    listOf(Color(0xFFB9C7E9).copy(alpha = 0.20f), Color.Transparent)
                },
                center = Offset(size.width * 0.95f, size.height * 0.86f),
                radius = reach * 0.55f
            )
        )
    }
}
