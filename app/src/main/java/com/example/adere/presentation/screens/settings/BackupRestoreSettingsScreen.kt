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
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.adere.presentation.screens.settings.components.SettingsItemCard
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.SecurityOrange

@Composable
fun BackupRestoreSettingsScreen(
    onCreateBackup: () -> Unit,
    onRestoreFromFile: () -> Unit,
    onExportPdf: () -> Unit,
    onViewBackupLocation: () -> Unit,
    onBackClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Backup & Restore",
                subtitle = "Encrypted backups · Vault restore · Emergency PDF export",
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
            // ── Vault backup ──────────────────────────────────────────────────
            SettingsSectionTitle("Vault Backup")

            SettingsItemCard(
                icon = Icons.Default.Backup,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Create Encrypted Backup",
                subtitle = "Export vault as AES-256 encrypted file with timestamp",
                onClick = onCreateBackup,
                testTag = "backup_create_item"
            )

            SettingsItemCard(
                icon = Icons.Default.FolderOpen,
                iconTint = Color(0xFF6366F1),
                iconBg = Color(0xFF6366F1).copy(alpha = 0.1f),
                title = "View Backup Location",
                subtitle = "Open folder containing your automatic backups",
                onClick = onViewBackupLocation,
                testTag = "backup_location_item"
            )

            // ── Vault restore ─────────────────────────────────────────────────
            SettingsSectionTitle("Vault Restore")

            SettingsItemCard(
                icon = Icons.Default.Restore,
                iconTint = SecurityOrange,
                iconBg = SecurityOrange.copy(alpha = 0.1f),
                title = "Restore from Backup File",
                subtitle = "Import encrypted backup · Merges with existing credentials",
                onClick = onRestoreFromFile,
                testTag = "backup_restore_item"
            )

            SettingsItemCard(
                icon = Icons.Default.CloudDownload,
                iconTint = Color(0xFF0EA5E9),
                iconBg = Color(0xFF0EA5E9).copy(alpha = 0.1f),
                title = "Import from Other Managers",
                subtitle = "Bitwarden, 1Password, LastPass CSV · Auto-detects format",
                onClick = { /* Import from CSV */ },
                testTag = "backup_import_item"
            )

            // ── Emergency export ──────────────────────────────────────────────
            SettingsSectionTitle("Emergency Export")

            SettingsItemCard(
                icon = Icons.Default.PictureAsPdf,
                iconTint = HealthRiskRed,
                iconBg = HealthRiskRed.copy(alpha = 0.1f),
                title = "Export Emergency PDF",
                subtitle = "Unencrypted readable document · Print for offline storage",
                onClick = onExportPdf,
                testTag = "backup_pdf_item"
            )

            SettingsItemCard(
                icon = Icons.Default.FileDownload,
                iconTint = Color(0xFF10B981),
                iconBg = Color(0xFF10B981).copy(alpha = 0.1f),
                title = "Export to CSV",
                subtitle = "Plaintext spreadsheet · For migration to other managers",
                onClick = { /* Export CSV */ },
                testTag = "backup_csv_item"
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}