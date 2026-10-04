package com.example.adere.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.domain.model.VaultItem
import com.example.adere.presentation.theme.ProfessionalColors
import com.example.adere.presentation.theme.ProfessionalElevation
import com.example.adere.presentation.theme.ProfessionalRadius
import com.example.adere.presentation.theme.ProfessionalSpacing
import com.example.adere.presentation.theme.ProfessionalTiming

/**
 * Professional Vault Item Card - Refined, enterprise-grade design
 */
@Composable
fun ProfessionalVaultItemCard(
    item: VaultItem,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    val animatedElevation by animateDpAsState(
        targetValue = when {
            isPressed -> ProfessionalElevation.Level1
            isHovered -> ProfessionalElevation.Level3
            else -> ProfessionalElevation.Level2
        },
        animationSpec = tween(ProfessionalTiming.Fast),
        label = "card_elevation"
    )
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(ProfessionalTiming.Fast),
        label = "card_scale"
    )
    
    val borderColor by animateColorAsState(
        targetValue = when {
            isPressed -> ProfessionalColors.PrimaryBlue300
            isHovered -> ProfessionalColors.BorderHover
            else -> ProfessionalColors.BorderDefault
        },
        animationSpec = tween(ProfessionalTiming.Fast),
        label = "border_color"
    )
    
    val brandDrawable = remember(item.title, item.payload.url) {
        BrandIconHelper.resolveBrandDrawable(item.title, item.payload.url)
    }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .shadow(
                elevation = animatedElevation,
                shape = RoundedCornerShape(ProfessionalRadius.Medium),
                spotColor = ProfessionalColors.Shadow300
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(ProfessionalRadius.Medium)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        shape = RoundedCornerShape(ProfessionalRadius.Medium),
        color = ProfessionalColors.SurfacePrimary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ProfessionalSpacing.Space5),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon/Logo Container
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(ProfessionalRadius.Medium))
                    .background(ProfessionalColors.Neutral50)
                    .border(
                        width = 1.dp,
                        color = ProfessionalColors.BorderDefault,
                        shape = RoundedCornerShape(ProfessionalRadius.Medium)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (brandDrawable != null) {
                    Image(
                        painter = painterResource(id = brandDrawable),
                        contentDescription = item.title,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        imageVector = BrandIconHelper.getCategoryFallbackIcon(item.category),
                        contentDescription = item.title,
                        tint = ProfessionalColors.PrimaryBlue600,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(ProfessionalSpacing.Space4))
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(ProfessionalSpacing.Space1)
            ) {
                // Title Row with Favorite
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ProfessionalSpacing.Space2)
                ) {
                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ProfessionalColors.Neutral900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    
                    if (item.isFavorite) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorite",
                            tint = ProfessionalColors.AccentGold500,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                
                // Subtitle Row with Category Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ProfessionalSpacing.Space2)
                ) {
                    // Category Badge
                    Surface(
                        shape = RoundedCornerShape(ProfessionalRadius.XSmall),
                        color = ProfessionalColors.PrimaryBlue50
                    ) {
                        Text(
                            text = item.category.title,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = ProfessionalColors.PrimaryBlue700,
                            modifier = Modifier.padding(
                                horizontal = ProfessionalSpacing.Space2,
                                vertical = 2.dp
                            )
                        )
                    }
                    
                    // Username/URL
                    Text(
                        text = item.username.ifBlank { 
                            item.payload.url.ifBlank { "—" } 
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = ProfessionalColors.Neutral600,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(ProfessionalSpacing.Space3))
            
            // Action Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(ProfessionalSpacing.Space1)
            ) {
                // Copy Button
                IconButton(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onCopyClick()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = ProfessionalColors.Neutral600,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Favorite Button
                IconButton(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onFavoriteClick()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = if (item.isFavorite) "Remove Favorite" else "Add Favorite",
                        tint = if (item.isFavorite) ProfessionalColors.AccentGold500 else ProfessionalColors.Neutral600,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Professional Security Health Card - Dashboard component
 */
@Composable
fun ProfessionalSecurityHealthCard(
    healthScore: Int,
    weakPasswords: Int,
    reusedPasswords: Int,
    oldPasswords: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(ProfessionalTiming.Fast),
        label = "scale"
    )
    
    val healthColor = when {
        healthScore >= 80 -> ProfessionalColors.Success600
        healthScore >= 60 -> ProfessionalColors.Warning600
        else -> ProfessionalColors.Error600
    }
    
    val healthStatus = when {
        healthScore >= 80 -> "Excellent"
        healthScore >= 60 -> "Good"
        else -> "Needs Attention"
    }
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .shadow(
                elevation = ProfessionalElevation.Level2,
                shape = RoundedCornerShape(ProfessionalRadius.Large),
                spotColor = ProfessionalColors.Shadow300
            )
            .border(
                width = 1.dp,
                color = ProfessionalColors.BorderDefault,
                shape = RoundedCornerShape(ProfessionalRadius.Large)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        shape = RoundedCornerShape(ProfessionalRadius.Large),
        color = ProfessionalColors.SurfacePrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ProfessionalSpacing.Space6)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Security Health",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ProfessionalColors.Neutral900
                    )
                    Spacer(modifier = Modifier.height(ProfessionalSpacing.Space1))
                    Text(
                        text = "Vault Analysis",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = ProfessionalColors.Neutral600
                    )
                }
                
                // Status Badge
                Surface(
                    shape = RoundedCornerShape(ProfessionalRadius.Small),
                    color = healthColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = healthStatus,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = healthColor,
                        modifier = Modifier.padding(
                            horizontal = ProfessionalSpacing.Space3,
                            vertical = ProfessionalSpacing.Space2
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(ProfessionalSpacing.Space6))
            
            // Score Display
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(ProfessionalSpacing.Space2)
            ) {
                Text(
                    text = "$healthScore",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = healthColor
                )
                Text(
                    text = "/ 100",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = ProfessionalColors.Neutral600,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(ProfessionalSpacing.Space5))
            
            // Issues Summary
            Column(
                verticalArrangement = Arrangement.spacedBy(ProfessionalSpacing.Space3)
            ) {
                if (weakPasswords > 0) {
                    IssueRow(
                        label = "Weak passwords",
                        count = weakPasswords,
                        color = ProfessionalColors.Error600
                    )
                }
                if (reusedPasswords > 0) {
                    IssueRow(
                        label = "Reused passwords",
                        count = reusedPasswords,
                        color = ProfessionalColors.Warning600
                    )
                }
                if (oldPasswords > 0) {
                    IssueRow(
                        label = "Old passwords",
                        count = oldPasswords,
                        color = ProfessionalColors.Info600
                    )
                }
            }
        }
    }
}

@Composable
private fun IssueRow(
    label: String,
    count: Int,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ProfessionalSpacing.Space2)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = ProfessionalColors.Neutral700
            )
        }
        
        Surface(
            shape = RoundedCornerShape(ProfessionalRadius.XSmall),
            color = color.copy(alpha = 0.1f)
        ) {
            Text(
                text = "$count",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = color,
                modifier = Modifier.padding(
                    horizontal = ProfessionalSpacing.Space3,
                    vertical = 2.dp
                )
            )
        }
    }
}
