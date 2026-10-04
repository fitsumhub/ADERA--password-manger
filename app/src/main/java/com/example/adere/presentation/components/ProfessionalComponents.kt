package com.example.adere.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.presentation.theme.ProfessionalColors
import com.example.adere.presentation.theme.ProfessionalElevation
import com.example.adere.presentation.theme.ProfessionalRadius
import com.example.adere.presentation.theme.ProfessionalSpacing
import com.example.adere.presentation.theme.ProfessionalTiming

/**
 * Professional Card - Enterprise-grade card component with refined aesthetics
 */
@Composable
fun ProfessionalCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: Dp = ProfessionalElevation.Level2,
    borderColor: Color = ProfessionalColors.BorderDefault,
    showBorder: Boolean = true,
    backgroundColor: Color = ProfessionalColors.SurfacePrimary,
    content: @Composable () -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed) ProfessionalElevation.Level1 else elevation,
        animationSpec = tween(ProfessionalTiming.Fast),
        label = "card_elevation"
    )
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(ProfessionalTiming.Fast),
        label = "card_scale"
    )
    
    Card(
        modifier = modifier
            .scale(animatedScale)
            .shadow(
                elevation = animatedElevation,
                shape = RoundedCornerShape(ProfessionalRadius.Medium),
                spotColor = ProfessionalColors.Shadow300
            )
            .then(
                if (showBorder) {
                    Modifier.border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(ProfessionalRadius.Medium)
                    )
                } else Modifier
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = onClick != null
            ) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick?.invoke()
            },
        shape = RoundedCornerShape(ProfessionalRadius.Medium),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Box(modifier = Modifier.padding(ProfessionalSpacing.Space5)) {
            content()
        }
    }
}

/**
 * Professional Button - Primary action button with refined styling
 */
@Composable
fun ProfessionalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val hapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = tween(ProfessionalTiming.Fast),
        label = "button_scale"
    )
    
    val (backgroundColor, textColor) = when (variant) {
        ButtonVariant.Primary -> ProfessionalColors.PrimaryBlue600 to Color.White
        ButtonVariant.Secondary -> ProfessionalColors.SecondaryTeal600 to Color.White
        ButtonVariant.Outline -> Color.Transparent to ProfessionalColors.PrimaryBlue600
        ButtonVariant.Ghost -> Color.Transparent to ProfessionalColors.Neutral700
        ButtonVariant.Danger -> ProfessionalColors.Error600 to Color.White
    }
    
    val height = when (size) {
        ButtonSize.Small -> 36.dp
        ButtonSize.Medium -> 44.dp
        ButtonSize.Large -> 52.dp
    }
    
    val horizontalPadding = when (size) {
        ButtonSize.Small -> ProfessionalSpacing.Space4
        ButtonSize.Medium -> ProfessionalSpacing.Space5
        ButtonSize.Large -> ProfessionalSpacing.Space6
    }
    
    Surface(
        modifier = modifier
            .height(height)
            .scale(animatedScale)
            .then(
                if (variant == ButtonVariant.Outline) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = if (enabled) ProfessionalColors.PrimaryBlue600 else ProfessionalColors.Neutral300,
                        shape = RoundedCornerShape(ProfessionalRadius.Medium)
                    )
                } else Modifier
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading
            ) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        shape = RoundedCornerShape(ProfessionalRadius.Medium),
        color = if (enabled) backgroundColor else ProfessionalColors.Neutral200
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = horizontalPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null && !isLoading) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) textColor else ProfessionalColors.Neutral500,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(ProfessionalSpacing.Space3))
            }
            
            Text(
                text = text,
                color = if (enabled) textColor else ProfessionalColors.Neutral500,
                fontSize = when (size) {
                    ButtonSize.Small -> 13.sp
                    ButtonSize.Medium -> 14.sp
                    ButtonSize.Large -> 16.sp
                },
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

enum class ButtonVariant {
    Primary, Secondary, Outline, Ghost, Danger
}

enum class ButtonSize {
    Small, Medium, Large
}

/**
 * Professional Badge - Status indicator with refined styling
 */
@Composable
fun ProfessionalBadge(
    text: String,
    variant: BadgeVariant = BadgeVariant.Default,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (variant) {
        BadgeVariant.Default -> ProfessionalColors.Neutral100 to ProfessionalColors.Neutral700
        BadgeVariant.Primary -> ProfessionalColors.PrimaryBlue100 to ProfessionalColors.PrimaryBlue700
        BadgeVariant.Success -> ProfessionalColors.Success100 to ProfessionalColors.Success700
        BadgeVariant.Warning -> ProfessionalColors.Warning100 to ProfessionalColors.Warning700
        BadgeVariant.Error -> ProfessionalColors.Error100 to ProfessionalColors.Error700
        BadgeVariant.Info -> ProfessionalColors.Info100 to ProfessionalColors.Info700
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(ProfessionalRadius.Small),
        color = backgroundColor
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = ProfessionalSpacing.Space3,
                vertical = ProfessionalSpacing.Space1
            )
        )
    }
}

