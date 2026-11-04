package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = JadeGreen,
    onPrimary = Slate950,
    primaryContainer = JadeGreenDark,
    onPrimaryContainer = Color.White,
    secondary = ParchmentGold,
    onSecondary = Slate950,
    secondaryContainer = ParchmentDark,
    onSecondaryContainer = ParchmentGold,
    tertiary = AmberGlow,
    onTertiary = Slate950,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = Slate850,
    onSurfaceVariant = Slate200,
    outline = Slate700,
    outlineVariant = Slate800,
    error = CrimsonAlert,
    onError = Color.White
)

private val LightColorScheme = darkColorScheme( // Force consistent tactical aesthetic for the field dispatch
    primary = JadeGreen,
    onPrimary = Slate950,
    primaryContainer = JadeGreenDark,
    onPrimaryContainer = Color.White,
    secondary = ParchmentGold,
    onSecondary = Slate950,
    secondaryContainer = ParchmentDark,
    onSecondaryContainer = ParchmentGold,
    tertiary = AmberGlow,
    onTertiary = Slate950,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = Slate850,
    onSurfaceVariant = Slate200,
    outline = Slate700,
    outlineVariant = Slate800,
    error = CrimsonAlert,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep intentional immersive palette
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
