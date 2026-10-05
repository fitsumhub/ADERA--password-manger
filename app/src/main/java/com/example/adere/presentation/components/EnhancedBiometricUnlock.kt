package com.example.adere.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalThemeAccents
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class BiometricState {
    Idle,
    Scanning,
    Success,
    Error,
    Unavailable
}

@Composable
fun EnhancedBiometricUnlockScreen(
    biometricState: BiometricState,
    onBiometricClick: () -> Unit,
    onPasswordClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Unlock Your Vault",
    subtitle: String = "Use your fingerprint or face to access your secure vault"
) {
    val themeAccents = LocalThemeAccents.current
    val hapticFeedback = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header Section
        BiometricHeader(
            title = title,
            subtitle = subtitle
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Main Biometric Animation
        AnimatedBiometricIcon(
            state = biometricState,
            onClick = onBiometricClick,
            modifier = Modifier.size(180.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // State Message
        BiometricStateMessage(
            state = biometricState,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Action Buttons
        BiometricActionButtons(
            biometricState = biometricState,
            onBiometricClick = {
                hapticFeedback.performHapticFeedback(
                    androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                )
                onBiometricClick()
            },
            onPasswordClick = {
                hapticFeedback.performHapticFeedback(
                    androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                )
                onPasswordClick()
            }
        )
    }
}

@Composable
private fun BiometricHeader(
    title: String,
    subtitle: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            textAlign = TextAlign.Center
        )
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AnimatedBiometricIcon(
    state: BiometricState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeAccents = LocalThemeAccents.current
    
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "biometric_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                enabled = state != BiometricState.Unavailable
            ) {
                isPressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        // Background Rings
        BiometricBackgroundRings(state = state)
        
        // Main Icon
        BiometricMainIcon(
            state = state,
            modifier = Modifier.size(80.dp)
        )
        
        // Scanning Effect Overlay
        if (state == BiometricState.Scanning) {
            BiometricScanningEffect(
                modifier = Modifier.size(140.dp)
            )
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
}

@Composable
private fun BiometricBackgroundRings(
    state: BiometricState,
    modifier: Modifier = Modifier
) {
    val themeAccents = LocalThemeAccents.current
    
    val ringColors = when (state) {
        BiometricState.Success -> listOf(
            Color(0xFF10B981).copy(alpha = 0.3f),
            Color(0xFF10B981).copy(alpha = 0.2f),
            Color(0xFF10B981).copy(alpha = 0.1f)
        )
        BiometricState.Error -> listOf(
            Color(0xFFEF4444).copy(alpha = 0.3f),
            Color(0xFFEF4444).copy(alpha = 0.2f),
            Color(0xFFEF4444).copy(alpha = 0.1f)
        )
        BiometricState.Unavailable -> listOf(
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f)
        )
        else -> listOf(
            themeAccents.primary.copy(alpha = 0.3f),
            themeAccents.primary.copy(alpha = 0.2f),
            themeAccents.primary.copy(alpha = 0.1f)
        )
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "rings")
    val animatedRadius by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring_radius"
    )

    Canvas(modifier = modifier.size(180.dp)) {
        val center = size.center
        val baseRadius = size.minDimension / 4
        
        ringColors.forEachIndexed { index, color ->
            val radius = baseRadius * (1.5f + index * 0.3f) * animatedRadius
            
            drawCircle(
                color = color,
                radius = radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

@Composable
private fun BiometricMainIcon(
    state: BiometricState,
    modifier: Modifier = Modifier
) {
    val themeAccents = LocalThemeAccents.current
    
    val iconColor = when (state) {
        BiometricState.Success -> Color(0xFF10B981)
        BiometricState.Error -> Color(0xFFEF4444)
        BiometricState.Unavailable -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> themeAccents.primary
    }
    
    val backgroundColor = when (state) {
        BiometricState.Success -> Color(0xFF10B981).copy(alpha = 0.15f)
        BiometricState.Error -> Color(0xFFEF4444).copy(alpha = 0.15f)
        BiometricState.Unavailable -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
        else -> themeAccents.primary.copy(alpha = 0.15f)
    }
    
    val rotation = remember { Animatable(0f) }
    
    LaunchedEffect(state) {
        when (state) {
            BiometricState.Success -> {
                rotation.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(600)
                )
                rotation.snapTo(0f)
            }
            BiometricState.Error -> {
                repeat(3) {
                    rotation.animateTo(10f, animationSpec = tween(100))
                    rotation.animateTo(-10f, animationSpec = tween(100))
                }
                rotation.animateTo(0f, animationSpec = tween(100))
            }
            else -> {}
        }
    }

    Box(
        modifier = modifier
            .rotate(rotation.value)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = iconColor.copy(alpha = 0.3f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = when (state) {
                BiometricState.Unavailable -> Icons.Default.Lock
                else -> Icons.Default.Fingerprint
            },
            contentDescription = "Biometric unlock",
            tint = iconColor,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
private fun BiometricScanningEffect(
    modifier: Modifier = Modifier
) {
    val themeAccents = LocalThemeAccents.current
    
    val infiniteTransition = rememberInfiniteTransition(label = "scanning")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "scanning_rotation"
    )
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanning_pulse"
    )

    Canvas(modifier = modifier) {
        val center = size.center
        val radius = size.minDimension / 3 * pulseScale
        
        rotate(rotation, center) {
            // Scanning line
            val lineLength = radius * 0.8f
            drawLine(
                color = themeAccents.primary.copy(alpha = 0.8f),
                start = Offset(center.x, center.y - lineLength),
                end = Offset(center.x, center.y + lineLength),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
            
            // Scanning dots
            for (i in 0 until 8) {
                val angle = (i * 45f) * (PI / 180f)
                val dotRadius = radius * 0.9f
                val x = center.x + cos(angle).toFloat() * dotRadius
                val y = center.y + sin(angle).toFloat() * dotRadius
                
                drawCircle(
                    color = themeAccents.primary.copy(alpha = 0.6f),
                    radius = 3.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
    }
}

@Composable
private fun BiometricStateMessage(
    state: BiometricState,
    modifier: Modifier = Modifier
) {
    val (message, color) = when (state) {
        BiometricState.Idle -> "Touch the fingerprint sensor" to MaterialTheme.colorScheme.onSurfaceVariant
        BiometricState.Scanning -> "Scanning..." to MaterialTheme.colorScheme.primary
        BiometricState.Success -> "Authentication successful!" to Color(0xFF10B981)
        BiometricState.Error -> "Authentication failed. Please try again." to Color(0xFFEF4444)
        BiometricState.Unavailable -> "Biometric authentication is not available" to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier,
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = color,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
private fun BiometricActionButtons(
    biometricState: BiometricState,
    onBiometricClick: () -> Unit,
    onPasswordClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Primary biometric button
        if (biometricState != BiometricState.Unavailable) {
            Button(
                onClick = onBiometricClick,
                enabled = biometricState == BiometricState.Idle || biometricState == BiometricState.Error,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = when (biometricState) {
                            BiometricState.Scanning -> "Scanning..."
                            BiometricState.Error -> "Try Again"
                            else -> "Use Biometric"
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
        
        // Secondary password button
        OutlinedButton(
            onClick = onPasswordClick,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Use Master Password",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}
