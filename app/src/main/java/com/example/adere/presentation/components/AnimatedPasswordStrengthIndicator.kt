package com.example.adere.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalThemeAccents
import kotlinx.coroutines.delay
import java.util.regex.Pattern

enum class PasswordStrength(
    val level: Int,
    val label: String,
    val color: Color,
    val description: String
) {
    VERY_WEAK(1, "Very Weak", Color(0xFFEF4444), "Easily guessable"),
    WEAK(2, "Weak", Color(0xFFF97316), "Add more characters"),
    FAIR(3, "Fair", Color(0xFFF59E0B), "Consider improvements"), 
    GOOD(4, "Good", Color(0xFF10B981), "Strong password"),
    EXCELLENT(5, "Excellent", Color(0xFF059669), "Very secure")
}

data class PasswordCriteria(
    val hasMinLength: Boolean = false,
    val hasUppercase: Boolean = false,
    val hasLowercase: Boolean = false,
    val hasNumbers: Boolean = false,
    val hasSpecialChars: Boolean = false,
    val hasNoRepeating: Boolean = false,
    val hasNoCommonPatterns: Boolean = false
)

@Composable
fun AnimatedPasswordStrengthIndicator(
    password: String,
    modifier: Modifier = Modifier,
    showDetailedCriteria: Boolean = true,
    animateOnAppear: Boolean = true
) {
    val themeAccents = LocalThemeAccents.current
    
    val criteria = remember(password) { calculatePasswordCriteria(password) }
    val strength = remember(password) { calculatePasswordStrength(password, criteria) }
    
    val progressAnimation = remember { Animatable(0f) }
    val scaleAnimation = remember { Animatable(if (animateOnAppear) 0f else 1f) }
    
    LaunchedEffect(strength) {
        if (animateOnAppear && scaleAnimation.value == 0f) {
            scaleAnimation.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
        
        progressAnimation.animateTo(
            targetValue = strength.level / 5f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }
    
    Card(
        modifier = modifier.scale(scaleAnimation.value),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Main strength indicator
            PasswordStrengthHeader(
                strength = strength,
                progress = progressAnimation.value
            )
            
            if (showDetailedCriteria && password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Criteria checklist
                PasswordCriteriaList(
                    criteria = criteria,
                    strength = strength
                )
            }
        }
    }
}

@Composable
private fun PasswordStrengthHeader(
    strength: PasswordStrength,
    progress: Float
) {
    val themeAccents = LocalThemeAccents.current
    
    val strengthColor by animateColorAsState(
        targetValue = strength.color,
        animationSpec = tween(durationMillis = 400),
        label = "strength_color"
    )
    
    val progressBarColor by animateColorAsState(
        targetValue = strength.color,
        animationSpec = tween(durationMillis = 400),
        label = "progress_color"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Password Strength",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = strength.label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = strengthColor,
                    fontSize = 16.sp
                )
            )
        }
        
        // Animated strength indicator circle
        AnimatedStrengthIndicator(
            strength = strength,
            progress = progress
        )
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    // Progress bar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            progressBarColor.copy(alpha = 0.7f),
                            progressBarColor
                        )
                    )
                )
        )
    }
    
    if (strength != PasswordStrength.VERY_WEAK) {
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = strength.description,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        )
    }
}
@Composable
private fun AnimatedStrengthIndicator(
    strength: PasswordStrength,
    progress: Float
) {
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    
    LaunchedEffect(strength) {
        // Pulse animation on strength change
        scale.animateTo(1.2f, animationSpec = tween(150))
        scale.animateTo(1f, animationSpec = tween(150))
        
        // Slight rotation for visual interest
        rotation.animateTo(360f, animationSpec = tween(500))
        rotation.snapTo(0f)
    }
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .scale(scale.value),
        contentAlignment = Alignment.Center
    ) {
        // Background circle
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            strength.color.copy(alpha = 0.2f),
                            strength.color.copy(alpha = 0.1f)
                        )
                    )
                )
                .border(2.dp, strength.color.copy(alpha = 0.3f), CircleShape)
        )
        
        // Strength level indicator
        val icon = when (strength) {
            PasswordStrength.VERY_WEAK, PasswordStrength.WEAK -> Icons.Default.Close
            PasswordStrength.FAIR -> Icons.Default.Warning
            PasswordStrength.GOOD, PasswordStrength.EXCELLENT -> Icons.Default.Check
        }
        
        Icon(
            imageVector = icon,
            contentDescription = "Strength: ${strength.label}",
            tint = strength.color,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun PasswordCriteriaList(
    criteria: PasswordCriteria,
    strength: PasswordStrength
) {
    val criteriaItems = listOf(
        "At least 8 characters" to criteria.hasMinLength,
        "Uppercase letter (A-Z)" to criteria.hasUppercase,
        "Lowercase letter (a-z)" to criteria.hasLowercase,
        "Number (0-9)" to criteria.hasNumbers,
        "Special character (!@#$%)" to criteria.hasSpecialChars,
        "No repeating patterns" to criteria.hasNoRepeating,
        "No common patterns" to criteria.hasNoCommonPatterns
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        criteriaItems.forEachIndexed { index, (criteriaText, isMet) ->
            AnimatedCriteriaItem(
                text = criteriaText,
                isMet = isMet,
                animationDelay = index * 50L
            )
        }
    }
}

@Composable
private fun AnimatedCriteriaItem(
    text: String,
    isMet: Boolean,
    animationDelay: Long = 0L
) {
    val scale = remember { Animatable(0f) }
    val checkScale = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        delay(animationDelay)
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }
    
    LaunchedEffect(isMet) {
        if (isMet) {
            checkScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )
        } else {
            checkScale.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )
        }
    }
    
    val textColor by animateColorAsState(
        targetValue = if (isMet) {
            Color(0xFF10B981)
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(300),
        label = "criteria_text_color"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (isMet) {
            Color(0xFF10B981)
        } else {
            MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(300),
        label = "criteria_icon_color"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale.value),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(
                    if (isMet) iconColor.copy(alpha = 0.2f) 
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                )
                .border(
                    width = 1.dp,
                    color = iconColor.copy(alpha = 0.5f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isMet) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Met",
                    tint = iconColor,
                    modifier = Modifier
                        .size(10.dp)
                        .scale(checkScale.value)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = textColor,
                fontSize = 12.sp,
                fontWeight = if (isMet) FontWeight.Medium else FontWeight.Normal
            )
        )
    }
}

