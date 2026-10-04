package com.example.adere.presentation.screens.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.presentation.components.AdereTopBar
import com.example.adere.presentation.screens.settings.components.SettingsItemCard
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.SecurityOrange

enum class SettingsCategory {
    SECURITY,
    PRIVACY,
    BACKUP_RESTORE,
    APPEARANCE,
    GENERATOR,
    NOTIFICATIONS,
    HELP_SUPPORT,
    ABOUT,
    DANGER_ZONE
}

@Composable
fun SettingsMainScreen(
    totalCredentialsCount: Int,
    favoritesCount: Int,
    isVaultProtected: Boolean,
    onNavigateCategory: (SettingsCategory) -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            AdereTopBar(
                title = "Settings",
                subtitle = "Vault · Security · Preferences"
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Vault status banner ───────────────────────────────────────────
            VaultStatusBanner(
                isVaultProtected = isVaultProtected,
                totalCredentialsCount = totalCredentialsCount,
                favoritesCount = favoritesCount
            )

            // ── Security & protection ─────────────────────────────────────────
            SettingsSectionTitle("Security & Protection")

            SettingsItemCard(
                icon = Icons.Default.Security,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Security",
                subtitle = "Biometrics, auto-lock timer, master password & recovery key",
                onClick = { onNavigateCategory(SettingsCategory.SECURITY) },
                testTag = "settings_cat_security"
            )

            SettingsItemCard(
                icon = Icons.Default.PrivacyTip,
                iconTint = SecurityOrange,
                iconBg = SecurityOrange.copy(alpha = 0.1f),
                title = "Privacy & Content Protection",
                subtitle = "Screenshot lock, clipboard auto-clear & recent apps mask",
                onClick = { onNavigateCategory(SettingsCategory.PRIVACY) },
                testTag = "settings_cat_privacy"
            )

            SettingsItemCard(
                icon = Icons.Default.Save,
                iconTint = Color(0xFF0EA5E9),
                iconBg = Color(0xFF0EA5E9).copy(alpha = 0.1f),
                title = "Backup & Restore",
                subtitle = "Encrypted backups, vault restore & emergency PDF export",
                onClick = { onNavigateCategory(SettingsCategory.BACKUP_RESTORE) },
                testTag = "settings_cat_backup"
            )

            // ── Preferences ───────────────────────────────────────────────────
            SettingsSectionTitle("Preferences")

            SettingsItemCard(
                icon = Icons.Default.ColorLens,
                iconTint = Color(0xFFA855F7),
                iconBg = Color(0xFFA855F7).copy(alpha = 0.1f),
                title = "Appearance & Themes",
                subtitle = "Color palettes, light & dark vault modes",
                onClick = { onNavigateCategory(SettingsCategory.APPEARANCE) },
                testTag = "settings_cat_appearance"
            )

            SettingsItemCard(
                icon = Icons.Default.Key,
                iconTint = Color(0xFF10B981),
                iconBg = Color(0xFF10B981).copy(alpha = 0.1f),
                title = "Password Generator",
                subtitle = "Default length, character sets & exclusion rules",
                onClick = { onNavigateCategory(SettingsCategory.GENERATOR) },
                testTag = "settings_cat_generator"
            )

            SettingsItemCard(
                icon = Icons.Default.Notifications,
                iconTint = SecurityOrange,
                iconBg = SecurityOrange.copy(alpha = 0.1f),
                title = "Notifications & Alerts",
                subtitle = "Security alerts & system notification permissions",
                onClick = { onNavigateCategory(SettingsCategory.NOTIFICATIONS) },
                testTag = "settings_cat_notifications"
            )

            // ── Information & support ─────────────────────────────────────────
            SettingsSectionTitle("Information & Support")

            SettingsItemCard(
                icon = Icons.Default.HelpOutline,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Help & Support",
                subtitle = "FAQ, zero-knowledge security guide & contact",
                onClick = { onNavigateCategory(SettingsCategory.HELP_SUPPORT) },
                testTag = "settings_cat_help"
            )

            SettingsItemCard(
                icon = Icons.Default.Info,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "About ADERE",
                subtitle = "Version 1.0.0 · Architecture · Terms & Privacy",
                onClick = { onNavigateCategory(SettingsCategory.ABOUT) },
                testTag = "settings_cat_about"
            )

            // ── Danger zone ───────────────────────────────────────────────────
            SettingsSectionTitle("Danger Zone")

            SettingsItemCard(
                icon = Icons.Default.DeleteForever,
                iconTint = HealthRiskRed,
                iconBg = HealthRiskRed.copy(alpha = 0.1f),
                title = "Danger Zone",
                subtitle = "Permanently destroy all vault data & factory reset",
                onClick = { onNavigateCategory(SettingsCategory.DANGER_ZONE) },
                testTag = "settings_cat_danger"
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ─── Vault status banner ──────────────────────────────────────────────────────

@Composable
private fun VaultStatusBanner(
    isVaultProtected: Boolean,
    totalCredentialsCount: Int,
    favoritesCount: Int
) {
    val primary = MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primary.copy(alpha = 0.07f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(primary.copy(alpha = 0.13f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = primary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isVaultProtected) "Vault Active" else "Vault Locked",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "AES-256-GCM · Zero-knowledge encrypted",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Stats row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(11.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatPill(
                        icon = Icons.Default.Lock,
                        iconColor = primary,
                        value = "$totalCredentialsCount",
                        label = "Credentials"
                    )

                    Box(
                        modifier = Modifier
                            .size(1.dp, 22.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    )

                    StatPill(
                        icon = Icons.Default.Star,
                        iconColor = Color(0xFFF59E0B),
                        value = "$favoritesCount",
                        label = "Favorites"
                    )
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    value: String,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(17.dp)
        )
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