enum class BadgeVariant {
    Default, Primary, Success, Warning, Error, Info
}

/**
 * Professional Icon Container - Consistent icon presentation
 */
@Composable
fun ProfessionalIconContainer(
    icon: ImageVector,
    variant: IconContainerVariant = IconContainerVariant.Default,
    size: IconContainerSize = IconContainerSize.Medium,
    modifier: Modifier = Modifier
) {
    val containerSize = when (size) {
        IconContainerSize.Small -> 32.dp
        IconContainerSize.Medium -> 40.dp
        IconContainerSize.Large -> 48.dp
        IconContainerSize.XLarge -> 56.dp
    }
    
    val iconSize = when (size) {
        IconContainerSize.Small -> 16.dp
        IconContainerSize.Medium -> 20.dp
        IconContainerSize.Large -> 24.dp
        IconContainerSize.XLarge -> 28.dp
    }
    
    val (backgroundColor, iconColor) = when (variant) {
        IconContainerVariant.Default -> ProfessionalColors.Neutral100 to ProfessionalColors.Neutral600
        IconContainerVariant.Primary -> ProfessionalColors.PrimaryBlue100 to ProfessionalColors.PrimaryBlue600
        IconContainerVariant.Success -> ProfessionalColors.Success100 to ProfessionalColors.Success600
        IconContainerVariant.Warning -> ProfessionalColors.Warning100 to ProfessionalColors.Warning600
        IconContainerVariant.Error -> ProfessionalColors.Error100 to ProfessionalColors.Error600
        IconContainerVariant.Info -> ProfessionalColors.Info100 to ProfessionalColors.Info600
    }
    
    Box(
        modifier = modifier
            .size(containerSize)
            .clip(RoundedCornerShape(ProfessionalRadius.Medium))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(iconSize)
        )
    }
}

enum class IconContainerVariant {
    Default, Primary, Success, Warning, Error, Info
}

enum class IconContainerSize {
    Small, Medium, Large, XLarge
}

/**
 * Professional Divider - Subtle content separator
 */
@Composable
fun ProfessionalDivider(
    modifier: Modifier = Modifier,
    color: Color = ProfessionalColors.BorderDivider,
    thickness: Dp = 1.dp,
    withGradient: Boolean = false
) {
    if (withGradient) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(thickness)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            color,
                            Color.Transparent
                        )
                    )
                )
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(thickness)
                .background(color)
        )
    }
}

/**
 * Professional Status Indicator - Visual status dot
 */
@Composable
fun ProfessionalStatusIndicator(
    status: StatusType,
    modifier: Modifier = Modifier,
    size: Dp = 8.dp,
    withPulse: Boolean = false
) {
    val statusColor = when (status) {
        StatusType.Active -> ProfessionalColors.Success500
        StatusType.Inactive -> ProfessionalColors.Neutral400
        StatusType.Warning -> ProfessionalColors.Warning500
        StatusType.Error -> ProfessionalColors.Error500
        StatusType.Info -> ProfessionalColors.Info500
    }
    
    Box(
        modifier = modifier.size(size * 2),
        contentAlignment = Alignment.Center
    ) {
        // Pulse effect background (optional)
        if (withPulse && status == StatusType.Active) {
            Box(
                modifier = Modifier
                    .size(size * 2)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.2f))
            )
        }
        
        // Status dot
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(statusColor)
        )
    }
}

enum class StatusType {
    Active, Inactive, Warning, Error, Info
}

/**
 * Professional Section Header - Clear content organization
 */
@Composable
fun ProfessionalSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = ProfessionalSpacing.Space5,
                vertical = ProfessionalSpacing.Space4
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ProfessionalColors.Neutral900
                )
                
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(ProfessionalSpacing.Space1))
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = ProfessionalColors.Neutral600
                    )
                }
            }
            
            action?.invoke()
        }
    }
}
