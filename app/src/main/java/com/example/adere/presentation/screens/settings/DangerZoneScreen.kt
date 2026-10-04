package com.example.adere.presentation.screens.settings

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
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.presentation.screens.settings.components.SettingsItemCard
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.ui.theme.HealthRiskRed

@Composable
fun DangerZoneScreen(
    onFactoryReset: (password: String, callback: (Result<Unit>) -> Unit) -> Unit,
    onDeleteAllData: (password: String, callback: (Result<Unit>) -> Unit) -> Unit,
    onBackClick: () -> Unit
) {
    var showFactoryResetDialog by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Danger Zone",
                subtitle = "Irreversible actions · Complete data destruction",
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Warning banner ────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = HealthRiskRed.copy(alpha = 0.06f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    HealthRiskRed.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(HealthRiskRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Warning,
                                contentDescription = null,
                                tint = HealthRiskRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Destructive Operations",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = HealthRiskRed
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Actions below permanently destroy all vault data. " +
                                        "Ensure you have secure backups before proceeding.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = HealthRiskRed.copy(alpha = 0.8f)
                                )
                            )
                        }
                    }
                }
            }

            // ── Destructive actions ───────────────────────────────────────────
            SettingsSectionTitle("Irreversible Actions")

            SettingsItemCard(
                icon = Icons.Default.RestartAlt,
                iconTint = HealthRiskRed,
                iconBg = HealthRiskRed.copy(alpha = 0.1f),
                title = "Factory Reset",
                subtitle = "Reset app to initial state · Keeps app installed",
                onClick = { showFactoryResetDialog = true },
                testTag = "danger_factory_reset_item"
            )

            SettingsItemCard(
                icon = Icons.Default.DeleteForever,
                iconTint = HealthRiskRed,
                iconBg = HealthRiskRed.copy(alpha = 0.1f),
                title = "Delete All Vault Data",
                subtitle = "Permanently destroy all credentials and settings",
                onClick = { showDeleteAllDialog = true },
                testTag = "danger_delete_all_item"
            )

            // ── Recovery information ──────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Data Recovery",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                    Text(
                        text = "All destroyed data is immediately and permanently unrecoverable. " +
                                "ADERE uses secure deletion methods that overwrite memory sectors. " +
                                "Ensure you have exported backups to a secure location.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // ── Factory reset dialog ──────────────────────────────────────────────────

    if (showFactoryResetDialog) {
        DestructiveActionDialog(
            title = "Factory Reset ADERE?",
            description = "This will permanently delete all vault data, settings, and preferences. " +
                    "The app will return to its initial setup state. " +
                    "This action cannot be undone.",
            confirmText = "Factory Reset",
            onConfirm = { password, callback ->
                onFactoryReset(password, callback)
            },
            onDismiss = { showFactoryResetDialog = false }
        )
    }

    // ── Delete all data dialog ─────────────────────────────────────────────────

    if (showDeleteAllDialog) {
        DestructiveActionDialog(
            title = "Delete All Vault Data?",
            description = "This will permanently destroy all stored credentials, notes, and vault settings. " +
                    "App preferences will be preserved. " +
                    "This action cannot be undone.",
            confirmText = "Delete All Data",
            onConfirm = { password, callback ->
                onDeleteAllData(password, callback)
            },
            onDismiss = { showDeleteAllDialog = false }
        )
    }
}

// ─── Destructive action confirmation dialog ──────────────────────────────────

@Composable
private fun DestructiveActionDialog(
    title: String,
    description: String,
    confirmText: String,
    onConfirm: (password: String, callback: (Result<Unit>) -> Unit) -> Unit,
    onDismiss: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = HealthRiskRed,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = HealthRiskRed
                    )
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                )

                errorMessage?.let { error ->
                    Surface(
                        color = HealthRiskRed.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            color = HealthRiskRed,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = { Text("Enter Master Password") },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None
                                          else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff
                                              else Icons.Default.Visibility,
                                contentDescription = if (isPasswordVisible) "Hide password"
                                                    else "Show password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null,
                    enabled = !isProcessing
                )

                Surface(
                    color = HealthRiskRed.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "⚠️ This action is irreversible and will permanently destroy your data.",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = HealthRiskRed,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (password.isBlank()) {
                        errorMessage = "Master password is required for verification."
                        return@Button
                    }
                    isProcessing = true
                    onConfirm(password) { result ->
                        isProcessing = false
                        if (result.isSuccess) {
                            onDismiss()
                        } else {
                            errorMessage = result.exceptionOrNull()?.message 
                                ?: "Incorrect master password."
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HealthRiskRed),
                enabled = !isProcessing
            ) {
                Text(
                    text = if (isProcessing) "Processing..." else confirmText,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isProcessing
            ) {
                Text("Cancel")
            }
        }
    )
}