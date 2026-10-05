package com.example.adere.domain.usecase.backup

import com.example.adere.core.backup.BackupManager
import com.example.adere.data.local.VaultConfigStore
import com.example.adere.domain.model.BackupActionType
import com.example.adere.domain.model.BackupHistoryEntry
import com.example.adere.domain.repository.VaultRepository
import java.util.UUID

class CreateEncryptedBackupUseCase(
    private val repository: VaultRepository,
    private val configStore: VaultConfigStore
) {
    suspend operator fun invoke(passphrase: CharArray): Result<BackupManager.BackupExportResult> {
        val result = repository.exportEncryptedBackup(passphrase)
        if (result.isSuccess) {
            val exportData = result.getOrThrow()
            val entry = BackupHistoryEntry(
                id = UUID.randomUUID().toString(),
                timestamp = exportData.timestamp,
                itemCount = exportData.itemCount,
                fileSizeBytes = exportData.fileSizeBytes,
                actionType = BackupActionType.BACKUP_CREATED,
                fileName = exportData.defaultFileName
            )
            configStore.addBackupHistoryEntry(entry)
        }
        return result
    }
}

