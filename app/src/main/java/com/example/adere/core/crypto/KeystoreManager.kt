package com.example.adere.core.crypto

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Manages platform-protected keys in the Android KeyStore.
 *
 * Security model:
 * - The biometric Keystore key requires user authentication at the hardware level
 *   (setUserAuthenticationRequired = true).
 * - DEK decryption can ONLY happen after a real OS-level biometric event authorises
 *   the Cipher via BiometricPrompt.CryptoObject. The app UI cannot bypass this.
 * - KeyPermanentlyInvalidatedException (triggered by biometric enrollment changes)
 *   is handled by deleting the invalidated key so the user falls back to password.
 */
class KeystoreManager {

    companion object {
        private const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val BIOMETRIC_KEY_ALIAS = "adere_vault_biometric_key_v2"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH_BITS = 128
        const val IV_SIZE = 12
    }

    private val keyStore: KeyStore? = try {
        KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).apply { load(null) }
    } catch (_: Throwable) { null }

    fun hasBiometricKey(): Boolean =
        keyStore?.containsAlias(BIOMETRIC_KEY_ALIAS) ?: false

    fun deleteBiometricKey() {
        try {
            if (keyStore?.containsAlias(BIOMETRIC_KEY_ALIAS) == true) {
                keyStore.deleteEntry(BIOMETRIC_KEY_ALIAS)
            }
        } catch (_: Throwable) {}
    }

    /**
     * Generates a hardware-backed AES-256 GCM key that requires user authentication
     * before each use. The key can only be unlocked by a real BiometricPrompt event —
     * not by the app code alone.
     */
    fun generateBiometricKey(): SecretKey {
        deleteBiometricKey()

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE_PROVIDER
        )

        val spec = KeyGenParameterSpec.Builder(
            BIOMETRIC_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setRandomizedEncryptionRequired(true)
            // ── Hardware enforcement ──────────────────────────────────────────────
            // Requires the OS to perform a real biometric event before the key can be
            // used.  Without this the Keystore key is effectively unprotected on a
            // rooted device.  A timeout of -1 means "require auth for every use".
            .setUserAuthenticationRequired(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // API 30+: prefer strong biometric (fingerprint/face class 3)
                    setUserAuthenticationParameters(
                        0 /* timeout = require every time */,
                        KeyProperties.AUTH_BIOMETRIC_STRONG
                    )
                }
                // API 23–29: setUserAuthenticationValidityDurationSeconds(-1) is the
                // default when setUserAuthenticationRequired(true) is set without the
                // newer API, so no extra call is needed.
            }
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey? {
        val ks = keyStore ?: return null
        if (!ks.containsAlias(BIOMETRIC_KEY_ALIAS)) return null
        return (ks.getEntry(BIOMETRIC_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Encrypt path (called when enabling biometric — vault is already unlocked,
    // so no second factor is needed for the initial wrapping of the DEK).
    // This path does NOT require user auth because we're encrypting with the key
    // for the first time in an already-trusted session (user just authenticated
    // with their master password to get here).
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Encrypts the DEK with the Keystore key.
     * Call only when the vault is already unlocked (master-password session active).
     */
    fun encryptDek(dek: ByteArray): CryptoEngine.EncryptedData {
        val key = getSecretKey() ?: generateBiometricKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(dek)
        return CryptoEngine.EncryptedData(ciphertext, iv)
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Decrypt path (hardware-enforced biometric unlock flow).
    // Step 1: prepareBiometricCipher() — initialise Cipher in DECRYPT_MODE;
    //         pass it to BiometricPrompt.CryptoObject.
    // Step 2: decryptDekWithCipher() — called in BiometricPrompt.AuthenticationCallback
    //         .onAuthenticationSucceeded() with the hardware-authorised Cipher from
    //         result.cryptoObject.cipher.
    // ─────────────────────────────────────────────────────────────────────────────

    /**
     * Initialises (but does NOT yet use) a Cipher for DEK decryption.
     * Pass this to BiometricPrompt.CryptoObject so the OS can hardware-gate its use.
     *
     * @param iv The IV stored alongside the biometric-encrypted DEK ciphertext.
     * @return A Cipher ready to be wrapped in CryptoObject, or null if the key is gone.
     * @throws KeyPermanentlyInvalidatedException if biometric enrollment changed;
     *         caught by callers who should delete the key and fall back to password.
     */
    fun prepareBiometricCipher(iv: ByteArray): Cipher? {
        val key = getSecretKey() ?: return null
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)
        return cipher
    }

    /**
     * Completes DEK decryption using the Cipher that was authorised by the OS
     * via a real BiometricPrompt event.  The Cipher arrives from
     * BiometricPrompt.AuthenticationResult.cryptoObject.cipher.
     *
     * Because the Cipher was wrapped in a CryptoObject, the Android Keystore
     * will only permit its use after the TEE/StrongBox has verified the biometric.
     */
    fun decryptDekWithCipher(ciphertext: ByteArray, authorizedCipher: Cipher): Result<ByteArray> {
        return try {
            val dek = authorizedCipher.doFinal(ciphertext)
            Result.success(dek)
        } catch (e: KeyPermanentlyInvalidatedException) {
            deleteBiometricKey()
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Legacy software-only decrypt path kept for backward compatibility.
     * This is no longer called in the main biometric unlock flow, but is retained
     * so any existing encrypted DEKs stored before the v2 key upgrade still work
     * during migration (the user will be asked to re-enable biometrics after upgrade).
     */
    fun decryptDek(ciphertext: ByteArray, iv: ByteArray): Result<ByteArray> {
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
            Result.failure(e)
        }
    }
}
