package com.example.adere.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalThemeAccents
import kotlinx.coroutines.delay

/**
 * Empty state for vault list
 */
@Composable
fun EmptyVaultState(
    onAddItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    EnhancedEmptyState(
        icon = Icons.Default.Lock,
        title = "Your vault is empty",
        description = "Get started by adding your first password, note, or crypto wallet. All data is encrypted end-to-end.",
        primaryButtonText = "Add First Item",
        onPrimaryAction = onAddItem,
        modifier = modifier
    )
}

/**
 * Empty search results state
 */
@Composable
fun EmptySearchState(
    searchQuery: String,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    EnhancedEmptyState(
        icon = Icons.Default.Search,
        title = "No results found",
        description = "No items matched \"$searchQuery\". Try adjusting your search or browse by category.",
        primaryButtonText = "Clear Search",
        onPrimaryAction = onClearSearch,
        modifier = modifier
    )
}

/**
 * Empty category state
 */
@Composable
fun EmptyCategoryState(
    categoryName: String,
    onAddItem: () -> Unit,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    EnhancedEmptyState(
        icon = Icons.Default.Folder,
        title = "No $categoryName items",
        description = "You haven't added any items to this category yet. Add your first one or view all items.",
        primaryButtonText = "Add Item",
        secondaryButtonText = "View All",
        onPrimaryAction = onAddItem,
        onSecondaryAction = onViewAll,
        modifier = modifier
    )
}

/**
 * Generic enhanced empty state component
 */
@Composable
fun EnhancedEmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    primaryButtonText: String,
    onPrimaryAction: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryButtonText: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    accentColor: Color? = null
) {
    val themeAccents = LocalThemeAccents.current
    val hapticFeedback = LocalHapticFeedback.current
    
    var isVisible by remember { mutableStateOf(false) }
    
    val effectiveColor = accentColor ?: themeAccents.primary
    
    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + expandVertically()
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Animated icon with pulsing effect
                AnimatedIconBox(
                    icon = icon,
                    color = effectiveColor
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(28.dp))
                
                // Primary action button
                Button(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPrimaryAction()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = effectiveColor
                    ),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = primaryButtonText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                
                // Secondary action button (optional)
                if (secondaryButtonText != null && onSecondaryAction != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSecondaryAction()
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = secondaryButtonText,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Animated icon box with pulsing background
 */
@Composable
private fun AnimatedIconBox(
    icon: ImageVector,
    color: Color
) {
    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(400)
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        
        // Continuous subtle pulsing
        while (true) {
            scale.animateTo(
                targetValue = 1.05f,
                animationSpec = tween(2000)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(2000)
            )
        }
    }
    
    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale.value)
            .alpha(alpha.value)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.15f),
                        color.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            color.copy(alpha = 0.2f),
                            color.copy(alpha = 0.12f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

/**
 * Error state component
 */
@Composable
fun ErrorState(
    title: String,
    description: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    showRetryButton: Boolean = true
) {
    val themeAccents = LocalThemeAccents.current
    
    EnhancedEmptyState(
        icon = Icons.Default.Error,
        title = title,
        description = description,
        primaryButtonText = "Try Again",
        onPrimaryAction = onRetry,
        accentColor = themeAccents.error,
        modifier = modifier
    )
}

/**
 * Success state component
 */
@Composable
fun SuccessState(
    title: String,
    description: String,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String = "Continue"
) {
    val themeAccents = LocalThemeAccents.current
    
    EnhancedEmptyState(
        icon = Icons.Default.CheckCircle,
        title = title,
        description = description,
        primaryButtonText = buttonText,
        onPrimaryAction = onContinue,
        accentColor = themeAccents.success,
        modifier = modifier
    )
}

/**
 * Warning state component
 */
@Composable
fun WarningState(
    title: String,
    description: String,
    onPrimaryAction: () -> Unit,
    primaryButtonText: String = "Understood",
    modifier: Modifier = Modifier,
    onSecondaryAction: (() -> Unit)? = null,
    secondaryButtonText: String? = null
) {
    val themeAccents = LocalThemeAccents.current
    
    EnhancedEmptyState(
        icon = Icons.Default.Warning,
        title = title,
        description = description,
        primaryButtonText = primaryButtonText,
        secondaryButtonText = secondaryButtonText,
        onPrimaryAction = onPrimaryAction,
        onSecondaryAction = onSecondaryAction,
        accentColor = themeAccents.warning,
        modifier = modifier
    )
}
