package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAppTheme = staticCompositionLocalOf { LightAppThemeColors }

object AppTheme {
    val colors: AppThemeColors
        @Composable
        get() = LocalAppTheme.current
}

private val LightColorScheme = lightColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF312E81),
    secondary = AccentPurple,
    onSecondary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryIndigo,
    onPrimary = Color.White,
    primaryContainer = PrimaryIndigoHover,
    onPrimaryContainer = Color.White,
    secondary = AccentPurple,
    onSecondary = Color.White,
    background = BgDarkNavy,
    onBackground = TextMain,
    surface = BgLightNavy,
    onSurface = TextMain,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextMuted,
    outline = CardBorder
)

/**
 * Main theme wrapper for হিসাব Pro.
 * Light theme is enabled by default as requested.
 */
@Composable
fun HisabProTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) DarkAppThemeColors else LightAppThemeColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalAppTheme provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
