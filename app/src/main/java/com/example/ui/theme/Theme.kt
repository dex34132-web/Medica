package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MedicaAccentBlue,
    onPrimary = Color.White,
    primaryContainer = MedicaActivePill,
    onPrimaryContainer = MedicaAccentBlueLight,
    secondary = MedicaGreenDot,
    onSecondary = Color.White,
    background = MedicaBgDark,
    onBackground = MedicaTextPrimary,
    surface = MedicaCardDark,
    onSurface = MedicaTextPrimary,
    surfaceVariant = MedicaCardInner,
    onSurfaceVariant = MedicaTextSecondary,
    outline = MedicaBorderDark,
    outlineVariant = MedicaDivider
)

private val LightColorScheme = lightColorScheme(
    primary = MedicaAccentBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDF4FF),
    onPrimaryContainer = MedicaAccentBlue,
    secondary = MedicaGreenDot,
    onSecondary = Color.White,
    background = MedicaBgLight,
    onBackground = MedicaTextPrimaryLight,
    surface = MedicaCardLight,
    onSurface = MedicaTextPrimaryLight,
    surfaceVariant = MedicaCardInnerLight,
    onSurfaceVariant = MedicaTextSecondaryLight,
    outline = MedicaBorderLight,
    outlineVariant = MedicaDividerLight
)

@Composable
fun MedicaTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
