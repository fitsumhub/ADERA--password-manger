package com.example.adere.presentation.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Professional Design System - Enterprise-grade color palette and design tokens
 * Following financial/security industry standards with subtle, trustworthy aesthetics
 */
object ProfessionalColors {
    // Primary - Deep Professional Blue (Trust & Security)
    val PrimaryBlue900 = Color(0xFF0A2540)
    val PrimaryBlue800 = Color(0xFF0D3A5F)
    val PrimaryBlue700 = Color(0xFF134E7E)
    val PrimaryBlue600 = Color(0xFF1A5F99)
    val PrimaryBlue500 = Color(0xFF2470B4)
    val PrimaryBlue400 = Color(0xFF4A8FD0)
    val PrimaryBlue300 = Color(0xFF7BB2E8)
    val PrimaryBlue200 = Color(0xFFAFD3F2)
    val PrimaryBlue100 = Color(0xFFE3F2FD)
    val PrimaryBlue50 = Color(0xFFF5FAFF)
    
    // Secondary - Professional Teal (Innovation & Growth)
    val SecondaryTeal900 = Color(0xFF004D40)
    val SecondaryTeal800 = Color(0xFF00695C)
    val SecondaryTeal700 = Color(0xFF00796B)
    val SecondaryTeal600 = Color(0xFF00897B)
    val SecondaryTeal500 = Color(0xFF009688)
    val SecondaryTeal400 = Color(0xFF26A69A)
    val SecondaryTeal300 = Color(0xFF4DB6AC)
    val SecondaryTeal200 = Color(0xFF80CBC4)
    val SecondaryTeal100 = Color(0xFFB2DFDB)
    val SecondaryTeal50 = Color(0xFFE0F2F1)
    
    // Accent - Premium Gold (Premium & Excellence)
    val AccentGold900 = Color(0xFF663C00)
    val AccentGold800 = Color(0xFF8A5000)
    val AccentGold700 = Color(0xFFAD6300)
    val AccentGold600 = Color(0xFFD17700)
    val AccentGold500 = Color(0xFFF59300)
    val AccentGold400 = Color(0xFFFFA726)
    val AccentGold300 = Color(0xFFFFB74D)
    val AccentGold200 = Color(0xFFFFCC80)
    val AccentGold100 = Color(0xFFFFE0B2)
    val AccentGold50 = Color(0xFFFFF3E0)
    
    // Neutral - Professional Grays
    val Neutral900 = Color(0xFF1A1D23)
    val Neutral800 = Color(0xFF2D3139)
    val Neutral700 = Color(0xFF3E4451)
    val Neutral600 = Color(0xFF545B6B)
    val Neutral500 = Color(0xFF6B7280)
    val Neutral400 = Color(0xFF9CA3AF)
    val Neutral300 = Color(0xFFD1D5DB)
    val Neutral200 = Color(0xFFE5E7EB)
    val Neutral100 = Color(0xFFF3F4F6)
    val Neutral50 = Color(0xFFFAFAFB)
    val NeutralWhite = Color(0xFFFFFFFF)
    
    // Semantic Colors - Professional Status Indicators
    val Success900 = Color(0xFF014737)
    val Success700 = Color(0xFF027A48)
    val Success600 = Color(0xFF05A660)
    val Success500 = Color(0xFF12B76A)
    val Success300 = Color(0xFF6CE9A6)
    val Success100 = Color(0xFFD1FADF)
    val Success50 = Color(0xFFECFDF3)

    val Warning900 = Color(0xFF7A2E0E)
    val Warning700 = Color(0xFFB54708)
    val Warning600 = Color(0xFFE47D05)
    val Warning500 = Color(0xFFF79009)
    val Warning300 = Color(0xFFFDB022)
    val Warning100 = Color(0xFFFEF0C7)
    val Warning50 = Color(0xFFFFFAEB)

    val Error900 = Color(0xFF7A271A)
    val Error700 = Color(0xFFB42318)
    val Error600 = Color(0xFFD92D20)
    val Error500 = Color(0xFFF04438)
    val Error300 = Color(0xFFFDA29B)
    val Error100 = Color(0xFFFEE4E2)
    val Error50 = Color(0xFFFEF3F2)

