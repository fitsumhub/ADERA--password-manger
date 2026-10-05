package com.example.adere.core.backup

import com.example.adere.core.crypto.Base64Codec
import com.example.adere.core.crypto.CryptoEngine
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import com.example.adere.domain.model.VaultItemPayload
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Adere Encrypted Backup and Restore Manager (.adere format).
 *
 * Implements authenticated encrypted backups with AES-256-GCM AEAD encryption,
 * SHA-256 payload integrity verification, and complete migration metadata.
 * Plaintext secrets never leave memory.
 */
object BackupManager {

    const val BACKUP_MAGIC_PREFIX = "ADERE_VAULT_BACKUP"
    const val FORMAT_VERSION_V1 = "v1"
    const val FORMAT_VERSION_V2 = "v2"
    const val CURRENT_FORMAT_VERSION = FORMAT_VERSION_V2
    const val CURRENT_APP_VERSION = "2.0.0"

    enum class RestoreMode {
        REPLACE,
        MERGE
    }

    data class BackupHeaderInfo(
        val magic: String,
        val formatVersion: String,
        val isFormatValid: Boolean,
        val errorMessage: String? = null
    )

    data class BackupExportResult(
        val backupString: String,
        val itemCount: Int,
        val fileSizeBytes: Long = backupString.toByteArray(Charsets.UTF_8).size.toLong(),
        val defaultFileName: String = generateDefaultFileName(),
        val timestamp: Long = System.currentTimeMillis()
    )

    data class RestorePreview(
        val items: List<VaultItem>,
        val itemCount: Int,
        val categoryCount: Int,
        val categories: List<String>,
        val version: String,
        val appVersion: String,
        val exportedAt: Long
    )

    data class MergeResult(
        val resolvedItems: List<VaultItem>,
        val addedCount: Int,
        val updatedCount: Int,
        val unchangedCount: Int
    )

