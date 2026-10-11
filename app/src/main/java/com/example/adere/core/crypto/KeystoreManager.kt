package com.example.adere.core.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

interface BiometricKeyStore {
    fun hasBiometricKey(): Boolean
    fun deleteBiometricKey()
    fun encryptDek(dek: ByteArray): CryptoEngine.EncryptedData
    fun prepareBiometricCipher(iv: ByteArray): Cipher?
    fun decryptDekWithCipher(ciphertext: ByteArray, authorizedCipher: Cipher): Result<ByteArray>
    fun decryptDek(ciphertext: ByteArray, iv: ByteArray): Result<ByteArray>
}

/**
 * Manages platform-protected keys in the Android KeyStore for Biometric Unlock.
 *
 * Security model:
 * - Hardware-backed AES-256-GCM key stored in the Android KeyStore (TEE / StrongBox).
 * - Key cannot be exported from hardware or accessed by external applications.
 * - DEK is encrypted with this hardware key and securely stored in encrypted prefs.
 * - Unlocking requires BiometricPrompt authentication (fingerprint/face recognition).
 */
class KeystoreManager internal constructor(
    private val biometricKeyAlias: String = BIOMETRIC_KEY_ALIAS
) : BiometricKeyStore {

    companion object {
        private const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val BIOMETRIC_KEY_ALIAS = "adere_vault_biometric_key_v2"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH_BITS = 128
        const val IV_SIZE = 12
    }

    private fun getAndroidKeyStore(): KeyStore? {
        return try {
            KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).apply { load(null) }
        } catch (_: Exception) {
            null
        }
    }

    override fun hasBiometricKey(): Boolean {
        return try {
            getAndroidKeyStore()?.containsAlias(biometricKeyAlias) == true
        } catch (_: Exception) {
            false
        }
    }

    override fun deleteBiometricKey() {
        try {
            val keyStore = getAndroidKeyStore() ?: return
            if (keyStore.containsAlias(biometricKeyAlias)) {
                keyStore.deleteEntry(biometricKeyAlias)
            }
        } catch (_: Exception) {}
    }

    /**
     * Generates a hardware-backed AES-256 GCM key in the Android KeyStore.
     */
    fun generateBiometricKey(): SecretKey {
        try {
            deleteBiometricKey()
        } catch (_: Exception) {}

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE_PROVIDER
        )

        val builder = KeyGenParameterSpec.Builder(
            biometricKeyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setRandomizedEncryptionRequired(true)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            builder.setUserAuthenticationParameters(0, KeyProperties.AUTH_BIOMETRIC_STRONG)
        } else {
            @Suppress("DEPRECATION")
            builder.setUserAuthenticationRequired(true)
        }

        keyGenerator.init(builder.build())
        return keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey? {
        return try {
            getAndroidKeyStore()?.getKey(biometricKeyAlias, null) as? SecretKey
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Encrypts the DEK with the Keystore key.
     * Called when enabling biometric authentication in an active, unlocked vault session.
     */
    override fun encryptDek(dek: ByteArray): CryptoEngine.EncryptedData {
        val key = try {
            getSecretKey() ?: generateBiometricKey()
        } catch (_: Exception) {
            try {
                generateBiometricKey()
            } catch (e2: Exception) {
                throw IllegalStateException("Android Keystore initialization failed: ${e2.localizedMessage ?: e2.message}", e2)
            }
        }

        val cipher = Cipher.getInstance(TRANSFORMATION)
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key)
        } catch (_: Exception) {
            try {
                deleteBiometricKey()
                val freshKey = generateBiometricKey()
                cipher.init(Cipher.ENCRYPT_MODE, freshKey)
            } catch (e2: Exception) {
                throw IllegalStateException("Cipher encryption initialization failed: ${e2.localizedMessage ?: e2.message}", e2)
            }
        }

        val iv = cipher.iv ?: throw IllegalStateException("Cipher failed to generate IV")
        val ciphertext = cipher.doFinal(dek)
        return CryptoEngine.EncryptedData(ciphertext, iv)
    }

    /**
     * Initialises a Cipher in DECRYPT_MODE for DEK decryption.
     * Can be wrapped in BiometricPrompt.CryptoObject when strong biometric auth is active.
     *
     * @param iv The IV stored alongside the biometric-encrypted DEK ciphertext.
     * @return A Cipher ready to be wrapped in CryptoObject, or null if the key is missing/invalidated.
     */
    override fun prepareBiometricCipher(iv: ByteArray): Cipher? {
        return try {
            val key = getSecretKey() ?: return null
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            cipher
        } catch (_: KeyPermanentlyInvalidatedException) {
            deleteBiometricKey()
            null
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Completes DEK decryption using the hardware-authorized Cipher from
     * BiometricPrompt.AuthenticationResult.cryptoObject.cipher.
     */
    override fun decryptDekWithCipher(ciphertext: ByteArray, authorizedCipher: Cipher): Result<ByteArray> {
        return try {
            val dek = authorizedCipher.doFinal(ciphertext)
            Result.success(dek)
        } catch (e: KeyPermanentlyInvalidatedException) {
            deleteBiometricKey()
            Result.failure(e)
        } catch (e: Exception) {
            val message = e.localizedMessage ?: e.message ?: "Biometric decryption failed"
            Result.failure(Exception(message, e))
        }
    }

    /**
     * Decrypts the DEK using the stored Keystore key after BiometricPrompt has
     * verified the user's identity.
     */
    override fun decryptDek(ciphertext: ByteArray, iv: ByteArray): Result<ByteArray> {
        return try {
            val key = getSecretKey()
                ?: return Result.failure(IllegalStateException("Biometric key not found"))
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            val dek = cipher.doFinal(ciphertext)
            Result.success(dek)
        } catch (e: KeyPermanentlyInvalidatedException) {
            deleteBiometricKey()
            Result.failure(e)
        } catch (e: Exception) {
            val message = e.localizedMessage ?: e.message ?: "Biometric decryption failed"
            Result.failure(Exception(message, e))
        }
    }
}
