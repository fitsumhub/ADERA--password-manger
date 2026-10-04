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
    primary = Color(0xFF5E5CE6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF0FF),
    onPrimaryContainer = Color(0xFF27237E),
    secondary = Color(0xFFF5BA31),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = Color(0xFF48C9B0),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE6FFFA),
    onTertiaryContainer = Color(0xFF064E3B),
    background = Color(0xFFF7F8FC),
    onBackground = Color(0xFF1A1C29),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1C29),
    surfaceVariant = Color(0xFFF1F3F9),
    onSurfaceVariant = Color(0xFF737A8C),
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
    onErrorContainer = Color(0xFFB71C1C)
)

private fun createTotalSecurityDarkScheme() = darkColorScheme(
    primary = Color(0xFF7E7CF6),
    onPrimary = Color(0xFF27237E),
    primaryContainer = Color(0xFF4341B8),
    onPrimaryContainer = Color(0xFFEEF0FF),
    secondary = Color(0xFFFBBF24),
    onSecondary = Color(0xFF78350F),
    secondaryContainer = Color(0xFF92400E),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = Color(0xFF34D399),
    onTertiary = Color(0xFF064E3B),
    tertiaryContainer = Color(0xFF047857),
    onTertiaryContainer = Color(0xFFE6FFFA),
    background = Color(0xFF0F0F23),
    onBackground = Color(0xFFE5E7EB),
    surface = Color(0xFF1F1F37),
    onSurface = Color(0xFFE5E7EB),
    surfaceVariant = Color(0xFF2D2D4A),
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF374151),
    outlineVariant = Color(0xFF2D2D4A),
    scrim = Color(0x80000000),
    inverseSurface = Color(0xFFE5E7EB),
    inverseOnSurface = Color(0xFF1F1F37),
    inversePrimary = Color(0xFF5E5CE6),
    surfaceTint = Color(0xFF7E7CF6),
    error = Color(0xFFEF4444),
    onError = Color.White,
    errorContainer = Color(0xFFB91C1C),
    onErrorContainer = Color(0xFFFFEBEE)
)

private fun createObsidianEmeraldScheme() = darkColorScheme(
    primary = Color(0xFF10B981),
    onPrimary = Color(0xFF003824),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = Color(0xFFF59E0B),
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = Color(0xFF38BDF8),
    onTertiary = Color(0xFF003258),
    background = Color(0xFF070B0A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0F1715),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF16221F),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF203630),
    outlineVariant = Color(0xFF142420)
)

private fun createMidnightSapphireScheme() = darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF0C4A6E),
    onPrimaryContainer = Color(0xFFC2E7FF),
    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF003824),
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = Color(0xFFA855F7),
    onTertiary = Color(0xFF38006B),
    background = Color(0xFF060B12),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0D1726),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF132238),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF1D3557),
    outlineVariant = Color(0xFF101B2E)
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

private fun getTotalSecurityDarkAccents() = EnhancedThemeAccents(
    primary = Color(0xFF7E7CF6),
    primaryVariant = Color(0xFF5E5CE6),
    secondary = Color(0xFFFBBF24),
    background = Color(0xFF0F0F23),
    surface = Color(0xFF1F1F37),
    surfaceVariant = Color(0xFF2D2D4A),
    onPrimary = Color(0xFF27237E),
    onSecondary = Color(0xFF78350F),
    onBackground = Color(0xFFE5E7EB),
    onSurface = Color(0xFFE5E7EB),
    outline = Color(0xFF374151),
    outlineVariant = Color(0xFF2D2D4A),
    scrim = Color(0x80000000),
    inverseSurface = Color(0xFFE5E7EB),
    inverseOnSurface = Color(0xFF1F1F37),
    inversePrimary = Color(0xFF5E5CE6),
    surfaceTint = Color(0xFF7E7CF6),
    error = Color(0xFFEF4444),
    onError = Color.White,
    errorContainer = Color(0xFFB91C1C),
    onErrorContainer = Color(0xFFFFEBEE),
    success = Color(0xFF10B981),
    warning = Color(0xFFF59E0B),
    info = Color(0xFF38BDF8),
    onSuccess = Color.White,
    onWarning = Color.White,
    onInfo = Color.White,
    successContainer = Color(0xFF064E3B),
    warningContainer = Color(0xFF78350F),
    infoContainer = Color(0xFF0C4A6E),
    primaryGradient = Brush.linearGradient(listOf(Color(0xFF7E7CF6), Color(0xFF9F9EF7))),
    backgroundGradient = Brush.verticalGradient(listOf(Color(0xFF0F0F23), Color(0xFF1F1F37))),
    cardGradient = Brush.linearGradient(listOf(Color(0xFF1F1F37), Color(0xFF2A2A47))),
    accentGradient = Brush.radialGradient(listOf(Color(0x267E7CF6), Color.Transparent)),
    highContrastPrimary = Color(0xFF9F9EF7),
    highContrastSecondary = Color(0xFFFED570),
    focusRing = Color(0xFF7E7CF6),
    selectionBackground = Color(0x337E7CF6),
    shadowColor = Color(0x1A000000),
    elevationTint = Color(0x0A7E7CF6)
)

