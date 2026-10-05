package com.example.adere.core.common

import android.content.Context
import com.example.adere.core.crypto.KeystoreManager
import com.example.adere.data.local.AdereDatabase
import com.example.adere.data.local.VaultConfigStore
import com.example.adere.domain.repository.VaultRepository
import com.example.adere.domain.repository.VaultSessionManager
import com.example.adere.domain.usecase.backup.CreateEncryptedBackupUseCase
import com.example.adere.domain.usecase.backup.GenerateVaultPdfUseCase
import com.example.adere.domain.usecase.backup.GetBackupHistoryUseCase
import com.example.adere.domain.usecase.backup.RestoreBackupUseCase
import com.example.adere.domain.usecase.backup.ValidateBackupUseCase

class AdereAppContainer(context: Context) {
    val database: AdereDatabase = AdereDatabase.getDatabase(context)
    val configStore: VaultConfigStore = VaultConfigStore(context)
    val keystoreManager: KeystoreManager = KeystoreManager()
    val sessionManager: VaultSessionManager = VaultSessionManager(configStore, keystoreManager)
    val vaultRepository: VaultRepository = VaultRepository(database.vaultDao(), sessionManager)

    val createEncryptedBackupUseCase: CreateEncryptedBackupUseCase =
        CreateEncryptedBackupUseCase(vaultRepository, configStore)
    val validateBackupUseCase: ValidateBackupUseCase =
        ValidateBackupUseCase(vaultRepository)
    val restoreBackupUseCase: RestoreBackupUseCase =
        RestoreBackupUseCase(vaultRepository, configStore)
    val getBackupHistoryUseCase: GetBackupHistoryUseCase =
        GetBackupHistoryUseCase(configStore)
    val generateVaultPdfUseCase: GenerateVaultPdfUseCase =
        GenerateVaultPdfUseCase(vaultRepository)
}
