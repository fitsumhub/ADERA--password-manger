package com.example

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import com.example.adere.core.crypto.KeystoreManager
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class KeystoreManagerInstrumentedTest {
    @Test
    fun mainActivityLaunchesSuccessfully() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                assertFalse(activity.isFinishing)
            }
        }
    }

    @Test
    fun androidKeystoreEncryptsAndDecryptsVaultDek() {
        val keystoreManager = KeystoreManager("adere_test_${UUID.randomUUID()}")
        val dek = ByteArray(32) { index -> index.toByte() }

        try {
            val encrypted = keystoreManager.encryptDek(dek)
            assertTrue(keystoreManager.hasBiometricKey())

            val decrypted = keystoreManager.decryptDek(encrypted.ciphertext, encrypted.iv)
            assertTrue(decrypted.isSuccess)
            assertTrue(dek.contentEquals(decrypted.getOrThrow()))
        } finally {
            keystoreManager.deleteBiometricKey()
        }
    }
}
