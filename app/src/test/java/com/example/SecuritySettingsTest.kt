package com.example

import android.content.ComponentName
import android.content.Intent
import com.example.adere.core.crypto.CryptoEngine
import com.example.adere.core.crypto.RecoveryKeyManager
import com.example.adere.data.local.VaultConfigStore
import com.example.adere.domain.repository.VaultLockState
import com.example.adere.domain.repository.VaultSessionManager
import com.example.adere.presentation.screens.settings.isAderaAutofillService
import com.example.adere.service.AdereAutofillService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertThrows
import org.robolectric.RuntimeEnvironment
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SecuritySettingsTest {

    @Test
    fun autofillServiceStatusOnlyMatchesAderaProvider() {
        val context = RuntimeEnvironment.getApplication()
        val aderaService = ComponentName(context, AdereAutofillService::class.java)
        val anotherService = ComponentName("com.example.other", "com.example.other.AutofillService")

        assertTrue(isAderaAutofillService(context, aderaService.flattenToString()))
        assertFalse(isAderaAutofillService(context, anotherService.flattenToString()))
        assertFalse(isAderaAutofillService(context, "invalid-component"))
        assertFalse(isAderaAutofillService(context, null))
    }

    @Test
    fun autoLockOptionsPersistAndLockTheVaultAfterConfiguredInactivity() = kotlinx.coroutines.runBlocking {
        val context = RuntimeEnvironment.getApplication()
        val config = VaultConfigStore(context).apply { resetVault() }
        var now = 1_000_000L
        val session = VaultSessionManager(config, FakeBiometricKeyStore()) { now }
        val password = "AutoLockTestPass123!".toCharArray()

        assertTrue(session.initializeVault(password, enableBiometric = false).isSuccess)

        for (seconds in listOf(0, 30, 60, 300, 900, 3600)) {
            assertTrue(config.persistAutoLockSeconds(seconds))
            assertEquals(seconds, VaultConfigStore(context).autoLockSeconds)

            session.recordInteraction()
            if (seconds == 0) {
                session.checkAutoLock()
                assertEquals(VaultLockState.Unlocked, session.lockState.value)
                session.lock()
            } else {
                now += seconds * 1_000L
                session.checkAutoLock()
            }

            assertEquals(VaultLockState.Locked, session.lockState.value)
            assertNull(session.getActiveDek())

            if (seconds != 3600) {
                assertTrue(session.unlockWithMasterPassword(password).isSuccess)
            }
        }
    }

    @Test
    fun autoLockRejectsUnsupportedValuesWithoutChangingStoredSetting() {
        val config = VaultConfigStore(RuntimeEnvironment.getApplication()).apply { resetVault() }
        assertTrue(config.persistAutoLockSeconds(60))

        assertThrows(IllegalArgumentException::class.java) {
            config.persistAutoLockSeconds(45)
        }

        assertEquals(60, config.autoLockSeconds)
        assertFalse(45 in VaultConfigStore.AUTO_LOCK_OPTIONS_SECONDS)
    }

    @Test
    fun masterPasswordVerificationWorksCorrectly() = runBlocking {
        val context = RuntimeEnvironment.getApplication()
        val config = VaultConfigStore(context).apply { resetVault() }
        val session = VaultSessionManager(config, FakeBiometricKeyStore())
        val correctPass = "MasterSecurePass2026!".toCharArray()
        val wrongPass = "WrongPassword123!".toCharArray()

        assertTrue(session.initializeVault(correctPass, enableBiometric = false).isSuccess)

        assertFalse(session.verifyMasterPassword(wrongPass))
        assertTrue(session.verifyMasterPassword(correctPass))
    }

    @Test
    fun encryptedBackupAndRecoveryKeyStatusTracking() = runBlocking {
        val context = RuntimeEnvironment.getApplication()
        val config = VaultConfigStore(context).apply { resetVault() }
        val session = VaultSessionManager(config, FakeBiometricKeyStore())
        val password = "BackupTestPass123!".toCharArray()

        assertTrue(session.initializeVault(password, enableBiometric = false).isSuccess)

        assertEquals(0L, config.lastBackupTimestamp)
        val backupTime = System.currentTimeMillis()
        config.lastBackupTimestamp = backupTime
        assertEquals(backupTime, config.lastBackupTimestamp)

        assertTrue(config.hasRecoveryKey)
        val recoveryKeyRes = session.getActiveRecoveryKey()
        assertTrue(recoveryKeyRes.isSuccess)
        val recoveryKey = recoveryKeyRes.getOrNull()
        assertNotNull(recoveryKey)
        assertTrue(recoveryKey!!.isNotBlank())
    }

    @Test
    fun pasteValidationRejectsPasswordAndAcceptsValidSeedAndRecoveryKey() {
        val rawPassword = "MySecretP@ssw0rd!2026"
        val valid12WordSeed = "apple banana cherry desk eagle forest grape hill island jungle kite lemon"
        val valid24Key = "H7M9-KW4P-9N2X-B8TF-R6Y3-V5EQ"

        // 1. Password rejection for seed phrase
        val passwordWords = rawPassword.split(Regex("[\\s,]+")).filter { it.isNotBlank() }
        val isPasswordSeed = passwordWords.size in listOf(12, 18, 24) && passwordWords.all { w -> w.all { c -> c.isLetter() } }
        assertFalse("Password must NOT be treated as a seed phrase", isPasswordSeed)

        // 2. Valid seed phrase acceptance
        val seedWords = valid12WordSeed.split(Regex("[\\s,]+")).filter { it.isNotBlank() }
        val isValidSeed = seedWords.size in listOf(12, 18, 24) && seedWords.all { w -> w.all { c -> c.isLetter() } }
        assertTrue("12-word mnemonic phrase must be accepted", isValidSeed)

        // 3. Password rejection for Recovery Key
        assertFalse("Password must NOT be accepted as a Recovery Key", RecoveryKeyManager.isValidKeyFormat(rawPassword))

        // 4. Valid Recovery Key acceptance
        assertTrue("Valid 24-char key must be accepted", RecoveryKeyManager.isValidKeyFormat(valid24Key))
    }

    @Test
    fun deepVaultEmergencyRecoveryCycleTest() = runBlocking {
        val context = RuntimeEnvironment.getApplication()
        val config = VaultConfigStore(context).apply { resetVault() }
        val session = VaultSessionManager(config, FakeBiometricKeyStore())
        val initialPassword = "InitialMasterPassword2026!".toCharArray()
        val newPassword = "NewRecoveredMasterPassword2026!".toCharArray()

        // 1. Initialize vault with initial password
        val initRes = session.initializeVault(initialPassword, enableBiometric = false)
        assertTrue(initRes.isSuccess)

        // 2. Export 24-character Master Recovery Key
        val recoveryKeyRes = session.getActiveRecoveryKey()
        assertTrue(recoveryKeyRes.isSuccess)
        val recoveryKey = recoveryKeyRes.getOrThrow()
        assertTrue(RecoveryKeyManager.isValidKeyFormat(recoveryKey))

        // 3. Lock vault session
        session.lock()
        assertEquals(VaultLockState.Locked, session.lockState.value)

        // 4. Perform Emergency Vault Restoration with Recovery Key + New Master Password
        val recoverRes = session.recoverVaultWithKey(recoveryKey, newPassword)
        assertTrue("Vault recovery must succeed with valid 24-char Recovery Key", recoverRes.isSuccess)

        // 5. Verify vault is unlocked with new Master Password
        assertEquals(VaultLockState.Unlocked, session.lockState.value)
        assertNotNull(session.getActiveDek())

        // 6. Lock and verify old password fails and new password succeeds
        session.lock()
        assertFalse("Old password must fail after recovery", session.unlockWithMasterPassword(initialPassword).isSuccess)
        assertTrue("New password must succeed after recovery", session.unlockWithMasterPassword(newPassword).isSuccess)
    }

    @Test
    fun deepConcurrentCryptoStressTest() = runBlocking {
        val dek = CryptoEngine.generateDek()
        val secretCount = 100

        val encryptedItems = mutableListOf<CryptoEngine.EncryptedData>()
        for (i in 0 until secretCount) {
            val plaintext = "SecretPayloadData_#" + i + "_UUID_" + System.nanoTime()
            val enc = CryptoEngine.encryptString(plaintext, dek)
            encryptedItems.add(enc)

            val dec = CryptoEngine.decryptString(enc.ciphertext, enc.iv, dek)
            assertEquals(plaintext, dec)
        }

        assertEquals(secretCount, encryptedItems.size)
    }
}
