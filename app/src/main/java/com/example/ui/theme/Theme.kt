package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AmberGold,
    onPrimary = CharcoalDark,
    primaryContainer = AmberGoldDark,
    onPrimaryContainer = CreamWhite,
    secondary = FlameOrange,
    onSecondary = CreamWhite,
    secondaryContainer = CharcoalElevated,
    onSecondaryContainer = AmberGoldLight,
    tertiary = BasilGreen,
    onTertiary = CreamWhite,
    background = CharcoalDark,
    onBackground = TextPrimary,
    surface = CharcoalSurface,
    onSurface = TextPrimary,
    surfaceVariant = CharcoalCard,
    onSurfaceVariant = TextSecondary,
    outline = CharcoalBorder,
    outlineVariant = CharcoalBorder.copy(alpha = 0.5f),
    error = ChiliRed,
    onError = CreamWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Food Alchemist signature dark aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