// Utility functions for password analysis
private fun calculatePasswordCriteria(password: String): PasswordCriteria {
    return PasswordCriteria(
        hasMinLength = password.length >= 8,
        hasUppercase = password.any { it.isUpperCase() },
        hasLowercase = password.any { it.isLowerCase() },
        hasNumbers = password.any { it.isDigit() },
        hasSpecialChars = password.any { !it.isLetterOrDigit() },
        hasNoRepeating = !hasRepeatingChars(password),
        hasNoCommonPatterns = !hasCommonPatterns(password)
    )
}

private fun calculatePasswordStrength(password: String, criteria: PasswordCriteria): PasswordStrength {
    if (password.isEmpty()) return PasswordStrength.VERY_WEAK
    
    val score = listOf(
        criteria.hasMinLength,
        criteria.hasUppercase,
        criteria.hasLowercase,
        criteria.hasNumbers,
        criteria.hasSpecialChars,
        criteria.hasNoRepeating,
        criteria.hasNoCommonPatterns
    ).count { it }
    
    return when (score) {
        0, 1 -> PasswordStrength.VERY_WEAK
        2, 3 -> PasswordStrength.WEAK
        4 -> PasswordStrength.FAIR
        5, 6 -> PasswordStrength.GOOD
        7 -> PasswordStrength.EXCELLENT
        else -> PasswordStrength.VERY_WEAK
    }
}

private fun hasRepeatingChars(password: String): Boolean {
    if (password.length < 3) return false
    
    for (i in 0..password.length - 3) {
        val substring = password.substring(i, i + 3)
        if (substring.toSet().size == 1) return true
    }
    return false
}

private fun hasCommonPatterns(password: String): Boolean {
    val commonPatterns = listOf(
        "123", "abc", "qwe", "asd", "zxc", "password", 
        "admin", "user", "test", "000", "111", "222"
    )
    
    val lowerPassword = password.lowercase()
    return commonPatterns.any { lowerPassword.contains(it) }
}