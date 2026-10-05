package com.example.adere.presentation.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.example.ui.theme.LocalThemeAccents
import com.example.ui.theme.ThemeAccents
import com.example.ui.theme.VaultThemePalette
import kotlin.math.abs

// Enhanced theme data with accessibility features
data class EnhancedThemeAccents(
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val outline: Color,
    val outlineVariant: Color,
    val scrim: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inversePrimary: Color,
    val surfaceTint: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    
    // Custom accent colors for enhanced theming
    val success: Color,
    val warning: Color,
    val info: Color,
    val onSuccess: Color,
    val onWarning: Color,
    val onInfo: Color,
    val successContainer: Color,
    val warningContainer: Color,
    val infoContainer: Color,
    
    // Gradient definitions
    val primaryGradient: Brush,
    val backgroundGradient: Brush,
    val cardGradient: Brush,
    val accentGradient: Brush,
    
    // Accessibility helpers
    val highContrastPrimary: Color,
    val highContrastSecondary: Color,
    val focusRing: Color,
    val selectionBackground: Color,
    
    // Shadow and elevation colors
    val shadowColor: Color,
    val elevationTint: Color,
)

val LocalEnhancedTheme = staticCompositionLocalOf {
    EnhancedThemeAccents(
        primary = Color(0xFF5E5CE6),
        primaryVariant = Color(0xFF4341B8),
        secondary = Color(0xFFF5BA31),
        background = Color(0xFFF7F8FC),
        surface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFFF1F3F9),
        onPrimary = Color.White,
        onSecondary = Color(0xFF78350F),
        onBackground = Color(0xFF1A1C29),
        onSurface = Color(0xFF1A1C29),
        outline = Color(0xFFECEEF5),
        outlineVariant = Color(0xFFF3F4F8),
        scrim = Color(0x80000000),
        inverseSurface = Color(0xFF1A1C29),
        inverseOnSurface = Color(0xFFF7F8FC),
        inversePrimary = Color(0xFF7E7CF6),
        surfaceTint = Color(0xFF5E5CE6),
        error = Color(0xFFEF4444),
        onError = Color.White,
        errorContainer = Color(0xFFFFEBEE),
        onErrorContainer = Color(0xFFB71C1C),
        success = Color(0xFF10B981),
        warning = Color(0xFFF59E0B),
        info = Color(0xFF3B82F6),
        onSuccess = Color.White,
        onWarning = Color.White,
        onInfo = Color.White,
        successContainer = Color(0xFFECFDF5),
        warningContainer = Color(0xFFFEF3C7),
        infoContainer = Color(0xFFEBF8FF),
        primaryGradient = Brush.linearGradient(listOf(Color(0xFF5E5CE6), Color(0xFF7E7CF6))),
        backgroundGradient = Brush.verticalGradient(listOf(Color(0xFFF7F8FC), Color(0xFFFFFFFF))),
        cardGradient = Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFFAFBFF))),
        accentGradient = Brush.radialGradient(listOf(Color(0x265E5CE6), Color.Transparent)),
        highContrastPrimary = Color(0xFF2D2B7A),
        highContrastSecondary = Color(0xFF8B4513),
        focusRing = Color(0xFF5E5CE6),
        selectionBackground = Color(0x335E5CE6),
        shadowColor = Color(0x1A5E5CE6),
        elevationTint = Color(0x0A5E5CE6)
    )
}

enum class ContrastLevel {
    STANDARD,
    MEDIUM,
    HIGH
}

enum class ColorBlindnessType {
    NONE,
    PROTANOPIA,    // Red-blind
    DEUTERANOPIA,  // Green-blind
    TRITANOPIA,    // Blue-blind
    MONOCHROMACY   // Complete color blindness
}

data class AccessibilityPreferences(
    val contrastLevel: ContrastLevel = ContrastLevel.STANDARD,
    val colorBlindnessType: ColorBlindnessType = ColorBlindnessType.NONE,
    val reduceMotion: Boolean = false,
    val increaseFontSize: Boolean = false,
    val enhanceFocus: Boolean = false
)

