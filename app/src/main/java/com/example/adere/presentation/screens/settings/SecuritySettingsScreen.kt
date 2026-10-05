package com.example.adere.presentation.screens.settings
import com.example.ui.theme.Shapes
import androidx.compose.foundation.shape.CircleShape

import android.widget.Toast
import androidx.biometric.BiometricManager
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
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.presentation.screens.settings.components.SettingsItemCard
import com.example.adere.presentation.screens.settings.components.SettingsOptionDialog
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.adere.presentation.screens.settings.components.SettingsSwitchItem
import com.example.adere.presentation.screens.settings.components.SettingsValueItem
import com.example.ui.theme.HealthRiskRed

fun formatAutoLockLabel(seconds: Int): String = when (seconds) {
    0    -> "Immediately"
    30   -> "30 sec"
    60   -> "1 min"
    300  -> "5 min"
    900  -> "15 min"
    3600 -> "1 hour"
    -1   -> "Never"
    else -> "${seconds}s"
}

@Composable
fun SecuritySettingsScreen(
    biometricEnabled: Boolean,
    autoLockSeconds: Int,
    onToggleBiometric: (Boolean, (Result<Unit>) -> Unit) -> Unit,
    onSetAutoLockSeconds: (Int, (Result<Unit>) -> Unit) -> Unit,
    onRefreshBiometricState: () -> Unit,
    onChangeMasterPassword: (old: String, new: String, (Result<Unit>) -> Unit) -> Unit,
    onGetRecoveryKey: () -> Result<String>,
    onRegenerateRecoveryKey: ((Result<String>) -> Unit) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showAutoLockDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showRecoveryKeyDialog by remember { mutableStateOf(false) }
    var showRegenerateConfirmDialog by remember { mutableStateOf(false) }
    var currentRecoveryKey by remember { mutableStateOf("") }

    val biometricManager = remember { BiometricManager.from(context) }
    val canAuth = remember {
        biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
    }
    val biometricAvailable = canAuth == BiometricManager.BIOMETRIC_SUCCESS

    val primary = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Security",
                subtitle = "Biometrics · Auto-lock · Master password · Recovery key",
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
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ── Biometric unlock ──────────────────────────────────────────────
            SettingsSectionTitle("Biometric Unlock")

            val bioSubtitle = when (canAuth) {
                BiometricManager.BIOMETRIC_SUCCESS ->
                    "Unlock vault using fingerprint or face ID — hardware-enforced"
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                    "No biometrics enrolled. Set up fingerprint in System Settings."
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                    "No biometric hardware detected on this device."
                else -> "Biometric unlock currently unavailable."
            }

            SettingsSwitchItem(
                icon = Icons.Default.Fingerprint,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Biometric Decryption",
                subtitle = bioSubtitle,
                checked = biometricEnabled && biometricAvailable,
                onCheckedChange = { enable ->
                    if (enable && !biometricAvailable) {
                        val msg = if (canAuth == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED)
                            "Enrol a fingerprint in Android Settings first."
                        else "Biometrics not supported on this device."
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    } else {
                        onToggleBiometric(enable) { res ->
                            if (!res.isSuccess) {
                                Toast.makeText(context, "Failed to toggle biometric: ${res.exceptionOrNull()?.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                                onRefreshBiometricState()
                            }
                        }
                    }
                },
                enabled = biometricAvailable,
                testTag = "settings_switch_biometric"
            )

            // ── Session protection ────────────────────────────────────────────
            SettingsSectionTitle("Session Protection")

            SettingsValueItem(
                icon = Icons.Default.Timer,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Auto-Lock Timer",
                subtitle = "Clear DEK from memory after inactivity",
                valueText = formatAutoLockLabel(autoLockSeconds),
                onClick = { showAutoLockDialog = true },
                testTag = "settings_autolock_item"
            )

            // ── Master password & recovery ────────────────────────────────────
            SettingsSectionTitle("Master Password & Recovery")

            SettingsItemCard(
                icon = Icons.Default.LockReset,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Change Master Password",
                subtitle = "Re-encrypt vault DEK with a new passphrase",
                onClick = { showChangePasswordDialog = true },
                testTag = "settings_change_password_item"
            )

            SettingsItemCard(
                icon = Icons.Default.Key,
                iconTint = Color(0xFFF59E0B),
                iconBg = Color(0xFFF59E0B).copy(alpha = 0.1f),
                title = "Master Recovery Key",
                subtitle = "View or regenerate your 24-character emergency recovery key",
                onClick = {
                    val res = onGetRecoveryKey()
                    if (res.isSuccess) {
                        currentRecoveryKey = res.getOrDefault("")
                        showRecoveryKeyDialog = true
                    } else {
                        Toast.makeText(
                            context,
                            res.exceptionOrNull()?.message ?: "Vault must be unlocked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                testTag = "settings_recovery_key_item"
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // ── Dialogs ────────────────────────────────────────────────────────────────

    if (showAutoLockDialog) {
        SettingsOptionDialog(
            title = "Auto-Lock Timer",
            options = listOf(
                0 to "Immediately on background",
                30 to "30 seconds",
                60 to "1 minute",
                300 to "5 minutes",
                900 to "15 minutes",
                3600 to "1 hour"
            ),
            selectedOption = autoLockSeconds,
            onSelectOption = {
                onSetAutoLockSeconds(it) { res ->
                    if (!res.isSuccess) {
                        Toast.makeText(context, "Failed to set auto-lock: ${res.exceptionOrNull()?.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDismiss = { showAutoLockDialog = false }
        )
    }

    if (showChangePasswordDialog) {
        ChangeMasterPasswordDialog(
            onConfirm = { old, new, cb ->
                onChangeMasterPassword(old, new, cb)
            },
            onDismiss = { showChangePasswordDialog = false }
        )
    }

    if (showRecoveryKeyDialog && currentRecoveryKey.isNotBlank()) {
        RecoveryKeyViewDialog(
            recoveryKey = currentRecoveryKey,
            onCopy = {
                clipboardManager.setText(AnnotatedString(currentRecoveryKey))
                Toast.makeText(context, "Recovery key copied", Toast.LENGTH_SHORT).show()
            },
            onRegenerate = {
                showRecoveryKeyDialog = false
                showRegenerateConfirmDialog = true
            },
            onDismiss = { showRecoveryKeyDialog = false }
        )
    }

    if (showRegenerateConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRegenerateConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Warning,
                        contentDescription = null,
                        tint = HealthRiskRed,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Regenerate Recovery Key?",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    "This will permanently invalidate your current recovery key. " +
                    "Any written copies of the old key will stop working immediately. " +
                    "Make sure to note down the new key right away.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = HealthRiskRed),
                    onClick = {
                        showRegenerateConfirmDialog = false
                        onRegenerateRecoveryKey { res ->
                            if (res.isSuccess) {
                                currentRecoveryKey = res.getOrDefault("")
                                showRecoveryKeyDialog = true
                                Toast.makeText(context, "New recovery key generated", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Failed: ${res.exceptionOrNull()?.message ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) { Text("Regenerate") }
            },
            dismissButton = {
                TextButton(onClick = { showRegenerateConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ─── Change master-password dialog ───────────────────────────────────────────

@Composable
private fun ChangeMasterPasswordDialog(
    onConfirm: (old: String, new: String, cb: (Result<Unit>) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    var currentPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var currentVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val newStrength = remember(newPass) {
        var s = 0
        if (newPass.length >= 8) s++
        if (newPass.length >= 12) s++
        if (newPass.any { it.isDigit() }) s++
        if (newPass.any { it.isUpperCase() } && newPass.any { it.isLowerCase() }) s++
        if (newPass.any { !it.isLetterOrDigit() }) s++
        s
    }

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Text("Change Master Password", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Re-encrypts your vault DEK. All stored secrets remain safe.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                errorMsg?.let {
                    Surface(
                        color = HealthRiskRed.copy(alpha = 0.1f),
                        shape = Shapes.medium
                    ) {
                        Text(
                            it,
                            color = HealthRiskRed,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = currentPass,
                    onValueChange = { currentPass = it; errorMsg = null },
                    label = { Text("Current Password") },
                    singleLine = true,
                    visualTransformation = if (currentVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { currentVisible = !currentVisible }) {
                            Icon(
                                if (currentVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("input_current_password"),
                    isError = errorMsg != null
                )

                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it; errorMsg = null },
                    label = { Text("New Password (min 8 chars)") },
                    singleLine = true,
                    visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { newVisible = !newVisible }) {
                            Icon(
                                if (newVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("input_new_password")
                )

                // Strength bar
                if (newPass.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        LinearProgressIndicator(
                            progress = { (newStrength / 5f).coerceIn(0.08f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(Shapes.medium),
                            color = when {
                                newStrength <= 2 -> HealthRiskRed
                                newStrength <= 3 -> Color(0xFFF59E0B)
                                else -> Color(0xFF10B981)
                            },
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = when {
                                newStrength <= 2 -> "Weak password"
                                newStrength <= 3 -> "Moderate password"
                                else -> "Strong password"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = when {
                                    newStrength <= 2 -> HealthRiskRed
                                    newStrength <= 3 -> Color(0xFFF59E0B)
                                    else -> Color(0xFF10B981)
                                }
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = confirmPass,
                    onValueChange = { confirmPass = it; errorMsg = null },
                    label = { Text("Confirm New Password") },
                    singleLine = true,
                    visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth().testTag("input_confirm_password"),
                    isError = confirmPass.isNotEmpty() && confirmPass != newPass
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    when {
                        currentPass.isBlank() -> errorMsg = "Enter your current master password."
                        newPass.length < 8 -> errorMsg = "New password must be at least 8 characters."
                        newPass != confirmPass -> errorMsg = "New passwords do not match."
                        else -> {
                            isSubmitting = true
                            onConfirm(currentPass, newPass) { result ->
                                isSubmitting = false
                                if (result.isSuccess) onDismiss()
                                else errorMsg = result.exceptionOrNull()?.message
                                    ?: "Incorrect current password."
                            }
                        }
                    }
                }
            ) {
                Text(if (isSubmitting) "Updating…" else "Update Password")
            }
        },
        dismissButton = {
            TextButton(enabled = !isSubmitting, onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ─── Recovery key view dialog ─────────────────────────────────────────────────

@Composable
private fun RecoveryKeyViewDialog(
    recoveryKey: String,
    onCopy: () -> Unit,
    onRegenerate: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Key, contentDescription = null,
                    tint = Color(0xFFF59E0B), modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Text("Master Recovery Key", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    "Write this key down and store it in a secure physical location. " +
                    "It restores vault access if you forget your master password.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // Key display box
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = recoveryKey,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onCopy) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Warning banner
                Surface(
                    color = HealthRiskRed.copy(alpha = 0.08f),
                    shape = Shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Warning,
                            contentDescription = null,
                            tint = HealthRiskRed,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Never share your recovery key. ADERE staff will never ask for it.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                color = HealthRiskRed,
                                lineHeight = 15.sp
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done") }
        },
        dismissButton = {
            OutlinedButton(onClick = onRegenerate) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Regenerate")
                }
            }
        }
    )
}
