package com.app.alphabetlauncher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    background = Color(0xFF0A111C),
    onBackground = Color(0xFFF4F7FC),
    surface = Color(0xFF101C2B),
    onSurface = Color(0xFFF4F7FC),
    primary = Color(0xFFB8D5F0),
    onPrimary = Color(0xFF102336)
)

private val LightColors = lightColorScheme(
    background = Color(0xFFF7F9FC),
    onBackground = Color(0xFF1A2939),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A2939),
    primary = Color(0xFF315E81),
    onPrimary = Color.White
)

@Composable
fun AlphabetLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