@Composable
fun EnhancedAdereTheme(
    palette: VaultThemePalette = VaultThemePalette.TOTAL_SECURITY,
    accessibilityPrefs: AccessibilityPreferences = AccessibilityPreferences(),
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    animateTransitions: Boolean = true,
    content: @Composable () -> Unit
) {
    val baseColorScheme = getEnhancedColorScheme(palette, isDarkTheme)
    val adjustedColorScheme = applyAccessibilityAdjustments(
        baseColorScheme, 
        accessibilityPrefs
    )
    
    val enhancedAccents = createEnhancedAccents(
        palette = palette,
        isDarkTheme = isDarkTheme,
        accessibilityPrefs = accessibilityPrefs
    )
    
    val animatedAccents = if (animateTransitions) {
        animateEnhancedAccents(enhancedAccents)
    } else {
        enhancedAccents
    }
    
    CompositionLocalProvider(
        LocalEnhancedTheme provides animatedAccents
    ) {
        MaterialTheme(
            colorScheme = adjustedColorScheme,
            typography = getEnhancedTypography(accessibilityPrefs),
            shapes = getEnhancedShapes(accessibilityPrefs),
            content = content
        )
    }
}

@Composable
private fun animateEnhancedAccents(target: EnhancedThemeAccents): EnhancedThemeAccents {
    val animationSpec = spring<Color>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    return EnhancedThemeAccents(
        primary = animateColorAsState(
            targetValue = target.primary,
            animationSpec = animationSpec,
            label = "primary"
        ).value,
        primaryVariant = animateColorAsState(
            targetValue = target.primaryVariant,
            animationSpec = animationSpec,
            label = "primaryVariant"
        ).value,
        secondary = animateColorAsState(
            targetValue = target.secondary,
            animationSpec = animationSpec,
            label = "secondary"
        ).value,
        background = animateColorAsState(
            targetValue = target.background,
            animationSpec = animationSpec,
            label = "background"
        ).value,
        surface = animateColorAsState(
            targetValue = target.surface,
            animationSpec = animationSpec,
            label = "surface"
        ).value,
        surfaceVariant = animateColorAsState(
            targetValue = target.surfaceVariant,
            animationSpec = animationSpec,
            label = "surfaceVariant"
        ).value,
        onPrimary = animateColorAsState(
            targetValue = target.onPrimary,
            animationSpec = animationSpec,
            label = "onPrimary"
        ).value,
        onSecondary = animateColorAsState(
            targetValue = target.onSecondary,
            animationSpec = animationSpec,
            label = "onSecondary"
        ).value,
        onBackground = animateColorAsState(
            targetValue = target.onBackground,
            animationSpec = animationSpec,
            label = "onBackground"
        ).value,
        onSurface = animateColorAsState(
            targetValue = target.onSurface,
            animationSpec = animationSpec,
            label = "onSurface"
        ).value,
        outline = animateColorAsState(
            targetValue = target.outline,
            animationSpec = animationSpec,
            label = "outline"
        ).value,
        outlineVariant = animateColorAsState(
            targetValue = target.outlineVariant,
            animationSpec = animationSpec,
            label = "outlineVariant"
        ).value,
        scrim = target.scrim,
        inverseSurface = target.inverseSurface,
        inverseOnSurface = target.inverseOnSurface,
        inversePrimary = target.inversePrimary,
        surfaceTint = target.surfaceTint,
        error = target.error,
        onError = target.onError,
        errorContainer = target.errorContainer,
        onErrorContainer = target.onErrorContainer,
        success = animateColorAsState(
            targetValue = target.success,
            animationSpec = animationSpec,
            label = "success"
        ).value,
        warning = animateColorAsState(
            targetValue = target.warning,
            animationSpec = animationSpec,
            label = "warning"
        ).value,
        info = animateColorAsState(
            targetValue = target.info,
            animationSpec = animationSpec,
            label = "info"
        ).value,
        onSuccess = target.onSuccess,
        onWarning = target.onWarning,
        onInfo = target.onInfo,
        successContainer = target.successContainer,
        warningContainer = target.warningContainer,
        infoContainer = target.infoContainer,
        primaryGradient = target.primaryGradient,
        backgroundGradient = target.backgroundGradient,
        cardGradient = target.cardGradient,
        accentGradient = target.accentGradient,
        highContrastPrimary = target.highContrastPrimary,
        highContrastSecondary = target.highContrastSecondary,
        focusRing = target.focusRing,
        selectionBackground = target.selectionBackground,
        shadowColor = target.shadowColor,
        elevationTint = target.elevationTint
    )
}