    val Info900 = Color(0xFF102A56)
    val Info700 = Color(0xFF1849A9)
    val Info600 = Color(0xFF1570EF)
    val Info500 = Color(0xFF2E90FA)
    val Info300 = Color(0xFF84CAFF)
    val Info100 = Color(0xFFD1E9FF)
    val Info50 = Color(0xFFEFF8FF)
    
    // Surface Colors - Professional Backgrounds
    val SurfacePrimary = NeutralWhite
    val SurfaceSecondary = Neutral50
    val SurfaceTertiary = Neutral100
    val SurfaceElevated = NeutralWhite
    val SurfaceOverlay = Color(0xF2FFFFFF)
    
    // Border Colors - Subtle Professional Borders
    val BorderDefault = Neutral200
    val BorderHover = Neutral300
    val BorderFocus = PrimaryBlue500
    val BorderDivider = Neutral100
    
    // Shadow Colors - Professional Depth
    val Shadow100 = Color(0x0A000000)
    val Shadow200 = Color(0x14000000)
    val Shadow300 = Color(0x1F000000)
    val Shadow400 = Color(0x29000000)
}

/**
 * Professional Typography System - Clear hierarchy and excellent readability
 */
object ProfessionalTypography {
    // Display - Large hero text
    val DisplayLarge = 57.sp to FontWeight.Bold
    val DisplayMedium = 45.sp to FontWeight.Bold
    val DisplaySmall = 36.sp to FontWeight.Bold
    
    // Headline - Section headers
    val HeadlineLarge = 32.sp to FontWeight.Bold
    val HeadlineMedium = 28.sp to FontWeight.SemiBold
    val HeadlineSmall = 24.sp to FontWeight.SemiBold
    
    // Title - Card/Component titles
    val TitleLarge = 22.sp to FontWeight.SemiBold
    val TitleMedium = 16.sp to FontWeight.SemiBold
    val TitleSmall = 14.sp to FontWeight.Medium
    
    // Body - Main content text
    val BodyLarge = 16.sp to FontWeight.Normal
    val BodyMedium = 14.sp to FontWeight.Normal
    val BodySmall = 12.sp to FontWeight.Normal
    
    // Label - Buttons, tags, labels
    val LabelLarge = 14.sp to FontWeight.SemiBold
    val LabelMedium = 12.sp to FontWeight.SemiBold
    val LabelSmall = 11.sp to FontWeight.Medium
    
    // Caption - Metadata, timestamps
    val CaptionLarge = 12.sp to FontWeight.Normal
    val CaptionSmall = 10.sp to FontWeight.Normal
}

/**
 * Professional Spacing System - 8pt grid with consistent rhythm
 */
object ProfessionalSpacing {
    val Space0 = 0.dp
    val Space1 = 2.dp    // Hairline spacing
    val Space2 = 4.dp    // Extra tight
    val Space3 = 8.dp    // Tight
    val Space4 = 12.dp   // Compact
    val Space5 = 16.dp   // Normal
    val Space6 = 20.dp   // Comfortable
    val Space7 = 24.dp   // Relaxed
    val Space8 = 32.dp   // Spacious
    val Space9 = 40.dp   // Very spacious
    val Space10 = 48.dp  // Extra spacious
    val Space11 = 64.dp  // Huge
    val Space12 = 80.dp  // Massive
}

/**
 * Professional Border Radius - Consistent rounded corners
 */
object ProfessionalRadius {
    val None = 0.dp
    val XSmall = 4.dp    // Subtle rounding
    val Small = 8.dp     // Standard UI elements
    val Medium = 12.dp   // Cards, buttons
    val Large = 16.dp    // Featured cards
    val XLarge = 20.dp   // Hero elements
    val XXLarge = 24.dp  // Special components
    val Rounded = 28.dp  // Pill-shaped
    val Circle = 999.dp  // Perfect circles
}

/**
 * Professional Elevation System - Subtle depth with shadows
 */