private fun getObsidianEmeraldAccents() = EnhancedThemeAccents(
    primary = Color(0xFF10B981),
    primaryVariant = Color(0xFF059669),
    secondary = Color(0xFFF59E0B),
    background = Color(0xFF070B0A),
    surface = Color(0xFF0F1715),
    surfaceVariant = Color(0xFF16221F),
    onPrimary = Color(0xFF003824),
    onSecondary = Color(0xFF451A03),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    outline = Color(0xFF203630),
    outlineVariant = Color(0xFF142420),
    scrim = Color(0x80000000),
    inverseSurface = Color(0xFFF8FAFC),
    inverseOnSurface = Color(0xFF0F1715),
    inversePrimary = Color(0xFF6EE7B7),
    surfaceTint = Color(0xFF10B981),
    error = Color(0xFFEF4444),
    onError = Color.White,
    errorContainer = Color(0xFFB91C1C),
    onErrorContainer = Color(0xFFFFEBEE),
    success = Color(0xFF10B981),
    warning = Color(0xFFF59E0B),
    info = Color(0xFF38BDF8),
    onSuccess = Color.White,
    onWarning = Color.White,
    onInfo = Color.White,
    successContainer = Color(0xFF064E3B),
    warningContainer = Color(0xFF78350F),
    infoContainer = Color(0xFF0C4A6E),
    primaryGradient = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF34D399))),
    backgroundGradient = Brush.verticalGradient(listOf(Color(0xFF070B0A), Color(0xFF0F1715))),
    cardGradient = Brush.linearGradient(listOf(Color(0xFF0F1715), Color(0xFF1B2C27))),
    accentGradient = Brush.radialGradient(listOf(Color(0x2610B981), Color.Transparent)),
    highContrastPrimary = Color(0xFF34D399),
    highContrastSecondary = Color(0xFFFBBF24),
    focusRing = Color(0xFF10B981),
    selectionBackground = Color(0x3310B981),
    shadowColor = Color(0x1A000000),
    elevationTint = Color(0x0A10B981)
)

private fun getMidnightSapphireAccents() = EnhancedThemeAccents(
    primary = Color(0xFF38BDF8),
    primaryVariant = Color(0xFF0EA5E9),
    secondary = Color(0xFF34D399),
    background = Color(0xFF060B12),
    surface = Color(0xFF0D1726),
    surfaceVariant = Color(0xFF132238),
    onPrimary = Color(0xFF003258),
    onSecondary = Color(0xFF003824),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    outline = Color(0xFF1D3557),
    outlineVariant = Color(0xFF101B2E),
    scrim = Color(0x80000000),
    inverseSurface = Color(0xFFF8FAFC),
    inverseOnSurface = Color(0xFF0D1726),
    inversePrimary = Color(0xFF7DD3FC),
    surfaceTint = Color(0xFF38BDF8),
    error = Color(0xFFEF4444),
    onError = Color.White,
    errorContainer = Color(0xFFB91C1C),
    onErrorContainer = Color(0xFFFFEBEE),
    success = Color(0xFF10B981),
    warning = Color(0xFFF59E0B),
    info = Color(0xFF38BDF8),
    onSuccess = Color.White,
    onWarning = Color.White,
    onInfo = Color.White,
    successContainer = Color(0xFF064E3B),
    warningContainer = Color(0xFF78350F),
    infoContainer = Color(0xFF0C4A6E),
    primaryGradient = Brush.linearGradient(listOf(Color(0xFF38BDF8), Color(0xFF7DD3FC))),
    backgroundGradient = Brush.verticalGradient(listOf(Color(0xFF060B12), Color(0xFF0D1726))),
    cardGradient = Brush.linearGradient(listOf(Color(0xFF0D1726), Color(0xFF142033))),
    accentGradient = Brush.radialGradient(listOf(Color(0x2638BDF8), Color.Transparent)),
    highContrastPrimary = Color(0xFF7DD3FC),
    highContrastSecondary = Color(0xFF6EE7B7),
    focusRing = Color(0xFF38BDF8),
    selectionBackground = Color(0x3338BDF8),
    shadowColor = Color(0x1A000000),
    elevationTint = Color(0x0A38BDF8)
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