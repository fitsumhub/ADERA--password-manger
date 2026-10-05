package com.example.adere.domain.usecase.backup

import com.example.adere.core.backup.PdfExportManager
import com.example.adere.domain.repository.VaultRepository
import java.io.OutputStream

class GenerateVaultPdfUseCase(
    private val repository: VaultRepository
) {
    suspend operator fun invoke(
        options: PdfExportManager.ExportOptions,
        outputStream: OutputStream
    ): Result<Int> {
        return repository.exportToPdf(options, outputStream)
    }
}

