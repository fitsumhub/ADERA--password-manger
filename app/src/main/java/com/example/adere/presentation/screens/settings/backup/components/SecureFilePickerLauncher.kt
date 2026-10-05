package com.example.adere.presentation.screens.settings.backup.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class SecureFilePickerState(
    val launchCreateBackupFile: (defaultName: String) -> Unit,
    val launchOpenBackupFile: () -> Unit,
    val launchCreatePdfFile: (defaultName: String) -> Unit
)

@Composable
fun rememberSecureFilePickerLauncher(
    onBackupFileSelectedForSave: (Uri) -> Unit,
    onBackupFileSelectedForRestore: (Uri) -> Unit,
    onPdfFileSelectedForSave: (Uri) -> Unit
): SecureFilePickerState {
    val createBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        if (uri != null) onBackupFileSelectedForSave(uri)
    }

    val openBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) onBackupFileSelectedForRestore(uri)
    }

    val createPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) onPdfFileSelectedForSave(uri)
    }

    return SecureFilePickerState(
        launchCreateBackupFile = { defaultName -> createBackupLauncher.launch(defaultName) },
        launchOpenBackupFile = { openBackupLauncher.launch(arrayOf("*/*")) },
        launchCreatePdfFile = { defaultName -> createPdfLauncher.launch(defaultName) }
    )
}

object SecureShareHelper {
    fun shareEncryptedBackup(
        context: Context,
        fileName: String,
        content: String
    ) {
        try {
            val cacheDir = File(context.cacheDir, "backups").apply { mkdirs() }
            cacheDir.listFiles()?.forEach { it.delete() }

            val file = File(cacheDir, fileName)
            FileOutputStream(file).use { out ->
                out.write(content.toByteArray(Charsets.UTF_8))
                out.flush()
            }

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Adere Encrypted Backup")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Share Encrypted Backup"))
        } catch (_: Exception) {}
    }
}