object ProfessionalElevation {
    val Level0 = 0.dp    // Flat (no shadow)
    val Level1 = 1.dp    // Slight lift (hover states)
    val Level2 = 2.dp    // Standard cards
    val Level3 = 4.dp    // Elevated cards
    val Level4 = 6.dp    // Floating elements
    val Level5 = 8.dp    // Modals, dialogs
    val Level6 = 12.dp   // Popups, tooltips
    val Level7 = 16.dp   // Maximum elevation
}

/**
 * Professional Animation Timing - Smooth, professional motion
 */
object ProfessionalTiming {
    const val Instant = 0
    const val Immediate = 100     // Very quick feedback
    const val Fast = 200          // Quick transitions
    const val Normal = 300        // Standard animations
    const val Comfortable = 400   // Relaxed animations
    const val Slow = 500          // Deliberate animations
    const val Slower = 700        // Emphasis animations
    const val VerySlow = 1000     // Special effects
}

/**
 * Professional Icon Sizes - Consistent visual weight
 */
object ProfessionalIconSizes {
    val Tiny = 12.dp
    val Small = 16.dp
    val Medium = 20.dp
    val Large = 24.dp
    val XLarge = 32.dp
    val XXLarge = 40.dp
    val Huge = 48.dp
    val Massive = 64.dp
}

/**
 * Professional Touch Targets - Accessibility-first sizing
 */
object ProfessionalTouchTargets {
    val Minimum = 44.dp  // iOS minimum
    val Standard = 48.dp // Android minimum
    val Comfortable = 56.dp
    val Large = 64.dp
}

/**
 * Professional Container Widths - Optimal reading lengths
 */
object ProfessionalContainers {
    val Compact = 400.dp
    val Standard = 600.dp
    val Wide = 800.dp
    val XWide = 1000.dp
    val Full = 1200.dp
}

/**
 * Professional Z-Index - Layering system
 */
object ProfessionalZIndex {
    const val Background = 0
    const val Content = 1
    const val Overlay = 10
    const val FloatingButton = 50
    const val Header = 100
    const val Drawer = 200
    const val Modal = 300
    const val Tooltip = 400
    const val Toast = 500
}

/**
 * Professional Opacity Values - Consistent transparency
 */
object ProfessionalOpacity {
    const val Invisible = 0f
    const val Faint = 0.04f
    const val Light = 0.08f
    const val Medium = 0.12f
    const val Strong = 0.16f
    const val Heavy = 0.24f
    const val Intense = 0.32f
    const val Solid = 1f
}

/**
 * Professional Line Heights - Optimal readability
 */
object ProfessionalLineHeights {
    const val Tight = 1.2f
    const val Normal = 1.5f
    const val Relaxed = 1.75f
    const val Loose = 2f
}

/**
 * Professional Letter Spacing - Typography refinement
 */
object ProfessionalLetterSpacing {
    val Tight = (-0.5).sp
    val Normal = 0.sp
    val Relaxed = 0.25.sp
    val Wide = 0.5.sp
    val ExtraWide = 1.sp
}

/**
 * Professional Blur Effects - Modern glassmorphism
 */
object ProfessionalBlur {
    val Subtle = 8.dp
    val Medium = 16.dp
    val Strong = 24.dp
    val Intense = 32.dp
}

/**
 * Professional Gradient Angles - Consistent gradients
 */
object ProfessionalGradients {
    const val TopToBottom = 180f
    const val BottomToTop = 0f
    const val LeftToRight = 90f
    const val RightToLeft = 270f
    const val TopLeftToBottomRight = 135f
    const val BottomLeftToTopRight = 45f
}

/**
 * Professional Aspect Ratios - Standard proportions
 */
object ProfessionalAspectRatios {
    const val Square = 1f / 1f
    const val Landscape = 16f / 9f
    const val Portrait = 9f / 16f
    const val Wide = 21f / 9f
    const val Classic = 4f / 3f
    const val Golden = 1.618f / 1f
}

/**
 * Professional Breakpoints - Responsive design
 */
object ProfessionalBreakpoints {
    val Phone = 0.dp
    val PhoneLarge = 360.dp
    val Tablet = 600.dp
    val TabletLarge = 840.dp
    val Desktop = 1024.dp
    val DesktopLarge = 1440.dp
}
