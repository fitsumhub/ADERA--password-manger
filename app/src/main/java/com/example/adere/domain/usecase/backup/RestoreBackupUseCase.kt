package com.example.adere.domain.usecase.backup

import com.example.adere.core.backup.BackupManager
import com.example.adere.data.local.VaultConfigStore
import com.example.adere.domain.model.BackupActionType
import com.example.adere.domain.model.BackupHistoryEntry
import com.example.adere.domain.repository.VaultRepository
import java.util.UUID

class RestoreBackupUseCase(
    private val repository: VaultRepository,
    private val configStore: VaultConfigStore
) {
    suspend operator fun invoke(
        content: String,
        passphrase: CharArray,
        mode: BackupManager.RestoreMode = BackupManager.RestoreMode.MERGE,
        fileName: String = "Adere_Backup.adere"
    ): Result<Int> {
        val result = repository.restoreFromEncryptedBackup(content, passphrase, mode)
        if (result.isSuccess) {
            val count = result.getOrThrow()
            val entry = BackupHistoryEntry(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                itemCount = count,
                fileSizeBytes = content.toByteArray(Charsets.UTF_8).size.toLong(),
                actionType = if (mode == BackupManager.RestoreMode.REPLACE)
                    BackupActionType.BACKUP_RESTORED_REPLACE
                else
                    BackupActionType.BACKUP_RESTORED_MERGE,
                fileName = fileName
            )
            configStore.addBackupHistoryEntry(entry)
        }
        return result
    }
}

