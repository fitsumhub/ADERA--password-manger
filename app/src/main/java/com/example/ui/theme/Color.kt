package com.example.ui.theme

import androidx.compose.ui.graphics.Color

val CleanBg = Color(0xFFF6F7F9)
val CleanSurface = Color(0xFFFFFFFF)
val CleanSurfaceVariant = Color(0xFFEEF1F5)
val CleanBorder = Color(0xFFDDE2E9)
val CleanBorderSubtle = Color(0xFFE8ECF1)

val CharcoalBg = Color(0xFF11151B)
val CharcoalSurface = Color(0xFF191F27)
val CharcoalSurfaceVariant = Color(0xFF222A35)
val CharcoalSurfaceElevated = Color(0xFF28313E)
val CharcoalBorder = Color(0xFF35404E)
val CharcoalBorderSubtle = Color(0xFF2B3440)

val TextDarkPrimary = Color(0xFF09090B)
val TextDarkSecondary = Color(0xFF71717A)
val TextDarkMuted = Color(0xFFA1A1AA)

val LightTextPrimary = Color(0xFFFAFAFA)
val LightTextSecondary = Color(0xFFA1A1AA)

val TextPrimary = TextDarkPrimary
val TextSecondary = TextDarkSecondary
val TextMuted = TextDarkMuted

val TotalSecurityPrimary = Color(0xFF334155)
val TotalSecurityPrimaryLight = Color(0xFF475569)
val TotalSecurityPrimaryDark = Color(0xFF1E293B)
val TotalSecurityPrimaryContainer = Color(0xFFE2E8F0)
val TotalSecurityOnPrimaryContainer = Color(0xFF1E293B)

val EmeraldPrimary = Color(0xFF10B981)
val EmeraldLight = Color(0xFF34D399)
val EmeraldDark = Color(0xFF059669)
val EmeraldContainer = Color(0xFF064E3B)
val OnEmeraldContainer = Color(0xFF10B981)

val SapphirePrimary = Color(0xFF3B82F6)
val SapphireLight = Color(0xFF60A5FA)
val SapphireDark = Color(0xFF2563EB)
val SapphireContainer = Color(0xFF1E3A8A)
val SapphireBg = Color(0xFF101722)
val SapphireSurface = Color(0xFF182231)
val SapphireSurfaceVariant = Color(0xFF222E40)
val SapphireBorder = Color(0xFF35445A)

val CategorySocialBlue = Color(0xFF2563EB) 
val CategoryAppsYellow = Color(0xFFD97706) 
val CategoryCardTeal = Color(0xFF0D9488)   
val CategoryCoral = Color(0xFFE11D48)

val HealthSafeBlue = Color(0xFF3B82F6)
val HealthRefusedYellow = Color(0xFFD97706)
val HealthRiskRed = Color(0xFFE11D48)
val HealthCompromisedTeal = Color(0xFF0D9488)

val SecurityOrange = Color(0xFFF59E0B)
val SecurityRed = Color(0xFFE11D48)
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
        title = "Quiet Graphite",
        subtitle = "Soft slate with a clean, light canvas",
        primaryColor = TotalSecurityPrimary,
        surfaceColor = Color(0xFFFFFFFF),
        bgColor = CleanBg
    ),
    OBSIDIAN_EMERALD(
        title = "Graphite Emerald",
        subtitle = "Layered charcoal with a calm emerald accent",
        primaryColor = Color(0xFF10B981),
        surfaceColor = CharcoalSurface,
        bgColor = CharcoalBg
    ),
    MIDNIGHT_SAPPHIRE(
        title = "Midnight Slate",
        subtitle = "Deep blue-gray with a restrained cobalt accent",
        primaryColor = Color(0xFF3B82F6),
        surfaceColor = SapphireSurface,
        bgColor = SapphireBg
    )
}