private fun getEnhancedColorScheme(
    palette: VaultThemePalette,
    isDarkTheme: Boolean
): ColorScheme {
    return when (palette) {
        VaultThemePalette.TOTAL_SECURITY -> {
            if (isDarkTheme) createTotalSecurityDarkScheme() 
            else createTotalSecurityLightScheme()
        }
        VaultThemePalette.OBSIDIAN_EMERALD -> createObsidianEmeraldScheme()
        VaultThemePalette.MIDNIGHT_SAPPHIRE -> createMidnightSapphireScheme()
    }
}

private fun createTotalSecurityLightScheme() = lightColorScheme(
    primary = Color(0xFF000000),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE5E5E5),
    onPrimaryContainer = Color(0xFF000000),
    secondary = Color(0xFF000000),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0F0F0),
    onSecondaryContainer = Color(0xFF000000),
    tertiary = Color(0xFF666666),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF5F5F5),
    onTertiaryContainer = Color(0xFF111111),
    background = Color(0xFFF7F7F7),
    onBackground = Color(0xFF111111),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111111),
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF666666),
    outline = Color(0xFFE5E5E5),
    outlineVariant = Color(0xFFF0F0F0),
    scrim = Color(0x80000000),
    inverseSurface = Color(0xFF111111),
    inverseOnSurface = Color(0xFFFFFFFF),
    inversePrimary = Color(0xFF666666),
    surfaceTint = Color(0xFF000000),
    error = Color(0xFFFF3366),
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C)
)

private fun createTotalSecurityDarkScheme() = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF333333),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF1A1A1A),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = Color(0xFFAAAAAA),
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF222222),
    onTertiaryContainer = Color(0xFFFFFFFF),
    background = Color(0xFF000000),
    onBackground = Color(0xFFF5F5F5),
    surface = Color(0xFF0A0A0A),
    onSurface = Color(0xFFF5F5F5),
    surfaceVariant = Color(0xFF141414),
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline = Color(0xFF222222),
    outlineVariant = Color(0xFF1A1A1A),
    scrim = Color(0x80000000),
    inverseSurface = Color(0xFFFFFFFF),
    inverseOnSurface = Color(0xFF000000),
    inversePrimary = Color(0xFF333333),
    surfaceTint = Color(0xFFFFFFFF),
    error = Color(0xFFFF3366),
    onError = Color.White,
    errorContainer = Color(0xFFB91C1C),
    onErrorContainer = Color(0xFFFFEBEE)
)

private fun createObsidianEmeraldScheme() = darkColorScheme(
    primary = Color(0xFF00FF88),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF00331B),
    onPrimaryContainer = Color(0xFF00FF88),
    secondary = Color(0xFF00CC6A),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF002211),
    onSecondaryContainer = Color(0xFF00FF88),
    tertiary = Color(0xFF00AA55),
    onTertiary = Color(0xFF000000),
    background = Color(0xFF000000),
    onBackground = Color(0xFFF5F5F5),
    surface = Color(0xFF0A0A0A),
    onSurface = Color(0xFFF5F5F5),
    surfaceVariant = Color(0xFF141414),
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline = Color(0xFF222222),
    outlineVariant = Color(0xFF1A1A1A)
)

