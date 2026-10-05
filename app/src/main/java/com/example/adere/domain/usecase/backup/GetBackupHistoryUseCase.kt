package com.example.adere.domain.usecase.backup

import com.example.adere.data.local.VaultConfigStore
import com.example.adere.domain.model.BackupHistoryEntry

class GetBackupHistoryUseCase(
    private val configStore: VaultConfigStore
) {
    operator fun invoke(): List<BackupHistoryEntry> {
        return configStore.getBackupHistory()
    }

    fun clearHistory() {
        configStore.clearBackupHistory()
    }
}

