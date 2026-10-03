package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MedicaRed,
    onPrimary = Color.White,
    primaryContainer = MedicaRedContainer,
    onPrimaryContainer = MedicaOnRedContainer,
    secondary = MedicaBlue,
    onSecondary = Color.White,
    secondaryContainer = MedicaBlueContainer,
    onSecondaryContainer = MedicaOnBlueContainer,
    tertiary = MedicaGreen,
    onTertiary = Color.White,
    tertiaryContainer = MedicaGreenContainer,
    onTertiaryContainer = MedicaOnGreenContainer,
    background = MedicaDarkBackground,
    onBackground = MedicaDarkTextPrimary,
    surface = MedicaDarkSurface,
    onSurface = MedicaDarkTextPrimary,
    surfaceVariant = MedicaDarkSurfaceContainer,
    onSurfaceVariant = MedicaDarkTextSecondary,
    outline = MedicaDarkOutline,
    outlineVariant = MedicaDarkOutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = MedicaRedDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = MedicaBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = MedicaGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF065F46),
    background = MedicaLightBackground,
    onBackground = MedicaLightTextPrimary,
    surface = MedicaLightSurface,
    onSurface = MedicaLightTextPrimary,
    surfaceVariant = MedicaLightSurfaceContainer,
    onSurfaceVariant = MedicaLightTextSecondary,
    outline = MedicaLightOutline,
    outlineVariant = MedicaLightOutlineVariant
)

@Composable
fun MedicaTheme(
    darkTheme: Boolean = true, // Default to true for emergency tactical dark mode
    dynamicColor: Boolean = false, // Keep consistent medical emergency colors
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
