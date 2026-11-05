package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StealthCyanColorScheme = darkColorScheme(
    primary = CubeCyan,
    onPrimary = Color(0xFF001F24),
    primaryContainer = Color(0xFF00363D),
    onPrimaryContainer = CubeCyan,
    secondary = CubeElectricBlue,
    onSecondary = Color(0xFF001E2F),
    tertiary = CubeNeonPurple,
    background = CubeBgStealth,
    onBackground = CubeTextPrimary,
    surface = CubeSurfaceStealth,
    onSurface = CubeTextPrimary,
    surfaceVariant = CubeCardStealth,
    onSurfaceVariant = CubeTextSecondary,
    outline = CubeBorderSubtle,
    outlineVariant = CubeBorderHighlight
)

private val OledMatrixColorScheme = darkColorScheme(
    primary = CubeCyan,
    onPrimary = Color(0xFF001F24),
    primaryContainer = Color(0xFF002B30),
    onPrimaryContainer = CubeCyan,
    secondary = CubeElectricBlue,
    onSecondary = Color(0xFF001E2F),
    tertiary = CubeEmerald,
    background = CubeBgOled,
    onBackground = CubeTextPrimary,
    surface = CubeSurfaceOled,
    onSurface = CubeTextPrimary,
    surfaceVariant = CubeCardOled,
    onSurfaceVariant = CubeTextSecondary,
    outline = Color(0xFF162030),
    outlineVariant = Color(0xFF26354D)
)

@Composable
fun MyApplicationTheme(
    oledPitchMode: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (oledPitchMode) OledMatrixColorScheme else StealthCyanColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
