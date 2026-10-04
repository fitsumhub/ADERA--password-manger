package com.example.adere.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.ui.theme.LocalThemeAccents
import kotlinx.coroutines.delay

/**
 * Global spacing constants for consistent UI
 */
object AppSpacing {
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
    val huge = 48.dp
}

/**
 * Global corner radius constants
 */
object AppRadius {
    val small = 8.dp
    val medium = 12.dp
    val large = 16.dp
    val extraLarge = 20.dp
    val huge = 24.dp
}

/**
 * Global elevation constants
 */
object AppElevation {
    val none = 0.dp
    val small = 2.dp
    val medium = 4.dp
    val large = 8.dp
    val extraLarge = 12.dp
}

/**
 * Pressable modifier with scale animation
 */
fun Modifier.pressableScale(
    targetScale: Float = 0.96f,
    onPress: (() -> Unit)? = null
): Modifier {
    // Note: This would require remember/state which can't be in a Modifier function
    // This is a placeholder for the concept - actual implementation would be a composable
    return this
}

/**
 * Toast-style notification popup
 */
@Composable
fun ToastNotification(
    message: String,
    onDismiss: () -> Unit,
    isSuccess: Boolean = true,
    durationMillis: Long = 2000L
) {
    val themeAccents = LocalThemeAccents.current
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.8f) }
    
    LaunchedEffect(Unit) {
        // Fade in
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        
        // Wait
        delay(durationMillis)
        
        // Fade out
        alpha.animateTo(targetValue = 0f)
        onDismiss()
    }
    
    val backgroundColor = if (isSuccess) themeAccents.success else themeAccents.error
    
    Popup(
        alignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .padding(top = 16.dp)
                .alpha(alpha.value)
                .scale(scale.value)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            backgroundColor.copy(alpha = 0.95f),
                            backgroundColor.copy(alpha = 0.9f)
                        )
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                color = Color.White
            )
        }
    }
}

/**
 * Haptic feedback wrapper for clickable items
 */
@Composable
fun Modifier.hapticClickable(
    onClick: () -> Unit
): Modifier {
    val hapticFeedback = LocalHapticFeedback.current
    
    return this.pointerInput(Unit) {
        detectTapGestures(
            onTap = {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
        )
    }
}

/**
 * Divider with gradient effect
 */
@Composable
fun GradientDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        color.copy(alpha = 0.3f),
                        color.copy(alpha = 0.5f),
                        color.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                )
            )
    )
}

/**
 * Section header for lists
 */
@Composable
fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val themeAccents = LocalThemeAccents.current
    val hapticFeedback = LocalHapticFeedback.current
    
    Box(
        modifier = modifier
            .padding(horizontal = AppSpacing.medium, vertical = AppSpacing.small)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = themeAccents.primary,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onActionClick()
                        }
                    }
            )
        }
    }
}

/**
 * Animated badge for notifications/counts
 */
@Composable
fun AnimatedBadge(
    count: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null
) {
    val themeAccents = LocalThemeAccents.current
    val scale = remember { Animatable(0.5f) }
    
    LaunchedEffect(count) {
        if (count > 0) {
            scale.animateTo(
                targetValue = 1.2f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }
    
    if (count > 0) {
        Box(
            modifier = modifier
                .scale(scale.value)
                .background(
                    color = backgroundColor ?: themeAccents.error,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (count > 99) "99+" else count.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = Color.White
            )
        }
    }
}

/**
 * Shimmer effect colors for loading states
 */
object ShimmerColors {
    @Composable
    fun getColors(): List<Color> {
        return listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    }
}

/**
 * Standard card elevation
 */
object CardElevation {
    val default = 3.dp
    val pressed = 1.dp
    val elevated = 6.dp
}

/**
 * Animation durations
 */
object AnimationDuration {
    const val fast = 150
    const val normal = 300
    const val slow = 500
}

/**
 * Typography extensions
 */
object TextStyles {
    @Composable
    fun sectionTitle() = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    )
    
    @Composable
    fun cardTitle() = MaterialTheme.typography.titleSmall.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
    
    @Composable
    fun body() = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp
    )
    
    @Composable
    fun caption() = MaterialTheme.typography.bodySmall.copy(
        fontSize = 12.sp
    )
}
