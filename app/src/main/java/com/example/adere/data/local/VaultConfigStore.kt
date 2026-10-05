package com.example.adere.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.adere.domain.model.BackupHistoryEntry
import org.json.JSONArray
import org.json.JSONObject

/**
 * Stores non-secret vault metadata and protected/encrypted key material.
 *
 * Strictly adheres to rule:
 * - Master password is NEVER stored.
 * - DEK is NEVER stored in plaintext.
 * - Only stored as ciphertext encrypted with MasterKey or KeystoreKey.
 */
class VaultConfigStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("adere_vault_config", Context.MODE_PRIVATE)

    companion object {
        val AUTO_LOCK_OPTIONS_SECONDS = setOf(0, 30, 60, 300, 900, 3600)
        const val DEFAULT_AUTO_LOCK_SECONDS = 60

        private const val KEY_INITIALIZED = "vault_initialized"
        private const val KEY_MASTER_SALT = "master_salt"
        private const val KEY_ENCRYPTED_DEK = "encrypted_dek"
        private const val KEY_ENCRYPTED_DEK_IV = "encrypted_dek_iv"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_BIOMETRIC_USER_DISABLED = "biometric_user_disabled"
        private const val KEY_BIOMETRIC_ENCRYPTED_DEK = "biometric_encrypted_dek"
        private const val KEY_BIOMETRIC_ENCRYPTED_DEK_IV = "biometric_encrypted_dek_iv"
        private const val KEY_AUTO_LOCK_SECONDS = "auto_lock_seconds"
        private const val KEY_SCREENSHOT_PROTECTION = "screenshot_protection"
        private const val KEY_CLIPBOARD_CLEAR_SECONDS = "clipboard_clear_seconds"
        private const val KEY_FAILED_ATTEMPTS = "failed_attempts"
        private const val KEY_LOCKOUT_UNTIL = "lockout_until_ms"
        private const val KEY_LANGUAGE = "selected_language"
        private const val KEY_THEME_PALETTE = "selected_theme_palette"
        private const val KEY_RECOVERY_SALT = "recovery_salt"
        private const val KEY_RECOVERY_ENCRYPTED_DEK = "recovery_encrypted_dek"
        private const val KEY_RECOVERY_ENCRYPTED_DEK_IV = "recovery_encrypted_dek_iv"
        private const val KEY_STORED_RECOVERY_KEY_ENC = "stored_recovery_key_enc"
        private const val KEY_STORED_RECOVERY_KEY_IV = "stored_recovery_key_iv"
        private const val KEY_HAS_RECOVERY_KEY = "has_recovery_key"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_HIDE_RECENT_APPS = "hide_recent_apps"
        private const val KEY_LAST_BACKUP_TIMESTAMP = "last_backup_timestamp"
        private const val KEY_BACKUP_HISTORY_JSON = "backup_history_json"
    }

    var isInitialized: Boolean
        get() = prefs.getBoolean(KEY_INITIALIZED, false)
        set(value) = prefs.edit().putBoolean(KEY_INITIALIZED, value).apply()

    var masterSaltBase64: String
        get() = prefs.getString(KEY_MASTER_SALT, "") ?: ""
        set(value) = prefs.edit().putString(KEY_MASTER_SALT, value).apply()

    var encryptedDekBase64: String
        get() = prefs.getString(KEY_ENCRYPTED_DEK, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ENCRYPTED_DEK, value).apply()

    var encryptedDekIvBase64: String
        get() = prefs.getString(KEY_ENCRYPTED_DEK_IV, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ENCRYPTED_DEK_IV, value).apply()

    var isBiometricEnabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()

    var isBiometricUserDisabled: Boolean
        get() = prefs.getBoolean(KEY_BIOMETRIC_USER_DISABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_BIOMETRIC_USER_DISABLED, value).apply()

    var biometricEncryptedDekBase64: String
        get() = prefs.getString(KEY_BIOMETRIC_ENCRYPTED_DEK, "") ?: ""
        set(value) = prefs.edit().putString(KEY_BIOMETRIC_ENCRYPTED_DEK, value).apply()

    var biometricDekIvBase64: String
        get() = prefs.getString(KEY_BIOMETRIC_ENCRYPTED_DEK_IV, "") ?: ""
        set(value) = prefs.edit().putString(KEY_BIOMETRIC_ENCRYPTED_DEK_IV, value).apply()

    var autoLockSeconds: Int
        get() {
            val configured = prefs.getInt(KEY_AUTO_LOCK_SECONDS, DEFAULT_AUTO_LOCK_SECONDS)
            return configured.takeIf { it in AUTO_LOCK_OPTIONS_SECONDS } ?: DEFAULT_AUTO_LOCK_SECONDS
        }
        set(value) {
            require(value in AUTO_LOCK_OPTIONS_SECONDS) { "Unsupported auto-lock interval." }
            prefs.edit().putInt(KEY_AUTO_LOCK_SECONDS, value).apply()
        }

    fun persistAutoLockSeconds(seconds: Int): Boolean {
        require(seconds in AUTO_LOCK_OPTIONS_SECONDS) { "Unsupported auto-lock interval." }
        return prefs.edit().putInt(KEY_AUTO_LOCK_SECONDS, seconds).commit()
    }

    var isScreenshotProtectionEnabled: Boolean
        get() = prefs.getBoolean(KEY_SCREENSHOT_PROTECTION, false) // disabled by default so streaming emulator works
        set(value) = prefs.edit().putBoolean(KEY_SCREENSHOT_PROTECTION, value).apply()

    var clipboardClearSeconds: Int
        get() = prefs.getInt(KEY_CLIPBOARD_CLEAR_SECONDS, 30) // 30 seconds default
        set(value) = prefs.edit().putInt(KEY_CLIPBOARD_CLEAR_SECONDS, value).apply()

    var failedAttempts: Int
        get() = prefs.getInt(KEY_FAILED_ATTEMPTS, 0)
        set(value) = prefs.edit().putInt(KEY_FAILED_ATTEMPTS, value).apply()

    var lockoutUntilMs: Long
        get() = prefs.getLong(KEY_LOCKOUT_UNTIL, 0L)
        set(value) = prefs.edit().putLong(KEY_LOCKOUT_UNTIL, value).apply()

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    var themePalette: String
        get() = prefs.getString(KEY_THEME_PALETTE, "TOTAL_SECURITY") ?: "TOTAL_SECURITY"
        set(value) = prefs.edit().putString(KEY_THEME_PALETTE, value).apply()

    var recoverySaltBase64: String
        get() = prefs.getString(KEY_RECOVERY_SALT, "") ?: ""
        set(value) = prefs.edit().putString(KEY_RECOVERY_SALT, value).apply()

    var recoveryEncryptedDekBase64: String
        get() = prefs.getString(KEY_RECOVERY_ENCRYPTED_DEK, "") ?: ""
        set(value) = prefs.edit().putString(KEY_RECOVERY_ENCRYPTED_DEK, value).apply()

    var recoveryEncryptedDekIvBase64: String
        get() = prefs.getString(KEY_RECOVERY_ENCRYPTED_DEK_IV, "") ?: ""
        set(value) = prefs.edit().putString(KEY_RECOVERY_ENCRYPTED_DEK_IV, value).apply()

    var storedRecoveryKeyEncBase64: String
        get() = prefs.getString(KEY_STORED_RECOVERY_KEY_ENC, "") ?: ""
        set(value) = prefs.edit().putString(KEY_STORED_RECOVERY_KEY_ENC, value).apply()

    var storedRecoveryKeyIvBase64: String
        get() = prefs.getString(KEY_STORED_RECOVERY_KEY_IV, "") ?: ""
        set(value) = prefs.edit().putString(KEY_STORED_RECOVERY_KEY_IV, value).apply()

    var isNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()

    var isHideRecentAppsContentEnabled: Boolean
        get() = prefs.getBoolean(KEY_HIDE_RECENT_APPS, false)
        set(value) = prefs.edit().putBoolean(KEY_HIDE_RECENT_APPS, value).apply()

    var hasRecoveryKey: Boolean
        get() = prefs.getBoolean(KEY_HAS_RECOVERY_KEY, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_RECOVERY_KEY, value).apply()

    var lastBackupTimestamp: Long
        get() = prefs.getLong(KEY_LAST_BACKUP_TIMESTAMP, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_BACKUP_TIMESTAMP, value).apply()

    fun getBackupHistory(): List<BackupHistoryEntry> {
        val raw = prefs.getString(KEY_BACKUP_HISTORY_JSON, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<BackupHistoryEntry>()
            for (i in 0 until array.length()) {
                list.add(BackupHistoryEntry.fromJson(array.getJSONObject(i)))
            }
            list.sortedByDescending { it.timestamp }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addBackupHistoryEntry(entry: BackupHistoryEntry) {
        val current = getBackupHistory().toMutableList()
        current.removeAll { it.id == entry.id }
        current.add(0, entry)
        // Keep up to 50 latest backup events
        val limited = current.take(50)
        val array = JSONArray()
        for (item in limited) {
            array.put(item.toJson())
        }
        prefs.edit().putString(KEY_BACKUP_HISTORY_JSON, array.toString()).apply()
        if (entry.actionType == com.example.adere.domain.model.BackupActionType.BACKUP_CREATED) {
            lastBackupTimestamp = entry.timestamp
        }
    }

    fun clearBackupHistory() {
        prefs.edit().remove(KEY_BACKUP_HISTORY_JSON).apply()
    }

    fun resetVault() {
        prefs.edit().clear().apply()
    }
}
