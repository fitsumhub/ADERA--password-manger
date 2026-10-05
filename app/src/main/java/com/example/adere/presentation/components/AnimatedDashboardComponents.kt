package com.example.adere.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.domain.model.VaultCategory
import com.example.ui.theme.CategoryAppsYellow
import com.example.ui.theme.CategoryCardTeal
import com.example.ui.theme.CategorySocialBlue
import com.example.ui.theme.LocalThemeAccents
import kotlinx.coroutines.delay

@Composable
fun AnimatedDashboardHeader(
    onLockClick: () -> Unit,
    onNavigateToSecurity: () -> Unit,
    animationDelay: Long = 0L
) {
    val hapticFeedback = LocalHapticFeedback.current
    val scale = remember { Animatable(0f) }
    val offsetY = remember { Animatable(50f) }
    
    LaunchedEffect(Unit) {
        delay(animationDelay)
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale.value)
            .offset(y = offsetY.value.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedIconButton(
            onClick = {
                hapticFeedback.performHapticFeedback(
                    androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                )
                onLockClick()
            },
            icon = Icons.Default.Menu,
            contentDescription = "Menu"
        )

        Spacer(modifier = Modifier.weight(1f))

        AnimatedNotificationIcon(onClick = onNavigateToSecurity)

        Spacer(modifier = Modifier.width(8.dp))

        AnimatedProfileAvatar()
    }
}

@Composable
fun AnimatedIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "button_scale"
    )

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
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
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(26.dp)
        )
    }
}
@Composable
fun AnimatedNotificationIcon(
    onClick: () -> Unit
) {
    val themeAccents = LocalThemeAccents.current
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "notification_scale"
    )
    
    val badgePulse = remember { Animatable(0.8f) }
    
    LaunchedEffect(Unit) {
        while (true) {
            badgePulse.animateTo(1.2f, animationSpec = tween(1000))
            badgePulse.animateTo(0.8f, animationSpec = tween(1000))
        }
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        themeAccents.surface,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            )
            .border(1.dp, themeAccents.border, CircleShape)
            .clickable { onClick() }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.NotificationsNone,
            contentDescription = "Notifications",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )
        
        // Animated notification badge
        Box(
            modifier = Modifier
                .size(8.dp)
                .scale(badgePulse.value)
                .align(Alignment.TopEnd)
                .offset(x = (-6).dp, y = 6.dp)
                .clip(CircleShape)
                .background(Color(0xFFEF4444))
                .shadow(4.dp, CircleShape)
        )
    }
}

@Composable
fun AnimatedProfileAvatar() {
    val themeAccents = LocalThemeAccents.current
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "avatar_scale"
    )

    Box(
        modifier = Modifier
            .size(42.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .border(1.5.dp, themeAccents.primary, CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile",
            tint = themeAccents.primary,
            modifier = Modifier.size(22.dp)
        )
    }
}
@Composable
fun AnimatedSecuritySummaryCard(
    onNavigateToSecurity: () -> Unit,
    animationDelay: Long = 200L
) {
    val themeAccents = LocalThemeAccents.current
    val scale = remember { Animatable(0f) }
    val offsetY = remember { Animatable(30f) }
    var isPressed by remember { mutableStateOf(false) }
    
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "card_scale"
    )
    
    LaunchedEffect(Unit) {
        delay(animationDelay)
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale.value * cardScale)
            .offset(y = offsetY.value.dp)
            .clip(RoundedCornerShape(8.dp))
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = themeAccents.primary.copy(alpha = 0.3f)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onNavigateToSecurity() }
                )
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    themeAccents.primary.copy(alpha = 0.3f),
                                    themeAccents.primary.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Security Status",
                        tint = themeAccents.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Vault Security Score: 100%",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Zero vulnerabilities detected. AES-256 active.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    )
                }
            }
        }
    }
}
@Composable
fun AnimatedSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    animationDelay: Long = 400L
) {
    val scale = remember { Animatable(0f) }
    val offsetX = remember { Animatable(-50f) }
    
    LaunchedEffect(Unit) {
        delay(animationDelay)
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        offsetX.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale.value)
            .offset(x = offsetX.value.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    "Search vault items...", 
                    color = MaterialTheme.colorScheme.onSurfaceVariant, 
                    fontSize = 15.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .weight(1f)
                .testTag("dashboard_search_input")
        )

        Spacer(modifier = Modifier.width(10.dp))

        AnimatedFilterButton(onClick = onFilterClick)
    }
}

