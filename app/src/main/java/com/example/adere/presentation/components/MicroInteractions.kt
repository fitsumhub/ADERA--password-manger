package com.example.adere.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.example.ui.theme.LocalThemeAccents
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

enum class CopyFeedbackType {
    SUCCESS,
    ERROR,
    WARNING
}

@Composable
fun AnimatedCopyButton(
    onCopy: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Copy",
    successMessage: String = "Copied!",
    showLabel: Boolean = true,
    size: Int = 36
) {
    val hapticFeedback = LocalHapticFeedback.current
    val themeAccents = LocalThemeAccents.current
    
    var isPressed by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "copy_button_scale"
    )
    
    val rotation = remember { Animatable(0f) }
    val iconScale = remember { Animatable(1f) }
    
    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            // Success animation sequence
            iconScale.animateTo(1.3f, animationSpec = tween(150))
            rotation.animateTo(360f, animationSpec = tween(400))
            iconScale.animateTo(1f, animationSpec = tween(150))
            rotation.snapTo(0f)
            
            delay(1500) // Show success state
            showSuccess = false
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Main button
        Surface(
            modifier = Modifier
                .size(size.dp)
                .scale(scale)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            hapticFeedback.performHapticFeedback(
                                androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                            )
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = {
                            onCopy()
                            showSuccess = true
                        }
                    )
                },
            shape = CircleShape,
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = if (showSuccess) {
                                listOf(
                                    Color(0xFF10B981).copy(alpha = 0.2f),
                                    Color(0xFF10B981).copy(alpha = 0.1f)
                                )
                            } else {
                                listOf(
                                    themeAccents.primary.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            }
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = if (showSuccess) {
                            Color(0xFF10B981).copy(alpha = 0.3f)
                        } else {
                            themeAccents.primary.copy(alpha = 0.2f)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (showSuccess) Icons.Default.Check else Icons.Default.ContentCopy,
                    contentDescription = label,
                    tint = if (showSuccess) Color(0xFF10B981) else themeAccents.primary,
                    modifier = Modifier
                        .size((size * 0.5f).dp)
                        .scale(iconScale.value)
                        .rotate(rotation.value)
                )
            }
        }
        
        // Success popup
        AnimatedVisibility(
            visible = showSuccess && showLabel,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.offset(y = (-50).dp)
        ) {
            CopySuccessPopup(
                message = successMessage,
                type = CopyFeedbackType.SUCCESS
            )
        }
    }
}

@Composable
fun AnimatedFavoriteButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 36
) {
    val hapticFeedback = LocalHapticFeedback.current
    
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "favorite_scale"
    )
    
    val heartScale = remember { Animatable(1f) }
    val heartRotation = remember { Animatable(0f) }
    
    LaunchedEffect(isFavorite) {
        if (isFavorite) {
            // Favorite animation
            heartScale.animateTo(1.4f, animationSpec = tween(200))
            heartRotation.animateTo(15f, animationSpec = tween(100))
            heartRotation.animateTo(-15f, animationSpec = tween(100))
            heartRotation.animateTo(0f, animationSpec = tween(100))
            heartScale.animateTo(1f, animationSpec = tween(200))
        }
    }

    Surface(
        modifier = modifier
            .size(size.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        hapticFeedback.performHapticFeedback(
                            androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                        )
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onToggle() }
                )
            },
        shape = CircleShape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = if (isFavorite) {
                            listOf(
                                Color(0xFFEF4444).copy(alpha = 0.2f),
                                Color(0xFFEF4444).copy(alpha = 0.1f)
                            )
                        } else {
                            listOf(
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        }
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = if (isFavorite) {
                        Color(0xFFEF4444).copy(alpha = 0.3f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                tint = if (isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size((size * 0.5f).dp)
                    .scale(heartScale.value)
                    .rotate(heartRotation.value)
            )
        }
    }
}

@Composable
fun AnimatedVisibilityToggle(
    isVisible: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 36
) {
    val hapticFeedback = LocalHapticFeedback.current
    val themeAccents = LocalThemeAccents.current
    
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "visibility_scale"
    )
    
    val iconRotation = remember { Animatable(0f) }
    
    LaunchedEffect(isVisible) {
        iconRotation.animateTo(
            targetValue = if (isVisible) 360f else -360f,
            animationSpec = tween(300)
        )
        iconRotation.snapTo(0f)
    }

    IconButton(
        onClick = {
            hapticFeedback.performHapticFeedback(
                androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
            )
            onToggle()
        },
        modifier = modifier
            .size(size.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
    ) {
        Icon(
            imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
            contentDescription = if (isVisible) "Hide" else "Show",
            tint = if (isVisible) themeAccents.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size((size * 0.6f).dp)
                .rotate(iconRotation.value)
        )
    }
}

@Composable
private fun CopySuccessPopup(
    message: String,
    type: CopyFeedbackType,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (type) {
        CopyFeedbackType.SUCCESS -> Color(0xFF10B981)
        CopyFeedbackType.ERROR -> Color(0xFFEF4444)
        CopyFeedbackType.WARNING -> Color(0xFFF59E0B)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            backgroundColor,
                            backgroundColor.copy(alpha = 0.9f)
                        )
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@Composable
fun FloatingCopyFeedback(
    isVisible: Boolean,
    message: String,
    type: CopyFeedbackType = CopyFeedbackType.SUCCESS,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { with(density) { 40.dp.roundToPx() } }
        ) + fadeIn() + scaleIn(initialScale = 0.8f),
        exit = slideOutVertically(
            targetOffsetY = { with(density) { (-40).dp.roundToPx() } }
        ) + fadeOut() + scaleOut(targetScale = 0.8f)
    ) {
        Popup(
            alignment = Alignment.BottomCenter,
            offset = IntOffset(0, with(density) { (-100).dp.roundToPx() }),
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = false)
        ) {
            CopySuccessPopup(
                message = message,
                type = type,
                modifier = modifier
            )
        }
    }
    
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(2000)
            onDismiss()
        }
    }
}

// Specialized copy button for vault items
@Composable
fun VaultItemCopyButton(
    value: String,
    label: String,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCopiedFeedback by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        AnimatedCopyButton(
            onCopy = {
                onCopy(value)
                showCopiedFeedback = true
            },
            label = "Copy $label",
            successMessage = "$label copied!",
            showLabel = false
        )
        
        FloatingCopyFeedback(
            isVisible = showCopiedFeedback,
            message = "$label copied to clipboard",
            onDismiss = { showCopiedFeedback = false }
        )
    }
}