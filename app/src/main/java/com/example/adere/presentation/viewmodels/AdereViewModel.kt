package com.example.adere.presentation.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.adere.core.utilities.AppIconManager
import com.example.adere.core.backup.BackupManager
import com.example.adere.core.crypto.PasswordHealthAnalyzer
import com.example.adere.core.crypto.TOTPGenerator
import com.example.adere.data.local.VaultConfigStore
import com.example.adere.domain.model.BackupHistoryEntry
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import com.example.adere.domain.repository.VaultLockState
import com.example.adere.domain.repository.VaultRepository
import com.example.adere.domain.repository.VaultSessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdereViewModel(
    val repository: VaultRepository,
    val sessionManager: VaultSessionManager,
    val configStore: VaultConfigStore
) : ViewModel() {

    val lockState: StateFlow<VaultLockState> = sessionManager.lockState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(VaultCategory.ALL)
    val selectedCategory: StateFlow<VaultCategory> = _selectedCategory.asStateFlow()

    val rawItems: StateFlow<List<VaultItem>> = repository.allItemsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredItems: StateFlow<List<VaultItem>> = combine(
        rawItems,
        _searchQuery,
        _selectedCategory
    ) { items, query, category ->
        items.filter { item ->
            val matchesCategory = when (category) {
                VaultCategory.ALL -> true
                VaultCategory.FAVORITES -> item.isFavorite
                else -> item.category == category
            }
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.category.title.contains(query, ignoreCase = true) ||
                    item.category.name.contains(query, ignoreCase = true) ||
                    item.username.contains(query, ignoreCase = true) ||
                    item.payload.url.contains(query, ignoreCase = true) ||
                    item.payload.cryptoNetwork.contains(query, ignoreCase = true) ||
                    item.payload.wifiSsid.contains(query, ignoreCase = true) ||
                    item.payload.notes.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _healthReport = MutableStateFlow(
        PasswordHealthAnalyzer.VaultHealthReport(
            0, 0, 0, 0, 0, 0,
            PasswordHealthAnalyzer.SecurityStatus.STRONG,
            100
        )
    )
    val healthReport: StateFlow<PasswordHealthAnalyzer.VaultHealthReport> = _healthReport.asStateFlow()

    private val _totpMap = MutableStateFlow<Map<String, TOTPGenerator.TotpResult>>(emptyMap())
    val totpMap: StateFlow<Map<String, TOTPGenerator.TotpResult>> = _totpMap.asStateFlow()

    // Settings observable state
    private val _autoLockSeconds = MutableStateFlow(configStore.autoLockSeconds)
    val autoLockSeconds: StateFlow<Int> = _autoLockSeconds.asStateFlow()

    private val _screenshotProtection = MutableStateFlow(configStore.isScreenshotProtectionEnabled)
    val screenshotProtection: StateFlow<Boolean> = _screenshotProtection.asStateFlow()

    private val _clipboardClearSeconds = MutableStateFlow(configStore.clipboardClearSeconds)
    val clipboardClearSeconds: StateFlow<Int> = _clipboardClearSeconds.asStateFlow()

    private val _biometricEnabled = MutableStateFlow(sessionManager.isBiometricEnabled())
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(configStore.isNotificationsEnabled)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _hideRecentAppsContent = MutableStateFlow(configStore.isHideRecentAppsContentEnabled)
    val hideRecentAppsContent: StateFlow<Boolean> = _hideRecentAppsContent.asStateFlow()

    private val _themePalette = MutableStateFlow(
        try {
            com.example.ui.theme.VaultThemePalette.valueOf(configStore.themePalette)
        } catch (_: Exception) {
            com.example.ui.theme.VaultThemePalette.TOTAL_SECURITY
        }
    )
    val themePalette: StateFlow<com.example.ui.theme.VaultThemePalette> = _themePalette.asStateFlow()

    private val _customAppName = MutableStateFlow(configStore.customAppName)
    val customAppName: StateFlow<String> = _customAppName.asStateFlow()

    private val _selectedAppIcon = MutableStateFlow(configStore.selectedAppIcon)
    val selectedAppIcon: StateFlow<String> = _selectedAppIcon.asStateFlow()

    private val _backupHistory = MutableStateFlow<List<BackupHistoryEntry>>(configStore.getBackupHistory())
    val backupHistory: StateFlow<List<BackupHistoryEntry>> = _backupHistory.asStateFlow()

    private val _lastBackupTimestamp = MutableStateFlow<Long>(configStore.lastBackupTimestamp)
    val lastBackupTimestamp: StateFlow<Long> = _lastBackupTimestamp.asStateFlow()

    private val _lastTamperingTimestamp = MutableStateFlow<Long>(configStore.lastTamperingAttemptTimestamp)
    val lastTamperingTimestamp: StateFlow<Long> = _lastTamperingTimestamp.asStateFlow()

    private val _hasPendingSecurityAlert = MutableStateFlow<Boolean>(configStore.hasPendingSecurityAlert)
    val hasPendingSecurityAlert: StateFlow<Boolean> = _hasPendingSecurityAlert.asStateFlow()

    fun dismissSecurityAlert() {
        configStore.hasPendingSecurityAlert = false
        _hasPendingSecurityAlert.value = false
    }

    fun refreshSecurityAuditState() {
        _lastTamperingTimestamp.value = configStore.lastTamperingAttemptTimestamp
        _hasPendingSecurityAlert.value = configStore.hasPendingSecurityAlert
    }

    init {
        viewModelScope.launch {
            while (isActive) {
                sessionManager.checkAutoLock()
                delay(1_000)
            }
        }

        // Periodic TOTP refresh ticker
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                updateTotpCodes()
                delay(1000)
            }
        }

        // Recalculate health report when items change
        viewModelScope.launch {
            rawItems.collect {
                refreshHealthReport()
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        sessionManager.recordInteraction()
    }

    fun selectCategory(category: VaultCategory) {
        _selectedCategory.value = category
        sessionManager.recordInteraction()
    }

    private fun updateTotpCodes() {
        val currentItems = rawItems.value
        val newMap = mutableMapOf<String, TOTPGenerator.TotpResult>()
        for (item in currentItems) {
            if (item.payload.totpSecret.isNotBlank()) {
                val res = TOTPGenerator.generateCurrentTotp(
                    secretBase32 = item.payload.totpSecret,
                    timeStepSeconds = item.payload.totpPeriod.toLong(),
                    digits = item.payload.totpDigits
                )
                if (res != null) {
                    newMap[item.id] = res
                }
            }
        }
        _totpMap.value = newMap
    }

    fun refreshHealthReport() {
        viewModelScope.launch {
            _healthReport.value = repository.computeHealthReport()
        }
    }

    fun initializeMasterPassword(password: String, enableBiometrics: Boolean, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = sessionManager.initializeVault(password.toCharArray(), enableBiometrics)
            _biometricEnabled.value = configStore.isBiometricEnabled
            onResult(res)
        }
    }

    fun unlockWithPassword(password: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = sessionManager.unlockWithMasterPassword(password.toCharArray())
            _biometricEnabled.value = sessionManager.isBiometricEnabled()
            onResult(res)
        }
    }

    fun verifyMasterPassword(password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.Default) {
            val valid = sessionManager.verifyMasterPassword(password.toCharArray())
            withContext(Dispatchers.Main) {
                onResult(valid)
            }
        }
    }

    fun unlockWithBiometric(authorizedCipher: javax.crypto.Cipher, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = sessionManager.unlockWithBiometric(authorizedCipher)
            _biometricEnabled.value = sessionManager.isBiometricEnabled()
            onResult(res)
        }
    }

    fun unlockWithBiometricAuth(onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = sessionManager.unlockWithBiometricAuth()
            _biometricEnabled.value = sessionManager.isBiometricEnabled()
            onResult(res)
        }
    }

    /** Returns a Cipher initialised for DEK decryption, ready to wrap in CryptoObject. */
    fun prepareBiometricCipher(): javax.crypto.Cipher? = sessionManager.prepareBiometricCipher()

    fun recoverVaultWithKey(recoveryKey: String, newMasterPassword: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = sessionManager.recoverVaultWithKey(recoveryKey, newMasterPassword.toCharArray())
            _biometricEnabled.value = configStore.isBiometricEnabled
            onResult(res)
        }
    }

    fun getActiveRecoveryKey(): Result<String> {
        return sessionManager.getActiveRecoveryKey()
    }

    fun regenerateRecoveryKey(onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val res = sessionManager.regenerateRecoveryKey()
            onResult(res)
        }
    }

    fun lockVault() {
        sessionManager.lock()
        _biometricEnabled.value = sessionManager.isBiometricEnabled()
    }

    fun saveItem(item: VaultItem, onResult: (Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            sessionManager.recordInteraction()
            val res = repository.saveItem(item)
            onResult(res)
        }
    }

    fun deleteItem(id: String, onResult: (Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            sessionManager.recordInteraction()
            val res = repository.deleteItem(id)
            onResult(res)
        }
    }

    fun toggleFavorite(id: String, currentFav: Boolean) {
        viewModelScope.launch {
            sessionManager.recordInteraction()
            repository.toggleFavorite(id, !currentFav)
        }
    }

    // Backup & Restore
    fun refreshBackupHistory() {
        _backupHistory.value = configStore.getBackupHistory()
        _lastBackupTimestamp.value = configStore.lastBackupTimestamp
    }

    fun clearBackupHistory() {
        configStore.clearBackupHistory()
        _backupHistory.value = emptyList<BackupHistoryEntry>()
    }

    fun inspectBackupHeader(content: String): Result<BackupManager.BackupHeaderInfo> {
        return BackupManager.validateBackupHeader(content)
    }

    fun previewBackup(
        content: String,
        passphrase: String,
        onResult: (Result<BackupManager.RestorePreview>) -> Unit
    ) {
        viewModelScope.launch {
            sessionManager.recordInteraction()
            val res = repository.validateBackup(content, passphrase.toCharArray())
            onResult(res)
        }
    }

    fun exportBackup(passphrase: String, onResult: (Result<BackupManager.BackupExportResult>) -> Unit) {
        viewModelScope.launch {
            sessionManager.recordInteraction()
            val res = repository.exportEncryptedBackup(passphrase.toCharArray())
            if (res.isSuccess) {
                val exportData = res.getOrThrow()
                val entry = BackupHistoryEntry(
                    id = java.util.UUID.randomUUID().toString(),
                    timestamp = exportData.timestamp,
                    itemCount = exportData.itemCount,
                    fileSizeBytes = exportData.fileSizeBytes,
                    actionType = com.example.adere.domain.model.BackupActionType.BACKUP_CREATED,
                    fileName = exportData.defaultFileName
                )
                configStore.addBackupHistoryEntry(entry)
                refreshBackupHistory()
            }
            onResult(res)
        }
    }

    fun exportToPdf(
        options: com.example.adere.core.backup.PdfExportManager.ExportOptions,
        outputStream: java.io.OutputStream,
        onResult: (Result<Int>) -> Unit
    ) {
        viewModelScope.launch {
            sessionManager.recordInteraction()
            val res = repository.exportToPdf(options, outputStream)
            onResult(res)
        }
    }

    fun restoreBackupWithMode(
        content: String,
        passphrase: String,
        mode: BackupManager.RestoreMode,
        fileName: String = "Adere_Backup.adere",
        onResult: (Result<Int>) -> Unit
    ) {
        viewModelScope.launch {
            sessionManager.recordInteraction()
            val res = repository.restoreFromEncryptedBackup(content, passphrase.toCharArray(), mode)
            if (res.isSuccess) {
                val count = res.getOrThrow()
                val entry = BackupHistoryEntry(
                    id = java.util.UUID.randomUUID().toString(),
                    timestamp = System.currentTimeMillis(),
                    itemCount = count,
                    fileSizeBytes = content.toByteArray(Charsets.UTF_8).size.toLong(),
                    actionType = if (mode == BackupManager.RestoreMode.REPLACE)
                        com.example.adere.domain.model.BackupActionType.BACKUP_RESTORED_REPLACE
                    else
                        com.example.adere.domain.model.BackupActionType.BACKUP_RESTORED_MERGE,
                    fileName = fileName
                )
                configStore.addBackupHistoryEntry(entry)
                refreshBackupHistory()
            }
            onResult(res)
        }
    }

    fun restoreBackup(content: String, passphrase: String, onResult: (Result<Int>) -> Unit) {
        restoreBackupWithMode(content, passphrase, BackupManager.RestoreMode.MERGE, onResult = onResult)
    }

    fun changeMasterPassword(oldPass: String, newPass: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = sessionManager.changeMasterPassword(oldPass.toCharArray(), newPass.toCharArray())
            onResult(res)
        }
    }

    fun setBiometric(enabled: Boolean, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = sessionManager.setBiometricEnabled(enabled)
            _biometricEnabled.value = sessionManager.isBiometricEnabled()
            onResult(res)
        }
    }

    fun refreshBiometricState() {
        if (sessionManager.isBiometricEnabled()) {
            sessionManager.prepareBiometricCipher()
        }
        _biometricEnabled.value = sessionManager.isBiometricEnabled()
    }

    fun setAutoLockSeconds(seconds: Int, onResult: (Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    check(seconds in VaultConfigStore.AUTO_LOCK_OPTIONS_SECONDS) {
                        "Unsupported auto-lock interval."
                    }
                    check(configStore.persistAutoLockSeconds(seconds)) {
                        "Auto-lock settings could not be saved."
                    }
                }
            }
            if (result.isSuccess) {
                sessionManager.recordInteraction()
                _autoLockSeconds.value = seconds
            }
            onResult(result)
        }
    }

    fun setScreenshotProtection(enabled: Boolean) {
        configStore.isScreenshotProtectionEnabled = enabled
        _screenshotProtection.value = enabled
    }

    fun setClipboardClearSeconds(seconds: Int) {
        configStore.clipboardClearSeconds = seconds
        _clipboardClearSeconds.value = seconds
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        configStore.isNotificationsEnabled = enabled
        _notificationsEnabled.value = enabled
    }

    fun setHideRecentAppsContent(enabled: Boolean) {
        configStore.isHideRecentAppsContentEnabled = enabled
        _hideRecentAppsContent.value = enabled
    }

    fun setThemePalette(palette: com.example.ui.theme.VaultThemePalette) {
        configStore.themePalette = palette.name
        _themePalette.value = palette
    }

    fun setCustomAppName(name: String) {
        configStore.customAppName = name
        _customAppName.value = configStore.customAppName
    }

    fun setSelectedAppIcon(preset: AppIconManager.AppIconPreset, context: Context) {
        configStore.selectedAppIcon = preset.id
        _selectedAppIcon.value = preset.id
        AppIconManager.applyAppIcon(context, preset)
    }

    fun requestInAppUninstall(
        context: Context,
        onResult: (Result<Unit>) -> Unit
    ) {
        try {
            val packageName = context.packageName
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            onResult(Result.success(Unit))
        } catch (e: Exception) {
            onResult(Result.failure(e))
        }
    }

    class Factory(
        private val repository: VaultRepository,
        private val sessionManager: VaultSessionManager,
        private val configStore: VaultConfigStore
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdereViewModel(repository, sessionManager, configStore) as T
        }
    }
}