private fun createMidnightSapphireScheme() = darkColorScheme(
    primary = Color(0xFF0055FF),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF001A4D),
    onPrimaryContainer = Color(0xFF0055FF),
    secondary = Color(0xFF4D88FF),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF001133),
    onSecondaryContainer = Color(0xFF4D88FF),
    tertiary = Color(0xFF0044CC),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFF000000),
    onBackground = Color(0xFFF5F5F5),
    surface = Color(0xFF050505),
    onSurface = Color(0xFFF5F5F5),
    surfaceVariant = Color(0xFF0A0A0A),
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline = Color(0xFF1A1A1A),
    outlineVariant = Color(0xFF111111)
)

private fun createEnhancedAccents(
    palette: VaultThemePalette,
    isDarkTheme: Boolean,
    accessibilityPrefs: AccessibilityPreferences
): EnhancedThemeAccents {
    val baseColors = when (palette) {
        VaultThemePalette.TOTAL_SECURITY -> if (isDarkTheme) {
            getTotalSecurityDarkAccents()
        } else {
            getTotalSecurityLightAccents()
        }
        VaultThemePalette.OBSIDIAN_EMERALD -> getObsidianEmeraldAccents()
        VaultThemePalette.MIDNIGHT_SAPPHIRE -> getMidnightSapphireAccents()
    }
    
    return applyAccessibilityEnhancements(baseColors, accessibilityPrefs)
}

private fun getTotalSecurityLightAccents() = EnhancedThemeAccents(
    primary = Color(0xFF000000),
    primaryVariant = Color(0xFF333333),
    secondary = Color(0xFF000000),
    background = Color(0xFFF7F7F7),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF0F0F0),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF111111),
    onSurface = Color(0xFF111111),
    outline = Color(0xFFE5E5E5),
    outlineVariant = Color(0xFFF0F0F0),
    scrim = Color(0x80000000),
    inverseSurface = Color(0xFF111111),
    inverseOnSurface = Color(0xFFFFFFFF),
    inversePrimary = Color(0xFF666666),
    surfaceTint = Color(0xFF000000),
    error = Color(0xFFFF3366),
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),
    success = Color(0xFF00FF88),
    warning = Color(0xFFFFB800),
    info = Color(0xFF0055FF),
    onSuccess = Color.Black,
    onWarning = Color.Black,
    onInfo = Color.White,
    successContainer = Color(0xFFE5FFE5),
    warningContainer = Color(0xFFFFF5CC),
    infoContainer = Color(0xFFE5EEFF),
    primaryGradient = Brush.linearGradient(listOf(Color(0xFF000000), Color(0xFF111111))),
    backgroundGradient = Brush.verticalGradient(listOf(Color(0xFFF7F7F7), Color(0xFFF7F7F7))),
    cardGradient = Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFFFFFFF))),
    accentGradient = Brush.radialGradient(listOf(Color(0x0A000000), Color.Transparent)),
    highContrastPrimary = Color(0xFF000000),
    highContrastSecondary = Color(0xFF000000),
    focusRing = Color(0xFF000000),
    selectionBackground = Color(0x1A000000),
    shadowColor = Color(0x05000000),
    elevationTint = Color.Transparent
)

