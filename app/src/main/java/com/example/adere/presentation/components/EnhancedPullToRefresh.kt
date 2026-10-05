package com.example.adere.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.ui.theme.LocalThemeAccents
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

enum class PullRefreshState {
    Idle,
    Pulling,
    ReadyToRefresh,
    Refreshing,
    Success,
    Error
}

@Composable
fun EnhancedPullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    refreshThreshold: Dp = 80.dp,
    maxPullDistance: Dp = 120.dp,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current
    val themeAccents = LocalThemeAccents.current
    
    var pullDistance by remember { mutableFloatStateOf(0f) }
    var refreshState by remember { mutableStateOf(PullRefreshState.Idle) }
    
    val refreshThresholdPx = with(density) { refreshThreshold.toPx() }
    val maxPullDistancePx = with(density) { maxPullDistance.toPx() }
    
    val indicatorOffset by animateFloatAsState(
        targetValue = when (refreshState) {
            PullRefreshState.Idle -> -60f
            PullRefreshState.Pulling -> min(pullDistance * 0.7f, maxPullDistancePx * 0.7f)
            PullRefreshState.ReadyToRefresh -> refreshThresholdPx * 0.7f
            PullRefreshState.Refreshing -> refreshThresholdPx * 0.5f
            PullRefreshState.Success, PullRefreshState.Error -> refreshThresholdPx * 0.5f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "indicator_offset"
    )
    
    LaunchedEffect(isRefreshing) {
        if (isRefreshing && refreshState != PullRefreshState.Refreshing) {
            refreshState = PullRefreshState.Refreshing
        } else if (!isRefreshing && refreshState == PullRefreshState.Refreshing) {
            refreshState = PullRefreshState.Success
            delay(1000) // Show success state
            refreshState = PullRefreshState.Idle
            pullDistance = 0f
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = {
                        if (refreshState == PullRefreshState.Idle) {
                            refreshState = PullRefreshState.Pulling
                        }
                    },
                    onDragEnd = {
                        when (refreshState) {
                            PullRefreshState.ReadyToRefresh -> {
                                hapticFeedback.performHapticFeedback(
                                    androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                                )
                                onRefresh()
                                refreshState = PullRefreshState.Refreshing
                            }
                            PullRefreshState.Pulling -> {
                                refreshState = PullRefreshState.Idle
                                pullDistance = 0f
                            }
                            else -> {}
                        }
                    }
                ) { _, dragAmount ->
                    if (refreshState == PullRefreshState.Pulling || refreshState == PullRefreshState.ReadyToRefresh) {
                        pullDistance = (pullDistance + dragAmount * 0.5f).coerceAtLeast(0f)
                        
                        if (pullDistance >= refreshThresholdPx && refreshState == PullRefreshState.Pulling) {
                            refreshState = PullRefreshState.ReadyToRefresh
                            hapticFeedback.performHapticFeedback(
                                androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                            )
                        } else if (pullDistance < refreshThresholdPx && refreshState == PullRefreshState.ReadyToRefresh) {
                            refreshState = PullRefreshState.Pulling
                        }
                    }
                }
            }
    ) {
        // Refresh indicator
        PullRefreshIndicator(
            state = refreshState,
            progress = pullDistance / refreshThresholdPx,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = with(density) { indicatorOffset.toDp() })
                .zIndex(1f)
        )
        
        // Content with offset during pull
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = with(density) { (pullDistance * 0.3f).toDp() })
        ) {
            content()
        }
    }
}

