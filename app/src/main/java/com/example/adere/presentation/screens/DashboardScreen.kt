package com.example.adere.presentation.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.adere.core.crypto.PasswordHealthAnalyzer
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import com.example.adere.presentation.components.AllRecentActivitySheet
import com.example.adere.presentation.components.EnhancedRecentActivityRow
import com.example.adere.presentation.components.RecentActivityDetailSheet
import com.example.adere.presentation.components.VaultItemAvatar
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Executive 3D Dashboard Screen for ADERA Encrypted Vault.
 * Features 3D glassmorphic brand icons, quick creation actions, category tiles,
 * password health audit analytics, and tactile haptic micro-interactions.
 */
@Composable
fun DashboardScreen(
    items: List<VaultItem>,
    healthReport: PasswordHealthAnalyzer.VaultHealthReport,
    onNavigateToItemDetail: (String) -> Unit,
    onNavigateToAddItem: (VaultCategory) -> Unit,
    onNavigateToSecurity: () -> Unit,
    onNavigateToCategory: (VaultCategory) -> Unit,
    onQuickCopy: (VaultItem) -> Unit,
    onLockClick: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    var selectedActivityItem by remember { mutableStateOf<VaultItem?>(null) }
    var showAllActivitySheet by remember { mutableStateOf(false) }

    val recentItems = remember(items) {
        items.asSequence().sortedByDescending { it.updatedAt }.take(5).toList()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Glass3DBadge(
                                primaryColor = MaterialTheme.colorScheme.primary,
                                iconVector = Icons.Default.Shield,
                                size = 42.dp,
                                iconSize = 22.dp,
                                badgeShape = RoundedCornerShape(12.dp)
                            )

                            Column {
                                Text(
                                    text = "ADERA",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.8.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                    Text(
                                        text = "Encrypted Vault • Zero-Knowledge",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onLockClick()
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock vault",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {

            // ── 1. Security Health Audit Card ─────────────────────────────────
            item {
                SecurityHealthCard3D(
                    healthReport = healthReport,
                    totalItemsCount = items.size,
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onNavigateToSecurity()
                }
            }

            // ── 2. Quick Add Actions (Enhanced 3D Real Brand Logos) ──────────
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "QUICK CREATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = "Popular Brands",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    QuickActionsRow3D(
                        onNavigateToAddItem = { cat ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateToAddItem(cat)
                        }
                    )
                }
            }

            // ── 3. Vault Categories (Enhanced 3D Real Brand Logos) ───────────
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "CATEGORIES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        TextButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNavigateToCategory(VaultCategory.ALL)
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "See all (${items.size})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    CategoriesLazyRow3D(
                        items = items,
                        onNavigateToCategory = { cat ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateToCategory(cat)
                        }
                    )
                }
            }

            // ── 4. Recently Updated Items Header ─────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "RECENT ACTIVITY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    TextButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showAllActivitySheet = true
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            if (recentItems.isNotEmpty()) {
                items(recentItems, key = { it.id }) { item ->
                    EnhancedRecentActivityRow(
                        item = item,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedActivityItem = item
                        },
                        onCopy = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onQuickCopy(item)
                        }
                    )
                }
            } else {
                item {
                    EmptyVaultPrompt(
                        onAddItem = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateToAddItem(VaultCategory.SOCIAL)
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Interactive Activity Detail Sheet
    selectedActivityItem?.let { activityItem ->
        RecentActivityDetailSheet(
            item = activityItem,
            onDismiss = { selectedActivityItem = null },
            onNavigateToFullDetail = { itemId ->
                selectedActivityItem = null
                onNavigateToItemDetail(itemId)
            },
            onQuickCopy = onQuickCopy
        )
    }

    // "View All Activity" Log Sheet
    if (showAllActivitySheet) {
        AllRecentActivitySheet(
            items = items,
            onDismiss = { showAllActivitySheet = false },
            onSelectItem = { selectedItem ->
                selectedActivityItem = selectedItem
            },
            onQuickCopy = onQuickCopy
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3D Glass / Orb Icon Badge Component
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Renders real-world brand drawables or vector icons inside a 3D glassmorphic badge container.
 */
@Composable
fun Glass3DBadge(
    primaryColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iconSize: Dp = 26.dp,
    @DrawableRes iconRes: Int? = null,
    iconVector: ImageVector? = null,
    @DrawableRes secondaryIconRes: Int? = null,
    badgeShape: Shape = RoundedCornerShape(14.dp)
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 5.dp,
                shape = badgeShape,
                ambientColor = primaryColor.copy(alpha = 0.20f),
                spotColor = primaryColor.copy(alpha = 0.35f)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.24f),
                        primaryColor.copy(alpha = 0.08f),
                        primaryColor.copy(alpha = 0.18f)
                    )
                ),
                shape = badgeShape
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.85f),
                        primaryColor.copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.20f)
                    )
                ),
                shape = badgeShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Specular 3D glass reflection sheen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(badgeShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(size.value * 2.2f, size.value * 2.2f)
                    )
                )
        )

        if (iconRes != null) {
            if (secondaryIconRes != null) {
                // Overlapping 3D multi-logo depth stack
                Box(
                    modifier = Modifier.size(size),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = secondaryIconRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize * 0.86f)
                            .offset(x = (-5).dp, y = (-3).dp)
                            .shadow(2.dp, CircleShape)
                            .border(1.5.dp, Color.White, CircleShape)
                            .clip(CircleShape)
                    )
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize * 0.98f)
                            .offset(x = (5).dp, y = (3).dp)
                            .shadow(4.dp, CircleShape)
                            .border(1.8.dp, Color.White, CircleShape)
                            .clip(CircleShape)
                    )
                }
            } else {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(iconSize)
                        .shadow(2.dp, RoundedCornerShape(6.dp))
                        .clip(RoundedCornerShape(6.dp))
                )
            }
        } else if (iconVector != null) {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Executive 3D Security Health Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SecurityHealthCard3D(
    healthReport: PasswordHealthAnalyzer.VaultHealthReport,
    totalItemsCount: Int,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(value = false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    val scoreColor = when {
        healthReport.healthScorePercent >= 80 -> Color(0xFF10B981) // Emerald Green
        healthReport.healthScorePercent >= 50 -> Color(0xFFF59E0B) // Amber
        else -> Color(0xFFF43F5E) // Rose Red
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = scoreColor.copy(alpha = 0.22f)
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.2.dp,
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.8f),
                    scoreColor.copy(alpha = 0.35f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Glass3DBadge(
                    primaryColor = scoreColor,
                    iconVector = Icons.Default.Shield,
                    size = 50.dp,
                    iconSize = 26.dp,
                    badgeShape = RoundedCornerShape(14.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Security Audit",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "${healthReport.overallStatus.label} • $totalItemsCount Vault Items",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(scoreColor.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${healthReport.healthScorePercent}%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = scoreColor,
                            fontSize = 24.sp
                        )
                    )
                }
            }

            // 3D Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth((healthReport.healthScorePercent / 100f).coerceIn(0f, 1f))
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    scoreColor.copy(alpha = 0.7f),
                                    scoreColor
                                )
                            )
                        )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (healthReport.weakCount > 0) {
                        StatusSummaryBadge(
                            count = healthReport.weakCount,
                            label = "Weak",
                            badgeColor = Color(0xFFF59E0B)
                        )
                    }
                    if (healthReport.reusedCount > 0) {
                        StatusSummaryBadge(
                            count = healthReport.reusedCount,
                            label = "Reused",
                            badgeColor = Color(0xFFF43F5E)
                        )
                    }
                    if (healthReport.weakCount == 0 && healthReport.reusedCount == 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "All credentials pass health audit",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusSummaryBadge(
    count: Int,
    label: String,
    badgeColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(badgeColor.copy(alpha = 0.12f))
            .border(0.8.dp, badgeColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$count $label",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = badgeColor
            )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Quick Creation Row with 3D Real Brand Logos
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsRow3D(onNavigateToAddItem: (VaultCategory) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
    ) {
        item {
            QuickAction3DCard(
                label = "Social",
                subtitle = "Insta, FB, X",
                iconRes = R.drawable.ic_brand_instagram,
                secondaryIconRes = R.drawable.ic_brand_facebook,
                brandColor = Color(0xFFE1306C),
                onClick = { onNavigateToAddItem(VaultCategory.SOCIAL) }
            )
        }
        item {
            QuickAction3DCard(
                label = "Banking",
                subtitle = "CBE, Telebirr",
                iconRes = R.drawable.ic_brand_cbe,
                secondaryIconRes = R.drawable.ic_brand_telebirr,
                brandColor = Color(0xFF0284C7),
                onClick = { onNavigateToAddItem(VaultCategory.BANKING) }
            )
        }
        item {
            QuickAction3DCard(
                label = "Crypto",
                subtitle = "BTC, Web3",
                iconRes = R.drawable.ic_brand_bitcoin,
                secondaryIconRes = R.drawable.ic_brand_metamask,
                brandColor = Color(0xFFF7931A),
                onClick = { onNavigateToAddItem(VaultCategory.CRYPTO) }
            )
        }
        item {
            QuickAction3DCard(
                label = "Email",
                subtitle = "Gmail, Mail",
                iconRes = R.drawable.ic_brand_google,
                brandColor = Color(0xFF4285F4),
                onClick = { onNavigateToAddItem(VaultCategory.EMAIL) }
            )
        }
        item {
            QuickAction3DCard(
                label = "Cards & ID",
                subtitle = "Credit, Passports",
                iconVector = Icons.Default.Badge,
                brandColor = Color(0xFFEC4899),
                onClick = { onNavigateToAddItem(VaultCategory.IDENTITY) }
            )
        }
        item {
            QuickAction3DCard(
                label = "Apps",
                subtitle = "Apple, GitHub",
                iconRes = R.drawable.ic_brand_apple,
                secondaryIconRes = R.drawable.ic_brand_github,
                brandColor = Color(0xFF8B5CF6),
                onClick = { onNavigateToAddItem(VaultCategory.APPS) }
            )
        }
        item {
            QuickAction3DCard(
                label = "Seed Vault",
                subtitle = "12/24 Words",
                iconRes = R.drawable.ic_brand_seedphrase,
                brandColor = Color(0xFF10B981),
                onClick = { onNavigateToAddItem(VaultCategory.CRYPTO) }
            )
        }
        item {
            QuickAction3DCard(
                label = "Wi-Fi",
                subtitle = "Networks",
                iconVector = Icons.Default.Wifi,
                brandColor = Color(0xFF06B6D4),
                onClick = { onNavigateToAddItem(VaultCategory.WIFI) }
            )
        }
    }
}

@Composable
private fun QuickAction3DCard(
    label: String,
    subtitle: String,
    brandColor: Color,
    onClick: () -> Unit,
    @DrawableRes iconRes: Int? = null,
    @DrawableRes secondaryIconRes: Int? = null,
    iconVector: ImageVector? = null,
) {
    var isPressed by remember { mutableStateOf(value = false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chipScale"
    )

    Card(
        modifier = Modifier
            .width(126.dp)
            .scale(scale)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = brandColor.copy(alpha = 0.35f)
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.5.dp,
            Brush.verticalGradient(
                listOf(
                    brandColor.copy(alpha = 0.5f),
                    brandColor.copy(alpha = 0.15f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier.background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        brandColor.copy(alpha = 0.15f),
                        brandColor.copy(alpha = 0.03f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 14.dp, horizontal = 10.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Glass3DBadge(
                        primaryColor = brandColor,
                        iconRes = iconRes,
                        secondaryIconRes = secondaryIconRes,
                        iconVector = iconVector,
                        size = 52.dp,
                        iconSize = 28.dp,
                        badgeShape = RoundedCornerShape(16.dp)
                    )

                    // Plus badge indicator
                    Box(
                        modifier = Modifier
                            .offset(x = 6.dp, y = (-4).dp)
                            .size(18.dp)
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(brandColor)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = brandColor.copy(alpha = 0.12f),
                        border = BorderStroke(0.5.dp, brandColor.copy(alpha = 0.25f))
                    ) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = brandColor
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vault Categories Row with 3D Real Brand Logos
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CategoriesLazyRow3D(
    items: List<VaultItem>,
    onNavigateToCategory: (VaultCategory) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Category3DTile(
                title = "Social",
                itemCount = items.count { it.category == VaultCategory.SOCIAL },
                primaryIconRes = R.drawable.ic_brand_instagram,
                secondaryIconRes = R.drawable.ic_brand_facebook,
                accentColor = Color(0xFFEC4899),
                onClick = { onNavigateToCategory(VaultCategory.SOCIAL) }
            )
        }
        item {
            Category3DTile(
                title = "Banking",
                itemCount = items.count { it.category == VaultCategory.BANKING },
                primaryIconRes = R.drawable.ic_brand_cbe,
                secondaryIconRes = R.drawable.ic_brand_telebirr,
                accentColor = Color(0xFF0D9488),
                onClick = { onNavigateToCategory(VaultCategory.BANKING) }
            )
        }
        item {
            Category3DTile(
                title = "Crypto",
                itemCount = items.count { it.category == VaultCategory.CRYPTO },
                primaryIconRes = R.drawable.ic_brand_bitcoin,
                secondaryIconRes = R.drawable.ic_brand_ethereum,
                accentColor = Color(0xFFF59E0B),
                onClick = { onNavigateToCategory(VaultCategory.CRYPTO) }
            )
        }
        item {
            Category3DTile(
                title = "Email",
                itemCount = items.count { it.category == VaultCategory.EMAIL },
                primaryIconRes = R.drawable.ic_brand_google,
                accentColor = Color(0xFF0284C7),
                onClick = { onNavigateToCategory(VaultCategory.EMAIL) }
            )
        }
        item {
            Category3DTile(
                title = "Apps",
                itemCount = items.count { it.category == VaultCategory.APPS },
                primaryIconRes = R.drawable.ic_brand_apple,
                secondaryIconRes = R.drawable.ic_brand_github,
                accentColor = Color(0xFF8B5CF6),
                onClick = { onNavigateToCategory(VaultCategory.APPS) }
            )
        }
        item {
            Category3DTile(
                title = "Websites",
                itemCount = items.count { it.category == VaultCategory.WEBSITE },
                primaryIconRes = R.drawable.ic_brand_figma,
                secondaryIconRes = R.drawable.ic_brand_discord,
                accentColor = Color(0xFF6366F1),
                onClick = { onNavigateToCategory(VaultCategory.WEBSITE) }
            )
        }
        item {
            Category3DTile(
                title = "Wallets",
                itemCount = items.count { it.category == VaultCategory.WALLETS },
                primaryIconRes = R.drawable.ic_brand_metamask,
                secondaryIconRes = R.drawable.ic_brand_seedphrase,
                accentColor = Color(0xFF10B981),
                onClick = { onNavigateToCategory(VaultCategory.WALLETS) }
            )
        }
        item {
            Category3DTile(
                title = "Wi-Fi",
                itemCount = items.count { it.category == VaultCategory.WIFI },
                iconVector = Icons.Default.Wifi,
                accentColor = Color(0xFF06B6D4),
                onClick = { onNavigateToCategory(VaultCategory.WIFI) }
            )
        }
        item {
            Category3DTile(
                title = "Notes",
                itemCount = items.count { it.category == VaultCategory.NOTES },
                iconVector = Icons.AutoMirrored.Filled.Note,
                accentColor = Color(0xFF64748B),
                onClick = { onNavigateToCategory(VaultCategory.NOTES) }
            )
        }
        item {
            Category3DTile(
                title = "2FA",
                itemCount = items.count { it.category == VaultCategory.TOTP_2FA },
                iconVector = Icons.Default.QrCode,
                accentColor = Color(0xFFDB2777),
                onClick = { onNavigateToCategory(VaultCategory.TOTP_2FA) }
            )
        }
    }
}

@Composable
private fun Category3DTile(
    title: String,
    itemCount: Int,
    accentColor: Color,
    onClick: () -> Unit,
    @DrawableRes primaryIconRes: Int? = null,
    @DrawableRes secondaryIconRes: Int? = null,
    iconVector: ImageVector? = null,
) {
    var isPressed by remember { mutableStateOf(value = false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "categoryTileScale"
    )

    Card(
        modifier = Modifier
            .width(128.dp)
            .scale(scale)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = accentColor.copy(alpha = 0.20f)
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.2.dp,
            Brush.verticalGradient(
                listOf(
                    Color.White.copy(alpha = 0.8f),
                    accentColor.copy(alpha = 0.22f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier.background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Glass3DBadge(
                    primaryColor = accentColor,
                    iconRes = primaryIconRes,
                    secondaryIconRes = secondaryIconRes,
                    iconVector = iconVector,
                    size = 42.dp,
                    iconSize = 22.dp,
                    badgeShape = RoundedCornerShape(12.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$itemCount items",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Dashboard Vault Item Activity Row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DashboardVaultRow(
    item: VaultItem,
    onClick: () -> Unit,
    onCopy: () -> Unit
) {
    var isCopied by remember { mutableStateOf(value = false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(1500.milliseconds)
            isCopied = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                VaultItemAvatar(
                    title = item.title,
                    category = item.category,
                    url = item.payload.url,
                    size = 44.dp,
                    cornerRadius = 10.dp
                )

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.username.ifBlank { item.category.title },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = item.category.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = {
                    isCopied = true
                    onCopy()
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCopied) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                AnimatedContent(
                    targetState = isCopied,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "copyIconAnimation"
                ) { copied ->
                    if (copied) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Copied",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Quick copy",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Empty Vault State Prompt
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmptyVaultPrompt(onAddItem: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Glass3DBadge(
                primaryColor = MaterialTheme.colorScheme.primary,
                iconVector = Icons.Default.Lock,
                size = 56.dp,
                iconSize = 28.dp,
                badgeShape = RoundedCornerShape(16.dp)
            )

            Text(
                text = "Your Vault is Empty",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = "Store passwords, seed phrases, PINs, and secure notes with zero-knowledge AES-256 encryption.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            )
            TextButton(
                onClick = onAddItem,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = "+ Add First Item",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}
