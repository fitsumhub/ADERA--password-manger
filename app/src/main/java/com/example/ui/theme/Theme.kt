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
import androidx.compose.ui.graphics.SolidColor

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
        primary = TotalSecurityPrimary,
        primaryLight = TotalSecurityPrimaryLight,
        primaryGlow = TotalSecurityPrimaryLight,
        surface = CleanSurface,
        surfaceElevated = CleanSurface,
        background = CleanBg,
        border = CleanBorder,
        borderGlow = TotalSecurityPrimary,
        ambientGradient = SolidColor(CleanBg),
        cardBorderGradient = SolidColor(CleanBorder),
        error   = Color(0xFFE11D48),
        success = Color(0xFF10B981),
        warning = Color(0xFFF59E0B),
        info    = Color(0xFF3B82F6),
    )
}

private fun getAppColorScheme(palette: VaultThemePalette): ColorScheme {
    return when (palette) {
        VaultThemePalette.TOTAL_SECURITY -> lightColorScheme(
            primary = TotalSecurityPrimary,
            onPrimary = Color.White,
            primaryContainer = TotalSecurityPrimaryContainer,
            onPrimaryContainer = TotalSecurityOnPrimaryContainer,
            secondary = TotalSecurityPrimary,
            onSecondary = Color.White,
            secondaryContainer = CleanSurfaceVariant,
            onSecondaryContainer = TotalSecurityPrimary,
            tertiary = TotalSecurityPrimary,
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
            primary = Color(0xFF10B981),
            onPrimary = Color(0xFF06261C),
            primaryContainer = Color(0xFF064E3B),
            onPrimaryContainer = Color(0xFF6EE7B7),
            secondary = Color(0xFF10B981),
            onSecondary = Color(0xFF06261C),
            secondaryContainer = CharcoalSurfaceVariant,
            onSecondaryContainer = Color(0xFF6EE7B7),
            tertiary = Color(0xFF10B981),
            onTertiary = Color(0xFF06261C),
            background = CharcoalBg,
            onBackground = LightTextPrimary,
            surface = CharcoalSurface,
            onSurface = LightTextPrimary,
            surfaceVariant = CharcoalSurfaceVariant,
            onSurfaceVariant = LightTextSecondary,
            outline = CharcoalBorder,
            outlineVariant = CharcoalBorderSubtle,
            error = HealthRiskRed,
            onError = Color.White
        )
        VaultThemePalette.MIDNIGHT_SAPPHIRE -> darkColorScheme(
            primary = SapphirePrimary,
            onPrimary = Color.White,
            primaryContainer = SapphireContainer,
            onPrimaryContainer = Color(0xFFDBEAFE),
            secondary = SapphirePrimary,
            onSecondary = Color.White,
            secondaryContainer = CharcoalSurfaceVariant,
            onSecondaryContainer = Color(0xFFDBEAFE),
            tertiary = SapphirePrimary,
            onTertiary = Color.White,
            background = SapphireBg,
            onBackground = LightTextPrimary,
            surface = SapphireSurface,
            onSurface = LightTextPrimary,
            surfaceVariant = SapphireSurfaceVariant,
            onSurfaceVariant = LightTextSecondary,
            outline = SapphireBorder,
            outlineVariant = CharcoalBorderSubtle,
            error = HealthRiskRed,
            onError = Color.White
        )
    }
}

private fun getThemeAccents(palette: VaultThemePalette): ThemeAccents {
    return when (palette) {
        VaultThemePalette.TOTAL_SECURITY -> ThemeAccents(
            primary = TotalSecurityPrimary,
            primaryLight = TotalSecurityPrimaryLight,
            primaryGlow = Color.Transparent,
            surface = CleanSurface,
            surfaceElevated = CleanSurface,
            background = CleanBg,
            border = CleanBorder,
            borderGlow = TotalSecurityPrimary,
            ambientGradient = SolidColor(CleanBg),
            cardBorderGradient = SolidColor(CleanBorder),
            error   = Color(0xFFE11D48),
            success = Color(0xFF10B981),
            warning = Color(0xFFF59E0B),
            info    = Color(0xFF3B82F6),
        )
        VaultThemePalette.OBSIDIAN_EMERALD -> ThemeAccents(
            primary = Color(0xFF10B981),
            primaryLight = Color(0xFF34D399),
            primaryGlow = Color.Transparent,
            surface = CharcoalSurface,
            surfaceElevated = CharcoalSurfaceVariant,
            background = CharcoalBg,
            border = CharcoalBorder,
            borderGlow = Color(0xFF064E3B),
            ambientGradient = SolidColor(CharcoalBg),
            cardBorderGradient = SolidColor(CharcoalBorder),
            error   = Color(0xFFE11D48),
            success = Color(0xFF10B981),
            warning = Color(0xFFF59E0B),
            info    = Color(0xFF3B82F6),
        )
        VaultThemePalette.MIDNIGHT_SAPPHIRE -> ThemeAccents(
            primary = SapphirePrimary,
            primaryLight = SapphireLight,
            primaryGlow = Color.Transparent,
            surface = SapphireSurface,
            surfaceElevated = SapphireSurfaceVariant,
            background = SapphireBg,
            border = SapphireBorder,
            borderGlow = SapphireContainer,
            ambientGradient = SolidColor(SapphireBg),
            cardBorderGradient = SolidColor(SapphireBorder),
            error   = Color(0xFFE11D48),
            success = Color(0xFF10B981),
            warning = Color(0xFFF59E0B),
            info    = SapphirePrimary,
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
