package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class ThemeAccents(
    val primary: Color,
    val primaryLight: Color,
    val primaryGlow: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val background: Color,
    val border: Color,
    val borderGlow: Color,
    val ambientGradient: Brush,
    val cardBorderGradient: Brush,
    val error: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
)

val LocalThemeAccents = staticCompositionLocalOf {
    ThemeAccents(
        primary = EmeraldPrimary,
        primaryLight = EmeraldLight,
        primaryGlow = Color(0x2010B981),
        surface = CleanSurface,
        surfaceElevated = CleanSurface,
        background = CleanBg,
        border = CleanBorder,
        borderGlow = Color(0xFFA7F3D0),
        ambientGradient = Brush.verticalGradient(listOf(CleanBg, CleanSurface)),
        cardBorderGradient = Brush.linearGradient(listOf(CleanBorder, EmeraldPrimary.copy(alpha = 0.3f))),
        error   = Color(0xFFF43F5E),
        success = Color(0xFF10B981),
        warning = Color(0xFFF59E0B),
        info    = Color(0xFF06B6D4),
    )
}

private fun getAppColorScheme(palette: VaultThemePalette): ColorScheme {
    return when (palette) {
        VaultThemePalette.TOTAL_SECURITY -> lightColorScheme(
            primary = EmeraldPrimary,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFECFDF5),
            onPrimaryContainer = Color(0xFF065F46),
            secondary = ElectricCyanPrimary,
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFE0F2FE),
            onSecondaryContainer = Color(0xFF075985),
            tertiary = EmeraldDark,
            onTertiary = Color.White,
            background = CleanBg,
            onBackground = TextDarkPrimary,
            surface = CleanSurface,
            onSurface = TextDarkPrimary,
            surfaceVariant = CleanSurfaceVariant,
            onSurfaceVariant = TextDarkSecondary,
            outline = CleanBorder,
            outlineVariant = CleanBorderSubtle,
            error = HealthRiskRed,
            onError = Color.White
        )
        VaultThemePalette.OBSIDIAN_EMERALD -> darkColorScheme(
            primary = EmeraldPrimary,
            onPrimary = Color(0xFF022C22),
            primaryContainer = Color(0xFF064E3B),
            onPrimaryContainer = Color(0xFF6EE7B7),
            secondary = EmeraldLight,
            onSecondary = Color(0xFF022C22),
            secondaryContainer = NavySurfaceVariant,
            onSecondaryContainer = Color(0xFF6EE7B7),
            tertiary = EmeraldPrimary,
            onTertiary = Color(0xFF022C22),
            background = NavyBg,
            onBackground = LightTextPrimary,
            surface = NavySurface,
            onSurface = LightTextPrimary,
            surfaceVariant = NavySurfaceVariant,
            onSurfaceVariant = LightTextSecondary,
            outline = NavyBorder,
            outlineVariant = NavyBorderSubtle,
            error = HealthRiskRed,
            onError = Color.White
        )
        VaultThemePalette.MIDNIGHT_SAPPHIRE -> darkColorScheme(
            primary = ElectricCyanPrimary,
            onPrimary = Color(0xFF083344),
            primaryContainer = ElectricCyanContainer,
            onPrimaryContainer = Color(0xFF67E8F9),
            secondary = ElectricCyanLight,
            onSecondary = Color(0xFF083344),
            secondaryContainer = NavySurfaceVariant,
            onSecondaryContainer = Color(0xFF67E8F9),
            tertiary = ElectricCyanPrimary,
            onTertiary = Color(0xFF083344),
            background = NavyBg,
            onBackground = LightTextPrimary,
            surface = NavySurface,
            onSurface = LightTextPrimary,
            surfaceVariant = NavySurfaceVariant,
            onSurfaceVariant = LightTextSecondary,
            outline = NavyBorder,
            outlineVariant = NavyBorderSubtle,
            error = HealthRiskRed,
            onError = Color.White
        )
    }
}

private fun getThemeAccents(palette: VaultThemePalette): ThemeAccents {
    return when (palette) {
        VaultThemePalette.TOTAL_SECURITY -> ThemeAccents(
            primary = EmeraldPrimary,
            primaryLight = EmeraldLight,
            primaryGlow = Color(0x2010B981),
            surface = CleanSurface,
            surfaceElevated = CleanSurface,
            background = CleanBg,
            border = CleanBorder,
            borderGlow = Color(0xFFA7F3D0),
            ambientGradient = Brush.verticalGradient(listOf(CleanBg, CleanSurface)),
            cardBorderGradient = Brush.linearGradient(listOf(CleanBorder, EmeraldPrimary.copy(alpha = 0.3f))),
            error   = Color(0xFFF43F5E),
            success = Color(0xFF10B981),
            warning = Color(0xFFF59E0B),
            info    = Color(0xFF06B6D4),
        )
        VaultThemePalette.OBSIDIAN_EMERALD -> ThemeAccents(
            primary = EmeraldPrimary,
            primaryLight = EmeraldLight,
            primaryGlow = Color(0x3310B981),
            surface = NavySurface,
            surfaceElevated = NavySurfaceElevated,
            background = NavyBg,
            border = NavyBorder,
            borderGlow = Color(0xFF064E3B),
            ambientGradient = Brush.verticalGradient(listOf(NavyBg, NavySurface)),
            cardBorderGradient = Brush.linearGradient(listOf(NavyBorder, EmeraldPrimary.copy(alpha = 0.3f))),
            error   = Color(0xFFF43F5E),
            success = Color(0xFF10B981),
            warning = Color(0xFFF59E0B),
            info    = Color(0xFF06B6D4),
        )
        VaultThemePalette.MIDNIGHT_SAPPHIRE -> ThemeAccents(
            primary = ElectricCyanPrimary,
            primaryLight = ElectricCyanLight,
            primaryGlow = Color(0x3306B6D4),
            surface = NavySurface,
            surfaceElevated = NavySurfaceElevated,
            background = NavyBg,
            border = NavyBorder,
            borderGlow = ElectricCyanContainer,
            ambientGradient = Brush.verticalGradient(listOf(NavyBg, NavySurface)),
            cardBorderGradient = Brush.linearGradient(listOf(NavyBorder, ElectricCyanPrimary.copy(alpha = 0.3f))),
            error   = Color(0xFFF43F5E),
            success = Color(0xFF10B981),
            warning = Color(0xFFF59E0B),
            info    = ElectricCyanPrimary,
        )
    }
}

@Composable
fun AdereTheme(
    palette: VaultThemePalette = VaultThemePalette.TOTAL_SECURITY,
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = palette != VaultThemePalette.TOTAL_SECURITY,
    content: @Composable () -> Unit
) {
    val colorScheme = getAppColorScheme(palette)
    val accents = getThemeAccents(palette)

    CompositionLocalProvider(LocalThemeAccents provides accents) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    AdereTheme(darkTheme = darkTheme, content = content)
}
