package com.example.adere.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.crypto.PasswordHealthAnalyzer
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import com.example.adere.presentation.components.BrandIconHelper
import com.example.ui.theme.Shapes
import com.example.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
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
    val recentItems = remember(items) {
        items.sortedByDescending { it.updatedAt }.take(5)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // App name + lock icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ADERA",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    IconButton(
                        onClick = onLockClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock vault",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                // Personalized greeting subtitle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Vault is secure  •  ${items.size} items",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // ── Security Health Card ──────────────────────────────────────────
            item {
                SecurityHealthCard(
                    healthReport = healthReport,
                    onClick = onNavigateToSecurity
                )
            }

            // ── Quick Actions Row ─────────────────────────────────────────────
            item {
                QuickActionsRow(onNavigateToAddItem = onNavigateToAddItem)
            }

            // ── Categories ───────────────────────────────────────────────────
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Categories",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        TextButton(onClick = { onNavigateToCategory(VaultCategory.ALL) }) {
                            Text(
                                text = "See all",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(end = 4.dp)
                    ) {
                        item {
                            CategoryTile(
                                title = "Social",
                                icon = Icons.Default.Share,
                                accentColor = Color(0xFF2563EB),
                                onClick = { onNavigateToCategory(VaultCategory.SOCIAL) }
                            )
                        }
                        item {
                            CategoryTile(
                                title = "Banking",
                                icon = Icons.Default.AccountBalance,
                                accentColor = Color(0xFF0D9488),
                                onClick = { onNavigateToCategory(VaultCategory.BANKING) }
                            )
                        }
                        item {
                            CategoryTile(
                                title = "Crypto",
                                icon = Icons.Default.CurrencyBitcoin,
                                accentColor = Color(0xFFF7931A),
                                onClick = { onNavigateToCategory(VaultCategory.CRYPTO) }
                            )
                        }
                        item {
                            CategoryTile(
                                title = "Notes",
                                icon = Icons.AutoMirrored.Filled.Note,
                                accentColor = Color(0xFFEC4899),
                                onClick = { onNavigateToCategory(VaultCategory.NOTES) }
                            )
                        }
                        item {
                            CategoryTile(
                                title = "Email",
                                icon = Icons.Default.Email,
                                accentColor = Color(0xFF4285F4),
                                onClick = { onNavigateToCategory(VaultCategory.EMAIL) }
                            )
                        }
                        item {
                            CategoryTile(
                                title = "WiFi",
                                icon = Icons.Default.Wifi,
                                accentColor = Color(0xFF0EA5E9),
                                onClick = { onNavigateToCategory(VaultCategory.WIFI) }
                            )
                        }
                    }
                }
            }

            // ── Recently Updated ─────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Recently updated",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    TextButton(onClick = { onNavigateToCategory(VaultCategory.ALL) }) {
                        Text(
                            text = "See all",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            if (recentItems.isNotEmpty()) {
                items(recentItems, key = { it.id }) { item ->
                    VaultItemRow(
                        item = item,
                        onClick = { onNavigateToItemDetail(item.id) },
                        onCopy = { onQuickCopy(item) }
                    )
                }
            } else {
                item {
                    EmptyVaultPrompt(
                        onAddItem = { onNavigateToAddItem(VaultCategory.SOCIAL) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(88.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Security Health Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SecurityHealthCard(
    healthReport: PasswordHealthAnalyzer.VaultHealthReport,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = Shapes.large
            )
            .clickable(onClick = onClick)
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Icon + title/subtitle + score
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(Shapes.medium)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Security Health",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = healthReport.overallStatus.label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Text(
                    text = "${healthReport.healthScorePercent}%",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 32.sp
                    )
                )
            }

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth((healthReport.healthScorePercent / 100f).coerceIn(0f, 1f))
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            // Summary line
            Text(
                text = if (healthReport.weakCount + healthReport.reusedCount == 0) {
                    "All passwords are strong"
                } else {
                    "${healthReport.weakCount} weak · ${healthReport.reusedCount} reused"
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Quick Actions Row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsRow(onNavigateToAddItem: (VaultCategory) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionChip(
            label = "Password",
            icon = Icons.Default.Lock,
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToAddItem(VaultCategory.SOCIAL) }
        )
        QuickActionChip(
            label = "Wallet",
            icon = Icons.Default.AccountBalanceWallet,
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToAddItem(VaultCategory.CRYPTO) }
        )
        QuickActionChip(
            label = "Note",
            icon = Icons.AutoMirrored.Filled.Note,
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToAddItem(VaultCategory.NOTES) }
        )
        QuickActionChip(
            label = "WiFi",
            icon = Icons.Default.Wifi,
            modifier = Modifier.weight(1f),
            onClick = { onNavigateToAddItem(VaultCategory.WIFI) }
        )
    }
}

@Composable
private fun QuickActionChip(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(Shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), Shapes.medium)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Category Tile
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CategoryTile(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(88.dp)
            .height(80.dp)
            .clip(Shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), Shapes.medium)
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vault Item Row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun VaultItemRow(
    item: VaultItem,
    onClick: () -> Unit,
    onCopy: () -> Unit
) {
    val brandDrawable = remember(item.title, item.payload.url) {
        BrandIconHelper.resolveBrandDrawable(item.title, item.payload.url)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes.medium)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), Shapes.medium)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(Shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (brandDrawable != null) {
                    Image(
                        painter = painterResource(id = brandDrawable),
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = BrandIconHelper.getCategoryFallbackIcon(item.category),
                        contentDescription = item.title,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Title + subtitle
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                val subtitle = item.username.ifBlank { item.payload.url.ifBlank { item.category.title } }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Copy icon
            IconButton(onClick = onCopy) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy password",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Empty State
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmptyVaultPrompt(onAddItem: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 56.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            modifier = Modifier.size(56.dp)
        )

        Text(
            text = "Your vault is empty",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Add your first secure credential",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextMuted
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .clip(Shapes.medium)
                .background(MaterialTheme.colorScheme.primary)
                .clickable(onClick = onAddItem)
                .padding(horizontal = 28.dp, vertical = 13.dp)
        ) {
            Text(
                text = "Add Item",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}