@Composable
fun AnimatedFilterButton(onClick: () -> Unit) {
    val themeAccents = LocalThemeAccents.current
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "filter_scale"
    )

    Box(
        modifier = Modifier
            .size(52.dp)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        themeAccents.primary,
                        themeAccents.primary.copy(alpha = 0.9f)
                    )
                )
            )
            .clickable { onClick() }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "Reset Filter",
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}
@Composable
fun AnimatedCategoryGrid(
    onNavigateToCategory: (VaultCategory) -> Unit,
    animationDelay: Long = 600L
) {
    val categories = listOf(
        Triple("Social", CategorySocialBlue, Icons.Default.Share) to VaultCategory.SOCIAL,
        Triple("Apps", CategoryAppsYellow, Icons.Default.Smartphone) to VaultCategory.WEBSITE,
        Triple("Card", CategoryCardTeal, Icons.Default.CreditCard) to VaultCategory.BANKING
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categories.forEachIndexed { index, (categoryData, vaultCategory) ->
            val (title, backgroundColor, icon) = categoryData
            
            AnimatedCategoryTile(
                title = title,
                backgroundColor = backgroundColor,
                icon = icon,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateToCategory(vaultCategory) },
                animationDelay = animationDelay + (index * 100L)
            )
        }
    }
}

@Composable
fun AnimatedCategoryTile(
    title: String,
    backgroundColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    animationDelay: Long = 0L
) {
    val scale = remember { Animatable(0f) }
    val offsetY = remember { Animatable(30f) }
    var isPressed by remember { mutableStateOf(false) }
    
    val tileScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "tile_scale"
    )
    
    LaunchedEffect(Unit) {
        delay(animationDelay)
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Card(
        modifier = modifier
            .height(90.dp)
            .scale(scale.value * tileScale)
            .offset(y = offsetY.value.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = backgroundColor.copy(alpha = 0.3f)
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
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            backgroundColor,
                            backgroundColor.copy(alpha = 0.8f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}
@Composable
fun AnimatedSectionHeader(
    title: String,
    actionText: String = "See All",
    onActionClick: () -> Unit = {},
    animationDelay: Long = 0L
) {
    val scale = remember { Animatable(0f) }
    val offsetX = remember { Animatable(-30f) }
    
    LaunchedEffect(Unit) {
        delay(animationDelay)
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
        offsetX.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale.value)
            .offset(x = offsetX.value.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp
            )
        )
        
        var isPressed by remember { mutableStateOf(false) }
        val textScale by animateFloatAsState(
            targetValue = if (isPressed) 0.95f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessHigh
            ),
            label = "action_text_scale"
        )
        
        Text(
            text = actionText,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .scale(textScale)
                .clickable { onActionClick() }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        }
                    )
                }
        )
    }
}

// Staggered animation helper for lists
@Composable
fun <T> AnimatedLazyColumn(
    items: List<T>,
    itemContent: @Composable (item: T, index: Int) -> Unit,
    baseDelay: Long = 800L,
    staggerDelay: Long = 100L
) {
    items.forEachIndexed { index, item ->
        val itemDelay = baseDelay + (index * staggerDelay)
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 400,
                    delayMillis = itemDelay.toInt()
                )
            ),
            exit = slideOutVertically() + fadeOut()
        ) {
            itemContent(item, index)
        }
    }
}
