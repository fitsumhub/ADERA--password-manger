package com.example

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.adere.core.backup.BackupManager
import com.example.adere.core.crypto.CryptoEngine
import com.example.adere.core.crypto.PasswordGenerator
import com.example.adere.core.crypto.PasswordHealthAnalyzer
import com.example.adere.core.crypto.TOTPGenerator
import com.example.adere.core.utilities.AppIconManager
import com.example.adere.data.local.VaultConfigStore
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import com.example.adere.domain.model.VaultItemPayload
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {

    @Test
    fun testAesGcmEncryptionDecryptionRoundTrip() {
        val testDek = CryptoEngine.generateDek()
        val originalPlaintext = "SuperSecretAderePassword!2026#Vault"

        val encrypted = CryptoEngine.encryptString(originalPlaintext, testDek)
        assertNotNull(encrypted.ciphertext)
        assertNotNull(encrypted.iv)
        assertEquals(CryptoEngine.GCM_IV_SIZE_BYTES, encrypted.iv.size)

        val decrypted = CryptoEngine.decryptString(encrypted.ciphertext, encrypted.iv, testDek)
        assertEquals(originalPlaintext, decrypted)
    }

    @Test(expected = Exception::class)
    fun testAesGcmCorruptedCiphertextFails() {
        val testDek = CryptoEngine.generateDek()
        val original = "SensitiveSeedPhrase"
        val encrypted = CryptoEngine.encryptString(original, testDek)

        // Tamper with the ciphertext
        val corruptedCiphertext = encrypted.ciphertext.copyOf()
        corruptedCiphertext[0] = (corruptedCiphertext[0].toInt() xor 0xFF).toByte()

        // Should throw AEADBadTagException / GeneralSecurityException
        CryptoEngine.decryptString(corruptedCiphertext, encrypted.iv, testDek)
    }

    @Test
    fun testDifferentIvsProducedForIdenticalPlaintext() {
        val testDek = CryptoEngine.generateDek()
        val text = "ConstantPlaintext"

        val enc1 = CryptoEngine.encryptString(text, testDek)
        val enc2 = CryptoEngine.encryptString(text, testDek)

        assertFalse(enc1.iv.contentEquals(enc2.iv))
        assertFalse(enc1.ciphertext.contentEquals(enc2.ciphertext))
    }

    @Test
    fun testPbkdf2KeyDerivation() {
        val salt = CryptoEngine.generateSalt()
        val pass = "AdereMasterPassword2026!".toCharArray()

        val key1 = CryptoEngine.deriveKey(pass, salt, iterations = 10_000)
        val key2 = CryptoEngine.deriveKey(pass, salt, iterations = 10_000)

        assertTrue(key1.encoded.contentEquals(key2.encoded))

        // Different salt yields different key
        val salt2 = CryptoEngine.generateSalt()
        val key3 = CryptoEngine.deriveKey(pass, salt2, iterations = 10_000)
        assertFalse(key1.encoded.contentEquals(key3.encoded))
    }

    @Test
    fun testPasswordGeneratorOptionsAndEntropy() {
        val options = PasswordGenerator.GeneratorOptions(
            length = 24,
            includeUppercase = true,
            includeLowercase = true,
            includeDigits = true,
            includeSymbols = true,
            excludeAmbiguous = true,
        )

        val result = PasswordGenerator.generate(options)
        assertEquals(24, result.password.length)
        assertTrue(result.password.any { it.isUpperCase() })
        assertTrue(result.password.any { it.isLowerCase() })
        assertTrue(result.password.any { it.isDigit() })
        assertTrue(result.entropyBits > 80.0)
        assertEquals(PasswordGenerator.PasswordStrength.VERY_STRONG, result.strength)
    }

    @Test
    fun testPasswordGeneratorPassphraseMode() {
        val options = PasswordGenerator.GeneratorOptions(
            isPassphraseMode = true,
            passphraseWordCount = 4,
            passphraseSeparator = "-"
        )
        val result = PasswordGenerator.generate(options)
        assertTrue(result.password.contains("-"))
        assertTrue(result.password.length >= 16)
    }

    @Test
    fun testTotpGenerationAndBase32Decoding() {
        // Standard RFC 4226 / 6238 Base32 test vector
        val secret = "JBSWY3DPEHPK3PXP"
        val decoded = TOTPGenerator.decodeBase32(secret)
        assertNotNull(decoded)
        assertEquals("Hello", String(decoded!!.take(5).toByteArray(), Charsets.UTF_8))

        val result = TOTPGenerator.generateCurrentTotp(
            secretBase32 = secret,
            currentTimeMillis = 1600000000000L
        )
        assertNotNull(result)
        assertEquals(6, result!!.code.length)
        assertTrue(result.secondsRemaining in 0..30)
    }

    @Test
    fun testPasswordHealthAnalyzer() {
        val items = listOf(
            PasswordHealthAnalyzer.PasswordItemInfo(
                id = "1",
                title = "Service A",
                username = "user1",
                passwordPlaintext = "weak",
                has2FA = false,
                passwordLastChangedEpochMs = System.currentTimeMillis()
            ),
            PasswordHealthAnalyzer.PasswordItemInfo(
                id = "2",
                title = "Service B",
                username = "user2",
                passwordPlaintext = "weak", // Reused
                has2FA = false,
                passwordLastChangedEpochMs = System.currentTimeMillis()
            ),
            PasswordHealthAnalyzer.PasswordItemInfo(
                id = "3",
                title = "Service C",
                username = "user3",
                passwordPlaintext = "ComplexP@ssw0rd!2026#Secure",
                has2FA = true,
                passwordLastChangedEpochMs = System.currentTimeMillis()
            )
        )

        val report = PasswordHealthAnalyzer.analyze(items)
        assertEquals(3, report.totalItems)
        assertEquals(3, report.totalPasswords)
        assertEquals(2, report.weakCount) // "weak" is short
        assertEquals(2, report.reusedCount) // "weak" used twice
        assertEquals(2, report.missing2faCount)
    }

    @Test
    fun testEncryptedBackupAndRestore() {
        val item1 = VaultItem(
            id = UUID.randomUUID().toString(),
            category = VaultCategory.SOCIAL,
            title = "Test Social",
            username = "alice",
            payload = VaultItemPayload(password = "P@ssword123", url = "https://example.com")
        )

        val backupPassphrase = "MySecretBackupKey999!".toCharArray()
        val exportResult = BackupManager.createEncryptedBackup(listOf(item1), backupPassphrase)

        assertTrue(exportResult.backupString.startsWith("ADERE_VAULT_BACKUP:v2:") || exportResult.backupString.startsWith("ADERE_VAULT_BACKUP:v1:"))
        assertEquals(1, exportResult.itemCount)

        // Decrypt with correct passphrase
        val restoreResult = BackupManager.decryptAndValidateBackup(exportResult.backupString, backupPassphrase)
        assertTrue(restoreResult.isSuccess)
        val preview = restoreResult.getOrThrow()
        assertEquals(1, preview.itemCount)
        assertEquals("Test Social", preview.items[0].title)
        assertEquals("P@ssword123", preview.items[0].payload.password)

        // Decrypt with incorrect passphrase fails securely
        val wrongResult = BackupManager.decryptAndValidateBackup(exportResult.backupString, "WrongPassword!".toCharArray())
        assertTrue(wrongResult.isFailure)
    }

    @Test
    fun testPdfExportGeneration() {
        val item1 = VaultItem(
            id = UUID.randomUUID().toString(),
            category = VaultCategory.EMAIL,
            title = "Personal Gmail",
            username = "alice@example.com",
            payload = VaultItemPayload(password = "Secr3tP@ss!", url = "https://mail.google.com")
        )
        val item2 = VaultItem(
            id = UUID.randomUUID().toString(),
            category = VaultCategory.CRYPTO,
            title = "Bitcoin Cold Storage",
            username = "bc1q...",
            payload = VaultItemPayload(
                cryptoAddress = "bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq",
                cryptoNetwork = "Bitcoin"
            )
        )

        val byteArrayOutputStream = java.io.ByteArrayOutputStream()
        val options = com.example.adere.core.backup.PdfExportManager.ExportOptions(
            includePasswords = true,
            includeCryptoSecrets = true,
            includeNotes = true
        )
        com.example.adere.core.backup.PdfExportManager.exportToPdf(
            items = listOf(item1, item2),
            options = options,
            outputStream = byteArrayOutputStream
        )

        val pdfBytes = byteArrayOutputStream.toByteArray()
        assertTrue(pdfBytes.isNotEmpty())
        val pdfHeader = String(pdfBytes.take(5).toByteArray(), Charsets.US_ASCII)
        assertEquals("%PDF-", pdfHeader)
    }

    @Test
    fun testVaultSearchFilteringByTitleAndCategory() {
        val item1 = VaultItem(
            id = "1",
            category = VaultCategory.SOCIAL,
            title = "Instagram Account",
            username = "insta_user",
            payload = VaultItemPayload(password = "P@ss1")
        )
        val item2 = VaultItem(
            id = "2",
            category = VaultCategory.CRYPTO,
            title = "Cold Storage Ledger",
            username = "0x123",
            payload = VaultItemPayload(cryptoNetwork = "Ethereum")
        )
        val item3 = VaultItem(
            id = "3",
            category = VaultCategory.EMAIL,
            title = "Work Gmail",
            username = "worker@company.com",
            payload = VaultItemPayload(password = "P@ss3")
        )
        val allItems = listOf(item1, item2, item3)

        fun filterItems(query: String, category: VaultCategory = VaultCategory.ALL): List<VaultItem> {
            return allItems.filter { item ->
                val matchesCategory = when (category) {
                    VaultCategory.ALL -> true
                    VaultCategory.FAVORITES -> item.isFavorite
                    else -> item.category == category
                }
                val matchesQuery = query.isBlank() ||
                        item.title.contains(query, ignoreCase = true) ||
                        item.category.title.contains(query, ignoreCase = true) ||
                        item.category.name.contains(query, ignoreCase = true) ||
                        item.username.contains(query, ignoreCase = true)
                matchesCategory && matchesQuery
            }
        }

        // Search by title "Instagram"
        val byTitle = filterItems("Instagram")
        assertEquals(1, byTitle.size)
        assertEquals("Instagram Account", byTitle[0].title)

        // Search by category "Crypto"
        val byCategory = filterItems("Crypto")
        assertEquals(1, byCategory.size)
        assertEquals("Cold Storage Ledger", byCategory[0].title)

        // Search by category "Social"
        val bySocialCategory = filterItems("Social")
        assertEquals(1, bySocialCategory.size)
        assertEquals("Instagram Account", bySocialCategory[0].title)

        // Search by category "Email"
        val byEmailCategory = filterItems("Email")
        assertEquals(1, byEmailCategory.size)
        assertEquals("Work Gmail", byEmailCategory[0].title)

        // Search with non-matching query
        val emptyResult = filterItems("NonExistentXYZ")
        assertTrue(emptyResult.isEmpty())
    }

    @Test
    fun testBiometricSessionLifecycleAndLockState() {
        val context = org.robolectric.RuntimeEnvironment.getApplication()
        val configStore = com.example.adere.data.local.VaultConfigStore(context)
        val keystoreManager = FakeBiometricKeyStore()
        val sessionManager = com.example.adere.domain.repository.VaultSessionManager(configStore, keystoreManager)

        // Fresh session is uninitialized
        assertFalse(sessionManager.isVaultInitialized())
        assertEquals(com.example.adere.domain.repository.VaultLockState.Uninitialized, sessionManager.lockState.value)

        // Test lock state transitions
        sessionManager.lock()
        assertEquals(com.example.adere.domain.repository.VaultLockState.Uninitialized, sessionManager.lockState.value)
    }

    @Test
    fun testMasterRecoveryKeyFormatAndValidation() {
        val key = com.example.adere.core.crypto.RecoveryKeyManager.generateRecoveryKey()
        assertNotNull(key)
        assertEquals(29, key.length) // 24 chars + 5 hyphens = 29
        assertTrue(com.example.adere.core.crypto.RecoveryKeyManager.isValidKeyFormat(key))

        // Normalization
        val raw = com.example.adere.core.crypto.RecoveryKeyManager.normalizeKey(key)
        assertEquals(24, raw.length)
        val reformatted = com.example.adere.core.crypto.RecoveryKeyManager.formatKey(raw)
        assertEquals(key, reformatted)

        // Invalid key rejects
        assertFalse(com.example.adere.core.crypto.RecoveryKeyManager.isValidKeyFormat("INVALID-TOO-SHORT"))
    }

    @Test
    fun testVaultRecoveryWithKeyWhenPasswordForgotten() = kotlinx.coroutines.runBlocking {
        val context = org.robolectric.RuntimeEnvironment.getApplication()
        val configStore = com.example.adere.data.local.VaultConfigStore(context)
        val keystoreManager = FakeBiometricKeyStore()
        val sessionManager = com.example.adere.domain.repository.VaultSessionManager(configStore, keystoreManager)

        // 1. Initialize vault with initial master password
        val initResult = sessionManager.initializeVault("InitialPass123!".toCharArray(), enableBiometric = false)
        assertTrue(initResult.isSuccess)
        assertTrue(sessionManager.isVaultInitialized())

        // 2. Fetch the active recovery key
        val keyResult = sessionManager.getActiveRecoveryKey()
        assertTrue(keyResult.isSuccess)
        val recoveryKey = keyResult.getOrThrow()
        assertTrue(recoveryKey.isNotBlank())

        // 3. User locks vault and forgets password
        sessionManager.lock()
        assertEquals(com.example.adere.domain.repository.VaultLockState.Locked, sessionManager.lockState.value)

        // 4. Attempt recovery with wrong key fails
        val badKeyResult = sessionManager.recoverVaultWithKey("AAAA-BBBB-CCCC-DDDD-EEEE-FFFF", "NewPass456!".toCharArray())
        assertTrue(badKeyResult.isFailure)
        assertEquals(com.example.adere.domain.repository.VaultLockState.Locked, sessionManager.lockState.value)

        // 5. Successful recovery using correct Master Recovery Key
        val recoveryResult = sessionManager.recoverVaultWithKey(recoveryKey, "NewPass456!".toCharArray())
        assertTrue(recoveryResult.isSuccess)
        assertEquals(com.example.adere.domain.repository.VaultLockState.Unlocked, sessionManager.lockState.value)

        // 6. Test lock and unlock with new password
        sessionManager.lock()
        assertEquals(com.example.adere.domain.repository.VaultLockState.Locked, sessionManager.lockState.value)

        // Old password must fail
        val oldUnlock = sessionManager.unlockWithMasterPassword("InitialPass123!".toCharArray())
        assertTrue(oldUnlock.isFailure)

        // New password must succeed
        val newUnlock = sessionManager.unlockWithMasterPassword("NewPass456!".toCharArray())
        assertTrue(newUnlock.isSuccess)
        assertEquals(com.example.adere.domain.repository.VaultLockState.Unlocked, sessionManager.lockState.value)
    }

    @Test
    fun testCorruptedBackupHandling() {
        val badBackupString = "ADERE_VAULT_BACKUP:v1:INVALID_BASE64_DATA"
        val result = BackupManager.decryptAndValidateBackup(badBackupString, "AnyPass123!".toCharArray())
        assertTrue(result.isFailure)
    }

    @Test
    fun testPasswordStrengthEvaluation() {
        val weak = PasswordGenerator.evaluateStrength(20.0, 4)
        assertEquals(PasswordGenerator.PasswordStrength.VERY_WEAK, weak)

        val veryStrong = PasswordGenerator.evaluateStrength(120.0, 32)
        assertEquals(PasswordGenerator.PasswordStrength.VERY_STRONG, veryStrong)
    }

    @Test
    fun testExtremelyLongPasswordRoundTrip() {
        val dek = CryptoEngine.generateDek()
        val longText = "A".repeat(5000) + "!@#$%"
        val encrypted = CryptoEngine.encryptString(longText, dek)
        val decrypted = CryptoEngine.decryptString(encrypted.ciphertext, encrypted.iv, dek)
        assertEquals(longText, decrypted)
    }

    @Test
    fun testPasswordHealthOverallRating() {
        val items = listOf(
            PasswordHealthAnalyzer.PasswordItemInfo(
                id = "1",
                title = "Secure Service",
                username = "admin",
                passwordPlaintext = "SecureP@ssw0rd!2026#",
                has2FA = true,
                passwordLastChangedEpochMs = System.currentTimeMillis()
            )
        )
        val report = PasswordHealthAnalyzer.analyze(items)
        assertEquals("10 / 10", report.overallRating)
    }

    @Test
    fun testKeystoreBiometricFlow() = kotlinx.coroutines.runBlocking {
        val keystoreManager = FakeBiometricKeyStore()
        val testDek = CryptoEngine.generateDek()

        // 1. Encryption
        val encrypted = keystoreManager.encryptDek(testDek)
        assertNotNull(encrypted.ciphertext)
        assertNotNull(encrypted.iv)
        assertTrue(keystoreManager.hasBiometricKey())

        // 2. Direct Keystore Decryption
        val decryptedResult = keystoreManager.decryptDek(encrypted.ciphertext, encrypted.iv)
        assertTrue(decryptedResult.isSuccess)
        val decryptedDek = decryptedResult.getOrThrow()
        assertTrue(testDek.contentEquals(decryptedDek))

        // 3. VaultSessionManager with enableBiometric = true
        val context = org.robolectric.RuntimeEnvironment.getApplication()
        val configStore = com.example.adere.data.local.VaultConfigStore(context)
        configStore.resetVault()
        val sessionManager = com.example.adere.domain.repository.VaultSessionManager(configStore, keystoreManager)

        val initResult = sessionManager.initializeVault("MasterPass123!".toCharArray(), enableBiometric = true)
        assertTrue(initResult.isSuccess)
        assertTrue(configStore.isBiometricEnabled)
        assertTrue(sessionManager.isBiometricEnabled())
        assertEquals(com.example.adere.domain.repository.VaultLockState.Unlocked, sessionManager.lockState.value)

        // Lock vault
        sessionManager.lock()
        assertEquals(com.example.adere.domain.repository.VaultLockState.Locked, sessionManager.lockState.value)

        // Unlock with biometric auth
        val unlockResult = sessionManager.unlockWithBiometricAuth()
        assertTrue(unlockResult.isSuccess)
        assertEquals(com.example.adere.domain.repository.VaultLockState.Unlocked, sessionManager.lockState.value)

        // Active DEK matches original
        assertNotNull(sessionManager.getActiveDek())
    }

    @Test
    fun testV2EncryptedBackupCreationAndIntegrity() {
        val item1 = VaultItem(
            id = UUID.randomUUID().toString(),
            category = VaultCategory.WEBSITE,
            title = "ProtonMail",
            username = "security@proton.me",
            payload = VaultItemPayload(password = "UltraSafePass#2026", url = "https://mail.proton.me")
        )
        val item2 = VaultItem(
            id = UUID.randomUUID().toString(),
            category = VaultCategory.CRYPTO,
            title = "Ethereum Vault",
            username = "0xAdere...",
            payload = VaultItemPayload(cryptoAddress = "0x1234567890abcdef", cryptoNetwork = "Ethereum")
        )

        val passphrase = "MasterBackupPassphrase2026!".toCharArray()
        val exportResult = BackupManager.createEncryptedBackup(listOf(item1, item2), passphrase)

        // 1. Verify v2 format and structure
        assertTrue(exportResult.backupString.startsWith("ADERE_VAULT_BACKUP:v2:"))
        val parts = exportResult.backupString.split(":")
        assertEquals(6, parts.size) // MAGIC, v2, salt, iv, ciphertext, checksum
        assertEquals(2, exportResult.itemCount)
        assertTrue(exportResult.fileSizeBytes > 0)
        assertTrue(exportResult.defaultFileName.startsWith("Adere_Backup_"))
        assertTrue(exportResult.defaultFileName.endsWith(".adere"))

        // 2. Validate header inspection
        val headerInfoResult = BackupManager.validateBackupHeader(exportResult.backupString)
        assertTrue(headerInfoResult.isSuccess)
        val headerInfo = headerInfoResult.getOrThrow()
        assertEquals("ADERE_VAULT_BACKUP", headerInfo.magic)
        assertEquals("v2", headerInfo.formatVersion)
        assertTrue(headerInfo.isFormatValid)

        // 3. Round-trip decrypt & validate preview
        val previewResult = BackupManager.decryptAndValidateBackup(exportResult.backupString, passphrase)
        assertTrue(previewResult.isSuccess)
        val preview = previewResult.getOrThrow()
        assertEquals(2, preview.itemCount)
        assertEquals(2, preview.categoryCount)
        assertEquals("v2", preview.version)
        assertEquals("ProtonMail", preview.items[0].title)
        assertEquals("UltraSafePass#2026", preview.items[0].payload.password)
        assertEquals("Ethereum Vault", preview.items[1].title)
    }

    @Test
    fun testTamperedV2BackupIntegrityChecksumFails() {
        val item = VaultItem(
            id = UUID.randomUUID().toString(),
            category = VaultCategory.BANKING,
            title = "Swiss Bank",
            username = "swiss_user",
            payload = VaultItemPayload(password = "BankSecure2026!")
        )
        val passphrase = "Passphrase123!".toCharArray()
        val exportResult = BackupManager.createEncryptedBackup(listOf(item), passphrase)

        // Tamper with the ciphertext component (part index 4)
        val parts = exportResult.backupString.split(":").toMutableList()
        val originalCiphertext = parts[4]
        // Change one character in the Base64 ciphertext
        val tamperedCiphertext = if (originalCiphertext.endsWith("A")) {
            originalCiphertext.dropLast(1) + "B"
        } else {
            originalCiphertext.dropLast(1) + "A"
        }
        parts[4] = tamperedCiphertext
        val tamperedBackupString = parts.joinToString(":")

        // Decryption must fail due to SHA-256 checksum mismatch
        val result = BackupManager.decryptAndValidateBackup(tamperedBackupString, passphrase)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("integrity", ignoreCase = true) == true)
    }

    @Test
    fun testMergeVaultItemsConflictResolution() {
        val itemId1 = UUID.randomUUID().toString()
        val itemId2 = UUID.randomUUID().toString()
        val itemId3 = UUID.randomUUID().toString()

        val existingItem1 = VaultItem(
            id = itemId1,
            category = VaultCategory.WEBSITE,
            title = "Existing Item 1",
            username = "user1",
            updatedAt = 1000L
        )
        val existingItem2 = VaultItem(
            id = itemId2,
            category = VaultCategory.SOCIAL,
            title = "Existing Item 2 (Newer Locally)",
            username = "user2",
            updatedAt = 5000L
        )

        val incomingItem1 = VaultItem(
            id = itemId1,
            category = VaultCategory.WEBSITE,
            title = "Existing Item 1 (Updated Remotely)",
            username = "user1_new",
            updatedAt = 2000L // Newer than existingItem1
        )
        val incomingItem2 = VaultItem(
            id = itemId2,
            category = VaultCategory.SOCIAL,
            title = "Existing Item 2 (Older in Backup)",
            username = "user2_old",
            updatedAt = 3000L // Older than existingItem2
        )
        val incomingItem3 = VaultItem(
            id = itemId3,
            category = VaultCategory.BANKING,
            title = "Completely New Incoming Item",
            username = "user3",
            updatedAt = 4000L
        )

        val existingList = listOf(existingItem1, existingItem2)
        val incomingList = listOf(incomingItem1, incomingItem2, incomingItem3)

        val mergeResult = BackupManager.resolveMerge(existingList, incomingList)

        assertEquals(3, mergeResult.resolvedItems.size)
        assertEquals(1, mergeResult.addedCount) // itemId3 added
        assertEquals(1, mergeResult.updatedCount) // itemId1 updated
        assertEquals(1, mergeResult.unchangedCount) // itemId2 retained because local was newer

        val resolvedItem1 = mergeResult.resolvedItems.first { it.id == itemId1 }
        assertEquals("Existing Item 1 (Updated Remotely)", resolvedItem1.title)
        assertEquals("user1_new", resolvedItem1.username)

        val resolvedItem2 = mergeResult.resolvedItems.first { it.id == itemId2 }
        assertEquals("Existing Item 2 (Newer Locally)", resolvedItem2.title)

        val resolvedItem3 = mergeResult.resolvedItems.first { it.id == itemId3 }
        assertEquals("Completely New Incoming Item", resolvedItem3.title)
    }

    @Test
    fun testBackupHistoryModelSerialization() {
        val entry = com.example.adere.domain.model.BackupHistoryEntry(
            id = "test-uuid-1234",
            timestamp = 1775312345000L,
            itemCount = 42,
            fileSizeBytes = 102400L,
            actionType = com.example.adere.domain.model.BackupActionType.BACKUP_CREATED,
            fileName = "Adere_Backup_2026-10-04.adere"
        )

        val json = entry.toJson()
        val parsed = com.example.adere.domain.model.BackupHistoryEntry.fromJson(json)

        assertEquals(entry.id, parsed.id)
        assertEquals(entry.timestamp, parsed.timestamp)
        assertEquals(entry.itemCount, parsed.itemCount)
        assertEquals(entry.fileSizeBytes, parsed.fileSizeBytes)
        assertEquals(entry.actionType, parsed.actionType)
        assertEquals(entry.fileName, parsed.fileName)
    }

    @Test
    fun testPdfExportOptionsMaskingSupport() {
        val item = VaultItem(
            id = UUID.randomUUID().toString(),
            category = VaultCategory.WEBSITE,
            title = "Secret Portal",
            username = "agent007",
            payload = VaultItemPayload(password = "SuperSecretUnmasked12345")
        )

        val outputStream = java.io.ByteArrayOutputStream()
        val maskedOptions = com.example.adere.core.backup.PdfExportManager.ExportOptions(
            includePasswords = false,
            maskPasswords = true
        )

        com.example.adere.core.backup.PdfExportManager.exportToPdf(
            items = listOf(item),
            options = maskedOptions,
            outputStream = outputStream
        )

        val pdfBytes = outputStream.toByteArray()
        assertTrue(pdfBytes.isNotEmpty())
        // When masked, the plaintext password must NOT appear anywhere in the generated PDF raw stream
        val pdfContent = String(pdfBytes, Charsets.ISO_8859_1)
        assertFalse(pdfContent.contains("SuperSecretUnmasked12345"))
    }

    @Test
    fun testWifiQrCodeFormatting() {
        val standardWifi = com.example.adere.core.utilities.WifiQrCodeGenerator.formatWifiString(
            ssid = "AdereOffice",
            password = "SecurePassword2026!",
            securityType = "WPA2/WPA3"
        )
        assertEquals("WIFI:T:WPA;S:AdereOffice;P:SecurePassword2026!;;\n".trim(), standardWifi)

        val escapedWifi = com.example.adere.core.utilities.WifiQrCodeGenerator.formatWifiString(
            ssid = "Guest;Network:East",
            password = "p\\ass;word:123",
            securityType = "WPA"
        )
        assertEquals("WIFI:T:WPA;S:Guest\\;Network\\:East;P:p\\\\ass\\;word\\:123;;", escapedWifi)

        val openWifi = com.example.adere.core.utilities.WifiQrCodeGenerator.formatWifiString(
            ssid = "Airport_Free_Wifi",
            password = "",
            securityType = "Open"
        )
        assertEquals("WIFI:T:nopass;S:Airport_Free_Wifi;P:;;", openWifi)
    }

    @Test
    fun testWifiQrCodeBitmapGeneration() {
        val wifiPayload = com.example.adere.core.utilities.WifiQrCodeGenerator.formatWifiString(
            ssid = "TestNet",
            password = "SecretPass123"
        )
        val bitmap = com.example.adere.core.utilities.WifiQrCodeGenerator.generateQrBitmap(wifiPayload, 256)
        assertNotNull(bitmap)
        assertEquals(256, bitmap.width)
        assertEquals(256, bitmap.height)
    }

    @Test
    fun testHapticFeedbackHelperExecution() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        // Verify all tactile levels execute safely without crashing
        com.example.adere.core.utilities.HapticFeedbackHelper.tick(context)
        com.example.adere.core.utilities.HapticFeedbackHelper.click(context)
        com.example.adere.core.utilities.HapticFeedbackHelper.confirm(context)
        com.example.adere.core.utilities.HapticFeedbackHelper.warning(context)
        com.example.adere.core.utilities.HapticFeedbackHelper.error(context)
    }

    @Test
    fun testKeystoreBiometricEncryptDecryptRoundTrip() {
        val keystoreManager = FakeBiometricKeyStore()
        val originalDek = CryptoEngine.generateDek()

        val encryptedData = keystoreManager.encryptDek(originalDek)
        assertNotNull(encryptedData.ciphertext)
        assertNotNull(encryptedData.iv)

        val decryptResult = keystoreManager.decryptDek(encryptedData.ciphertext, encryptedData.iv)
        assertTrue(decryptResult.isSuccess)
        val decryptedDek = decryptResult.getOrThrow()
        assertTrue(originalDek.contentEquals(decryptedDek))
    }

    @Test
    fun testVaultSessionManagerBiometricLifecycle() = kotlinx.coroutines.runBlocking {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val configStore = com.example.adere.data.local.VaultConfigStore(context)
        val keystoreManager = FakeBiometricKeyStore()
        val sessionManager = com.example.adere.domain.repository.VaultSessionManager(configStore, keystoreManager)

        val masterPassword = "TestVaultPassword123!".toCharArray()

        // 1. Initialize vault with biometric enabled
        val initRes = sessionManager.initializeVault(masterPassword, enableBiometric = true)
        assertTrue(initRes.isSuccess)
        assertTrue(sessionManager.isBiometricEnabled())
        assertTrue(configStore.isBiometricEnabled)
        assertTrue(configStore.biometricEncryptedDekBase64.isNotBlank())
        assertTrue(configStore.biometricDekIvBase64.isNotBlank())

        // 2. Lock vault
        sessionManager.lock()
        assertEquals(com.example.adere.domain.repository.VaultLockState.Locked, sessionManager.lockState.value)

        // 3. Unlock with Biometric Auth
        val bioUnlockRes = sessionManager.unlockWithBiometricAuth()
        assertTrue(bioUnlockRes.isSuccess)
        assertEquals(com.example.adere.domain.repository.VaultLockState.Unlocked, sessionManager.lockState.value)
        assertNotNull(sessionManager.getActiveDek())

        // 4. Test toggling biometric OFF and ON in session
        val disableRes = sessionManager.setBiometricEnabled(false)
        assertTrue(disableRes.isSuccess)
        assertFalse(sessionManager.isBiometricEnabled())
        assertFalse(configStore.isBiometricEnabled)

        val enableRes = sessionManager.setBiometricEnabled(true)
        assertTrue(enableRes.isSuccess)
        assertTrue(sessionManager.isBiometricEnabled())
        assertTrue(configStore.isBiometricEnabled)

        // 5. Lock and unlock again with biometrics
        sessionManager.lock()
        val secondBioUnlock = sessionManager.unlockWithBiometricAuth()
        assertTrue(secondBioUnlock.isSuccess)
        assertEquals(com.example.adere.domain.repository.VaultLockState.Unlocked, sessionManager.lockState.value)
    }

    @Test
    fun testVerifyMasterPasswordIntegrity() = kotlinx.coroutines.runBlocking {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val configStore = com.example.adere.data.local.VaultConfigStore(context)
        configStore.resetVault()
        val keystoreManager = FakeBiometricKeyStore()
        val sessionManager = com.example.adere.domain.repository.VaultSessionManager(configStore, keystoreManager)

        val masterPassword = "CorrectMasterPassword2026!".toCharArray()
        sessionManager.initializeVault(masterPassword, enableBiometric = false)

        // Test correct password verification without altering state
        assertTrue(sessionManager.verifyMasterPassword("CorrectMasterPassword2026!".toCharArray()))
        // Test wrong password
        assertFalse(sessionManager.verifyMasterPassword("WrongPassword123!".toCharArray()))
        assertFalse(sessionManager.verifyMasterPassword("".toCharArray()))
    }

    @Test
    fun testScreenshotProtectionPersistenceAcrossInstances() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val configStore1 = com.example.adere.data.local.VaultConfigStore(context)
        configStore1.isScreenshotProtectionEnabled = true
        assertTrue(configStore1.isScreenshotProtectionEnabled)

        // Create new instance; must retain true and not be overridden by init
        val configStore2 = com.example.adere.data.local.VaultConfigStore(context)
        assertTrue(configStore2.isScreenshotProtectionEnabled)

        configStore2.isScreenshotProtectionEnabled = false
        assertFalse(configStore2.isScreenshotProtectionEnabled)

        val configStore3 = com.example.adere.data.local.VaultConfigStore(context)
        assertFalse(configStore3.isScreenshotProtectionEnabled)
    }

    @Test
    fun testPasswordHealthAnalyzerRatingScaling() {
        // 1. Excellent report
        val strongItems = listOf(
            PasswordHealthAnalyzer.PasswordItemInfo(
                id = "1",
                title = "Item 1",
                username = "user1",
                passwordPlaintext = "V3ryStr0ng!Pass#2026@Longer",
                has2FA = true,
                passwordLastChangedEpochMs = System.currentTimeMillis()
            )
        )
        val reportStrong = PasswordHealthAnalyzer.analyze(strongItems)
        assertEquals(100, reportStrong.healthScorePercent)
        assertEquals("10 / 10", reportStrong.overallRating)

        // 2. All weak items report
        val weakItems = listOf(
            PasswordHealthAnalyzer.PasswordItemInfo(
                id = "1",
                title = "Item 1",
                username = "user1",
                passwordPlaintext = "12345",
                has2FA = false,
                passwordLastChangedEpochMs = System.currentTimeMillis()
            ),
            PasswordHealthAnalyzer.PasswordItemInfo(
                id = "2",
                title = "Item 2",
                username = "user2",
                passwordPlaintext = "12345",
                has2FA = false,
                passwordLastChangedEpochMs = System.currentTimeMillis()
            )
        )
        val reportWeak = PasswordHealthAnalyzer.analyze(weakItems)
        assertTrue(reportWeak.healthScorePercent < 40)
        assertFalse(reportWeak.overallRating == "10 / 10")
    }

    @Test
    fun testZeroKnowledgeScrubbingOnLock() = kotlinx.coroutines.runBlocking {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val configStore = com.example.adere.data.local.VaultConfigStore(context)
        val keystoreManager = FakeBiometricKeyStore()
        val sessionManager = com.example.adere.domain.repository.VaultSessionManager(configStore, keystoreManager)

        sessionManager.initializeVault("ScrubTestPassword123!".toCharArray(), enableBiometric = false)
        assertNotNull(sessionManager.getActiveDek())
        val dekRef = sessionManager.getActiveDek()!!

        // Lock vault
        sessionManager.lock()
        assertEquals(com.example.adere.domain.repository.VaultLockState.Locked, sessionManager.lockState.value)
        assertEquals(null, sessionManager.getActiveDek())
        // Verify previous in-memory array bytes were zeroed out
        assertTrue(dekRef.all { it == 0.toByte() })
    }

    @Test
    fun testBiometricToggleAndKeystoreManager() = kotlinx.coroutines.runBlocking {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val configStore = com.example.adere.data.local.VaultConfigStore(context)
        val keystoreManager = FakeBiometricKeyStore()
        val sessionManager = com.example.adere.domain.repository.VaultSessionManager(configStore, keystoreManager)

        sessionManager.initializeVault("BioToggleTestPassword123!".toCharArray(), enableBiometric = false)
        assertFalse(sessionManager.isBiometricEnabled())

        // Enable biometrics while vault is unlocked
        val enableResult = sessionManager.setBiometricEnabled(true)
        assertTrue(enableResult.isSuccess)
        assertTrue(sessionManager.isBiometricEnabled())
        assertTrue(configStore.isBiometricEnabled)
        assertTrue(configStore.biometricEncryptedDekBase64.isNotBlank())
        assertTrue(configStore.biometricDekIvBase64.isNotBlank())

        // Disable biometrics
        val disableResult = sessionManager.setBiometricEnabled(false)
        assertTrue(disableResult.isSuccess)
        assertFalse(sessionManager.isBiometricEnabled())
        assertFalse(configStore.isBiometricEnabled)
        assertTrue(configStore.biometricEncryptedDekBase64.isBlank())

        // Lock vault and verify toggling when locked fails gracefully with non-null message
        sessionManager.lock()
        val lockToggleRes = sessionManager.setBiometricEnabled(true)
        assertTrue(lockToggleRes.isFailure)
        assertNotNull(lockToggleRes.exceptionOrNull()?.message)
        assertTrue(lockToggleRes.exceptionOrNull()?.message?.isNotBlank() == true)
    }

    @Test
    fun testAppErrorHandlerNeverReturnsNullOrCrypticString() {
        val operations = com.example.adere.core.utilities.AppErrorHandler.Operation.values()
        val testExceptions = listOf<Throwable?>(
            null,
            Exception(),
            Exception(""),
            Exception("null"),
            NullPointerException(),
            NullPointerException(null as String?),
            IllegalStateException(),
            IllegalStateException("Vault must be unlocked to modify biometrics."),
            IllegalArgumentException("Unsupported auto-lock interval."),
            android.security.keystore.KeyPermanentlyInvalidatedException(),
            android.security.keystore.UserNotAuthenticatedException()
        )

        for (op in operations) {
            for (ex in testExceptions) {
                val msg = com.example.adere.core.utilities.AppErrorHandler.getMessage(ex, op)
                assertNotNull("Message should never be null for op $op", msg)
                assertTrue("Message should never be blank for op $op", msg.isNotBlank())
                assertFalse("Message should never be 'null' for op $op", msg == "null")
                assertFalse("Message should never be 'Failed: null' for op $op", msg.contains("Failed: null"))
                assertFalse("Message should never contain raw Exception class names for op $op", msg.contains("NullPointerException"))
            }
        }
    }

    @Test
    fun testAppIdentitySettings() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val configStore = VaultConfigStore(context)

        // 1. Test Custom App Name defaults, user input, and validation
        assertEquals("Adera Password Manager", configStore.customAppName)
        configStore.customAppName = "Fitsum"
        assertEquals("Fitsum", configStore.customAppName)
        configStore.customAppName = "My Private Vault"
        assertEquals("My Private Vault", configStore.customAppName)

        // Test empty/whitespace fallback
        configStore.customAppName = "   "
        assertEquals("Adera Password Manager", configStore.customAppName)

        // Test max length trimming (30 chars)
        configStore.customAppName = "A Very Long Custom Application Name Exceeding Thirty Characters Limit"
        assertTrue(configStore.customAppName.length <= 30)

        // 2. Test App Icon Preset Selection
        assertEquals("default", configStore.selectedAppIcon)
        configStore.selectedAppIcon = AppIconManager.AppIconPreset.CALCULATOR.id
        assertEquals("calculator", configStore.selectedAppIcon)
    }

    @Test
    fun testAutoLockAllOptionsValidationAndPersistence() {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val configStore = com.example.adere.data.local.VaultConfigStore(context)

        val supportedOptions = listOf(0, 30, 60, 300, 900, 3600)
        for (sec in supportedOptions) {
            val persisted = configStore.persistAutoLockSeconds(sec)
            assertTrue("Should successfully persist auto-lock option $sec", persisted)
            assertEquals(sec, configStore.autoLockSeconds)
        }

        // Test unsupported value throws IllegalArgumentException
        val invalidValues = listOf(-1, 15, 45, 120, 5000)
        for (inv in invalidValues) {
            var threw = false
            try {
                configStore.persistAutoLockSeconds(inv)
            } catch (_: IllegalArgumentException) {
                threw = true
            }
            assertTrue("Should reject invalid interval $inv", threw)
        }
    }

    @Test
    fun testAutoLockTimerLocksVaultAndScrubsDek() = kotlinx.coroutines.runBlocking {
        val context = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.content.Context>()
        val configStore = com.example.adere.data.local.VaultConfigStore(context)
        val keystoreManager = FakeBiometricKeyStore()

        var simulatedTime = 1_000_000L
        val sessionManager = com.example.adere.domain.repository.VaultSessionManager(
            configStore = configStore,
            keystoreManager = keystoreManager,
            currentTimeMillis = { simulatedTime }
        )

        // Initialize and unlock
        sessionManager.initializeVault("AutoLockTestPass123!".toCharArray(), enableBiometric = false)
        assertEquals(com.example.adere.domain.repository.VaultLockState.Unlocked, sessionManager.lockState.value)
        val dekRef = sessionManager.getActiveDek()
        assertNotNull(dekRef)

        // Configure 30 seconds
        configStore.persistAutoLockSeconds(30)

        // Advance time by 10s -> Should remain Unlocked
        simulatedTime += 10_000L
        sessionManager.checkAutoLock()
        assertEquals(com.example.adere.domain.repository.VaultLockState.Unlocked, sessionManager.lockState.value)
        assertNotNull(sessionManager.getActiveDek())

        // User interacts -> Reset activity timer
        sessionManager.recordInteraction()

        // Advance time by 25s -> Should remain Unlocked (only 25s since last interaction)
        simulatedTime += 25_000L
        sessionManager.checkAutoLock()
        assertEquals(com.example.adere.domain.repository.VaultLockState.Unlocked, sessionManager.lockState.value)
        assertNotNull(sessionManager.getActiveDek())

        // Advance time by another 10s (total 35s since last interaction) -> Should LOCK vault
        simulatedTime += 10_000L
        sessionManager.checkAutoLock()
        assertEquals(com.example.adere.domain.repository.VaultLockState.Locked, sessionManager.lockState.value)
        assertEquals(null, sessionManager.getActiveDek())
        assertTrue("Previous DEK should be scrubbed with zeros", dekRef!!.all { it == 0.toByte() })
    }

    @Test
    fun testDashboardQuickPasswordGenerator() {
        val options = PasswordGenerator.GeneratorOptions(
            length = 20,
            includeUppercase = true,
            includeLowercase = true,
            includeDigits = true,
            includeSymbols = true,
            excludeAmbiguous = true
        )
        val result = PasswordGenerator.generate(options)

        assertEquals(20, result.password.length)
        assertTrue(result.entropyBits > 60.0)
        assertTrue(
            result.strength == PasswordGenerator.PasswordStrength.STRONG ||
                    result.strength == PasswordGenerator.PasswordStrength.VERY_STRONG
        )
    }

    @Test
    fun testDashboardVaultHealthCalculation() {
        val items = listOf(
            PasswordHealthAnalyzer.PasswordItemInfo(
                id = "1",
                title = "Weak Account",
                username = "user1",
                passwordPlaintext = "123456",
                has2FA = false,
                passwordLastChangedEpochMs = System.currentTimeMillis()
            ),
            PasswordHealthAnalyzer.PasswordItemInfo(
                id = "2",
                title = "Strong Account",
                username = "user2",
                passwordPlaintext = "Tr0ub4doR&321#Password!",
                has2FA = true,
                passwordLastChangedEpochMs = System.currentTimeMillis()
            )
        )

        val report = PasswordHealthAnalyzer.analyze(items)
        assertEquals(2, report.totalPasswords)
        assertEquals(1, report.weakCount)
        assertTrue(report.healthScorePercent in 0..100)
    }
}
