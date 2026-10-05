package com.example.adere.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.adere.domain.model.VaultItem
import com.example.ui.theme.LocalThemeAccents
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * Enhanced vault list with smooth animations, swipe actions, and modern design
 */
@Composable
fun EnhancedVaultList(
    items: List<VaultItem>,
    modifier: Modifier = Modifier,
    onItemClick: (VaultItem) -> Unit,
    onToggleFavorite: (VaultItem) -> Unit,
    onQuickCopy: (VaultItem) -> Unit,
    onDelete: ((VaultItem) -> Unit)? = null,
    showAnimations: Boolean = true
) {
    val themeAccents = LocalThemeAccents.current
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.id }
        ) { index, item ->
            EnhancedVaultItemCard(
                item = item,
                onClick = { onItemClick(item) },
                onToggleFavorite = { onToggleFavorite(item) },
                onQuickCopy = { onQuickCopy(item) },
                onDelete = if (onDelete != null) {
                    { onDelete(item) }
                } else null,
                animationDelay = if (showAnimations) index * 50L else 0L,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("enhanced_vault_item_${item.id}")
            )
        }
    }
}

/**
 * Enhanced vault item card with swipe actions and animations
 */
@Composable
fun EnhancedVaultItemCard(
    item: VaultItem,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onQuickCopy: () -> Unit,
    onDelete: (() -> Unit)? = null,
    animationDelay: Long = 0L,
    modifier: Modifier = Modifier
) {
    val themeAccents = LocalThemeAccents.current
    val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    
    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var isRevealed by remember { mutableStateOf(false) }
    
    // Animate entrance
    LaunchedEffect(Unit) {
        delay(animationDelay)
        isVisible = true
    }
    
    // Animated values
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else if (isRevealed) 8.dp else 4.dp,
        animationSpec = tween(200),
        label = "elevation"
    )
    
    val brandDrawable = remember(item.title, item.payload.url) {
        BrandIconHelper.resolveBrandDrawable(item.title, item.payload.url)
    }
    
    val swipeThreshold = with(density) { 100.dp.toPx() }
    val maxSwipe = with(density) { 180.dp.toPx() }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + slideInHorizontally(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            initialOffsetX = { it / 2 }
        ) + expandVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        exit = fadeOut() + shrinkVertically()
    ) {
        Box(
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            // Swipe action background
            if (offsetX < -swipeThreshold) {
                SwipeActionBackground(
                    offsetX = offsetX,
                    maxSwipe = maxSwipe,
                    onCopyClick = {
                        onQuickCopy()
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        coroutineScope.launch {
                            offsetX = 0f
                        }
                    },
                    onDeleteClick = onDelete?.let {
                        {
                            it()
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    }
                )
            }
            
            // Main card content
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(offsetX.roundToInt(), 0) }
                    .shadow(elevation, RoundedCornerShape(8.dp))
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                isPressed = true
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            onDragEnd = {
                                isPressed = false
                                coroutineScope.launch {
                                    if (offsetX < -swipeThreshold) {
                                        // Snap to revealed state
                                        val targetOffset = -maxSwipe
                                        val animatable = Animatable(offsetX)
                                        animatable.animateTo(
                                            targetValue = targetOffset,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMedium
                                            )
                                        ) {
                                            offsetX = value
                                        }
                                        isRevealed = true
                                    } else {
                                        // Snap back
                                        val animatable = Animatable(offsetX)
                                        animatable.animateTo(
                                            targetValue = 0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessMedium
                                            )
                                        ) {
                                            offsetX = value
                                        }
                                        isRevealed = false
                                    }
                                }
                            },
                            onDragCancel = {
                                isPressed = false
                                coroutineScope.launch {
                                    val animatable = Animatable(offsetX)
                                    animatable.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        )
                                    ) {
                                        offsetX = value
                                    }
                                    isRevealed = false
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                offsetX = (offsetX + dragAmount).coerceIn(-maxSwipe, 0f)
                            }
                        )
                    }
                    .clickable(
                        enabled = offsetX == 0f,
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onClick()
                        }
                    ),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                                )
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon/Logo
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            themeAccents.primary.copy(alpha = 0.12f),
                                            themeAccents.primary.copy(alpha = 0.06f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (brandDrawable != null) {
                                Image(
                                    painter = painterResource(id = brandDrawable),
                                    contentDescription = item.title,
                                    modifier = Modifier.size(32.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = BrandIconHelper.getCategoryFallbackIcon(item.category),
                                    contentDescription = item.title,
                                    tint = themeAccents.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Text content
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                
                                if (item.isFavorite) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Favorite",
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Category badge
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = themeAccents.primary.copy(alpha = 0.1f),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = item.category.title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 10.sp
                                        ),
                                        color = themeAccents.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                
                                Text(
                                    text = item.username.ifBlank { 
                                        item.payload.url.ifBlank { "No username" } 
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Action buttons (only visible when not swiped)
                        AnimatedVisibility(
                            visible = offsetX == 0f,
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            Row {
                                AnimatedCopyButton(
                                    onCopy = {
                                        onQuickCopy()
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    modifier = Modifier.testTag("copy_secret_${item.id}")
                                )
                                
                                Spacer(modifier = Modifier.width(4.dp))
                                
                                AnimatedFavoriteButton(
                                    isFavorite = item.isFavorite,
                                    onToggle = {
                                        onToggleFavorite()
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    modifier = Modifier.testTag("favorite_button_${item.id}")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Swipe action background with action buttons
 */
@Composable
private fun SwipeActionBackground(
    offsetX: Float,
    maxSwipe: Float,
    onCopyClick: () -> Unit,
    onDeleteClick: (() -> Unit)?
) {
    val themeAccents = LocalThemeAccents.current
    val revealProgress = (offsetX.absoluteValue / maxSwipe).coerceIn(0f, 1f)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (onDeleteClick != null) {
                        listOf(
                            themeAccents.error.copy(alpha = 0.1f * revealProgress),
                            themeAccents.info.copy(alpha = 0.15f * revealProgress)
                        )
                    } else {
                        listOf(
                            themeAccents.info.copy(alpha = 0.1f * revealProgress),
                            themeAccents.info.copy(alpha = 0.15f * revealProgress)
                        )
                    }
                )
            )
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Copy button
            SwipeActionButton(
                icon = Icons.Default.ContentCopy,
                backgroundColor = themeAccents.info,
                contentDescription = "Copy",
                alpha = revealProgress,
                onClick = onCopyClick
            )
            
            // Delete button (if provided)
            if (onDeleteClick != null) {
                SwipeActionButton(
                    icon = Icons.Default.Delete,
                    backgroundColor = themeAccents.error,
                    contentDescription = "Delete",
                    alpha = revealProgress,
                    onClick = onDeleteClick
                )
            }
        }
    }
}

/**
 * Individual swipe action button
 */
@Composable
private fun SwipeActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    contentDescription: String,
    alpha: Float,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (alpha > 0.3f) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "action_button_scale"
    )
    
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .alpha(alpha)
            .clip(CircleShape)
            .background(backgroundColor)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
