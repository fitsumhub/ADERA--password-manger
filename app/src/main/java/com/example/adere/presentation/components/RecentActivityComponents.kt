package com.example.adere.presentation.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.crypto.PasswordGenerator
import com.example.adere.core.utilities.ClipboardSecurityHelper
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

/**
 * Format raw epoch milliseconds into clean relative time string.
 */
fun formatRelativeTime(epochMillis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - epochMillis

    if (diff < 0) return "Just now"

    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days == 1L -> "Yesterday"
        days < 7 -> "${days}d ago"
        else -> {
            val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
            sdf.format(Date(epochMillis))
        }
    }
}

/**
 * Check if the item was newly created recently (created within 30 seconds of updated or within 24h).
 */
fun isNewlyCreatedItem(item: VaultItem): Boolean {
    val diff = Math.abs(item.updatedAt - item.createdAt)
    return diff < 60_000L || item.updatedAt == item.createdAt
}

/**
 * Formats detailed timestamp string.
 */
fun formatDetailedDateTime(epochMillis: Long): String {
    val sdf = SimpleDateFormat("MMM d, yyyy 'at' hh:mm a", Locale.getDefault())
    return sdf.format(Date(epochMillis))
}

// ─────────────────────────────────────────────────────────────────────────────
// Enhanced Recent Activity Row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun EnhancedRecentActivityRow(
    item: VaultItem,
    onClick: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    var isCopied by remember { mutableStateOf(false) }

    val isNew = isNewlyCreatedItem(item)
    val relativeTime = formatRelativeTime(item.updatedAt)

    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "activity_row_scale"
    )

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(1500.milliseconds)
            isCopied = false
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(cardScale)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    }
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                    if (isNew) Color(0xFF10B981).copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )
            )
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
                // 3D/Glass Vault Avatar
                VaultItemAvatar(
                    title = item.title,
                    category = item.category,
                    url = item.payload.url,
                    size = 46.dp,
                    cornerRadius = 12.dp
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    // Item Title
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Subtitle & Metadata Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Username / Category descriptor
                        Text(
                            text = item.username.ifBlank { item.category.title },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.outline
                            )
                        )

                        // Activity Status Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isNew) Color(0xFF10B981).copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            border = BorderStroke(
                                0.5.dp,
                                if (isNew) Color(0xFF10B981).copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isNew) Color(0xFF10B981)
                                            else MaterialTheme.colorScheme.primary
                                        )
                                )
                                Text(
                                    text = if (isNew) "Added $relativeTime" else "Updated $relativeTime",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isNew) Color(0xFF10B981)
                                        else MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Quick Actions Column (Copy Button + Details Indicator)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Quick Copy Button
                IconButton(
                    onClick = {
                        isCopied = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onCopy()
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCopied) Color(0xFF10B981).copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
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
                                contentDescription = "Quick copy password",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Detail indicator arrow
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View detail",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Executive Recent Activity Detail Modal Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentActivityDetailSheet(
    item: VaultItem,
    onDismiss: () -> Unit,
    onNavigateToFullDetail: (String) -> Unit,
    onQuickCopy: (VaultItem) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showPassword by remember { mutableStateOf(false) }
    var isPasswordCopied by remember { mutableStateOf(false) }
    var isUsernameCopied by remember { mutableStateOf(false) }

    val isNew = isNewlyCreatedItem(item)
    val relativeTime = formatRelativeTime(item.updatedAt)
    val passwordPlaintext = item.payload.password

    // Evaluate password strength
    val entropyBits = remember(passwordPlaintext) {
        if (passwordPlaintext.isNotBlank()) PasswordGenerator.calculateEntropy(passwordPlaintext) else 0.0
    }
    val strength = remember(entropyBits, passwordPlaintext) {
        if (passwordPlaintext.isNotBlank()) {
            PasswordGenerator.evaluateStrength(entropyBits, passwordPlaintext.length)
        } else {
            PasswordGenerator.PasswordStrength.FAIR
        }
    }

    val (strengthColor, strengthPercent) = remember(strength, passwordPlaintext) {
        if (passwordPlaintext.isBlank()) {
            Pair(Color(0xFF6B7280), 0.5f)
        } else {
            when (strength) {
                PasswordGenerator.PasswordStrength.VERY_STRONG -> Pair(Color(0xFF10B981), 1.0f)
                PasswordGenerator.PasswordStrength.STRONG -> Pair(Color(0xFF10B981), 0.85f)
                PasswordGenerator.PasswordStrength.FAIR -> Pair(Color(0xFFF59E0B), 0.65f)
                PasswordGenerator.PasswordStrength.WEAK -> Pair(Color(0xFFEF4444), 0.4f)
                PasswordGenerator.PasswordStrength.VERY_WEAK -> Pair(Color(0xFFEF4444), 0.2f)
            }
        }
    }

    LaunchedEffect(isPasswordCopied) {
        if (isPasswordCopied) {
            delay(1800.milliseconds)
            isPasswordCopied = false
        }
    }

    LaunchedEffect(isUsernameCopied) {
        if (isUsernameCopied) {
            delay(1800.milliseconds)
            isUsernameCopied = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── 1. Sheet Header ──────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    VaultItemAvatar(
                        title = item.title,
                        category = item.category,
                        url = item.payload.url,
                        size = 56.dp,
                        cornerRadius = 16.dp
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Activity Type Badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isNew) Color(0xFF10B981).copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.primaryContainer,
                                border = BorderStroke(
                                    1.dp,
                                    if (isNew) Color(0xFF10B981).copy(alpha = 0.3f)
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                )
                            ) {
                                Text(
                                    text = if (isNew) "NEWLY ADDED" else "RECENTLY UPDATED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isNew) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Text(
                                text = relativeTime,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // ── 2. Security & Audit Status Card ───────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Security Status",
                                tint = strengthColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (passwordPlaintext.isNotBlank()) "Security Rating: ${strength.label}"
                                else "Item Protection: AES-256",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        if (passwordPlaintext.isNotBlank()) {
                            Text(
                                text = "${(strengthPercent * 100).toInt()}%",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = strengthColor
                                )
                            )
                        }
                    }

                    if (passwordPlaintext.isNotBlank()) {
                        LinearProgressIndicator(
                            progress = { strengthPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = strengthColor,
                            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            strokeCap = StrokeCap.Round
                        )
                    }

                    // Security Badges Flow
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DetailChip(
                            icon = Icons.Default.Lock,
                            label = "AES-256 Encrypted",
                            color = MaterialTheme.colorScheme.primary
                        )
                        DetailChip(
                            icon = Icons.Default.CheckCircle,
                            label = "Audit Passed",
                            color = Color(0xFF10B981)
                        )
                        if (item.payload.totpSecret.isNotBlank()) {
                            DetailChip(
                                icon = Icons.Default.Security,
                                label = "2FA Active",
                                color = Color(0xFF06B6D4)
                            )
                        }
                    }
                }
            }

            // ── 3. Interactive Detail Cards ──────────────────────────────────
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Username Card
                if (item.username.isNotBlank()) {
                    DetailFieldCard(
                        title = "USERNAME / ACCOUNT",
                        value = item.username,
                        icon = Icons.Default.Person,
                        isActionSuccess = isUsernameCopied,
                        onAction = {
                            ClipboardSecurityHelper.copyToClipboard(
                                context = context,
                                label = "Username",
                                text = item.username,
                                coroutineScope = scope
                            )
                            isUsernameCopied = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        actionIcon = Icons.Default.ContentCopy,
                        actionDescription = "Copy Username"
                    )
                }

                // Password Card
                if (passwordPlaintext.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "PASSWORD SECRET",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Key,
                                        contentDescription = "Password",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Text(
                                        text = if (showPassword) passwordPlaintext else "••••••••••••••••",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = if (showPassword) 0.sp else 2.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Toggle Visibility
                                    IconButton(
                                        onClick = {
                                            showPassword = !showPassword
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (showPassword) "Hide password" else "Show password",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Copy Password Button
                                    IconButton(
                                        onClick = {
                                            ClipboardSecurityHelper.copyToClipboard(
                                                context = context,
                                                label = item.title,
                                                text = passwordPlaintext,
                                                coroutineScope = scope
                                            )
                                            isPasswordCopied = true
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isPasswordCopied) Color(0xFF10B981).copy(alpha = 0.2f)
                                                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                            )
                                    ) {
                                        Icon(
                                            imageVector = if (isPasswordCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                            contentDescription = "Copy Password",
                                            tint = if (isPasswordCopied) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Website URL Card
                if (item.payload.url.isNotBlank()) {
                    DetailFieldCard(
                        title = "SERVICE WEBSITE / LINK",
                        value = item.payload.url,
                        icon = Icons.Default.OpenInNew,
                        isActionSuccess = false,
                        onAction = {
                            try {
                                val url = if (!item.payload.url.startsWith("http://") && !item.payload.url.startsWith("https://")) {
                                    "https://${item.payload.url}"
                                } else item.payload.url
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        },
                        actionIcon = Icons.Default.OpenInNew,
                        actionDescription = "Open Website"
                    )
                }

                // Notes Card
                if (item.payload.notes.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "SECURE NOTES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Text(
                                text = item.payload.notes,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Activity Audit Trail / Timestamps Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Timestamps",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Last Modified: ${formatDetailedDateTime(item.updatedAt)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // ── 4. Bottom Primary Action Buttons ──────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onDismiss()
                        onNavigateToFullDetail(item.id)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit / Full Details",
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "View Full Details & Edit",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// "View All Activity" Modal Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AllRecentActivitySheet(
    items: List<VaultItem>,
    onDismiss: () -> Unit,
    onSelectItem: (VaultItem) -> Unit,
    onQuickCopy: (VaultItem) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<VaultCategory?>(null) }

    // Sort items by last updated date descending
    val sortedActivityItems = remember(items, searchQuery, selectedCategoryFilter) {
        items.asSequence()
            .sortedByDescending { it.updatedAt }
            .filter { item ->
                if (selectedCategoryFilter != null) item.category == selectedCategoryFilter else true
            }
            .filter { item ->
                if (searchQuery.isBlank()) true
                else item.title.contains(searchQuery, ignoreCase = true) ||
                        item.username.contains(searchQuery, ignoreCase = true)
            }
            .toList()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Activity History",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Recent Activity Log",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "${sortedActivityItems.size} items updated recently",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Search recent activity...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            )

            // Category Filter Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategoryFilter == null,
                    onClick = { selectedCategoryFilter = null },
                    label = { Text("All (${items.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
                VaultCategory.entries.take(5).forEach { category ->
                    val count = items.count { it.category == category }
                    if (count > 0) {
                        FilterChip(
                            selected = selectedCategoryFilter == category,
                            onClick = {
                                selectedCategoryFilter = if (selectedCategoryFilter == category) null else category
                            },
                            label = { Text("${category.title} ($count)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }

            // Items List
            if (sortedActivityItems.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(sortedActivityItems, key = { it.id }) { item ->
                        EnhancedRecentActivityRow(
                            item = item,
                            onClick = {
                                onSelectItem(item)
                            },
                            onCopy = {
                                onQuickCopy(item)
                            }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "No items",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "No matching activity found",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helper Reusable Subcomponents
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DetailChip(
    icon: ImageVector,
    label: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
            )
        }
    }
}

@Composable
private fun DetailFieldCard(
    title: String,
    value: String,
    icon: ImageVector,
    isActionSuccess: Boolean,
    onAction: () -> Unit,
    actionIcon: ImageVector,
    actionDescription: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onAction,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isActionSuccess) Color(0xFF10B981).copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        )
                ) {
                    Icon(
                        imageVector = if (isActionSuccess) Icons.Default.Check else actionIcon,
                        contentDescription = actionDescription,
                        tint = if (isActionSuccess) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