    fun generateDefaultFileName(timestamp: Long = System.currentTimeMillis()): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return "Adere_Backup_${dateFormat.format(Date(timestamp))}.adere"
    }

    /**
     * Inspects only the header of an incoming backup string without decrypting.
     * Useful for immediate UX feedback before prompting for passphrase.
     */
    fun validateBackupHeader(backupContent: String): Result<BackupHeaderInfo> {
        val cleanContent = backupContent.trim().replace("\r", "").replace("\n", "")
        val parts = cleanContent.split(":")
        if (parts.isEmpty() || parts[0] != BACKUP_MAGIC_PREFIX) {
            return Result.failure(IllegalArgumentException("Not a valid Adere backup file. Missing magic header."))
        }
        if (parts.size < 5) {
            return Result.failure(IllegalArgumentException("Truncated or malformed backup file."))
        }
        val version = parts[1]
        if (version != FORMAT_VERSION_V1 && version != FORMAT_VERSION_V2) {
            return Result.failure(IllegalArgumentException("Unsupported backup version: $version"))
        }
        return Result.success(
            BackupHeaderInfo(
                magic = parts[0],
                formatVersion = version,
                isFormatValid = true
            )
        )
    }

    /**
     * Computes a SHA-256 integrity checksum over raw bytes.
     */
    private fun computeSha256(bytes: ByteArray): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(bytes)
    }

    /**
     * Exports a list of decrypted vault items to an encrypted .adere format
     * using a dedicated user-provided backup passphrase.
     */
    fun createEncryptedBackup(
        items: List<VaultItem>,
        backupPassphrase: CharArray,
        appVersion: String = CURRENT_APP_VERSION
    ): BackupExportResult {
        val timestamp = System.currentTimeMillis()
        val rootJson = JSONObject()
        rootJson.put("formatVersion", 2)
        rootJson.put("appVersion", appVersion)
        rootJson.put("exportedAt", timestamp)
        rootJson.put("itemCount", items.size)

        val distinctCategories = items.map { it.category.name }.distinct().sorted()
        val categoriesArray = JSONArray()
        distinctCategories.forEach { categoriesArray.put(it) }
        rootJson.put("categories", categoriesArray)
        rootJson.put("migrationVersion", 1)

        val itemsArray = JSONArray()
        for (item in items) {
            val itemJson = JSONObject()
            itemJson.put("id", item.id)
            itemJson.put("category", item.category.name)
            itemJson.put("title", item.title)
            itemJson.put("username", item.username)
            itemJson.put("payload", item.payload.toJson())
            itemJson.put("isFavorite", item.isFavorite)
            itemJson.put("createdAt", item.createdAt)
            itemJson.put("updatedAt", item.updatedAt)
            itemJson.put("passwordLastChanged", item.passwordLastChanged)
            itemsArray.put(itemJson)
        }
        rootJson.put("items", itemsArray)

        val plaintextBytes = rootJson.toString().toByteArray(Charsets.UTF_8)
        val salt = CryptoEngine.generateSalt()
        val derivedKey = CryptoEngine.deriveKey(backupPassphrase, salt)
        val encrypted = CryptoEngine.encrypt(plaintextBytes, derivedKey)

        val saltB64 = Base64Codec.encode(salt)
        val ivB64 = encrypted.toBase64Iv()
        val cipherB64 = encrypted.toBase64Ciphertext()

        // Compute SHA-256 integrity checksum over ciphertext
        val checksumBytes = computeSha256(encrypted.ciphertext)
        val checksumB64 = Base64Codec.encode(checksumBytes)

        val fullBackupContent = "$BACKUP_MAGIC_PREFIX:$FORMAT_VERSION_V2:$saltB64:$ivB64:$cipherB64:$checksumB64"
        val byteSize = fullBackupContent.toByteArray(Charsets.UTF_8).size.toLong()

        return BackupExportResult(
            backupString = fullBackupContent,
            itemCount = items.size,
            fileSizeBytes = byteSize,
            defaultFileName = generateDefaultFileName(timestamp),
            timestamp = timestamp
        )
    }

    /**
     * Decrypts and parses an encrypted backup string.
     * Supports both v2 and legacy v1 formats.
     * Throws an exception with safe user message on invalid password, corruption, or version mismatch.
     */
    fun decryptAndValidateBackup(
        backupContent: String,
        backupPassphrase: CharArray
    ): Result<RestorePreview> {
        return try {
            val cleanContent = backupContent.trim().replace("\r", "").replace("\n", "")
            val parts = cleanContent.split(":")
            if (parts.size < 5 || parts[0] != BACKUP_MAGIC_PREFIX) {
                return Result.failure(IllegalArgumentException("Invalid or unsupported Adere backup file format."))
            }

            val version = parts[1]
            if (version != FORMAT_VERSION_V1 && version != FORMAT_VERSION_V2) {
                return Result.failure(IllegalArgumentException("Unsupported backup format version: $version"))
            }

            val salt = Base64Codec.decode(parts[2])
            val iv = Base64Codec.decode(parts[3])
            val ciphertext = Base64Codec.decode(parts[4])

            // If format is v2 and contains checksum part, verify integrity first
            if (version == FORMAT_VERSION_V2 && parts.size >= 6) {
                val expectedChecksum = Base64Codec.decode(parts[5])
                val actualChecksum = computeSha256(ciphertext)
                if (!MessageDigest.isEqual(expectedChecksum, actualChecksum)) {
                    return Result.failure(IllegalArgumentException("Backup payload integrity check failed (corrupted or tampered)."))
                }
            }

            val derivedKey = CryptoEngine.deriveKey(backupPassphrase, salt)
            val decryptedBytes = try {
                CryptoEngine.decrypt(ciphertext, iv, derivedKey)
            } catch (_: Exception) {
                return Result.failure(IllegalArgumentException("Incorrect backup passphrase or corrupted payload."))
            }

            val decryptedJson = String(decryptedBytes, Charsets.UTF_8)
            val root = JSONObject(decryptedJson)
            val itemsArray = root.optJSONArray("items") ?: JSONArray()
            val itemsList = mutableListOf<VaultItem>()

            for (i in 0 until itemsArray.length()) {
                val obj = itemsArray.getJSONObject(i)
                val payloadObj = obj.opt("payload")
                val payloadJson = when (payloadObj) {
                    is JSONObject -> payloadObj.toString()
                    is String -> payloadObj
                    else -> "{}"
                }
                val item = VaultItem(
                    id = obj.optString("id", UUID.randomUUID().toString()),
                    category = VaultCategory.fromName(obj.optString("category", "OTHER")),
                    title = obj.optString("title", "Untitled Secret"),
                    username = obj.optString("username", ""),
                    payload = VaultItemPayload.fromJson(payloadJson),
                    isFavorite = obj.optBoolean("isFavorite", false),
                    createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                    updatedAt = obj.optLong("updatedAt", System.currentTimeMillis()),
                    passwordLastChanged = obj.optLong("passwordLastChanged", System.currentTimeMillis())
                )
                itemsList.add(item)
            }

            val categoriesList = mutableListOf<String>()
            val categoriesJsonArray = root.optJSONArray("categories")
            if (categoriesJsonArray != null) {
                for (i in 0 until categoriesJsonArray.length()) {
                    categoriesList.add(categoriesJsonArray.getString(i))
                }
            } else {
                categoriesList.addAll(itemsList.map { it.category.name }.distinct().sorted())
            }

            val appVer = root.optString("appVersion", if (version == "v1") "1.0.0" else CURRENT_APP_VERSION)
            val exportTime = root.optLong("exportedAt", System.currentTimeMillis())

            Result.success(
                RestorePreview(
                    items = itemsList,
                    itemCount = itemsList.size,
                    categoryCount = categoriesList.size,
                    categories = categoriesList,
                    version = version,
                    appVersion = appVer,
                    exportedAt = exportTime
                )
            )
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(Exception("Unable to restore backup: ${e.message ?: "Invalid payload or wrong passphrase"}"))
        }
    }

    /**
     * Resolves conflicts when merging an incoming backup into the existing vault.
     * Matches items by ID, or by identical (category + case-insensitive title + username).
     * If matched, the item with the newest `updatedAt` is preserved.
     * If no match, the incoming item is added.
     */
    fun resolveMerge(
        existingItems: List<VaultItem>,
        incomingItems: List<VaultItem>
    ): MergeResult {
        val resultList = existingItems.toMutableList()
        var addedCount = 0
        var updatedCount = 0
        var unchangedCount = 0

        for (incoming in incomingItems) {
            val matchIndex = resultList.indexOfFirst { existing ->
                existing.id == incoming.id || (
                    existing.category == incoming.category &&
                    existing.title.equals(incoming.title, ignoreCase = true) &&
                    existing.username.equals(incoming.username, ignoreCase = true)
                )
            }

            if (matchIndex >= 0) {
                val existing = resultList[matchIndex]
                if (incoming.updatedAt > existing.updatedAt) {
                    resultList[matchIndex] = incoming
                    updatedCount++
                } else {
                    unchangedCount++
                }
            } else {
                resultList.add(incoming)
                addedCount++
            }
        }

        return MergeResult(
            resolvedItems = resultList,
            addedCount = addedCount,
            updatedCount = updatedCount,
            unchangedCount = unchangedCount
        )
    }

    /**
     * Exports aggregated vault items into a formatted PDF document.
     */
    fun exportToPdf(
        items: List<VaultItem>,
        options: PdfExportManager.ExportOptions,
        outputStream: java.io.OutputStream
    ) {
        PdfExportManager.exportToPdf(items, options, outputStream)
    }

    /**
     * Exports encrypted vault items directly from Room database to a formatted PDF.
     */
    suspend fun exportFromRoomToPdf(
        vaultDao: com.example.adere.data.local.VaultDao,
        dek: javax.crypto.SecretKey,
        options: PdfExportManager.ExportOptions,
        outputStream: java.io.OutputStream
    ): Result<Int> {
        return RoomVaultPdfExporter(vaultDao).exportVaultToPdf(dek, options, outputStream)
    }
}