@Composable
private fun PullRefreshIndicator(
    state: PullRefreshState,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val themeAccents = LocalThemeAccents.current
    
    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(0f) }
    
    LaunchedEffect(state) {
        when (state) {
            PullRefreshState.Idle -> {
                scale.animateTo(0f, animationSpec = tween(300))
            }
            PullRefreshState.Pulling, PullRefreshState.ReadyToRefresh -> {
                scale.animateTo(1f, animationSpec = tween(300))
            }
            PullRefreshState.Refreshing -> {
                scale.animateTo(1f, animationSpec = tween(200))
                while (state == PullRefreshState.Refreshing) {
                    rotation.animateTo(360f, animationSpec = tween(1000, easing = LinearEasing))
                    rotation.snapTo(0f)
                }
            }
            PullRefreshState.Success -> {
                rotation.animateTo(0f, animationSpec = tween(200))
            }
            PullRefreshState.Error -> {
                rotation.animateTo(0f, animationSpec = tween(200))
            }
        }
    }

    Card(
        modifier = modifier
            .size(56.dp)
            .scale(scale.value),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        )
                    ),
                    shape = CircleShape
                )
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                PullRefreshState.Pulling, PullRefreshState.ReadyToRefresh -> {
                    CircularProgressRing(
                        progress = progress.coerceIn(0f, 1f),
                        color = if (state == PullRefreshState.ReadyToRefresh) {
                            themeAccents.primary
                        } else {
                            themeAccents.primary.copy(alpha = 0.6f)
                        }
                    )
                }
                PullRefreshState.Refreshing -> {
                    LoadingSpinner(
                        modifier = Modifier.rotate(rotation.value),
                        color = themeAccents.primary
                    )
                }
                PullRefreshState.Success -> {
                    SuccessCheckmark(color = Color(0xFF10B981))
                }
                PullRefreshState.Error -> {
                    ErrorIcon(color = Color(0xFFEF4444))
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun CircularProgressRing(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.size(32.dp)
    ) {
        drawCircularProgressRing(
            progress = progress,
            color = color,
            strokeWidth = 3.dp.toPx()
        )
    }
}

private fun DrawScope.drawCircularProgressRing(
    progress: Float,
    color: Color,
    strokeWidth: Float
) {
    val center = Offset(size.width / 2, size.height / 2)
    val radius = (size.minDimension / 2) - strokeWidth / 2
    
    // Background circle
    drawCircle(
        color = color.copy(alpha = 0.2f),
        radius = radius,
        center = center,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
    
    // Progress arc
    if (progress > 0f) {
        val sweepAngle = 360f * progress
        val startAngle = -90f
        
        val path = Path().apply {
            addArc(
                oval = androidx.compose.ui.geometry.Rect(
                    center = center,
                    radius = radius
                ),
                startAngleDegrees = startAngle,
                sweepAngleDegrees = sweepAngle
            )
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun LoadingSpinner(
    modifier: Modifier = Modifier,
    color: Color
) {
    Canvas(
        modifier = modifier.size(24.dp)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 4
        
        for (i in 0..7) {
            val angle = (i * 45f) * (PI / 180f)
            val alpha = (8 - i) / 8f
            
            val startX = center.x + cos(angle).toFloat() * radius * 0.5f
            val startY = center.y + sin(angle).toFloat() * radius * 0.5f
            val endX = center.x + cos(angle).toFloat() * radius
            val endY = center.y + sin(angle).toFloat() * radius
            
            drawLine(
                color = color.copy(alpha = alpha),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun SuccessCheckmark(
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500)
        )
    }
    
    Canvas(
        modifier = modifier.size(24.dp)
    ) {
        val strokeWidth = 3.dp.toPx()
        val checkPath = Path().apply {
            moveTo(size.width * 0.2f, size.height * 0.5f)
            lineTo(size.width * 0.45f, size.height * 0.7f)
            lineTo(size.width * 0.8f, size.height * 0.3f)
        }
        
        val bnds = checkPath.getBounds()
        drawPath(
            path = checkPath,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                    intervals = floatArrayOf(
                        bnds.width * progress.value,
                        bnds.width
                    )
                )
            )
        )
    }
}

@Composable
private fun ErrorIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.size(24.dp)
    ) {
        val strokeWidth = 3.dp.toPx()
        val iconSize = size.minDimension * 0.6f
        val center = Offset(size.width / 2, size.height / 2)
        
        // Draw X
        drawLine(
            color = color,
            start = Offset(center.x - iconSize / 2, center.y - iconSize / 2),
            end = Offset(center.x + iconSize / 2, center.y + iconSize / 2),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        
        drawLine(
            color = color,
            start = Offset(center.x + iconSize / 2, center.y - iconSize / 2),
            end = Offset(center.x - iconSize / 2, center.y + iconSize / 2),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