private fun getTotalSecurityDarkAccents() = EnhancedThemeAccents(
    primary = Color(0xFFFFFFFF),
    primaryVariant = Color(0xFFCCCCCC),
    secondary = Color(0xFFFFFFFF),
    background = Color(0xFF000000),
    surface = Color(0xFF0A0A0A),
    surfaceVariant = Color(0xFF141414),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onBackground = Color(0xFFF5F5F5),
    onSurface = Color(0xFFF5F5F5),
    outline = Color(0xFF222222),
    outlineVariant = Color(0xFF1A1A1A),
    scrim = Color(0x80000000),
    inverseSurface = Color(0xFFFFFFFF),
    inverseOnSurface = Color(0xFF000000),
    inversePrimary = Color(0xFF333333),
    surfaceTint = Color(0xFFFFFFFF),
    error = Color(0xFFFF3366),
    onError = Color.White,
    errorContainer = Color(0xFFB91C1C),
    onErrorContainer = Color(0xFFFFEBEE),
    success = Color(0xFF00FF88),
    warning = Color(0xFFFFB800),
    info = Color(0xFF0055FF),
    onSuccess = Color.Black,
    onWarning = Color.Black,
    onInfo = Color.White,
    successContainer = Color(0xFF00331B),
    warningContainer = Color(0xFF4D3700),
    infoContainer = Color(0xFF001A4D),
    primaryGradient = Brush.linearGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF0F0F0))),
    backgroundGradient = Brush.verticalGradient(listOf(Color(0xFF000000), Color(0xFF000000))),
    cardGradient = Brush.linearGradient(listOf(Color(0xFF0A0A0A), Color(0xFF0A0A0A))),
    accentGradient = Brush.radialGradient(listOf(Color(0x0AFFFFFF), Color.Transparent)),
    highContrastPrimary = Color(0xFFFFFFFF),
    highContrastSecondary = Color(0xFFFFFFFF),
    focusRing = Color(0xFFFFFFFF),
    selectionBackground = Color(0x33FFFFFF),
    shadowColor = Color.Transparent,
    elevationTint = Color.Transparent
)

private fun getObsidianEmeraldAccents() = EnhancedThemeAccents(
    primary = Color(0xFF00FF88),
    primaryVariant = Color(0xFF00CC6A),
    secondary = Color(0xFF00FF88),
    background = Color(0xFF000000),
    surface = Color(0xFF0A0A0A),
    surfaceVariant = Color(0xFF141414),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onBackground = Color(0xFFF5F5F5),
    onSurface = Color(0xFFF5F5F5),
    outline = Color(0xFF222222),
    outlineVariant = Color(0xFF1A1A1A),
    scrim = Color(0x80000000),
    inverseSurface = Color(0xFFF5F5F5),
    inverseOnSurface = Color(0xFF000000),
    inversePrimary = Color(0xFF00FF88),
    surfaceTint = Color(0xFF00FF88),
    error = Color(0xFFFF3366),
    onError = Color.White,
    errorContainer = Color(0xFFB91C1C),
    onErrorContainer = Color(0xFFFFEBEE),
    success = Color(0xFF00FF88),
    warning = Color(0xFFFFB800),
    info = Color(0xFF00D1FF),
    onSuccess = Color.Black,
    onWarning = Color.Black,
    onInfo = Color.Black,
    successContainer = Color(0xFF00331B),
    warningContainer = Color(0xFF4D3700),
    infoContainer = Color(0xFF002933),
    primaryGradient = Brush.linearGradient(listOf(Color(0xFF00FF88), Color(0xFF00FF88))),
    backgroundGradient = Brush.verticalGradient(listOf(Color(0xFF000000), Color(0xFF000000))),
    cardGradient = Brush.linearGradient(listOf(Color(0xFF0A0A0A), Color(0xFF0A0A0A))),
    accentGradient = Brush.radialGradient(listOf(Color(0x0A00FF88), Color.Transparent)),
    highContrastPrimary = Color(0xFF00FF88),
    highContrastSecondary = Color(0xFF00FF88),
    focusRing = Color(0xFF00FF88),
    selectionBackground = Color(0x3300FF88),
    shadowColor = Color.Transparent,
    elevationTint = Color.Transparent
)

