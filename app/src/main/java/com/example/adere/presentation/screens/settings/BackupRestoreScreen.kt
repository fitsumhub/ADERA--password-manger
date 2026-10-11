package com.example.adere.presentation.screens.settings
import com.example.ui.theme.Shapes
import androidx.compose.foundation.shape.CircleShape

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.backup.BackupManager
import com.example.adere.core.backup.PdfExportManager
import com.example.adere.domain.model.BackupHistoryEntry
import com.example.adere.presentation.components.MasterRecoveryKeyCard
import com.example.adere.presentation.screens.settings.backup.components.AuthenticationBottomSheet
import com.example.adere.presentation.screens.settings.backup.components.BackupActionCard
import com.example.adere.presentation.screens.settings.backup.components.BackupHistoryCard
import com.example.adere.presentation.screens.settings.backup.components.BackupProgressDialog
import com.example.adere.presentation.screens.settings.backup.components.BackupStepState
import com.example.adere.presentation.screens.settings.backup.components.BackupSuccessDialog
import com.example.adere.presentation.screens.settings.backup.components.ExportOptionsDialog
import com.example.adere.presentation.screens.settings.backup.components.PdfExportCard
import com.example.adere.presentation.screens.settings.backup.components.RestoreActionCard
import com.example.adere.presentation.screens.settings.backup.components.RestorePreviewDialog
import com.example.adere.presentation.screens.settings.backup.components.RestoreSuccessDialog
import com.example.adere.presentation.screens.settings.backup.components.SecureShareHelper
import com.example.adere.presentation.screens.settings.backup.components.SecurityStatusCard
import com.example.adere.presentation.screens.settings.backup.components.rememberSecureFilePickerLauncher
import com.example.adere.presentation.screens.settings.components.SettingsItemCard
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.TotalSecurityPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream

