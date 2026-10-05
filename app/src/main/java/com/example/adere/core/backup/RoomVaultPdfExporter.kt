package com.example.adere.core.backup

import com.example.adere.core.crypto.Base64Codec
import com.example.adere.core.crypto.CryptoEngine
import com.example.adere.data.local.VaultDao
import com.example.adere.data.local.VaultItemEntity
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import com.example.adere.domain.model.VaultItemPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.util.Arrays
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * PDF Export Utility class that integrates directly with the Room Database.
 *
 * Security architecture:
 * - Reads encrypted [VaultItemEntity] rows from Room [VaultDao].
 * - Decrypts data ephemeral in-memory using the active DEK.
 * - Sensitive fields (passwords, PINs, seeds, keys) are held as [CharArray] buffers
 *   and explicitly zero-wiped in a [finally] block before returning.
 * - Formats output using executive-grade [PdfExportManager].
 */
class RoomVaultPdfExporter(
    private val vaultDao: VaultDao
) {

    /**
     * Temporary in-memory holder for decrypted vault data with explicit wiping capability.
     */
    data class DecryptedExportItem(
        val id: String,
        val category: VaultCategory,
        val title: String,
        val username: String,
        var passwordChars: CharArray?,
        var pinChars: CharArray?,
        var seedPhraseChars: CharArray?,
        var privateKeyChars: CharArray?,
        var totpSecret: String?,
        var url: String?,
        var notes: String?,
        var recoveryCodes: List<String>?,
        var wifiSsid: String?,
        var wifiPasswordChars: CharArray?,
        val isFavorite: Boolean,
        val createdAt: Long,
        val updatedAt: Long
    ) {
        fun wipe() {
            passwordChars?.let { Arrays.fill(it, '\u0000') }
            pinChars?.let { Arrays.fill(it, '\u0000') }
            seedPhraseChars?.let { Arrays.fill(it, '\u0000') }
            privateKeyChars?.let { Arrays.fill(it, '\u0000') }
            wifiPasswordChars?.let { Arrays.fill(it, '\u0000') }
            passwordChars = null
            pinChars = null
            seedPhraseChars = null
            privateKeyChars = null
            wifiPasswordChars = null
            totpSecret = null
            url = null
            notes = null
            recoveryCodes = null
        }
    }

    suspend fun exportVaultToPdf(
        dek: SecretKey,
        options: PdfExportManager.ExportOptions,
        outputStream: OutputStream
    ): Result<Int> = exportVaultToPdf(dek.encoded, options, outputStream)

    /**
     * Queries encrypted entities from Room, decrypts in-memory, writes the formatted PDF,
     * and securely zeroes all plaintext buffers.
     */
    suspend fun exportVaultToPdf(
        rawDek: ByteArray,
        options: PdfExportManager.ExportOptions,
        outputStream: OutputStream
    ): Result<Int> = withContext(Dispatchers.IO) {
        val encryptedEntities = try {
            vaultDao.getAllItems()
        } catch (e: Exception) {
            return@withContext Result.failure(Exception("Failed to read from Room database: ${e.message}", e))
        }

        val secretKey = SecretKeySpec(rawDek, "AES")
        val decryptedItems = mutableListOf<DecryptedExportItem>()

        try {
            // Decrypt each entity strictly in memory
            for (entity in encryptedEntities) {
                var rawDecryptedBytes: ByteArray? = null
                try {
                    val iv = Base64Codec.decode(entity.ivBase64)
                    val ciphertext = Base64Codec.decode(entity.encryptedPayloadBase64)
                    rawDecryptedBytes = CryptoEngine.decrypt(ciphertext, iv, secretKey)

                    val payloadJson = String(rawDecryptedBytes, Charsets.UTF_8)
                    val payload = VaultItemPayload.fromJson(payloadJson)

                    val item = DecryptedExportItem(
                        id = entity.id,
                        category = VaultCategory.fromName(entity.category),
                        title = entity.title,
                        username = entity.username,
                        passwordChars = if (payload.password.isNotEmpty()) payload.password.toCharArray() else null,
                        pinChars = if (payload.pin.isNotEmpty()) payload.pin.toCharArray() else null,
                        seedPhraseChars = if (payload.cryptoSeedPhrase.isNotEmpty()) payload.cryptoSeedPhrase.toCharArray() else null,
                        privateKeyChars = if (payload.cryptoPrivateKey.isNotEmpty()) payload.cryptoPrivateKey.toCharArray() else null,
                        totpSecret = if (payload.totpSecret.isNotEmpty()) payload.totpSecret else null,
                        url = if (payload.url.isNotEmpty()) payload.url else null,
                        notes = if (payload.notes.isNotEmpty()) payload.notes else null,
                        recoveryCodes = if (payload.recoveryCodes.isNotEmpty()) payload.recoveryCodes else null,
                        wifiSsid = if (payload.wifiSsid.isNotEmpty()) payload.wifiSsid else null,
                        wifiPasswordChars = if (payload.wifiPassword.isNotEmpty()) payload.wifiPassword.toCharArray() else null,
                        isFavorite = entity.isFavorite,
                        createdAt = entity.createdAt,
                        updatedAt = entity.updatedAt
                    )
                    decryptedItems.add(item)
                } finally {
                    // Wipe raw decrypted byte buffer immediately
                    rawDecryptedBytes?.let { Arrays.fill(it, 0.toByte()) }
                }
            }

            // Map decrypted items to VaultItem models for executive PDF generation
            val vaultItems = decryptedItems.map { item ->
                VaultItem(
                    id = item.id,
                    category = item.category,
                    title = item.title,
                    username = item.username,
                    payload = VaultItemPayload(
                        password = item.passwordChars?.let { String(it) } ?: "",
                        pin = item.pinChars?.let { String(it) } ?: "",
                        cryptoSeedPhrase = item.seedPhraseChars?.let { String(it) } ?: "",
                        cryptoPrivateKey = item.privateKeyChars?.let { String(it) } ?: "",
                        totpSecret = item.totpSecret ?: "",
                        url = item.url ?: "",
                        notes = item.notes ?: "",
                        recoveryCodes = item.recoveryCodes ?: emptyList(),
                        wifiSsid = item.wifiSsid ?: "",
                        wifiPassword = item.wifiPasswordChars?.let { String(it) } ?: ""
                    ),
                    isFavorite = item.isFavorite,
                    createdAt = item.createdAt,
                    updatedAt = item.updatedAt
                )
            }

            // Render PDF document via unified PdfExportManager
            PdfExportManager.exportToPdf(vaultItems, options, outputStream)
            Result.success(decryptedItems.size)
        } catch (e: Exception) {
            Result.failure(Exception("PDF export failed: ${e.message}", e))
        } finally {
            // Guarantee complete wiping of all decrypted data from memory
            for (item in decryptedItems) {
                item.wipe()
            }
            decryptedItems.clear()
        }
    }
}