private fun getMidnightSapphireAccents() = EnhancedThemeAccents(
    primary = Color(0xFF0055FF),
    primaryVariant = Color(0xFF4D88FF),
    secondary = Color(0xFF00D1FF),
    background = Color(0xFF000000),
    surface = Color(0xFF050505),
    surfaceVariant = Color(0xFF0A0A0A),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF000000),
    onBackground = Color(0xFFF5F5F5),
    onSurface = Color(0xFFF5F5F5),
    outline = Color(0xFF1A1A1A),
    outlineVariant = Color(0xFF111111),
    scrim = Color(0x80000000),
    inverseSurface = Color(0xFFF5F5F5),
    inverseOnSurface = Color(0xFF050505),
    inversePrimary = Color(0xFF4D88FF),
    surfaceTint = Color(0xFF0055FF),
    error = Color(0xFFFF3366),
    onError = Color.White,
    errorContainer = Color(0xFFB91C1C),
    onErrorContainer = Color(0xFFFFEBEE),
    success = Color(0xFF00FF88),
    warning = Color(0xFFFFB800),
    info = Color(0xFF0055FF),
    onSuccess = Color.Black,
    onWarning = Color.Black,
    onInfo = Color.White,
    successContainer = Color(0xFF00331B),
    warningContainer = Color(0xFF4D3700),
    infoContainer = Color(0xFF001A4D),
    primaryGradient = Brush.linearGradient(listOf(Color(0xFF0055FF), Color(0xFF0055FF))),
    backgroundGradient = Brush.verticalGradient(listOf(Color(0xFF000000), Color(0xFF000000))),
    cardGradient = Brush.linearGradient(listOf(Color(0xFF050505), Color(0xFF050505))),
    accentGradient = Brush.radialGradient(listOf(Color(0x0A0055FF), Color.Transparent)),
    highContrastPrimary = Color(0xFF0055FF),
    highContrastSecondary = Color(0xFF00D1FF),
    focusRing = Color(0xFF0055FF),
    selectionBackground = Color(0x330055FF),
    shadowColor = Color.Transparent,
    elevationTint = Color.Transparent
)

private fun applyAccessibilityAdjustments(
    colorScheme: ColorScheme,
    prefs: AccessibilityPreferences
): ColorScheme {
    return when (prefs.contrastLevel) {
        ContrastLevel.STANDARD -> colorScheme
        ContrastLevel.MEDIUM -> enhanceContrast(colorScheme, 1.2f)
        ContrastLevel.HIGH -> enhanceContrast(colorScheme, 1.5f)
    }
}

private fun applyAccessibilityEnhancements(
    accents: EnhancedThemeAccents,
    prefs: AccessibilityPreferences
): EnhancedThemeAccents {
    var enhanced = accents
    
    // Apply contrast adjustments
    if (prefs.contrastLevel != ContrastLevel.STANDARD) {
        val multiplier = when (prefs.contrastLevel) {
            ContrastLevel.MEDIUM -> 1.2f
            ContrastLevel.HIGH -> 1.5f
            else -> 1f
        }
        enhanced = enhanceAccentsContrast(enhanced, multiplier)
    }
    
    // Apply colorblindness adjustments
    if (prefs.colorBlindnessType != ColorBlindnessType.NONE) {
        enhanced = adjustForColorBlindness(enhanced, prefs.colorBlindnessType)
    }
    
    // Enhanced focus indicators
    if (prefs.enhanceFocus) {
        enhanced = enhanced.copy(
            focusRing = enhanceColor(enhanced.focusRing, 1.3f),
            selectionBackground = enhanceColor(enhanced.selectionBackground, 1.2f)
        )
    }
    
    return enhanced
}

private fun enhanceContrast(colorScheme: ColorScheme, multiplier: Float): ColorScheme {
    return colorScheme.copy(
        primary = enhanceColor(colorScheme.primary, multiplier),
        secondary = enhanceColor(colorScheme.secondary, multiplier),
        tertiary = enhanceColor(colorScheme.tertiary, multiplier),
        error = enhanceColor(colorScheme.error, multiplier)
    )
}

private fun enhanceAccentsContrast(
    accents: EnhancedThemeAccents, 
    multiplier: Float
): EnhancedThemeAccents {
    return accents.copy(
        primary = enhanceColor(accents.primary, multiplier),
        secondary = enhanceColor(accents.secondary, multiplier),
        success = enhanceColor(accents.success, multiplier),
        warning = enhanceColor(accents.warning, multiplier),
        info = enhanceColor(accents.info, multiplier),
        error = enhanceColor(accents.error, multiplier),
        highContrastPrimary = enhanceColor(accents.primary, multiplier * 1.2f),
        highContrastSecondary = enhanceColor(accents.secondary, multiplier * 1.2f)
    )
}

