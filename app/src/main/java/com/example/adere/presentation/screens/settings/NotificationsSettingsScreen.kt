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
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.adere.presentation.screens.settings.components.SettingsItemCard
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.adere.presentation.screens.settings.components.SettingsSwitchItem
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.SecurityOrange

@Composable
fun NotificationsSettingsScreen(
    securityAlertsEnabled: Boolean,
    backupRemindersEnabled: Boolean,
    onToggleSecurityAlerts: (Boolean) -> Unit,
    onToggleBackupReminders: (Boolean) -> Unit,
    onOpenSystemNotificationSettings: () -> Unit,
    onBackClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Notifications & Alerts",
                subtitle = "Security alerts · Backup reminders · System permissions",
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
            // ── Security notifications ────────────────────────────────────────
            SettingsSectionTitle("Security Notifications")

            SettingsSwitchItem(
                icon = Icons.Default.Security,
                iconTint = SecurityOrange,
                iconBg = SecurityOrange.copy(alpha = 0.1f),
                title = "Security Alerts",
                subtitle = "Failed unlock attempts · Suspicious activity · Breach warnings",
                checked = securityAlertsEnabled,
                onCheckedChange = onToggleSecurityAlerts,
                testTag = "notif_switch_security"
            )

            SettingsSwitchItem(
                icon = Icons.Default.Warning,
                iconTint = HealthRiskRed,
                iconBg = HealthRiskRed.copy(alpha = 0.1f),
                title = "Breach Monitoring",
                subtitle = "Alert when your credentials appear in known data breaches",
                checked = true, // Could be configurable
                onCheckedChange = { /* Handle */ },
                testTag = "notif_switch_breach"
            )

            // ── Maintenance reminders ─────────────────────────────────────────
            SettingsSectionTitle("Maintenance Reminders")

            SettingsSwitchItem(
                icon = Icons.Default.NotificationImportant,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Backup Reminders",
                subtitle = "Weekly reminder to create encrypted vault backups",
                checked = backupRemindersEnabled,
                onCheckedChange = onToggleBackupReminders,
                testTag = "notif_switch_backup"
            )

            SettingsSwitchItem(
                icon = Icons.Default.NotificationsActive,
                iconTint = Color(0xFF10B981),
                iconBg = Color(0xFF10B981).copy(alpha = 0.1f),
                title = "Weak Password Alerts",
                subtitle = "Periodic scan for weak, reused or expired passwords",
                checked = true, // Could be configurable
                onCheckedChange = { /* Handle */ },
                testTag = "notif_switch_weak_passwords"
            )

            // ── System integration ────────────────────────────────────────────
            SettingsSectionTitle("System Integration")

            SettingsItemCard(
                icon = Icons.Default.Notifications,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "System Notification Settings",
                subtitle = "Configure ADERE notifications in Android settings",
                onClick = onOpenSystemNotificationSettings,
                testTag = "notif_system_settings_item"
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}