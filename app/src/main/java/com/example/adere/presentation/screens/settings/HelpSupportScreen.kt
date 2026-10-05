package com.example.adere.presentation.screens.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.presentation.screens.settings.components.SettingsItemCard
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SecurityOrange
import com.example.ui.theme.TotalSecurityPrimary

@Composable
fun HelpSupportScreen(
    onViewFAQ: () -> Unit = {},
    onViewSecurityGuide: () -> Unit = {},
    onContactSupport: () -> Unit = {},
    onReportBug: () -> Unit = {},
    onRateApp: () -> Unit = {},
    onJoinCommunity: () -> Unit = {},
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val primary = MaterialTheme.colorScheme.primary

    var showFaqDialog by remember { mutableStateOf(false) }
    var showSecurityGuideDialog by remember { mutableStateOf(false) }
    var showTutorialDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }

    fun sendEmail(subject: String, body: String = "") {
        val email = "support@aderevault.com"
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Send Email"))
        } catch (_: ActivityNotFoundException) {
            clipboardManager.setText(AnnotatedString(email))
            Toast.makeText(context, "Support email copied: $email", Toast.LENGTH_LONG).show()
        }
    }

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            clipboardManager.setText(AnnotatedString(url))
            Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    fun rateApp() {
        val packageName = context.packageName
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (_: ActivityNotFoundException) {
            openUrl("https://play.google.com/store/apps/details?id=$packageName")
        }
    }

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Help & Support",
                subtitle = "FAQ · Security guide · Support contact · Community",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Documentation & Guides ────────────────────────────────────────
            SettingsSectionTitle("Documentation & Guides")

            SettingsItemCard(
                icon = Icons.Default.QuestionMark,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.12f),
                title = "Frequently Asked Questions",
                subtitle = "Common questions regarding vault security, offline mode, and recovery",
                onClick = { showFaqDialog = true },
                testTag = "help_faq_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Security,
                iconTint = EmeraldPrimary,
                iconBg = EmeraldPrimary.copy(alpha = 0.12f),
                title = "Zero-Knowledge Security Guide",
                subtitle = "Deep dive into AES-256-GCM encryption and hardware KeyStore protection",
                onClick = { showSecurityGuideDialog = true },
                testTag = "help_security_guide_item"
            )

            SettingsItemCard(
                icon = Icons.AutoMirrored.Filled.Help,
                iconTint = TotalSecurityPrimary,
                iconBg = TotalSecurityPrimary.copy(alpha = 0.12f),
                title = "Getting Started Tutorial",
                subtitle = "Quick visual guide on adding items, biometrics, and backing up",
                onClick = { showTutorialDialog = true },
                testTag = "help_tutorial_item"
            )

            // ── Support & Feedback ────────────────────────────────────────────
            SettingsSectionTitle("Support & Feedback")

            SettingsItemCard(
                icon = Icons.Default.ContactSupport,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.12f),
                title = "Contact Support",
                subtitle = "Reach out to technical support via email: support@aderevault.com",
                onClick = {
                    sendEmail(
                        subject = "[ADERE Support] Technical Inquiry",
                        body = "\n\n--- Device Info ---\nApp Version: 1.0.0\nDevice: ${Build.MANUFACTURER} ${Build.MODEL}\nAndroid: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
                    )
                },
                testTag = "help_contact_item"
            )

            SettingsItemCard(
                icon = Icons.Default.BugReport,
                iconTint = Color(0xFFF59E0B),
                iconBg = Color(0xFFF59E0B).copy(alpha = 0.12f),
                title = "Report a Bug",
                subtitle = "Submit issue report with automatic device diagnostic details",
                onClick = {
                    sendEmail(
                        subject = "[ADERE Bug Report] Issue Description",
                        body = "Please describe the problem:\n\nSteps to reproduce:\n1.\n2.\n3.\n\n--- Diagnostic Info ---\nApp Version: 1.0.0 (Build 100)\nDevice: ${Build.MANUFACTURER} ${Build.MODEL}\nAndroid OS: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})\nSecurity Patch: ${Build.VERSION.SECURITY_PATCH}"
                    )
                },
                testTag = "help_bug_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Lightbulb,
                iconTint = Color(0xFF0EA5E9),
                iconBg = Color(0xFF0EA5E9).copy(alpha = 0.12f),
                title = "Feature Suggestion",
                subtitle = "Suggest new security features or user interface improvements",
                onClick = {
                    sendEmail(
                        subject = "[ADERE Feature Suggestion]",
                        body = "I would love to see this feature in ADERE:\n\nWhy this would be useful:\n"
                    )
                },
                testTag = "help_feature_item"
            )

            // ── Community & Rating ────────────────────────────────────────────
            SettingsSectionTitle("Community & Rating")

            SettingsItemCard(
                icon = Icons.Default.Star,
                iconTint = Color(0xFFF59E0B),
                iconBg = Color(0xFFF59E0B).copy(alpha = 0.12f),
                title = "Rate ADERE on Google Play",
                subtitle = "Support independent zero-knowledge offline security software",
                onClick = { rateApp() },
                testTag = "help_rate_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Forum,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.12f),
                title = "Community Forum & Source",
                subtitle = "Join Discussions, verify security audits & collaborate on GitHub",
                onClick = { openUrl("https://github.com") },
                testTag = "help_community_item"
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // ── FAQ Dialog ─────────────────────────────────────────────────────────────
    if (showFaqDialog) {
        val faqs = listOf(
            "How does zero-knowledge offline encryption work?" to
                    "ADERE is 100% offline. It has zero INTERNET permissions declared in AndroidManifest. Your master password derives an AES-256-GCM encryption key using PBKDF2 with 100,000 iterations locally. Nobody, including the developers, can access your data.",

            "What if I forget my Master Password?" to
                    "Because of zero-knowledge architecture, lost master passwords cannot be reset over email. However, your 24-character Master Recovery Key allows you to safely reset your Master Password without losing your encrypted records.",

            "How does Biometric Unlock work securely?" to
                    "When biometric unlock is enabled, your Data Encryption Key (DEK) is protected using the hardware-backed Android KeyStore. Only your authorized biometric sensor (fingerprint or face) can request hardware decryption.",

            "How do I backup or transfer my vault to another device?" to
                    "Go to Settings > Backup & PDF Export. You can export a strongly encrypted '.adere' file protected with a custom passphrase. You can then move this file via USB, SD card, or local Bluetooth to another device and restore it.",

            "Are my passwords in the system clipboard safe?" to
                    "ADERE includes an automatic clipboard scrub timer (configurable in Settings > Privacy). Copied credentials are automatically wiped from memory after your configured duration (default: 30 seconds)."
        )

        var expandedIndex by remember { mutableIntStateOf(-1) }

        AlertDialog(
            onDismissRequest = { showFaqDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QuestionMark, contentDescription = null, tint = primary, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Frequently Asked Questions", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    faqs.forEachIndexed { index, (q, a) ->
                        val isExpanded = expandedIndex == index
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedIndex = if (isExpanded) -1 else index },
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = q,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = primary
                                    )
                                }
                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = fadeIn() + expandVertically(),
                                    exit = fadeOut() + shrinkVertically()
                                ) {
                                    Text(
                                        text = a,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 18.sp
                                        ),
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showFaqDialog = false }) { Text("Close") }
            }
        )
    }

    // ── Security Guide Dialog ──────────────────────────────────────────────────
    if (showSecurityGuideDialog) {
        AlertDialog(
            onDismissRequest = { showSecurityGuideDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Zero-Knowledge Architecture", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecurityGuideSection(
                        title = "1. Pure Offline Operation",
                        desc = "ADERE does not contain internet permissions. No telemetry, no trackers, no remote analytics, and zero cloud synchronization."
                    )
                    SecurityGuideSection(
                        title = "2. AES-256-GCM Encryption",
                        desc = "Each credential payload is encrypted using Galois/Counter Mode (GCM) providing both confidentiality and cryptographic authenticity against tampering."
                    )
                    SecurityGuideSection(
                        title = "3. PBKDF2 Key Derivation",
                        desc = "Your master password is never stored. A 256-bit encryption key is derived using PBKDF2 with HMAC-SHA256 and 100,000 computation iterations."
                    )
                    SecurityGuideSection(
                        title = "4. Hardware Android KeyStore",
                        desc = "Biometric credentials bind the master encryption key to your device's Secure Element (StrongBox / TEE) hardware."
                    )
                    SecurityGuideSection(
                        title = "5. In-Memory Hardening",
                        desc = "Passphrases and keys in memory are automatically wiped upon inactivity or background session lock."
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showSecurityGuideDialog = false }) { Text("Understood") }
            }
        )
    }

    // ── Getting Started Tutorial Dialog ────────────────────────────────────────
    if (showTutorialDialog) {
        AlertDialog(
            onDismissRequest = { showTutorialDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Help, contentDescription = null, tint = TotalSecurityPrimary, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Getting Started Guide", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TutorialStepCard(step = "1", title = "Save Master Recovery Key", desc = "Keep your 24-character key written on paper in a secure location. It is your only recovery safety net.")
                    TutorialStepCard(step = "2", title = "Enable Biometric Unlock", desc = "Go to Settings > Security to turn on fingerprint or face unlock for instant vault access.")
                    TutorialStepCard(step = "3", title = "Store Passwords & 2FA TOTP", desc = "Tap the '+' tab to add logins, credit cards, Wi-Fi networks, and offline two-factor authenticator codes.")
                    TutorialStepCard(step = "4", title = "Export Cold Backups", desc = "Regularly export an encrypted .adere backup or PDF summary from Settings > Backup & PDF Export.")
                }
            },
            confirmButton = {
                Button(onClick = { showTutorialDialog = false }) { Text("Got It") }
            }
        )
    }
}

@Composable
private fun SecurityGuideSection(title: String, desc: String) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(3.dp))
        Text(desc, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp))
    }
}

@Composable
private fun TutorialStepCard(step: String, title: String, desc: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(TotalSecurityPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(step, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(2.dp))
                Text(desc, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
        }
    }
}
