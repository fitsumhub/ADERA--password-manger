package com.example.adere.domain.model

import org.json.JSONObject
import java.util.UUID

enum class BackupActionType {
    BACKUP_CREATED,
    BACKUP_RESTORED_MERGE,
    BACKUP_RESTORED_REPLACE
}

data class BackupHistoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val itemCount: Int = 0,
    val fileSizeBytes: Long = 0L,
    val actionType: BackupActionType = BackupActionType.BACKUP_CREATED,
    val fileName: String = "Adere_Backup.adere"
) {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("timestamp", timestamp)
        json.put("itemCount", itemCount)
        json.put("fileSizeBytes", fileSizeBytes)
        json.put("actionType", actionType.name)
        json.put("fileName", fileName)
        return json
    }

    companion object {
        fun fromJson(json: JSONObject): BackupHistoryEntry {
            return BackupHistoryEntry(
                id = json.optString("id", UUID.randomUUID().toString()),
                timestamp = json.optLong("timestamp", System.currentTimeMillis()),
                itemCount = json.optInt("itemCount", 0),
                fileSizeBytes = json.optLong("fileSizeBytes", 0L),
                actionType = try {
                    BackupActionType.valueOf(
                        json.optString("actionType", BackupActionType.BACKUP_CREATED.name)
                    )
                } catch (_: Exception) {
                    BackupActionType.BACKUP_CREATED
                },
                fileName = json.optString("fileName", "Adere_Backup.adere")
            )
        }
    }
}

