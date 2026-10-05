package com.example.adere.presentation.screens.settings
import com.example.ui.theme.Shapes
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.presentation.components.AdereTopBar
import com.example.adere.presentation.screens.settings.components.SettingsItemCard
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.SecurityOrange

enum class SettingsCategory {
    SECURITY,
    PRIVACY,
    BACKUP_RESTORE,
    APPEARANCE,
    NOTIFICATIONS,
    HELP_SUPPORT,
    ABOUT
}

@Composable
fun SettingsMainScreen(
    totalCredentialsCount: Int,
    favoritesCount: Int,
    isVaultProtected: Boolean,
    onNavigateCategory: (SettingsCategory) -> Unit,
    onQuickLock: () -> Unit = {}
) {
    val primary = MaterialTheme.colorScheme.primary
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AdereTopBar(
                title = "Settings",
                subtitle = "Vault Security · System Controls · Preferences"
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
            // ── Vault Status & Health Hero Banner ────────────────────────────
            VaultStatusBanner(
                isVaultProtected = isVaultProtected,
                totalCredentialsCount = totalCredentialsCount,
                favoritesCount = favoritesCount,
                onNavigateSecurity = { onNavigateCategory(SettingsCategory.SECURITY) },
                onQuickLock = onQuickLock
            )

            // ── Settings Search Bar ─────────────────────────────────────────
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Filter settings or features...") },
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
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                shape = Shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_search_field")
            )

            val query = searchQuery.trim().lowercase()

            // ── Security & Protection ───────────────────────────────────────
            if (query.isEmpty() || listOf("security", "biometric", "password", "lock", "recovery").any { it.contains(query) }) {
                SettingsSectionTitle("Security & Protection")

                SettingsItemCard(
                    icon = Icons.Default.Security,
                    iconTint = primary,
                    iconBg = primary.copy(alpha = 0.12f),
                    title = "Security & Master Password",
                    subtitle = "Biometrics, auto-lock timer, master password & 24-key recovery",
                    badgeText = "Grade A+",
                    badgeColor = EmeraldPrimary,
                    onClick = { onNavigateCategory(SettingsCategory.SECURITY) },
                    testTag = "settings_cat_security"
                )
            }

            if (query.isEmpty() || listOf("privacy", "screenshot", "clipboard", "mask", "content").any { it.contains(query) }) {
                SettingsItemCard(
                    icon = Icons.Default.PrivacyTip,
                    iconTint = SecurityOrange,
                    iconBg = SecurityOrange.copy(alpha = 0.12f),
                    title = "Privacy & Content Protection",
                    subtitle = "Screenshot lock, clipboard auto-clear & recent apps switcher mask",
                    badgeText = "Protected",
                    badgeColor = SecurityOrange,
                    onClick = { onNavigateCategory(SettingsCategory.PRIVACY) },
                    testTag = "settings_cat_privacy"
                )
            }

            if (query.isEmpty() || listOf("backup", "restore", "pdf", "export", "data").any { it.contains(query) }) {
                SettingsItemCard(
                    icon = Icons.Default.Save,
                    iconTint = Color(0xFF0EA5E9),
                    iconBg = Color(0xFF0EA5E9).copy(alpha = 0.12f),
                    title = "Backup & PDF Export",
                    subtitle = "Encrypted .adere backups, vault recovery & PDF summary sheet",
                    badgeText = "PDF Ready",
                    badgeColor = Color(0xFF0EA5E9),
                    onClick = { onNavigateCategory(SettingsCategory.BACKUP_RESTORE) },
                    testTag = "settings_cat_backup"
                )
            }



            // ── Preferences ─────────────────────────────────────────────────
            if (query.isEmpty() || listOf("appearance", "theme", "color", "dark", "palette").any { it.contains(query) }) {
                SettingsSectionTitle("Preferences")

                SettingsItemCard(
                    icon = Icons.Default.ColorLens,
                    iconTint = Color(0xFFA855F7),
                    iconBg = Color(0xFFA855F7).copy(alpha = 0.12f),
                    title = "Appearance & Themes",
                    subtitle = "Color palettes, custom themes, dark & light vault modes",
                    badgeText = "Active",
                    badgeColor = Color(0xFFA855F7),
                    onClick = { onNavigateCategory(SettingsCategory.APPEARANCE) },
                    testTag = "settings_cat_appearance"
                )
            }



            if (query.isEmpty() || listOf("notification", "alert", "permission", "system").any { it.contains(query) }) {
                SettingsItemCard(
                    icon = Icons.Default.Notifications,
                    iconTint = SecurityOrange,
                    iconBg = SecurityOrange.copy(alpha = 0.12f),
                    title = "Notifications & Security Alerts",
                    subtitle = "Security breach reminders & system notification permissions",
                    badgeText = "Enabled",
                    badgeColor = SecurityOrange,
                    onClick = { onNavigateCategory(SettingsCategory.NOTIFICATIONS) },
                    testTag = "settings_cat_notifications"
                )
            }

            // ── Information & Support ───────────────────────────────────────
            if (query.isEmpty() || listOf("help", "faq", "guide", "contact", "support").any { it.contains(query) }) {
                SettingsSectionTitle("Information & Support")

                SettingsItemCard(
                    icon = Icons.Default.HelpOutline,
                    iconTint = primary,
                    iconBg = primary.copy(alpha = 0.12f),
                    title = "Help & Support",
                    subtitle = "FAQ, zero-knowledge security guide & technical contact",
                    onClick = { onNavigateCategory(SettingsCategory.HELP_SUPPORT) },
                    testTag = "settings_cat_help"
                )
            }

            if (query.isEmpty() || listOf("about", "version", "licenses", "terms", "policy").any { it.contains(query) }) {
                SettingsItemCard(
                    icon = Icons.Default.Info,
                    iconTint = primary,
                    iconBg = primary.copy(alpha = 0.12f),
                    title = "About ADERE",
                    subtitle = "Version 1.0.0 · Architecture · Terms & Privacy Policy",
                    onClick = { onNavigateCategory(SettingsCategory.ABOUT) },
                    testTag = "settings_cat_about"
                )
            }



            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ─── Vault status banner ──────────────────────────────────────────────────────

@Composable
private fun VaultStatusBanner(
    isVaultProtected: Boolean,
    totalCredentialsCount: Int,
    favoritesCount: Int,
    onNavigateSecurity: () -> Unit,
    onQuickLock: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primary.copy(alpha = 0.08f),
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
                // Top status row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(Shapes.medium)
                            .background(primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isVaultProtected) "Vault Encrypted & Active" else "Vault Locked",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "AES-256-GCM · PBKDF2 / Argon2id · Zero-Knowledge",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Surface(
                        shape = Shapes.medium,
                        color = EmeraldPrimary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "Grade A+",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = EmeraldPrimary
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                // Stats row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatPill(
                        icon = Icons.Default.Lock,
                        iconColor = primary,
                        value = "$totalCredentialsCount",
                        label = "Stored Secrets"
                    )

                    Box(
                        modifier = Modifier
                            .size(1.dp, 24.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    )

                    StatPill(
                        icon = Icons.Default.Star,
                        iconColor = Color(0xFFF59E0B),
                        value = "$favoritesCount",
                        label = "Favorites"
                    )

                    Box(
                        modifier = Modifier
                            .size(1.dp, 24.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    )

                    StatPill(
                        icon = Icons.Default.Shield,
                        iconColor = EmeraldPrimary,
                        value = "100%",
                        label = "Offline Safety"
                    )
                }

                // Quick action bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onNavigateSecurity,
                        colors = ButtonDefaults.buttonColors(containerColor = primary),
                        shape = CircleShape,
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Security Audit", fontSize = 12.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = onQuickLock,
                        shape = CircleShape,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = HealthRiskRed)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Lock", modifier = Modifier.size(16.dp))
                            Text("Lock Vault", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    icon: ImageVector,
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
            modifier = Modifier.size(18.dp)
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