@Composable
fun BackupRestoreScreen(
    vaultItemsCount: Int = 0,
    lastBackupTimestamp: Long = 0L,
    backupHistory: List<BackupHistoryEntry> = emptyList(),
    onExportBackup: (passphrase: String, (Result<String>) -> Unit) -> Unit,
    onRestoreBackup: (content: String, passphrase: String, (Result<Int>) -> Unit) -> Unit,
    onRestoreBackupWithMode: ((content: String, passphrase: String, mode: BackupManager.RestoreMode, (Result<Int>) -> Unit) -> Unit)? = null,
    onPreviewBackup: ((content: String, passphrase: String, (Result<BackupManager.RestorePreview>) -> Unit) -> Unit)? = null,
    onExportPdf: (options: PdfExportManager.ExportOptions, isShare: Boolean) -> Unit,
    onTriggerBiometrics: (() -> Unit)? = null,
    onAuthenticateSensitiveAction: ((title: String, subtitle: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit)? = null,
    onVerifyMasterPassword: ((password: String, (Boolean) -> Unit) -> Unit)? = null,
    onClearBackupHistory: (() -> Unit)? = null,
    onGetRecoveryKey: (() -> Result<String>)? = null,
    onRegenerateRecoveryKey: (((Result<String>) -> Unit) -> Unit)? = null,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    // Authentication bottom sheet state
    var showAuthBottomSheet by remember { mutableStateOf(false) }
    var authTargetAction by remember { mutableStateOf<AuthAction?>(null) }
    var authErrorMessage by remember { mutableStateOf<String?>(null) }

    // Backup creation flow state
    var showBackupConfirmDialog by remember { mutableStateOf(false) }
    var backupPassphraseInput by remember { mutableStateOf("") }
    var isBackingUp by remember { mutableStateOf(false) }
    var showBackupProgressDialog by remember { mutableStateOf(false) }
    var backupPrepStep by remember { mutableStateOf(BackupStepState.PENDING) }
    var backupEncryptStep by remember { mutableStateOf(BackupStepState.PENDING) }
    var backupCreateStep by remember { mutableStateOf(BackupStepState.PENDING) }
    var backupVerifyStep by remember { mutableStateOf(BackupStepState.PENDING) }
    var createdBackupContent by remember { mutableStateOf<String?>(null) }
    var createdBackupFileName by remember { mutableStateOf("") }
    var createdBackupSize by remember { mutableStateOf(0L) }
    var showBackupSuccessDialog by remember { mutableStateOf(false) }

    // Pending file saving Uri target
    var pendingBackupContentToSave by remember { mutableStateOf<String?>(null) }

    // Restore flow state
    var selectedRestoreContent by remember { mutableStateOf<String?>(null) }
    var showRestorePassphraseDialog by remember { mutableStateOf(false) }
    var restorePassphraseInput by remember { mutableStateOf("") }
    var isVerifyingRestoreFile by remember { mutableStateOf(false) }
    var activeRestorePreview by remember { mutableStateOf<BackupManager.RestorePreview?>(null) }
    var showRestoreSuccessDialog by remember { mutableStateOf(false) }
    var lastRestoredCount by remember { mutableStateOf(0) }
    var lastRestoredMode by remember { mutableStateOf(BackupManager.RestoreMode.MERGE) }

    // PDF Export options state
    var showPdfOptionsDialog by remember { mutableStateOf(false) }

    // Master Recovery Key dialog states
    var showRecoveryKeyDialog by remember { mutableStateOf(false) }
    var showRegenerateConfirmDialog by remember { mutableStateOf(false) }
    var currentRecoveryKey by remember { mutableStateOf("") }

    // Setup Storage Access Framework (SAF) file pickers
    val filePickerState = rememberSecureFilePickerLauncher(
        onBackupFileSelectedForSave = { uri ->
            val content = pendingBackupContentToSave ?: createdBackupContent
            if (content != null) {
                try {
                    context.contentResolver.openOutputStream(uri)?.use { out: OutputStream ->
                        out.write(content.toByteArray(Charsets.UTF_8))
                        out.flush()
                    }
                    Toast.makeText(context, "Encrypted backup saved successfully", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to save file: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        },
        onBackupFileSelectedForRestore = { uri ->
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val reader = BufferedReader(InputStreamReader(stream, Charsets.UTF_8))
                    val content = reader.readText()
                    // Inspect file header immediately
                    val headerCheck = BackupManager.validateBackupHeader(content)
                    if (headerCheck.isSuccess) {
                        selectedRestoreContent = content
                        showRestorePassphraseDialog = true
                    } else {
                        Toast.makeText(
                            context,
                            headerCheck.exceptionOrNull()?.message ?: "Not a valid Adere backup file",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error reading backup file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        },
        onPdfFileSelectedForSave = { /* Handled via Activity launcher */ }
    )

    // Trigger sensitive action following authentication
    val onAuthSuccess = {
        showAuthBottomSheet = false
        authErrorMessage = null
        when (authTargetAction) {
            AuthAction.CREATE_BACKUP -> {
                backupPassphraseInput = ""
                showBackupConfirmDialog = true
            }
            AuthAction.EXPORT_PDF -> {
                showPdfOptionsDialog = true
            }
            null -> {}
        }
    }

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Backup & Export",
                subtitle = "Protect, restore, or securely export your vault.",
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // ── 1. Security Status Hero Card ─────────────────────────────────
            SecurityStatusCard(
                vaultItemsCount = vaultItemsCount,
                lastBackupTimestamp = lastBackupTimestamp,
                isVaultUnlocked = true
            )

            // ── 2. BACKUP Section ────────────────────────────────────────────
            SettingsSectionTitle("BACKUP")

            BackupActionCard(
                vaultItemsCount = vaultItemsCount,
                lastBackupTimestamp = lastBackupTimestamp,
                isProcessing = isBackingUp,
                onStartBackup = {
                    authTargetAction = AuthAction.CREATE_BACKUP
                    authErrorMessage = null
                    if (onAuthenticateSensitiveAction != null) {
                        onAuthenticateSensitiveAction(
                            "Authenticate to Back Up",
                            "Verify your identity before creating a secure vault backup.",
                            { onAuthSuccess() },
                            { error ->
                                authErrorMessage = error
                                showAuthBottomSheet = true
                            }
                        )
                    } else {
                        showAuthBottomSheet = true
                    }
                }
            )

            // ── 3. RESTORE BACKUP Section ────────────────────────────────────
            SettingsSectionTitle("RESTORE BACKUP")

            RestoreActionCard(
                isProcessing = isVerifyingRestoreFile,
                onChooseBackup = {
                    filePickerState.launchOpenBackupFile()
                }
            )

            // ── 4. BACKUP HISTORY Section ────────────────────────────────────
            SettingsSectionTitle("BACKUP HISTORY")

            BackupHistoryCard(
                history = backupHistory,
                onClearHistory = onClearBackupHistory
            )

            // ── 5. EXPORT Section ────────────────────────────────────────────
            SettingsSectionTitle("EXPORT")

            PdfExportCard(
                onExportPdf = {
                    authTargetAction = AuthAction.EXPORT_PDF
                    authErrorMessage = null
                    if (onAuthenticateSensitiveAction != null) {
                        onAuthenticateSensitiveAction(
                            "Authenticate to Export",
                            "This export contains decrypted passwords and sensitive credentials.",
                            { onAuthSuccess() },
                            { error ->
                                authErrorMessage = error
                                showAuthBottomSheet = true
                            }
                        )
                    } else {
                        showAuthBottomSheet = true
                    }
                }
            )

            // ── 6. EMERGENCY RECOVERY KEY Section ─────────────────────────────
            SettingsSectionTitle("EMERGENCY VAULT RECOVERY")

            SettingsItemCard(
                icon = Icons.Default.Key,
                iconTint = TotalSecurityPrimary,
                iconBg = TotalSecurityPrimary.copy(alpha = 0.12f),
                title = "Master Recovery Key",
                subtitle = "Display your 24-character emergency key for offline paper archiving",
                onClick = {
                    if (onGetRecoveryKey != null) {
                        val res = onGetRecoveryKey()
                        if (res.isSuccess) {
                            currentRecoveryKey = res.getOrThrow()
                            showRecoveryKeyDialog = true
                        } else {
                            Toast.makeText(context, res.exceptionOrNull()?.message ?: "Recovery key unavailable", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                testTag = "settings_backup_recovery_key_item"
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // ── Dialogs & Modals ─────────────────────────────────────────────────────

    // 1. Biometric / Password Authentication Bottom Sheet
    if (showAuthBottomSheet) {
        val sheetTitle = if (authTargetAction == AuthAction.EXPORT_PDF) "Authenticate to Export" else "Authenticate to Continue"
        val sheetSubtitle = if (authTargetAction == AuthAction.EXPORT_PDF)
            "This export may contain passwords and other sensitive information."
        else
            "Verify your identity before creating a secure vault backup."

        AuthenticationBottomSheet(
            title = sheetTitle,
            subtitle = sheetSubtitle,
            onBiometricClick = {
                if (onAuthenticateSensitiveAction != null) {
                    onAuthenticateSensitiveAction(
                        sheetTitle,
                        sheetSubtitle,
                        { onAuthSuccess() },
                        { error -> authErrorMessage = error }
                    )
                } else if (onTriggerBiometrics != null) {
                    onTriggerBiometrics()
                    onAuthSuccess()
                } else {
                    onAuthSuccess()
                }
            },
            onPasswordSubmit = { enteredPassword ->
                if (enteredPassword.isBlank()) {
                    authErrorMessage = "Please enter your Master Password"
                } else if (onVerifyMasterPassword != null) {
                    onVerifyMasterPassword(enteredPassword) { isValid ->
                        if (isValid) {
                            onAuthSuccess()
                        } else {
                            authErrorMessage = "Incorrect Master Password. Please try again."
                        }
                    }
                } else {
                    onAuthSuccess()
                }
            },
            onDismiss = {
                showAuthBottomSheet = false
                authErrorMessage = null
            },
            errorMessage = authErrorMessage
        )
    }

    // 2. Backup Configuration & Confirmation Dialog
    if (showBackupConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showBackupConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(Shapes.medium)
                            .background(EmeraldPrimary.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Rounded.Shield, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Ready to Back Up",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 17.5.sp)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Your vault will be encrypted before it leaves Adere.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.5.sp,
                            lineHeight = 18.sp
                        )
                    )

                    // Details box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Vault items", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$vaultItemsCount items", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Encryption", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("AES-256-GCM", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = EmeraldPrimary)
                        }
                    }

                    OutlinedTextField(
                        value = backupPassphraseInput,
                        onValueChange = { backupPassphraseInput = it },
                        label = { Text("Backup Passphrase") },
                        placeholder = { Text("Enter passphrase to encrypt file") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_backup_passphrase")
                    )

                    Text(
                        text = "This passphrase will be required to decrypt and restore your vault. It is never stored in plaintext.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pass = backupPassphraseInput.trim()
                        if (pass.isEmpty()) {
                            Toast.makeText(context, "Passphrase cannot be empty", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        showBackupConfirmDialog = false
                        showBackupProgressDialog = true
                        isBackingUp = true

                        // Stepped animated progress
                        coroutineScope.launch {
                            backupPrepStep = BackupStepState.RUNNING
                            delay(300)
                            backupPrepStep = BackupStepState.COMPLETED
                            backupEncryptStep = BackupStepState.RUNNING
                            delay(350)

                            onExportBackup(pass) { result ->
                                backupEncryptStep = BackupStepState.COMPLETED
                                backupCreateStep = BackupStepState.RUNNING

                                if (result.isSuccess) {
                                    val backupString = result.getOrThrow()
                                    backupCreateStep = BackupStepState.COMPLETED
                                    backupVerifyStep = BackupStepState.RUNNING

                                    coroutineScope.launch {
                                        delay(300)
                                        backupVerifyStep = BackupStepState.COMPLETED
                                        delay(200)

                                        isBackingUp = false
                                        showBackupProgressDialog = false

                                        createdBackupContent = backupString
                                        createdBackupFileName = BackupManager.generateDefaultFileName()
                                        createdBackupSize = backupString.toByteArray(Charsets.UTF_8).size.toLong()
                                        pendingBackupContentToSave = backupString
                                        showBackupSuccessDialog = true
                                    }
                                } else {
                                    isBackingUp = false
                                    showBackupProgressDialog = false
                                    Toast.makeText(
                                        context,
                                        "Backup creation failed: ${result.exceptionOrNull()?.message ?: "Unknown error"}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    },
                    shape = Shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier.testTag("btn_confirm_create_backup")
                ) {
                    Text("Create Secure Backup", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showBackupConfirmDialog = false },
                    shape = Shapes.medium
                ) {
                    Text("Cancel", fontSize = 13.5.sp)
                }
            },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // 3. Backup Progress Stepper Modal
    if (showBackupProgressDialog) {
        BackupProgressDialog(
            preparingStep = backupPrepStep,
            encryptingStep = backupEncryptStep,
            creatingStep = backupCreateStep,
            verifyingStep = backupVerifyStep
        )
    }

    // 4. Backup Success State Dialog
    if (showBackupSuccessDialog && createdBackupContent != null) {
        BackupSuccessDialog(
            fileName = createdBackupFileName,
            fileSizeBytes = createdBackupSize,
            itemCount = vaultItemsCount,
            timestamp = System.currentTimeMillis(),
            onSaveFile = {
                filePickerState.launchCreateBackupFile(createdBackupFileName)
            },
            onShare = {
                createdBackupContent?.let { content ->
                    SecureShareHelper.shareEncryptedBackup(context, createdBackupFileName, content)
                }
            },
            onDone = {
                showBackupSuccessDialog = false
            }
        )
    }

    // 5. Restore Passphrase Entry Dialog
    if (showRestorePassphraseDialog && selectedRestoreContent != null) {
        AlertDialog(
            onDismissRequest = { showRestorePassphraseDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(Shapes.medium)
                            .background(TotalSecurityPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Rounded.Key, contentDescription = null, tint = TotalSecurityPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Decrypt Backup",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 17.5.sp)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter the passphrase used when creating this .adere backup.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.5.sp
                        )
                    )
                    OutlinedTextField(
                        value = restorePassphraseInput,
                        onValueChange = { restorePassphraseInput = it },
                        label = { Text("Passphrase") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_restore_passphrase")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pass = restorePassphraseInput.trim()
                        if (pass.isEmpty()) {
                            Toast.makeText(context, "Passphrase cannot be empty", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        showRestorePassphraseDialog = false
                        isVerifyingRestoreFile = true

                        val content = selectedRestoreContent ?: return@Button
                        if (onPreviewBackup != null) {
                            onPreviewBackup(content, pass) { previewRes ->
                                isVerifyingRestoreFile = false
                                if (previewRes.isSuccess) {
                                    activeRestorePreview = previewRes.getOrThrow()
                                } else {
                                    Toast.makeText(
                                        context,
                                        previewRes.exceptionOrNull()?.message ?: "Decryption failed",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        } else {
                            // Direct preview validation via BackupManager
                            val previewRes = BackupManager.decryptAndValidateBackup(content, pass.toCharArray())
                            isVerifyingRestoreFile = false
                            if (previewRes.isSuccess) {
                                activeRestorePreview = previewRes.getOrThrow()
                            } else {
                                Toast.makeText(
                                    context,
                                    previewRes.exceptionOrNull()?.message ?: "Decryption failed",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    shape = Shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = TotalSecurityPrimary),
                    modifier = Modifier.testTag("btn_confirm_restore_passphrase")
                ) {
                    Text("Verify & Preview", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showRestorePassphraseDialog = false },
                    shape = Shapes.medium
                ) {
                    Text("Cancel", fontSize = 13.5.sp)
                }
            },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // 6. Restore Preview Dialog (Replace vs Merge)
    activeRestorePreview?.let { preview ->
        RestorePreviewDialog(
            itemCount = preview.itemCount,
            categoryCount = preview.categoryCount,
            version = preview.version,
            exportedAt = preview.exportedAt,
            onDismiss = { activeRestorePreview = null },
            onConfirmRestore = { selectedMode: BackupManager.RestoreMode ->
                val content = selectedRestoreContent ?: return@RestorePreviewDialog
                val pass = restorePassphraseInput.trim()

                if (onRestoreBackupWithMode != null) {
                    onRestoreBackupWithMode(content, pass, selectedMode) { res ->
                        activeRestorePreview = null
                        if (res.isSuccess) {
                            lastRestoredCount = res.getOrThrow()
                            lastRestoredMode = selectedMode
                            showRestoreSuccessDialog = true
                        } else {
                            val error = res.exceptionOrNull()?.localizedMessage
                                ?: res.exceptionOrNull()?.message
                                ?: "Failed to restore backup"
                            Toast.makeText(context, "Restore failed: $error", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    onRestoreBackup(content, pass) { res ->
                        activeRestorePreview = null
                        if (res.isSuccess) {
                            lastRestoredCount = res.getOrThrow()
                            lastRestoredMode = selectedMode
                            showRestoreSuccessDialog = true
                        } else {
                            val error = res.exceptionOrNull()?.localizedMessage
                                ?: res.exceptionOrNull()?.message
                                ?: "Failed to restore backup"
                            Toast.makeText(context, "Restore failed: $error", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        )
    }

    // 7. Restore Success State Dialog
    if (showRestoreSuccessDialog) {
        RestoreSuccessDialog(
            restoredCount = lastRestoredCount,
            mode = lastRestoredMode,
            onDone = {
                showRestoreSuccessDialog = false
                selectedRestoreContent = null
                restorePassphraseInput = ""
            }
        )
    }

    // 8. PDF Export Options Dialog
    if (showPdfOptionsDialog) {
        ExportOptionsDialog(
            onDismiss = { showPdfOptionsDialog = false },
            onConfirm = { options: PdfExportManager.ExportOptions, isShare: Boolean ->
                showPdfOptionsDialog = false
                onExportPdf(options, isShare)
            }
        )
    }

    // 9. Emergency Recovery Key Viewer Dialog
    if (showRecoveryKeyDialog) {
        AlertDialog(
            onDismissRequest = { showRecoveryKeyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(Shapes.medium)
                            .background(TotalSecurityPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = TotalSecurityPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Master Recovery Key",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 17.5.sp)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Store this recovery key in a physical safe. Anyone with this key can decrypt and recover your vault.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    )

                    MasterRecoveryKeyCard(
                        recoveryKey = currentRecoveryKey,
                        onCopyKey = {
                            clipboardManager.setText(AnnotatedString(currentRecoveryKey))
                            Toast.makeText(context, "Recovery key copied", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showRecoveryKeyDialog = false }) {
                    Text("Close", fontWeight = FontWeight.SemiBold)
                }
            },
            shape = Shapes.medium,
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

private enum class AuthAction {
    CREATE_BACKUP,
    EXPORT_PDF
}

