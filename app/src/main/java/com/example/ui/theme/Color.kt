package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Pristine Modern Canvas Tokens
val CleanBg = Color(0xFFF8FAFC)
val CleanSurface = Color(0xFFFFFFFF)
val CleanSurfaceVariant = Color(0xFFF1F5F9)
val CleanBorder = Color(0xFFE2E8F0)
val CleanBorderSubtle = Color(0xFFF8FAFC)

// Obsidian Dark Mode Canvas Tokens
val NavyBg = Color(0xFF0B132B)
val NavySurface = Color(0xFF121B2E)
val NavySurfaceVariant = Color(0xFF1B263C)
val NavySurfaceElevated = Color(0xFF24324D)
val NavyBorder = Color(0xFF2E3D59)
val NavyBorderSubtle = Color(0xFF1A263D)

// Compatibility aliases
val CharcoalBg = NavyBg
val CharcoalSurface = NavySurface
val CharcoalSurfaceVariant = NavySurfaceVariant
val CharcoalSurfaceElevated = NavySurfaceElevated
val CharcoalBorder = NavyBorder
val CharcoalBorderSubtle = NavyBorderSubtle

// Typography Color Tokens
val TextDarkPrimary = Color(0xFF0F172A)
val TextDarkSecondary = Color(0xFF475569)
val TextDarkMuted = Color(0xFF94A3B8)

val LightTextPrimary = Color(0xFFF8FAFC)
val LightTextSecondary = Color(0xFF94A3B8)

val TextPrimary: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface

val TextSecondary: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

val TextMuted: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)

// Primary Brand Colors: Financial-Grade Deep Vault Emerald & Precision Blue
val EmeraldPrimary = Color(0xFF059669)
val EmeraldLight = Color(0xFF10B981)
val EmeraldDark = Color(0xFF047857)
val EmeraldContainer = Color(0xFFECFDF5)
val OnEmeraldContainer = Color(0xFF065F46)

val ElectricCyanPrimary = Color(0xFF0284C7)
val ElectricCyanLight = Color(0xFF0EA5E9)
val ElectricCyanDark = Color(0xFF0369A1)
val ElectricCyanContainer = Color(0xFFE0F2FE)

val TotalSecurityPrimary = EmeraldPrimary
val TotalSecurityPrimaryLight = EmeraldLight
val TotalSecurityPrimaryDark = EmeraldDark
val TotalSecurityPrimaryContainer = EmeraldContainer
val TotalSecurityOnPrimaryContainer = OnEmeraldContainer

val SapphirePrimary = ElectricCyanPrimary
val SapphireLight = ElectricCyanLight
val SapphireDark = ElectricCyanDark
val SapphireContainer = ElectricCyanContainer
val SapphireBg = NavyBg
val SapphireSurface = NavySurface
val SapphireSurfaceVariant = NavySurfaceVariant
val SapphireBorder = NavyBorder

// Functional Category Accent Badges
val CategorySocialBlue = Color(0xFF2563EB)
val CategoryAppsYellow = Color(0xFFD97706)
val CategoryCardTeal = Color(0xFF0D9488)
val CategoryCoral = Color(0xFFF43F5E)

// Security Health Status Indicators
val HealthSafeBlue = Color(0xFF0284C7)
val HealthRefusedYellow = Color(0xFFD97706)
val HealthRiskRed = Color(0xFFF43F5E)
val HealthCompromisedTeal = Color(0xFF0D9488)

val SecurityOrange = Color(0xFFF59E0B)
val SecurityRed = Color(0xFFF43F5E)
val SecurityYellow = Color(0xFFFBBF24)

val GoldAccent = CategoryAppsYellow
val GoldLight = Color(0xFFF59E0B)

enum class VaultThemePalette(
    val title: String,
    val subtitle: String,
    val primaryColor: Color,
    val surfaceColor: Color,
    val bgColor: Color,
) {
    TOTAL_SECURITY(
        title = "Pristine Emerald",
        subtitle = "Clean white polished canvas with deep vault emerald accents",
        primaryColor = EmeraldPrimary,
        surfaceColor = CleanSurface,
        bgColor = CleanBg
    ),
    OBSIDIAN_EMERALD(
        title = "Obsidian Emerald",
        subtitle = "Deep obsidian slate canvas with vibrant mint emerald accents",
        primaryColor = Color(0xFF10B981),
        surfaceColor = NavySurface,
        bgColor = NavyBg
    ),
    MIDNIGHT_SAPPHIRE(
        title = "Midnight Cobalt",
        subtitle = "Deep obsidian slate with cobalt blue security accents",
        primaryColor = Color(0xFF0284C7),
        surfaceColor = NavySurface,
        bgColor = NavyBg
    )
}
