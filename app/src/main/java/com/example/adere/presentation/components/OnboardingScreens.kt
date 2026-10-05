package com.example.adere.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
 * Onboarding welcome screen with feature highlights
 */
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeAccents = LocalThemeAccents.current
    val hapticFeedback = LocalHapticFeedback.current

    var currentPage by remember { mutableIntStateOf(0) }
    // Drives the AnimatedVisibility for page content
    var contentVisible by remember { mutableStateOf(true) }

    val pages = listOf(
        OnboardingPage(
            icon = Icons.Default.Shield,
            title = "Military-Grade Security",
            description = "Your passwords are encrypted with AES-256, the same standard used by governments and financial institutions. Zero-knowledge architecture means only you can access your data.",
            accentColor = themeAccents.primary
        ),
        OnboardingPage(
            icon = Icons.Default.Lock,
            title = "Offline & Private",
            description = "No cloud, no sync, no tracking. Your vault stays on your device, completely isolated from the internet. Your secrets never leave your phone.",
            accentColor = themeAccents.success
        ),
        OnboardingPage(
            icon = Icons.Default.Fingerprint,
            title = "Biometric Unlock",
            description = "Quick and secure access with fingerprint or face recognition. Your master password is never stored, only you know it.",
            accentColor = themeAccents.info
        ),
        OnboardingPage(
            icon = Icons.Default.Security,
            title = "Security Health",
            description = "Real-time analysis of your passwords. Get alerts for weak, reused, or compromised credentials. Keep your digital life secure.",
            accentColor = themeAccents.warning
        )
    )

    // Cross-fade page content whenever currentPage changes
    LaunchedEffect(currentPage) {
        contentVisible = false
        delay(120)
        contentVisible = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        pages[currentPage].accentColor.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onSkip()
                    }
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.2f))

            // Page content with animations
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn() + scaleIn() + slideInHorizontally(initialOffsetX = { it / 2 }),
                exit = fadeOut() + scaleOut() + slideOutHorizontally(targetOffsetX = { -it / 2 })
            ) {
                OnboardingPageContent(page = pages[currentPage])
            }

            Spacer(modifier = Modifier.weight(0.3f))

            // Page indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 24.dp)
            ) {
                pages.forEachIndexed { index, _ ->
                    PageIndicator(
                        isActive = index == currentPage,
                        color = pages[currentPage].accentColor
                    )
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentPage > 0) {
                    OutlinedButton(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            currentPage--
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Back",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Button(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (currentPage < pages.lastIndex) {
                            currentPage++
                        } else {
                            onComplete()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = pages[currentPage].accentColor
                    )
                ) {
                    Text(
                        text = if (currentPage < pages.lastIndex) "Next" else "Get Started",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Onboarding page data
 */
private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val accentColor: Color
)

/**
 * Onboarding page content with animated icon
 */
@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(page) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        // Subtle continuous pulse instead of rotation (simpler and avoids infinite loop issues)
        while (true) {
            scale.animateTo(targetValue = 1.04f, animationSpec = tween(2500))
            scale.animateTo(targetValue = 1.00f, animationSpec = tween(2500))
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Animated icon
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(scale.value)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            page.accentColor.copy(alpha = 0.2f),
                            page.accentColor.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                page.accentColor.copy(alpha = 0.25f),
                                page.accentColor.copy(alpha = 0.15f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = page.accentColor,
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 24.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Page indicator dot — widens when active
 */
@Composable
private fun PageIndicator(
    isActive: Boolean,
    color: Color
) {
    val width by animateFloatAsState(
        targetValue = if (isActive) 32f else 8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "indicator_width"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.3f,
        animationSpec = tween(300),
        label = "indicator_alpha"
    )

    Box(
        modifier = Modifier
            .width(width.dp)
            .height(8.dp)
            .alpha(alpha)
            .clip(RoundedCornerShape(4.dp))
            .background(color)
    )
}

/**
 * Success completion screen shown after vault is first created
 */
@Composable
fun VaultCreatedSuccessScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeAccents = LocalThemeAccents.current
    val hapticFeedback = LocalHapticFeedback.current

    var isVisible by remember { mutableStateOf(false) }
    val checkScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
        delay(300)
        checkScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        themeAccents.success.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + scaleIn()
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(checkScale.value)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    themeAccents.success.copy(alpha = 0.2f),
                                    themeAccents.success.copy(alpha = 0.05f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(themeAccents.success),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Vault Created!",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your secure vault is ready. Start adding your passwords, notes, and crypto wallets.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onContinue()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeAccents.success
                )
            ) {
                Text(
                    text = "Start Using ADERE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}
