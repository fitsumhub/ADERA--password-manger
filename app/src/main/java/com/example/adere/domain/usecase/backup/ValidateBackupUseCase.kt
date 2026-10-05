package com.example.adere.domain.usecase.backup

import com.example.adere.core.backup.BackupManager
import com.example.adere.domain.repository.VaultRepository

class ValidateBackupUseCase(
    private val repository: VaultRepository
) {
    suspend operator fun invoke(content: String, passphrase: CharArray): Result<BackupManager.RestorePreview> {
        return repository.validateBackup(content, passphrase)
    }

    fun validateHeader(content: String): Result<BackupManager.BackupHeaderInfo> {
        return BackupManager.validateBackupHeader(content)
    }
}

