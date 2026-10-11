package com.example.adere.domain.repository

import com.example.adere.core.crypto.BiometricKeyStore
import com.example.adere.core.crypto.CryptoEngine
import com.example.adere.data.local.VaultConfigStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.Arrays

sealed interface VaultLockState {
    object Uninitialized : VaultLockState
    object Locked : VaultLockState
    object Unlocked : VaultLockState
}

/**
 * Manages vault lock state and ephemeral Data Encryption Key (DEK) lifecycle.
 * Plaintext keys are wiped from memory on lock.
 */
class VaultSessionManager(
    private val configStore: VaultConfigStore,
    private val keystoreManager: BiometricKeyStore,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis
) {

    private val _lockState = MutableStateFlow<VaultLockState>(
        if (!configStore.isInitialized) VaultLockState.Uninitialized else VaultLockState.Locked
    )
    val lockState: StateFlow<VaultLockState> = _lockState.asStateFlow()

    private var inMemoryDek: ByteArray? = null
    private var lastActiveTime: Long = currentTimeMillis()

    fun isVaultInitialized(): Boolean = configStore.isInitialized

    fun isBiometricEnabled(): Boolean =
        configStore.isBiometricEnabled &&
        configStore.biometricEncryptedDekBase64.isNotBlank() &&
        configStore.biometricDekIvBase64.isNotBlank() &&
        runCatching { keystoreManager.hasBiometricKey() }.getOrDefault(false)

    fun getActiveDek(): ByteArray? = inMemoryDek

    fun recordInteraction() {
        lastActiveTime = currentTimeMillis()
    }

    fun checkAutoLock() {
        if (_lockState.value != VaultLockState.Unlocked) return
        val timeoutSeconds = configStore.autoLockSeconds
        // "Immediately" is applied when the activity leaves the foreground.
        // It must not lock continuously while the user is actively in the app.
        if (timeoutSeconds <= 0) return
        val elapsed = (currentTimeMillis() - lastActiveTime) / 1000
        if (elapsed >= timeoutSeconds) {
            lock()
        }
    }

    /**
     * Initializes a brand new vault with the chosen Master Password.
     */
    suspend fun initializeVault(masterPassword: CharArray, enableBiometric: Boolean): Result<Unit> = withContext(Dispatchers.Default) {
        val passCopy = masterPassword.clone()
        return@withContext try {
            val salt = CryptoEngine.generateSalt()
            val masterKey = CryptoEngine.deriveKey(passCopy, salt)
            val newDek = CryptoEngine.generateDek()

            // Encrypt DEK with Master Key
            val encryptedDek = CryptoEngine.encrypt(newDek, masterKey)

            configStore.masterSaltBase64 = com.example.adere.core.crypto.Base64Codec.encode(salt)
            configStore.encryptedDekBase64 = encryptedDek.toBase64Ciphertext()
            configStore.encryptedDekIvBase64 = encryptedDek.toBase64Iv()

            // Generate and protect Master Recovery Key
            val recoveryKey = com.example.adere.core.crypto.RecoveryKeyManager.generateRecoveryKey()
            val normalizedRecoveryKey = com.example.adere.core.crypto.RecoveryKeyManager.normalizeKey(recoveryKey)
            val recoverySalt = CryptoEngine.generateSalt()
            val recoveryDerivedKey = CryptoEngine.deriveKey(normalizedRecoveryKey.toCharArray(), recoverySalt)
            val recoveryEncryptedDek = CryptoEngine.encrypt(newDek, recoveryDerivedKey)
            val storedRecoveryKeyEnc = CryptoEngine.encryptString(recoveryKey, newDek)

            configStore.recoverySaltBase64 = com.example.adere.core.crypto.Base64Codec.encode(recoverySalt)
            configStore.recoveryEncryptedDekBase64 = recoveryEncryptedDek.toBase64Ciphertext()
            configStore.recoveryEncryptedDekIvBase64 = recoveryEncryptedDek.toBase64Iv()
            configStore.storedRecoveryKeyEncBase64 = storedRecoveryKeyEnc.toBase64Ciphertext()
            configStore.storedRecoveryKeyIvBase64 = storedRecoveryKeyEnc.toBase64Iv()
            configStore.hasRecoveryKey = true

            if (enableBiometric) {
                val biometricEncrypted = keystoreManager.encryptDek(newDek)
                configStore.biometricEncryptedDekBase64 = biometricEncrypted.toBase64Ciphertext()
                configStore.biometricDekIvBase64 = biometricEncrypted.toBase64Iv()
                configStore.isBiometricEnabled = true
                configStore.isBiometricUserDisabled = false
            } else {
                configStore.isBiometricEnabled = false
                configStore.isBiometricUserDisabled = true
                keystoreManager.deleteBiometricKey()
            }

            configStore.isInitialized = true
            configStore.failedAttempts = 0
            configStore.lockoutUntilMs = 0L

            // Retain active DEK in memory
            inMemoryDek = newDek.clone()
            lastActiveTime = currentTimeMillis()
            _lockState.value = VaultLockState.Unlocked

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            Arrays.fill(passCopy, '\u0000')
        }
    }

    /**
     * Unlocks the vault using the Master Password.
     */
    suspend fun unlockWithMasterPassword(masterPassword: CharArray): Result<Unit> = withContext(Dispatchers.Default) {
        val now = System.currentTimeMillis()
        if (now < configStore.lockoutUntilMs) {
            val remainingSec = ((configStore.lockoutUntilMs - now) / 1000).coerceAtLeast(1)
            return@withContext Result.failure(IllegalStateException("Vault locked due to multiple failed attempts. Wait $remainingSec seconds or reset."))
        }

        val passCopy = masterPassword.clone()
        return@withContext try {
            val salt = com.example.adere.core.crypto.Base64Codec.decode(configStore.masterSaltBase64)
            val ciphertext = com.example.adere.core.crypto.Base64Codec.decode(configStore.encryptedDekBase64)
            val iv = com.example.adere.core.crypto.Base64Codec.decode(configStore.encryptedDekIvBase64)

            val masterKey = CryptoEngine.deriveKey(passCopy, salt)
            val decryptedDek = CryptoEngine.decrypt(ciphertext, iv, masterKey)

            // Successfully unlocked
            configStore.failedAttempts = 0
            configStore.lockoutUntilMs = 0L
            inMemoryDek = decryptedDek
            lastActiveTime = currentTimeMillis()
            _lockState.value = VaultLockState.Unlocked

            // Auto-provision Master Recovery Key for legacy vaults if missing
            if (!configStore.hasRecoveryKey || configStore.recoveryEncryptedDekBase64.isBlank()) {
                try {
                    val recoveryKey = com.example.adere.core.crypto.RecoveryKeyManager.generateRecoveryKey()
                    val normalizedKey = com.example.adere.core.crypto.RecoveryKeyManager.normalizeKey(recoveryKey)
                    val recoverySalt = CryptoEngine.generateSalt()
                    val recoveryDerivedKey = CryptoEngine.deriveKey(normalizedKey.toCharArray(), recoverySalt)
                    val recoveryEncryptedDek = CryptoEngine.encrypt(decryptedDek, recoveryDerivedKey)
                    val storedRecoveryKeyEnc = CryptoEngine.encryptString(recoveryKey, decryptedDek)

                    configStore.recoverySaltBase64 = com.example.adere.core.crypto.Base64Codec.encode(recoverySalt)
                    configStore.recoveryEncryptedDekBase64 = recoveryEncryptedDek.toBase64Ciphertext()
                    configStore.recoveryEncryptedDekIvBase64 = recoveryEncryptedDek.toBase64Iv()
                    configStore.storedRecoveryKeyEncBase64 = storedRecoveryKeyEnc.toBase64Ciphertext()
                    configStore.storedRecoveryKeyIvBase64 = storedRecoveryKeyEnc.toBase64Iv()
                    configStore.hasRecoveryKey = true
                } catch (_: Exception) {}
            }

            // Auto-provision or re-encrypt Biometric DEK if biometric is not user-disabled
            if (!configStore.isBiometricUserDisabled) {
                try {
                    val biometricEncrypted = keystoreManager.encryptDek(decryptedDek)
                    configStore.biometricEncryptedDekBase64 = biometricEncrypted.toBase64Ciphertext()
                    configStore.biometricDekIvBase64 = biometricEncrypted.toBase64Iv()
                    configStore.isBiometricEnabled = true
                } catch (_: Exception) {}
            }

            Result.success(Unit)
        } catch (e: Exception) {
            // Increment failed attempts and rate limit if necessary
            val attempts = configStore.failedAttempts + 1
            configStore.failedAttempts = attempts
            if (attempts >= 5) {
                configStore.lockoutUntilMs = System.currentTimeMillis() + (30_000L * (attempts - 4))
            }
            Result.failure(IllegalArgumentException("Incorrect master password."))
        } finally {
            Arrays.fill(passCopy, '\u0000')
        }
    }

    /**
     * Prepares a [javax.crypto.Cipher] initialized for DEK decryption so it can be
     * passed to [androidx.biometric.BiometricPrompt.CryptoObject].
     *
     * The Cipher is NOT usable yet — the Android Keystore will gate its use until the
     * OS confirms a successful biometric event.  Pass the returned Cipher wrapped in a
     * CryptoObject to BiometricPrompt.authenticate(); then pass the authorized Cipher
     * from [androidx.biometric.BiometricPrompt.AuthenticationResult.cryptoObject] to
     * [unlockWithBiometric].
     *
     * @return null if biometric unlock is not configured or the key has been invalidated.
     */
    fun prepareBiometricCipher(): javax.crypto.Cipher? {
        if (!configStore.isBiometricEnabled) return null
        return try {
            val iv = com.example.adere.core.crypto.Base64Codec.decode(configStore.biometricDekIvBase64)
            keystoreManager.prepareBiometricCipher(iv)
        } catch (e: android.security.keystore.KeyPermanentlyInvalidatedException) {
            // Biometric enrollment changed — purge the key so user is forced to re-enroll
            keystoreManager.deleteBiometricKey()
            configStore.isBiometricEnabled = false
            configStore.biometricEncryptedDekBase64 = ""
            configStore.biometricDekIvBase64 = ""
            null
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Completes vault unlock using the [javax.crypto.Cipher] that was hardware-authorized
     * by a real BiometricPrompt event (via CryptoObject).
     *
     * The Keystore will only allow [authorizedCipher] to decrypt after the TEE/StrongBox
     * has verified the user's biometric — the app code cannot trigger this itself.
     */
    fun unlockWithBiometric(authorizedCipher: javax.crypto.Cipher): Result<Unit> {
        if (!configStore.isBiometricEnabled) {
            return Result.failure(IllegalStateException("Biometric unlock is not enabled."))
        }
        return try {
            val ciphertext = com.example.adere.core.crypto.Base64Codec.decode(
                configStore.biometricEncryptedDekBase64
            )
            val decryptResult = keystoreManager.decryptDekWithCipher(ciphertext, authorizedCipher)
            val decryptedDek = decryptResult.getOrThrow()

            configStore.failedAttempts = 0
            configStore.lockoutUntilMs = 0L
            inMemoryDek = decryptedDek
            lastActiveTime = currentTimeMillis()
            _lockState.value = VaultLockState.Unlocked

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Completes vault unlock using the Keystore key after BiometricPrompt has verified the user.
     */
    fun unlockWithBiometricAuth(): Result<Unit> {
        if (!configStore.isBiometricEnabled || configStore.biometricEncryptedDekBase64.isBlank() || configStore.biometricDekIvBase64.isBlank()) {
            return Result.failure(IllegalStateException("Biometric unlock is not set up. Please unlock with Master Password."))
        }
        return try {
            val ciphertext = com.example.adere.core.crypto.Base64Codec.decode(
                configStore.biometricEncryptedDekBase64
            )
            val iv = com.example.adere.core.crypto.Base64Codec.decode(
                configStore.biometricDekIvBase64
            )
            val decryptResult = keystoreManager.decryptDek(ciphertext, iv)
            val decryptedDek = decryptResult.getOrThrow()

            configStore.failedAttempts = 0
            configStore.lockoutUntilMs = 0L
            inMemoryDek = decryptedDek
            lastActiveTime = currentTimeMillis()
            _lockState.value = VaultLockState.Unlocked

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Toggles biometric unlock on or off using active in-memory DEK.
     */
    fun setBiometricEnabled(enabled: Boolean): Result<Unit> {
        val dek = inMemoryDek ?: return Result.failure(IllegalStateException("Vault must be unlocked to modify biometrics."))
        return try {
            if (enabled) {
                val biometricEncrypted = keystoreManager.encryptDek(dek)
                configStore.biometricEncryptedDekBase64 = biometricEncrypted.toBase64Ciphertext()
                configStore.biometricDekIvBase64 = biometricEncrypted.toBase64Iv()
                configStore.isBiometricEnabled = true
                configStore.isBiometricUserDisabled = false
            } else {
                keystoreManager.deleteBiometricKey()
                configStore.isBiometricEnabled = false
                configStore.isBiometricUserDisabled = true
                configStore.biometricEncryptedDekBase64 = ""
                configStore.biometricDekIvBase64 = ""
            }
            Result.success(Unit)
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: e.message ?: "Failed to configure biometric encryption key"
            Result.failure(Exception(msg, e))
        }
    }

    /**
     * Cryptographically verifies whether the given Master Password matches the vault.
     * Derives key using PBKDF2-HMAC-SHA256 and tests decryption against stored encrypted DEK.
     * Does NOT unlock or modify vault state.
     */
    fun verifyMasterPassword(password: CharArray): Boolean {
        if (!configStore.isInitialized) return false
        val passCopy = password.clone()
        return try {
            val salt = com.example.adere.core.crypto.Base64Codec.decode(configStore.masterSaltBase64)
            val ciphertext = com.example.adere.core.crypto.Base64Codec.decode(configStore.encryptedDekBase64)
            val iv = com.example.adere.core.crypto.Base64Codec.decode(configStore.encryptedDekIvBase64)

            val masterKey = CryptoEngine.deriveKey(passCopy, salt)
            val decryptedDek = CryptoEngine.decrypt(ciphertext, iv, masterKey)
            Arrays.fill(decryptedDek, 0.toByte())
            true
        } catch (_: Exception) {
            false
        } finally {
            Arrays.fill(passCopy, '\u0000')
        }
    }

    /**
     * Changes Master Password without having to re-encrypt every item in the vault.
     * Simply re-encrypts the active DEK with the new derived Master Key.
     */
    suspend fun changeMasterPassword(oldPassword: CharArray, newPassword: CharArray): Result<Unit> = withContext(Dispatchers.Default) {
        val dek = inMemoryDek ?: return@withContext Result.failure(IllegalStateException("Vault is locked."))
        val newPassCopy = newPassword.clone()
        // Verify old password first (unlockWithMasterPassword clones and wipes oldPassword)
        val unlockTest = unlockWithMasterPassword(oldPassword)
        if (unlockTest.isFailure) {
            Arrays.fill(newPassCopy, '\u0000')
            return@withContext unlockTest
        }

        return@withContext try {
            val newSalt = CryptoEngine.generateSalt()
            val newMasterKey = CryptoEngine.deriveKey(newPassCopy, newSalt)
            val encryptedDek = CryptoEngine.encrypt(dek, newMasterKey)

            configStore.masterSaltBase64 = com.example.adere.core.crypto.Base64Codec.encode(newSalt)
            configStore.encryptedDekBase64 = encryptedDek.toBase64Ciphertext()
            configStore.encryptedDekIvBase64 = encryptedDek.toBase64Iv()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            Arrays.fill(newPassCopy, '\u0000')
        }
    }

    /**
     * Retrieves the decrypted Master Recovery Key for the user to view or backup.
     * Vault must be unlocked.
     */
    fun getActiveRecoveryKey(): Result<String> {
        val dek = inMemoryDek ?: return Result.failure(IllegalStateException("Vault must be unlocked to view recovery key."))
        return try {
            if (!configStore.hasRecoveryKey || configStore.storedRecoveryKeyEncBase64.isBlank()) {
                // Generate and store on the fly
                val newKey = com.example.adere.core.crypto.RecoveryKeyManager.generateRecoveryKey()
                val normalizedKey = com.example.adere.core.crypto.RecoveryKeyManager.normalizeKey(newKey)
                val recoverySalt = CryptoEngine.generateSalt()
                val recoveryDerivedKey = CryptoEngine.deriveKey(normalizedKey.toCharArray(), recoverySalt)
                val recoveryEncryptedDek = CryptoEngine.encrypt(dek, recoveryDerivedKey)
                val storedRecoveryKeyEnc = CryptoEngine.encryptString(newKey, dek)

                configStore.recoverySaltBase64 = com.example.adere.core.crypto.Base64Codec.encode(recoverySalt)
                configStore.recoveryEncryptedDekBase64 = recoveryEncryptedDek.toBase64Ciphertext()
                configStore.recoveryEncryptedDekIvBase64 = recoveryEncryptedDek.toBase64Iv()
                configStore.storedRecoveryKeyEncBase64 = storedRecoveryKeyEnc.toBase64Ciphertext()
                configStore.storedRecoveryKeyIvBase64 = storedRecoveryKeyEnc.toBase64Iv()
                configStore.hasRecoveryKey = true

                Result.success(newKey)
            } else {
                val ciphertext = com.example.adere.core.crypto.Base64Codec.decode(configStore.storedRecoveryKeyEncBase64)
                val iv = com.example.adere.core.crypto.Base64Codec.decode(configStore.storedRecoveryKeyIvBase64)
                val decrypted = CryptoEngine.decryptString(ciphertext, iv, dek)
                Result.success(decrypted)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Regenerates a new Master Recovery Key, re-encrypting the active DEK.
     */
    suspend fun regenerateRecoveryKey(): Result<String> = withContext(Dispatchers.Default) {
        val dek = inMemoryDek ?: return@withContext Result.failure(IllegalStateException("Vault must be unlocked to regenerate recovery key."))
        return@withContext try {
            val newRecoveryKey = com.example.adere.core.crypto.RecoveryKeyManager.generateRecoveryKey()
            val normalizedKey = com.example.adere.core.crypto.RecoveryKeyManager.normalizeKey(newRecoveryKey)
            val recoverySalt = CryptoEngine.generateSalt()
            val recoveryDerivedKey = CryptoEngine.deriveKey(normalizedKey.toCharArray(), recoverySalt)
            val recoveryEncryptedDek = CryptoEngine.encrypt(dek, recoveryDerivedKey)
            val storedRecoveryKeyEnc = CryptoEngine.encryptString(newRecoveryKey, dek)

            configStore.recoverySaltBase64 = com.example.adere.core.crypto.Base64Codec.encode(recoverySalt)
            configStore.recoveryEncryptedDekBase64 = recoveryEncryptedDek.toBase64Ciphertext()
            configStore.recoveryEncryptedDekIvBase64 = recoveryEncryptedDek.toBase64Iv()
            configStore.storedRecoveryKeyEncBase64 = storedRecoveryKeyEnc.toBase64Ciphertext()
            configStore.storedRecoveryKeyIvBase64 = storedRecoveryKeyEnc.toBase64Iv()
            configStore.hasRecoveryKey = true

            Result.success(newRecoveryKey)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Emergency vault recovery if the Master Password is forgotten.
     * Derives DEK from Recovery Key, generates new Master Password key, and re-encrypts DEK.
     */
    suspend fun recoverVaultWithKey(recoveryKeyInput: String, newMasterPassword: CharArray): Result<Unit> = withContext(Dispatchers.Default) {
        val now = System.currentTimeMillis()
        if (now < configStore.lockoutUntilMs) {
            val remainingSec = ((configStore.lockoutUntilMs - now) / 1000).coerceAtLeast(1)
            return@withContext Result.failure(IllegalStateException("Vault locked due to multiple failed attempts. Wait $remainingSec seconds or reset."))
        }

        val normalized = com.example.adere.core.crypto.RecoveryKeyManager.normalizeKey(recoveryKeyInput)
        if (!com.example.adere.core.crypto.RecoveryKeyManager.isValidKeyFormat(normalized)) {
            return@withContext Result.failure(IllegalArgumentException("Invalid recovery key format. Expected 24 alphanumeric characters."))
        }

        if (!configStore.hasRecoveryKey || configStore.recoveryEncryptedDekBase64.isBlank()) {
            return@withContext Result.failure(IllegalStateException("No recovery key found for this vault."))
        }

        val newPassCopy = newMasterPassword.clone()
        return@withContext try {
            val recoverySalt = com.example.adere.core.crypto.Base64Codec.decode(configStore.recoverySaltBase64)
            val recoveryCiphertext = com.example.adere.core.crypto.Base64Codec.decode(configStore.recoveryEncryptedDekBase64)
            val recoveryIv = com.example.adere.core.crypto.Base64Codec.decode(configStore.recoveryEncryptedDekIvBase64)

            // Derive key from Recovery Key
            val recoveryDerivedKey = CryptoEngine.deriveKey(normalized.toCharArray(), recoverySalt)
            val recoveredDek = CryptoEngine.decrypt(recoveryCiphertext, recoveryIv, recoveryDerivedKey)

            // Successfully decrypted DEK! Re-encrypt with new Master Password
            val newMasterSalt = CryptoEngine.generateSalt()
            val newMasterKey = CryptoEngine.deriveKey(newPassCopy, newMasterSalt)
            val newEncryptedDek = CryptoEngine.encrypt(recoveredDek, newMasterKey)

            configStore.masterSaltBase64 = com.example.adere.core.crypto.Base64Codec.encode(newMasterSalt)
            configStore.encryptedDekBase64 = newEncryptedDek.toBase64Ciphertext()
            configStore.encryptedDekIvBase64 = newEncryptedDek.toBase64Iv()

            // Update stored recovery key ciphertext with recovered DEK
            val storedRecoveryKeyEnc = CryptoEngine.encryptString(com.example.adere.core.crypto.RecoveryKeyManager.formatKey(normalized), recoveredDek)
            configStore.storedRecoveryKeyEncBase64 = storedRecoveryKeyEnc.toBase64Ciphertext()
            configStore.storedRecoveryKeyIvBase64 = storedRecoveryKeyEnc.toBase64Iv()

            // Re-encrypt Biometric DEK if biometric was enabled
            if (configStore.isBiometricEnabled) {
                try {
                    val biometricEncrypted = keystoreManager.encryptDek(recoveredDek)
                    configStore.biometricEncryptedDekBase64 = biometricEncrypted.toBase64Ciphertext()
                    configStore.biometricDekIvBase64 = biometricEncrypted.toBase64Iv()
                } catch (_: Exception) {
                    configStore.isBiometricEnabled = false
                }
            }

            // Reset lockouts and attempts
            configStore.failedAttempts = 0
            configStore.lockoutUntilMs = 0L

            inMemoryDek = recoveredDek
            lastActiveTime = currentTimeMillis()
            _lockState.value = VaultLockState.Unlocked

            Result.success(Unit)
        } catch (e: Exception) {
            val attempts = configStore.failedAttempts + 1
            configStore.failedAttempts = attempts
            if (attempts >= 5) {
                configStore.lockoutUntilMs = System.currentTimeMillis() + (30_000L * (attempts - 4))
            }
            Result.failure(IllegalArgumentException("Incorrect Master Recovery Key. Verification failed."))
        } finally {
            Arrays.fill(newPassCopy, '\u0000')
        }
    }

    /**
     * Locks vault immediately and scrubs in-memory DEK.
     */
    fun lock() {
        inMemoryDek?.let {
            Arrays.fill(it, 0.toByte())
        }
        inMemoryDek = null
        if (configStore.isInitialized) {
            _lockState.value = VaultLockState.Locked
        } else {
            _lockState.value = VaultLockState.Uninitialized
        }
    }
}