private fun enhanceColor(color: Color, multiplier: Float): Color {
    val luminance = color.luminance()
    return if (luminance > 0.5f) {
        // Light color - make darker
        Color(
            red = (color.red * (2f - multiplier)).coerceIn(0f, 1f),
            green = (color.green * (2f - multiplier)).coerceIn(0f, 1f),
            blue = (color.blue * (2f - multiplier)).coerceIn(0f, 1f),
            alpha = color.alpha
        )
    } else {
        // Dark color - make lighter
        Color(
            red = (color.red * multiplier).coerceIn(0f, 1f),
            green = (color.green * multiplier).coerceIn(0f, 1f),
            blue = (color.blue * multiplier).coerceIn(0f, 1f),
            alpha = color.alpha
        )
    }
}

private fun adjustForColorBlindness(
    accents: EnhancedThemeAccents,
    type: ColorBlindnessType
): EnhancedThemeAccents {
    return when (type) {
        ColorBlindnessType.PROTANOPIA -> adjustForProtanopia(accents)
        ColorBlindnessType.DEUTERANOPIA -> adjustForDeuteranopia(accents)
        ColorBlindnessType.TRITANOPIA -> adjustForTritanopia(accents)
        ColorBlindnessType.MONOCHROMACY -> adjustForMonochromacy(accents)
        else -> accents
    }
}

private fun adjustForProtanopia(accents: EnhancedThemeAccents): EnhancedThemeAccents {
    // Red-blind: Replace red/green distinctions with blue/yellow
    return accents.copy(
        error = Color(0xFF0EA5E9), // Blue instead of red
        success = Color(0xFFF59E0B), // Yellow-orange instead of green
        warning = Color(0xFF7C3AED) // Purple for distinction
    )
}

private fun adjustForDeuteranopia(accents: EnhancedThemeAccents): EnhancedThemeAccents {
    // Green-blind: Similar adjustments to protanopia
    return accents.copy(
        success = Color(0xFF0EA5E9), // Blue instead of green
        error = Color(0xFFEF4444), // Keep red as it's more distinguishable
        warning = Color(0xFFF59E0B) // Orange-yellow for warning
    )
}

private fun adjustForTritanopia(accents: EnhancedThemeAccents): EnhancedThemeAccents {
    // Blue-blind: Replace blue with green/red
    return accents.copy(
        primary = if (accents.primary.blue > 0.5f) Color(0xFF10B981) else accents.primary,
        info = Color(0xFF10B981), // Green instead of blue
        secondary = if (accents.secondary.blue > 0.5f) Color(0xFFF59E0B) else accents.secondary
    )
}

private fun adjustForMonochromacy(accents: EnhancedThemeAccents): EnhancedThemeAccents {
    // Complete color blindness: Use only brightness and contrast
    return accents.copy(
        primary = Color(0xFF404040),
        secondary = Color(0xFF808080),
        success = Color(0xFF606060),
        warning = Color(0xFF909090),
        info = Color(0xFF707070),
        error = Color(0xFF202020)
    )
}

private fun getEnhancedTypography(prefs: AccessibilityPreferences) = androidx.compose.material3.Typography(
    // Typography would be enhanced based on accessibility preferences
    // This is a placeholder for the actual typography enhancements
)

private fun getEnhancedShapes(prefs: AccessibilityPreferences) = androidx.compose.material3.Shapes(
    // Shapes would be enhanced based on accessibility preferences
    // This is a placeholder for the actual shape enhancements
)

// Utility functions for contrast calculations
fun Color.contrastRatio(other: Color): Float {
    val luminance1 = this.luminance()
    val luminance2 = other.luminance()
    val lighter = maxOf(luminance1, luminance2)
    val darker = minOf(luminance1, luminance2)
    return (lighter + 0.05f) / (darker + 0.05f)
}

fun Color.meetsWCAGAAA(background: Color): Boolean {
    return contrastRatio(background) >= 7.0f
}

fun Color.meetsWCAGAA(background: Color): Boolean {
    return contrastRatio(background) >= 4.5f
}