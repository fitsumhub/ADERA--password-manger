package com.example.adere.presentation.screens
import com.example.ui.theme.Shapes

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.crypto.PasswordGenerator
import com.example.adere.core.crypto.PasswordHealthAnalyzer
import com.example.adere.domain.model.VaultItem
import com.example.ui.theme.CleanBorder
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.HealthCompromisedTeal
import com.example.ui.theme.HealthRefusedYellow
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.HealthSafeBlue

enum class HealthCategoryFilter {
    ALL,
    SAFE,
    WEAK,
    REUSED,
    COMPROMISED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityHealthScreen(
    healthReport: PasswordHealthAnalyzer.VaultHealthReport,
    items: List<VaultItem>,
    onItemClick: (String) -> Unit,
    onLockClick: () -> Unit,
    onBackClick: () -> Unit = onLockClick,
    onHealthCardClick: () -> Unit = {},
) {
    var selectedFilter by remember { mutableStateOf(HealthCategoryFilter.ALL) }

    // Categorize items
    val weakItems = remember(items) {
        items.filter { item ->
            val pass = item.payload.password
            if (pass.isBlank()) false else {
                val entropy = PasswordGenerator.calculateEntropy(pass)
                val strength = PasswordGenerator.evaluateStrength(entropy, pass.length)
                strength == PasswordGenerator.PasswordStrength.VERY_WEAK ||
                strength == PasswordGenerator.PasswordStrength.WEAK ||
                pass.length < 10
            }
        }
    }

    val reusedItems = remember(items) {
        val passwords = items.map { it.payload.password }.filter { it.isNotBlank() }
        val counts = passwords.groupingBy { it }.eachCount()
        items.filter { (counts[it.payload.password] ?: 0) > 1 }
    }

    val oldThresholdMillis = remember {
        System.currentTimeMillis() - java.util.concurrent.TimeUnit.DAYS.toMillis(90L)
    }

    val compromisedItems = remember(items, oldThresholdMillis) {
        items.filter { item ->
            item.payload.password.isNotBlank() && item.passwordLastChanged > 0 && item.passwordLastChanged < oldThresholdMillis
        }
    }

    val safeItems = remember(items, weakItems, reusedItems, compromisedItems) {
        items.filter { it.payload.password.isNotBlank() && !weakItems.contains(it) && !reusedItems.contains(it) && !compromisedItems.contains(it) }
    }

    val displayedItems = remember(selectedFilter, items, safeItems, weakItems, reusedItems, compromisedItems) {
        when (selectedFilter) {
            HealthCategoryFilter.ALL -> items
            HealthCategoryFilter.SAFE -> safeItems
            HealthCategoryFilter.WEAK -> weakItems
            HealthCategoryFilter.REUSED -> reusedItems
            HealthCategoryFilter.COMPROMISED -> compromisedItems
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Security Audit & Health",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 20.sp
                                )
                            )
                            Text(
                                text = "Real-time vault entropy & health grade",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onLockClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(HealthRiskRed.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock Vault",
                            tint = HealthRiskRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Donut Chart Hero
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier.size(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(155.dp)) {
                        val strokeWidth = 22.dp.toPx()

                        val safeCount = safeItems.size
                        val compromisedCount = compromisedItems.size
                        val weakCount = weakItems.size
                        val reusedCount = reusedItems.size
                        val total = (safeCount + compromisedCount + weakCount + reusedCount).coerceAtLeast(1).toFloat()

                        if (items.none { it.payload.password.isNotBlank() }) {
                            drawArc(
                                color = CleanBorder,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth)
                            )
                        } else {
                            val safeSweep = (safeCount / total) * 360f
                            val compromisedSweep = (compromisedCount / total) * 360f
                            val riskSweep = (weakCount / total) * 360f
                            val refusedSweep = (reusedCount / total) * 360f

                            var currentAngle = -90f

                            // 1. Safe (Royal Blue)
                            if (safeSweep > 0f) {
                                drawArc(
                                    color = HealthSafeBlue,
                                    startAngle = currentAngle,
                                    sweepAngle = safeSweep,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth)
                                )
                                currentAngle += safeSweep
                            }
                            // 2. Compromised (Mint Teal)
                            if (compromisedSweep > 0f) {
                                drawArc(
                                    color = HealthCompromisedTeal,
                                    startAngle = currentAngle,
                                    sweepAngle = compromisedSweep,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth)
                                )
                                currentAngle += compromisedSweep
                            }
                            // 3. Weak / Risk (Red)
                            if (riskSweep > 0f) {
                                drawArc(
                                    color = HealthRiskRed,
                                    startAngle = currentAngle,
                                    sweepAngle = riskSweep,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth)
                                )
                                currentAngle += riskSweep
                            }
                            // 4. Refused / Reused (Yellow)
                            if (refusedSweep > 0f) {
                                drawArc(
                                    color = HealthRefusedYellow,
                                    startAngle = currentAngle,
                                    sweepAngle = refusedSweep,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth)
                                )
                            }
                        }
                    }

                    // Donut Center Text: Total score percentage
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${healthReport.healthScorePercent}%",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 32.sp
                            )
                        )
                        Text(
                            text = "Health Score",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // Legend Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendItem(label = "Safe", color = HealthSafeBlue)
                    LegendItem(label = "Reused", color = HealthRefusedYellow)
                    LegendItem(label = "Weak", color = HealthRiskRed)
                    LegendItem(label = "Compromised", color = HealthCompromisedTeal)
                }
            }

            // Overall Rating Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("overall_rating_card"),
                    shape = Shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Vault Encryption Grade",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "AES-256-GCM zero-knowledge score",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(HealthSafeBlue.copy(alpha = 0.15f))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = healthReport.overallRating,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = HealthSafeBlue,
                                    fontSize = 18.sp
                                )
                            )
                        }
                    }
                }
            }

            // Cryptographic Hardware Info Banner
            item {
                Surface(
                    shape = Shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Hardware-Backed Security Active",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "AES-256-GCM · PBKDF2 HMAC-SHA256 (100k rounds) · Memory volatile DEK wipe on lock",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            )
                        }
                    }
                }
            }

            // Interactive Status Breakdown Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "FILTER BY SECURITY STATUS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    HealthStatusCard(
                        title = "Safe Passwords",
                        count = safeItems.size,
                        icon = Icons.Default.Shield,
                        iconTint = HealthSafeBlue,
                        iconBg = HealthSafeBlue.copy(alpha = 0.12f),
                        isSelected = selectedFilter == HealthCategoryFilter.SAFE,
                        onClick = {
                            selectedFilter = if (selectedFilter == HealthCategoryFilter.SAFE) HealthCategoryFilter.ALL else HealthCategoryFilter.SAFE
                            onHealthCardClick()
                        }
                    )

                    HealthStatusCard(
                        title = "Weak / Vulnerable Passwords",
                        count = weakItems.size,
                        icon = Icons.Default.Warning,
                        iconTint = HealthRiskRed,
                        iconBg = HealthRiskRed.copy(alpha = 0.12f),
                        isSelected = selectedFilter == HealthCategoryFilter.WEAK,
                        onClick = {
                            selectedFilter = if (selectedFilter == HealthCategoryFilter.WEAK) HealthCategoryFilter.ALL else HealthCategoryFilter.WEAK
                            onHealthCardClick()
                        }
                    )

                    HealthStatusCard(
                        title = "Reused Passwords",
                        count = reusedItems.size,
                        icon = Icons.Default.Lock,
                        iconTint = HealthRefusedYellow,
                        iconBg = HealthRefusedYellow.copy(alpha = 0.12f),
                        isSelected = selectedFilter == HealthCategoryFilter.REUSED,
                        onClick = {
                            selectedFilter = if (selectedFilter == HealthCategoryFilter.REUSED) HealthCategoryFilter.ALL else HealthCategoryFilter.REUSED
                            onHealthCardClick()
                        }
                    )

                    HealthStatusCard(
                        title = "Compromised Passwords",
                        count = compromisedItems.size,
                        icon = Icons.Default.LockOpen,
                        iconTint = HealthCompromisedTeal,
                        iconBg = HealthCompromisedTeal.copy(alpha = 0.12f),
                        isSelected = selectedFilter == HealthCategoryFilter.COMPROMISED,
                        onClick = {
                            selectedFilter = if (selectedFilter == HealthCategoryFilter.COMPROMISED) HealthCategoryFilter.ALL else HealthCategoryFilter.COMPROMISED
                            onHealthCardClick()
                        }
                    )
                }
            }

            // Filtered Items Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (selectedFilter) {
                            HealthCategoryFilter.ALL -> "ALL VAULT ITEMS (${displayedItems.size})"
                            HealthCategoryFilter.SAFE -> "SAFE ITEMS (${displayedItems.size})"
                            HealthCategoryFilter.WEAK -> "WEAK ITEMS (${displayedItems.size})"
                            HealthCategoryFilter.REUSED -> "REUSED ITEMS (${displayedItems.size})"
                            HealthCategoryFilter.COMPROMISED -> "COMPROMISED ITEMS (${displayedItems.size})"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    if (selectedFilter != HealthCategoryFilter.ALL) {
                        TextButton(onClick = { selectedFilter = HealthCategoryFilter.ALL }) {
                            Text("Show All", fontSize = 12.sp)
                        }
                    }
                }
            }

            if (displayedItems.isEmpty()) {
                item {
                    Surface(
                        shape = Shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier.padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No items match this health status filter.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            } else {
                items(
                    items = displayedItems,
                    key = { it.id }
                ) { vaultItem ->
                    VaultItemHealthRow(
                        item = vaultItem,
                        onFixClick = { onItemClick(vaultItem.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun HealthStatusCard(
    title: String,
    count: Int,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes.medium)
            .clickable(onClick = onClick)
            .then(
                if (isSelected)
                    Modifier.border(2.dp, iconTint, Shapes.medium)
                else
                    Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), Shapes.medium)
            )
            .testTag("health_card_${title.lowercase().replace(" ", "_")}"),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    )
                )
                Text(
                    text = "$count secrets found",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (isSelected) iconTint else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun VaultItemHealthRow(
    item: VaultItem,
    onFixClick: () -> Unit
) {
    val pass = item.payload.password
    val strength = remember(pass) {
        val entropy = PasswordGenerator.calculateEntropy(pass)
        PasswordGenerator.evaluateStrength(entropy, pass.length)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onFixClick),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(Shapes.medium)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                if (item.username.isNotBlank()) {
                    Text(
                        text = item.username,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            // Strength Badge
            Surface(
                shape = Shapes.medium,
                color = when (strength) {
                    PasswordGenerator.PasswordStrength.VERY_WEAK, PasswordGenerator.PasswordStrength.WEAK -> HealthRiskRed.copy(alpha = 0.12f)
                    PasswordGenerator.PasswordStrength.FAIR -> HealthRefusedYellow.copy(alpha = 0.12f)
                    else -> EmeraldPrimary.copy(alpha = 0.12f)
                }
            ) {
                Text(
                    text = strength.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = when (strength) {
                            PasswordGenerator.PasswordStrength.VERY_WEAK, PasswordGenerator.PasswordStrength.WEAK -> HealthRiskRed
                            PasswordGenerator.PasswordStrength.FAIR -> HealthRefusedYellow
                            else -> EmeraldPrimary
                        }
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onFixClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Fix or edit password",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
