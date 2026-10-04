package com.example.adere.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.adere.presentation.navigation.NavDestination
import com.example.ui.theme.LocalThemeAccents
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AdereTotalSecurityBottomBar(
    currentTab: NavDestination,
    onSelectTab: (NavDestination) -> Unit,
    onCenterAddClick: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val themeAccents = LocalThemeAccents.current
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Glassmorphism Background Blur Effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
                        )
                    )
                )
                .blur(
                    radius = 20.dp,
                    edgeTreatment = BlurredEdgeTreatment.Rectangle
                )
        )

        // Enhanced floating navigation dock
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    spotColor = themeAccents.primary.copy(alpha = 0.4f),
                    ambientColor = themeAccents.primary.copy(alpha = 0.2f)
                ),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                themeAccents.borderGlow.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ),
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tab 1: Home
                    EnhancedBottomBarItem(
                        selected = currentTab == NavDestination.HOME,
                        onClick = { 
                            hapticFeedback.performHapticFeedback(
                                androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                            )
                            onSelectTab(NavDestination.HOME) 
                        },
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                        contentDescription = "Home",
                        testTag = "nav_tab_home"
                    )

                    // Tab 2: Vault
                    EnhancedBottomBarItem(
                        selected = currentTab == NavDestination.VAULT,
                        onClick = { 
                            hapticFeedback.performHapticFeedback(
                                androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                            )
                            onSelectTab(NavDestination.VAULT) 
                        },
                        selectedIcon = Icons.Filled.Lock,
                        unselectedIcon = Icons.Outlined.Lock,
                        contentDescription = "Vault",
                        testTag = "nav_tab_vault"
                    )

                    // Spacer for the center raised button
                    Spacer(modifier = Modifier.size(56.dp))

                    // Tab 4: Security / Health Audit
                    EnhancedBottomBarItem(
                        selected = currentTab == NavDestination.SECURITY,
                        onClick = { 
                            hapticFeedback.performHapticFeedback(
                                androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                            )
                            onSelectTab(NavDestination.SECURITY) 
                        },
                        selectedIcon = Icons.Filled.Shield,
                        unselectedIcon = Icons.Outlined.Shield,
                        contentDescription = "Security",
                        testTag = "nav_tab_security"
                    )

                    // Tab 5: Settings / Profile
                    EnhancedBottomBarItem(
                        selected = currentTab == NavDestination.SETTINGS,
                        onClick = { 
                            hapticFeedback.performHapticFeedback(
                                androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                            )
                            onSelectTab(NavDestination.SETTINGS) 
                        },
                        selectedIcon = Icons.Filled.Person,
                        unselectedIcon = Icons.Outlined.Person,
                        contentDescription = "Settings",
                        testTag = "nav_tab_settings"
                    )
                }
            }
        }

        // Enhanced Center Raised Floating Action Button
        EnhancedCenterFab(
            onClick = {
                hapticFeedback.performHapticFeedback(
                    androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                )
                onCenterAddClick()
            }
        )
    }
}

@Composable
private fun EnhancedBottomBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    contentDescription: String,
    testTag: String
) {
    val themeAccents = LocalThemeAccents.current
    val coroutineScope = rememberCoroutineScope()
    var isPressed by remember { mutableStateOf(false) }
    
    val scale = remember { Animatable(1f) }
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
            themeAccents.primary.copy(alpha = 0.15f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(300),
        label = "background_color"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (selected) {
            themeAccents.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(300),
        label = "icon_color"
    )

    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(
                targetValue = 0.85f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )
        } else {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .size(54.dp, 42.dp)
            .scale(scale.value)
            .clip(RoundedCornerShape(22.dp))
            .background(backgroundColor)
            .border(
                width = if (selected) 1.dp else 0.dp,
                color = if (selected) themeAccents.borderGlow.copy(alpha = 0.3f) else Color.Transparent,
                shape = RoundedCornerShape(22.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        // Add subtle glow effect for selected state
        if (selected) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                themeAccents.primary.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            radius = 40f
                        ),
                        shape = CircleShape
                    )
            )
        }
        
        Icon(
            imageVector = if (selected) selectedIcon else unselectedIcon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun EnhancedCenterFab(
    onClick: () -> Unit
) {
    val themeAccents = LocalThemeAccents.current
    var isPressed by remember { mutableStateOf(false) }
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(
                targetValue = 0.92f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                )
            )
        } else {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .offset(y = (-20).dp)
            .size(56.dp)
            .scale(scale.value)
            .shadow(
                elevation = 16.dp,
                shape = CircleShape,
                spotColor = themeAccents.primary.copy(alpha = 0.4f),
                ambientColor = themeAccents.primary.copy(alpha = 0.2f)
            )
            .clip(CircleShape)
            .testTag("nav_tab_add"),
        contentAlignment = Alignment.Center
    ) {
        // Glassmorphism background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            themeAccents.primary,
                            themeAccents.primary.copy(alpha = 0.9f)
                        ),
                        radius = 120f
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    ),
                    shape = CircleShape
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = { onClick() }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Item",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
