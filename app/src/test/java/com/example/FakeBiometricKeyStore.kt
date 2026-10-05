package com.example

import com.example.adere.core.crypto.BiometricKeyStore
import com.example.adere.core.crypto.CryptoEngine
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class FakeBiometricKeyStore : BiometricKeyStore {
    private var key: SecretKey? = null

    override fun hasBiometricKey(): Boolean = key != null

    override fun deleteBiometricKey() {
        key = null
    }

    override fun encryptDek(dek: ByteArray): CryptoEngine.EncryptedData {
        val encryptionKey = key ?: KeyGenerator.getInstance("AES")
            .apply { init(256) }
            .generateKey()
            .also { key = it }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey)
        return CryptoEngine.EncryptedData(cipher.doFinal(dek), cipher.iv)
    }

    override fun prepareBiometricCipher(iv: ByteArray): Cipher? {
        val encryptionKey = key ?: return null
        return runCatching {
            Cipher.getInstance("AES/GCM/NoPadding").apply {
                init(Cipher.DECRYPT_MODE, encryptionKey, GCMParameterSpec(128, iv))
            }
        }.getOrNull()
    }

    override fun decryptDekWithCipher(
        ciphertext: ByteArray,
        authorizedCipher: Cipher
    ): Result<ByteArray> = runCatching { authorizedCipher.doFinal(ciphertext) }

    override fun decryptDek(ciphertext: ByteArray, iv: ByteArray): Result<ByteArray> =
        runCatching {
            val authorizedCipher = prepareBiometricCipher(iv)
                ?: error("Biometric key not found")
            authorizedCipher.doFinal(ciphertext)
        }
}
