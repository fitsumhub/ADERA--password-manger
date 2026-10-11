package com.example.adere.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.adere.core.backup.BackupManager
import com.example.adere.core.backup.PdfExportManager
import com.example.adere.core.utilities.AppIconManager
import com.example.adere.domain.model.BackupHistoryEntry
import com.example.adere.presentation.screens.settings.AboutScreen
import com.example.adere.presentation.screens.settings.AppearanceSettingsScreen
import com.example.adere.presentation.screens.settings.BackupRestoreScreen
import com.example.adere.presentation.screens.settings.HelpSupportScreen
import com.example.adere.presentation.screens.settings.NotificationSettingsScreen
import com.example.adere.presentation.screens.settings.PrivacySettingsScreen
import com.example.adere.presentation.screens.settings.SecuritySettingsScreen
import com.example.adere.presentation.screens.settings.SettingsCategory
import com.example.adere.presentation.screens.settings.SettingsMainScreen
import com.example.ui.theme.VaultThemePalette

@Composable
fun SettingsScreen(
    totalCredentialsCount: Int = 0,
    favoritesCount: Int = 0,
    isVaultProtected: Boolean = true,
    biometricEnabled: Boolean = false,
    autoLockSeconds: Int = 60,
    customAppName: String = "ADERA",
    selectedAppIcon: String = "default",
    screenshotProtection: Boolean = false,
    clipboardClearSeconds: Int = 30,
    hideRecentAppsContent: Boolean = false,
    notificationsEnabled: Boolean = true,
    themePalette: VaultThemePalette = VaultThemePalette.TOTAL_SECURITY,
    onSelectThemePalette: (VaultThemePalette) -> Unit = {},
    onSetCustomAppName: (String) -> Unit = {},
    onSetSelectedAppIcon: (AppIconManager.AppIconPreset) -> Unit = {},
    onToggleBiometric: (Boolean, (Result<Unit>) -> Unit) -> Unit,
    onSetAutoLockSeconds: (Int, (Result<Unit>) -> Unit) -> Unit,
    onRefreshBiometricState: () -> Unit,
    onToggleScreenshotProtection: (Boolean) -> Unit = {},
    onSetClipboardClearSeconds: (Int) -> Unit = {},
    onToggleHideRecentApps: (Boolean) -> Unit = {},
    onRequestManageAdPrivacy: () -> Unit = {},
    onViewFAQ: () -> Unit = {},
    onViewSecurityGuide: () -> Unit = {},
    onContactSupport: () -> Unit = {},
    onReportBug: () -> Unit = {},
    onRateApp: () -> Unit = {},
    onJoinCommunity: () -> Unit = {},
    onViewArchitecture: () -> Unit = {},
    onViewPrivacyPolicy: () -> Unit = {},
    onViewTermsOfService: () -> Unit = {},
    onViewLicenses: () -> Unit = {},
    onCheckUpdates: () -> Unit = {},
    onToggleNotifications: (Boolean) -> Unit = {},
    onChangeMasterPassword: (old: String, new: String, (Result<Unit>) -> Unit) -> Unit = { _, _, cb -> cb(Result.success(Unit)) },
    lastBackupTimestamp: Long = 0L,
    lastTamperingTimestamp: Long = 0L,
    hasPendingSecurityAlert: Boolean = false,
    onDismissSecurityAlert: () -> Unit = {},
    backupHistory: List<BackupHistoryEntry> = emptyList(),
    onClearBackupHistory: (() -> Unit)? = null,
    onTriggerBiometrics: (() -> Unit)? = null,
    onAuthenticateSensitiveAction: ((title: String, subtitle: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit)? = null,
    onVerifyMasterPassword: ((password: String, (Boolean) -> Unit) -> Unit)? = null,
    onExportBackup: (passphrase: String, (Result<String>) -> Unit) -> Unit = { _, cb -> cb(Result.success("")) },
    onRestoreBackup: (content: String, passphrase: String, (Result<Int>) -> Unit) -> Unit = { _, _, cb -> cb(Result.success(0)) },
    onRestoreBackupWithMode: ((content: String, passphrase: String, mode: BackupManager.RestoreMode, (Result<Int>) -> Unit) -> Unit)? = null,
    onPreviewBackup: ((content: String, passphrase: String, (Result<BackupManager.RestorePreview>) -> Unit) -> Unit)? = null,
    onExportPdf: (options: PdfExportManager.ExportOptions, isShare: Boolean) -> Unit = { _, _ -> },
    onGetRecoveryKey: () -> Result<String> = { Result.failure(IllegalStateException()) },
    onRegenerateRecoveryKey: ((Result<String>) -> Unit) -> Unit = {},
    onLockClick: () -> Unit = {}
) {
    var activeCategory by remember { mutableStateOf<SettingsCategory?>(null) }

    if (activeCategory != null) {
        BackHandler {
            activeCategory = null
        }
    }

    when (activeCategory) {
        null -> {
            SettingsMainScreen(
                totalCredentialsCount = totalCredentialsCount,
                favoritesCount = favoritesCount,
                isVaultProtected = isVaultProtected,
                customAppName = customAppName,
                onNavigateCategory = { cat -> activeCategory = cat },
                onQuickLock = onLockClick
            )
        }

        SettingsCategory.SECURITY -> {
            SecuritySettingsScreen(
                biometricEnabled = biometricEnabled,
                autoLockSeconds = autoLockSeconds,
                lastBackupTimestamp = lastBackupTimestamp,
                lastTamperingTimestamp = lastTamperingTimestamp,
                hasPendingSecurityAlert = hasPendingSecurityAlert,
                onToggleBiometric = onToggleBiometric,
                onSetAutoLockSeconds = onSetAutoLockSeconds,
                onRefreshBiometricState = onRefreshBiometricState,
                onChangeMasterPassword = onChangeMasterPassword,
                onGetRecoveryKey = onGetRecoveryKey,
                onRegenerateRecoveryKey = onRegenerateRecoveryKey,
                onNavigateToBackup = { activeCategory = SettingsCategory.BACKUP_RESTORE },
                onDismissSecurityAlert = onDismissSecurityAlert,
                onTriggerBiometrics = onTriggerBiometrics,
                onBackClick = { activeCategory = null }
            )
        }

        SettingsCategory.PRIVACY -> {
            PrivacySettingsScreen(
                screenshotProtectionEnabled = screenshotProtection,
                appMaskingEnabled = hideRecentAppsContent,
                clipboardAutoClearSeconds = clipboardClearSeconds,
                onToggleScreenshotProtection = onToggleScreenshotProtection,
                onToggleAppMasking = onToggleHideRecentApps,
                onSetClipboardAutoClear = onSetClipboardClearSeconds,
                onRequestManageAdPrivacy = onRequestManageAdPrivacy,
                onBackClick = { activeCategory = null }
            )
        }

        SettingsCategory.BACKUP_RESTORE -> {
            BackupRestoreScreen(
                vaultItemsCount = totalCredentialsCount,
                lastBackupTimestamp = lastBackupTimestamp,
                backupHistory = backupHistory,
                onExportBackup = onExportBackup,
                onRestoreBackup = onRestoreBackup,
                onRestoreBackupWithMode = onRestoreBackupWithMode,
                onPreviewBackup = onPreviewBackup,
                onExportPdf = onExportPdf,
                onTriggerBiometrics = onTriggerBiometrics,
                onAuthenticateSensitiveAction = onAuthenticateSensitiveAction,
                onVerifyMasterPassword = onVerifyMasterPassword,
                onClearBackupHistory = onClearBackupHistory,
                onGetRecoveryKey = onGetRecoveryKey,
                onRegenerateRecoveryKey = onRegenerateRecoveryKey,
                onBackClick = { activeCategory = null }
            )
        }

        SettingsCategory.APPEARANCE -> {
            AppearanceSettingsScreen(
                currentPalette = themePalette,
                customAppName = customAppName,
                selectedAppIcon = selectedAppIcon,
                onSelectThemePalette = onSelectThemePalette,
                onSetCustomAppName = onSetCustomAppName,
                onSetSelectedAppIcon = onSetSelectedAppIcon,
                onBackClick = { activeCategory = null }
            )
        }

        SettingsCategory.NOTIFICATIONS -> {
            NotificationSettingsScreen(
                notificationsEnabled = notificationsEnabled,
                onToggleNotifications = onToggleNotifications,
                onBackClick = { activeCategory = null }
            )
        }

        SettingsCategory.HELP_SUPPORT -> {
            HelpSupportScreen(
                onViewFAQ = onViewFAQ,
                onViewSecurityGuide = onViewSecurityGuide,
                onContactSupport = onContactSupport,
                onReportBug = onReportBug,
                onRateApp = onRateApp,
                onJoinCommunity = onJoinCommunity,
                onBackClick = { activeCategory = null }
            )
        }

        SettingsCategory.ABOUT -> {
            AboutScreen(
                customAppName = customAppName,
                onViewArchitecture = onViewArchitecture,
                onViewPrivacyPolicy = onViewPrivacyPolicy,
                onViewTermsOfService = onViewTermsOfService,
                onViewLicenses = onViewLicenses,
                onCheckUpdates = onCheckUpdates,
                onBackClick = { activeCategory = null }
            )
        }

    }
}
