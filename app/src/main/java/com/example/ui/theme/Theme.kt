package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WarmLightColorScheme = lightColorScheme(
    primary = ClaudeTerracotta,
    onPrimary = Color.White,
    primaryContainer = ClaudeTerracottaLight,
    onPrimaryContainer = ClaudeAmberWarm,
    secondary = ClaudeAmber,
    onSecondary = Color.White,
    tertiary = ClaudeAmberWarm,
    background = ClaudeWarmCream,
    onBackground = ClaudeTextPrimaryLight,
    surface = ClaudeSurfaceLight,
    onSurface = ClaudeTextPrimaryLight,
    surfaceVariant = ClaudeCardLight,
    onSurfaceVariant = ClaudeTextSecondaryLight,
    outline = ClaudeBorderLight,
    outlineVariant = ClaudeBorderSubtleLight
)

private val WarmDarkColorScheme = darkColorScheme(
    primary = ClaudeTerracotta,
    onPrimary = Color.White,
    primaryContainer = ClaudeTerracottaDark,
    onPrimaryContainer = ClaudeTerracotta,
    secondary = ClaudeAmber,
    onSecondary = Color.White,
    tertiary = ClaudeAmberWarm,
    background = ClaudeDarkBg,
    onBackground = ClaudeTextPrimaryDark,
    surface = ClaudeDarkSurface,
    onSurface = ClaudeTextPrimaryDark,
    surfaceVariant = ClaudeDarkCard,
    onSurfaceVariant = ClaudeTextSecondaryDark,
    outline = ClaudeDarkBorder,
    outlineVariant = ClaudeDarkBorderSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) WarmDarkColorScheme else WarmLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
