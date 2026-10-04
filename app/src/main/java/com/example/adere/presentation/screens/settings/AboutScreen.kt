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
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Update
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.presentation.screens.settings.components.SettingsItemCard
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader

@Composable
fun AboutScreen(
    appVersion: String = "1.0.0",
    buildNumber: String = "100",
    onViewArchitecture: () -> Unit,
    onViewPrivacyPolicy: () -> Unit,
    onViewTermsOfService: () -> Unit,
    onViewLicenses: () -> Unit,
    onCheckUpdates: () -> Unit,
    onBackClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "About ADERE",
                subtitle = "Version $appVersion · Build $buildNumber",
                onBackClick = onBackClick
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── App info banner ───────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // App icon/logo placeholder
                        Box(
                            modifier = Modifier
                                .size(66.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "ADERE Password Manager",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Zero-Knowledge Encrypted Vault",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        // Version info
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            VersionPill("v$appVersion", primary)
                            VersionPill("Build $buildNumber", Color(0xFF6366F1))
                        }
                    }
                }
            }

            // ── Application info ──────────────────────────────────────────────
            SettingsSectionTitle("Application Information")

            SettingsItemCard(
                icon = Icons.Default.Architecture,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Architecture & Security",
                subtitle = "Technical overview of encryption and zero-knowledge design",
                onClick = onViewArchitecture,
                testTag = "about_architecture_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Update,
                iconTint = Color(0xFF10B981),
                iconBg = Color(0xFF10B981).copy(alpha = 0.1f),
                title = "Check for Updates",
                subtitle = "Ensure you have the latest security patches and features",
                onClick = onCheckUpdates,
                testTag = "about_updates_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Code,
                iconTint = Color(0xFF6366F1),
                iconBg = Color(0xFF6366F1).copy(alpha = 0.1f),
                title = "Open Source Licenses",
                subtitle = "Third-party libraries and attribution information",
                onClick = onViewLicenses,
                testTag = "about_licenses_item"
            )

            // ── Legal & privacy ───────────────────────────────────────────────
            SettingsSectionTitle("Legal & Privacy")

            SettingsItemCard(
                icon = Icons.Default.Policy,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Privacy Policy",
                subtitle = "How ADERE handles your data with zero-knowledge principles",
                onClick = onViewPrivacyPolicy,
                testTag = "about_privacy_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Info,
                iconTint = Color(0xFFF59E0B),
                iconBg = Color(0xFFF59E0B).copy(alpha = 0.1f),
                title = "Terms of Service",
                subtitle = "Legal agreement covering usage and liability",
                onClick = onViewTermsOfService,
                testTag = "about_terms_item"
            )

            // ── Footer info ───────────────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Built with security-first principles",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    Text(
                        text = "© 2024 ADERE. AES-256-GCM · PBKDF2 · Argon2id",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun VersionPill(
    text: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = color
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}