package com.example.adere.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.adere.presentation.screens.settings.components.SettingsOptionDialog
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.adere.presentation.screens.settings.components.SettingsSwitchItem
import com.example.adere.presentation.screens.settings.components.SettingsValueItem
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SecurityOrange

@Composable
fun PrivacySettingsScreen(
    screenshotProtectionEnabled: Boolean,
    appMaskingEnabled: Boolean,
    clipboardAutoClearSeconds: Int,
    onToggleScreenshotProtection: (Boolean) -> Unit,
    onToggleAppMasking: (Boolean) -> Unit,
    onSetClipboardAutoClear: (Int) -> Unit,
    onRequestManageAdPrivacy: () -> Unit = {},
    onBackClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    var showClipboardDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Privacy & Content Protection",
                subtitle = "Screenshot lock · Clipboard auto-clear · Recent apps masking",
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ── Screen protection ─────────────────────────────────────────────
            SettingsSectionTitle("Screen & Visual Protection")

            SettingsSwitchItem(
                icon = Icons.Default.Screenshot,
                iconTint = SecurityOrange,
                iconBg = SecurityOrange.copy(alpha = 0.12f),
                title = "Screenshot Protection",
                subtitle = "Block screenshots & screen recording video streams when vault is open",
                checked = screenshotProtectionEnabled,
                onCheckedChange = onToggleScreenshotProtection,
                testTag = "privacy_switch_screenshot"
            )

            SettingsSwitchItem(
                icon = Icons.Default.Visibility,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.12f),
                title = "Recent Apps Masking",
                subtitle = "Hide vault content and secrets in Android's recent apps switcher thumbnail",
                checked = appMaskingEnabled,
                onCheckedChange = onToggleAppMasking,
                testTag = "privacy_switch_masking"
            )

            // ── Clipboard protection ──────────────────────────────────────────
            SettingsSectionTitle("Clipboard Memory Hygiene")

            SettingsValueItem(
                icon = Icons.Default.ContentCopy,
                iconTint = EmeraldPrimary,
                iconBg = EmeraldPrimary.copy(alpha = 0.12f),
                title = "Clipboard Auto-Clear Timer",
                subtitle = "Automatically wipe copied passwords and secrets from system clipboard",
                valueText = formatClipboardClearLabel(clipboardAutoClearSeconds),
                onClick = { showClipboardDialog = true },
                testTag = "privacy_clipboard_item"
            )

            // ── Advertising Privacy & Choices ─────────────────────────────────
            SettingsSectionTitle("Advertising Privacy & Choices")

            SettingsValueItem(
                icon = Icons.Default.PrivacyTip,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.12f),
                title = "Ad Privacy & Consent Choices",
                subtitle = "Manage Google UMP ad personalization, GDPR/EEA preferences, or revoke consent",
                valueText = "Manage",
                onClick = onRequestManageAdPrivacy,
                testTag = "privacy_ad_consent_item"
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showClipboardDialog) {
        SettingsOptionDialog(
            title = "Clipboard Auto-Clear Timer",
            options = listOf(
                0 to "Never auto-clear",
                15 to "15 seconds",
                30 to "30 seconds (Recommended)",
                60 to "1 minute",
                120 to "2 minutes",
                300 to "5 minutes"
            ),
            selectedOption = clipboardAutoClearSeconds,
            onSelectOption = { onSetClipboardAutoClear(it) },
            onDismiss = { showClipboardDialog = false }
        )
    }
}

private fun formatClipboardClearLabel(seconds: Int): String = when (seconds) {
    0   -> "Never"
    15  -> "15 sec"
    30  -> "30 sec"
    60  -> "1 min"
    120 -> "2 min"
    300 -> "5 min"
    else -> "${seconds}s"
}
