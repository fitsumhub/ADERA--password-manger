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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.adere.presentation.screens.settings.components.SettingsSwitchItem
import com.example.adere.presentation.screens.settings.components.SettingsValueItem
import com.example.ui.theme.SecurityOrange

@Composable
fun PrivacySettingsScreen(
    screenshotProtectionEnabled: Boolean,
    appMaskingEnabled: Boolean,
    clipboardAutoClearSeconds: Int,
    onToggleScreenshotProtection: (Boolean) -> Unit,
    onToggleAppMasking: (Boolean) -> Unit,
    onSetClipboardAutoClear: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Privacy & Content Protection",
                subtitle = "Screenshot lock · Clipboard auto-clear · App masking",
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
            SettingsSectionTitle("Screen Protection")

            SettingsSwitchItem(
                icon = Icons.Default.Screenshot,
                iconTint = SecurityOrange,
                iconBg = SecurityOrange.copy(alpha = 0.1f),
                title = "Screenshot Protection",
                subtitle = "Block screenshots & screen recording when vault is open",
                checked = screenshotProtectionEnabled,
                onCheckedChange = onToggleScreenshotProtection,
                testTag = "privacy_switch_screenshot"
            )

            SettingsSwitchItem(
                icon = Icons.Default.Visibility,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Recent Apps Masking",
                subtitle = "Hide vault content in Android's recent apps switcher",
                checked = appMaskingEnabled,
                onCheckedChange = onToggleAppMasking,
                testTag = "privacy_switch_masking"
            )

            // ── Clipboard protection ──────────────────────────────────────────
            SettingsSectionTitle("Clipboard Protection")

            SettingsValueItem(
                icon = Icons.Default.ContentCopy,
                iconTint = Color(0xFF10B981),
                iconBg = Color(0xFF10B981).copy(alpha = 0.1f),
                title = "Clipboard Auto-Clear",
                subtitle = "Automatically clear copied passwords from system clipboard",
                valueText = formatClipboardClearLabel(clipboardAutoClearSeconds),
                onClick = { /* Show clipboard clear dialog */ },
                testTag = "privacy_clipboard_item"
            )

            // ── Data retention ────────────────────────────────────────────────
            SettingsSectionTitle("Data Retention")

            SettingsSwitchItem(
                icon = Icons.Default.History,
                iconTint = Color(0xFF6366F1),
                iconBg = Color(0xFF6366F1).copy(alpha = 0.1f),
                title = "Clear Search History",
                subtitle = "Auto-clear vault search terms after session ends",
                checked = true, // Could be configurable
                onCheckedChange = { /* Handle */ },
                testTag = "privacy_switch_search_history"
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

private fun formatClipboardClearLabel(seconds: Int): String = when (seconds) {
    0   -> "Never"
    30  -> "30 sec"
    60  -> "1 min"
    120 -> "2 min"
    300 -> "5 min"
    else -> "${seconds}s"
}