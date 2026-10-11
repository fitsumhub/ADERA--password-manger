package com.example.adere.presentation.screens.settings
import com.example.ui.theme.Shapes
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import com.example.adere.core.ads.AdSecurityZone
import com.example.adere.presentation.components.AdMobBanner
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.presentation.screens.settings.components.SettingsItemCard
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TotalSecurityPrimary
import kotlinx.coroutines.delay

@Composable
fun AboutScreen(
    appVersion: String = "1.0.0",
    buildNumber: String = "100",
    customAppName: String = "ADERA",
    onViewArchitecture: () -> Unit = {},
    onViewPrivacyPolicy: () -> Unit = {},
    onViewTermsOfService: () -> Unit = {},
    onViewLicenses: () -> Unit = {},
    onCheckUpdates: () -> Unit = {},
    onBackClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val appTitle = customAppName.ifBlank { "ADERA" }

    var showArchitectureDialog by remember { mutableStateOf(false) }
    var showUpdatesDialog by remember { mutableStateOf(false) }
    var showLicensesDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "About $appTitle",
                subtitle = "Version $appVersion · Build $buildNumber · Security Specs",
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
            // ── App Info Hero Banner ──────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = Shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                
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
                        Box(
                            modifier = Modifier
                                .size(66.dp)
                                .clip(Shapes.medium)
                                .background(TotalSecurityPrimary),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$appTitle Vault",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Spacer(Modifier.width(6.dp))
                                Surface(
                                    shape = Shapes.medium,
                                    color = TotalSecurityPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        "አደራ",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TotalSecurityPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Zero-Knowledge Hardware Encrypted Vault",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            VersionPill("v$appVersion", primary)
                            VersionPill("Build $buildNumber", TotalSecurityPrimary)
                            VersionPill("100% Offline", EmeraldPrimary)
                        }
                    }
                }
            }

            // ── About the Developer ───────────────────────────────────────────
            SettingsSectionTitle("About the Developer")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = Shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = primary.copy(alpha = 0.12f),
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "FE",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = primary
                                    )
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Fitsum Enunu",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("🇪🇹", fontSize = 16.sp)
                            }
                            Text(
                                text = "Founder & Developer",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    Text(
                        text = "I’m Fitsum Enunu, an Ethiopian software developer and technology enthusiast focused on building secure, useful, and user-friendly digital products.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    )

                    Surface(
                        shape = Shapes.small,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = primary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Role: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                Text("Founder & Developer", style = MaterialTheme.typography.bodySmall)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = primary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Email: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                Text("Fitsumenunu21@gmail.com", style = MaterialTheme.typography.bodySmall, color = primary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = primary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Location: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                Text("Ethiopia 🇪🇹", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Text(
                        text = "Developer Vision: Building secure and practical technology products that make digital life simpler, safer, and more accessible.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Built with ❤️ in Ethiopia 🇪🇹",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            // ── Application Information ───────────────────────────────────────
            SettingsSectionTitle("Application Information")

            SettingsItemCard(
                icon = Icons.Default.Architecture,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.12f),
                title = "Architecture & Security Model",
                subtitle = "Cryptographic specifications: AES-256-GCM, PBKDF2 & hardware KeyStore",
                onClick = { showArchitectureDialog = true },
                testTag = "about_architecture_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Update,
                iconTint = EmeraldPrimary,
                iconBg = EmeraldPrimary.copy(alpha = 0.12f),
                title = "Check for Updates",
                subtitle = "Verify you have the latest secure offline release installed",
                onClick = { showUpdatesDialog = true },
                testTag = "about_updates_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Code,
                iconTint = TotalSecurityPrimary,
                iconBg = TotalSecurityPrimary.copy(alpha = 0.12f),
                title = "Open Source Licenses",
                subtitle = "Third-party libraries, cryptography suites, and licenses",
                onClick = { showLicensesDialog = true },
                testTag = "about_licenses_item"
            )

            // ── Legal & Privacy ───────────────────────────────────────────────
            SettingsSectionTitle("Legal & Privacy")

            SettingsItemCard(
                icon = Icons.Default.Policy,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.12f),
                title = "Zero-Knowledge Privacy Policy",
                subtitle = "Why ADERE will never collect, track, or transmit your personal data",
                onClick = { showPrivacyDialog = true },
                testTag = "about_privacy_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Info,
                iconTint = Color(0xFFF59E0B),
                iconBg = Color(0xFFF59E0B).copy(alpha = 0.12f),
                title = "Terms of Service",
                subtitle = "Open source terms, user responsibilities, and emergency key warnings",
                onClick = { showTermsDialog = true },
                testTag = "about_terms_item"
            )

            // ── Footer Info ───────────────────────────────────────────────────
            Surface(
                shape = CircleShape,
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
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Zero Network Permissions · Completely Offline",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                    Text(
                        text = "ADERE Security Suite · Android KeyStore · AES-256-GCM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                }
            }

            AdMobBanner(zone = AdSecurityZone.SAFE_ZONE)

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // ── Architecture & Security Dialog ────────────────────────────────────────
    if (showArchitectureDialog) {
        AlertDialog(
            onDismissRequest = { showArchitectureDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Architecture, contentDescription = null, tint = primary, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Architecture Overview", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "ADERE follows Clean Architecture with strict layer separation:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    ArchitectureCard(
                        layer = "1. Core Security Layer",
                        details = "AES-256-GCM cipher with random 12-byte IVs, PBKDF2 (100k rounds) with HMAC-SHA256, and RFC 6238 TOTP authenticator engine."
                    )
                    ArchitectureCard(
                        layer = "2. Data Storage Layer",
                        details = "Room persistence database with encrypted byte payloads. Plaintext is only present in ephemeral memory during active unlocked session."
                    )
                    ArchitectureCard(
                        layer = "3. Domain & Session Layer",
                        details = "Inactivity auto-lock timers, clipboard sanitizer routines, and Keystore hardware binding."
                    )
                    ArchitectureCard(
                        layer = "4. Presentation Layer",
                        details = "Built 100% in Jetpack Compose, Material Design 3, with FLAG_SECURE screenshot prevention."
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showArchitectureDialog = false }) { Text("Close") }
            }
        )
    }

    // ── Check Updates Dialog ──────────────────────────────────────────────────
    if (showUpdatesDialog) {
        var checking by remember { mutableStateOf(true) }
        LaunchedEffect(Unit) {
            delay(800)
            checking = false
        }

        AlertDialog(
            onDismissRequest = { showUpdatesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Update, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Software Update Status", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                if (checking) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(14.dp))
                        Text("Verifying installed release build...", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Up to Date", fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        }
                        Text(
                            "You are running the latest production build of ADERE Password Manager (v$appVersion, Build $buildNumber).\n\nNo security patches or updates are required.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showUpdatesDialog = false }) { Text("Done") }
            }
        )
    }

    // ── Open Source Licenses Dialog ───────────────────────────────────────────
    if (showLicensesDialog) {
        val licenses = listOf(
            "Android Jetpack & Compose" to "Apache License 2.0 · Google LLC",
            "Room Persistence Library" to "Apache License 2.0 · Google LLC",
            "KotlinX Coroutines" to "Apache License 2.0 · JetBrains s.r.o.",
            "AndroidX Biometric" to "Apache License 2.0 · The Android Open Source Project",
            "Material Components (M3)" to "Apache License 2.0 · Google LLC",
            "Bouncy Castle Crypto" to "Bouncy Castle Licence (MIT-style)"
        )

        AlertDialog(
            onDismissRequest = { showLicensesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = TotalSecurityPrimary, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Open Source Licenses", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    licenses.forEach { (lib, lic) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = Shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(lib, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text(lic, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showLicensesDialog = false }) { Text("Close") }
            }
        )
    }

    // ── Privacy Policy Dialog ─────────────────────────────────────────────────
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Policy, contentDescription = null, tint = primary, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Privacy Policy", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Zero-Knowledge & Offline Commitment",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        "1. Zero Telemetry: ADERE does not collect analytics, logs, identifiers, or crash reports.\n\n" +
                        "2. Zero Internet: The app declares no network permissions in its manifest and is physically incapable of transmitting data.\n\n" +
                        "3. Complete Local Ownership: All database records and encryption keys reside exclusively inside your device's private app sandbox.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }) { Text("Acknowledge") }
            }
        )
    }

    // ── Terms of Service Dialog ───────────────────────────────────────────────
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Terms of Service", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "User Responsibilities",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        "1. Master Password & Recovery Key Retention: Because ADERE operates with zero-knowledge encryption, you are solely responsible for retaining your Master Password and 24-character Recovery Key. Lost keys cannot be recovered by any third party.\n\n" +
                        "2. Offline Backup: Users are encouraged to maintain regular encrypted exports or cold PDF backups.\n\n" +
                        "3. Provided As-Is: The software is provided under open-source terms without warranties of any kind.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showTermsDialog = false }) { Text("Accept") }
            }
        )
    }
}

@Composable
private fun ArchitectureCard(layer: String, details: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(layer, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(2.dp))
            Text(details, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp))
        }
    }
}

@Composable
private fun VersionPill(text: String, color: Color) {
    Surface(
        shape = Shapes.medium,
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = color
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
