package com.example.adere.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalThemeAccents
import kotlinx.coroutines.delay

@Composable
fun PressableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hapticFeedback: Boolean = true,
    content: @Composable () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "pressable_card_scale"
    )
    
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2.dp.value else 6.dp.value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pressable_card_elevation"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            if (hapticFeedback) {
                                haptic.performHapticFeedback(
                                    androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                                )
                            }
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = { onClick() }
                    )
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        content = { content() }
    )
}

@Composable
fun BounceButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    color: Color = MaterialTheme.colorScheme.primary,
    enabled: Boolean = true
) {
    val hapticFeedback = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "bounce_button_scale"
    )
    
    val bounce = remember { Animatable(1f) }
    
    LaunchedEffect(isPressed) {
        if (!isPressed) {
            bounce.animateTo(1.1f, animationSpec = tween(100))
            bounce.animateTo(1f, animationSpec = tween(100))
        }
    }

    Surface(
        modifier = modifier
            .scale(scale * bounce.value)
            .clip(CircleShape)
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                hapticFeedback.performHapticFeedback(
                    androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                )
                onClick()
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        shape = CircleShape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            color.copy(alpha = if (enabled) 1f else 0.5f),
                            color.copy(alpha = if (enabled) 0.8f else 0.4f)
                        )
                    )
                )
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun RotatingIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    rotationDegrees: Float = 180f,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    
    LaunchedEffect(Unit) {
        // Initial entrance animation
        rotation.animateTo(360f, animationSpec = tween(600))
        rotation.snapTo(0f)
    }

    IconButton(
        onClick = {
            onClick()
        },
        modifier = modifier
            .scale(scale.value)
            .rotate(rotation.value)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
    
    LaunchedEffect(onClick) {
        scale.animateTo(1.2f, animationSpec = tween(150))
        rotation.animateTo(rotationDegrees, animationSpec = tween(300))
        scale.animateTo(1f, animationSpec = tween(150))
        rotation.snapTo(0f)
    }
}

@Composable
fun PulsingIndicator(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF10B981),
    size: Int = 12
) {
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(0.8f) }
    
    LaunchedEffect(isActive) {
        if (isActive) {
            while (isActive) {
                scale.animateTo(1.3f, animationSpec = tween(800))
                alpha.animateTo(0.3f, animationSpec = tween(800))
                scale.animateTo(1f, animationSpec = tween(800))
                alpha.animateTo(0.8f, animationSpec = tween(800))
            }
        } else {
            scale.snapTo(1f)
            alpha.snapTo(0.8f)
        }
    }

    Box(
        modifier = modifier
            .size(size.dp)
            .scale(scale.value)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha.value))
    )
}

@Composable
fun SwipeRevealActions(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SwipeActionButton(
            icon = Icons.Default.Edit,
            label = "Edit",
            color = Color(0xFF3B82F6),
            onClick = onEdit
        )
        
        SwipeActionButton(
            icon = Icons.Default.Share,
            label = "Share",
            color = Color(0xFF10B981),
            onClick = onShare
        )
        
        SwipeActionButton(
            icon = Icons.Default.Delete,
            label = "Delete",
            color = Color(0xFFEF4444),
            onClick = onDelete
        )
    }
}

@Composable
private fun SwipeActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "swipe_action_scale"
    )

    Column(
        modifier = modifier
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
                    onTap = { onClick() }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = color,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun QuickActionGrid(
    actions: List<QuickAction>,
    modifier: Modifier = Modifier,
    columns: Int = 2
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        actions.chunked(columns).forEach { rowActions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowActions.forEach { action ->
                    QuickActionCard(
                        action = action,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Fill empty spaces
                repeat(columns - rowActions.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    action: QuickAction,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "quick_action_scale"
    )

    Card(
        modifier = modifier
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
                    onTap = { action.onClick() }
                )
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            action.color.copy(alpha = 0.1f),
                            action.color.copy(alpha = 0.05f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = action.color.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(action.color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.label,
                        tint = action.color,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Text(
                    text = action.label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}

data class QuickAction(
    val icon: ImageVector,
    val label: String,
    val color: Color,
    val onClick: () -> Unit
)
