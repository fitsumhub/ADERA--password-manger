package com.example.adere.presentation.screens.settings.backup.components
import com.example.ui.theme.Shapes
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Merge
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.backup.BackupManager
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.TotalSecurityPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RestorePreviewDialog(
    itemCount: Int,
    categoryCount: Int,
    version: String,
    exportedAt: Long,
    onDismiss: () -> Unit,
    onConfirmRestore: (BackupManager.RestoreMode) -> Unit
) {
    var selectedMode by remember { mutableStateOf(BackupManager.RestoreMode.MERGE) }

    val formattedDate = remember(exportedAt) {
        val formatter = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())
        formatter.format(Date(exportedAt))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(Shapes.medium)
                        .background(TotalSecurityPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Restore,
                        contentDescription = null,
                        tint = TotalSecurityPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Backup Found",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Verified cryptographic payload",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Metadata card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Vault items",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$itemCount items",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Categories",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$categoryCount categories",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Created",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Backup version",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "v$version",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Security notice warning
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f))
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WarningAmber,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Restoring this backup may replace or merge your current vault depending on your selected restore mode.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    )
                }

                Text(
                    text = "Select Restore Mode",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                // Mode 1: Merge
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .clickable { selectedMode = BackupManager.RestoreMode.MERGE },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedMode == BackupManager.RestoreMode.MERGE)
                            TotalSecurityPrimary.copy(alpha = 0.08f)
                        else
                            MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selectedMode == BackupManager.RestoreMode.MERGE)
                            TotalSecurityPrimary
                        else
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMode == BackupManager.RestoreMode.MERGE,
                            onClick = { selectedMode = BackupManager.RestoreMode.MERGE },
                            colors = RadioButtonDefaults.colors(selectedColor = TotalSecurityPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Merge with current vault",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.5.sp
                                )
                            )
                            Text(
                                text = "Preserves existing items and adds non-duplicate entries",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Mode 2: Replace
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .clickable { selectedMode = BackupManager.RestoreMode.REPLACE },
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedMode == BackupManager.RestoreMode.REPLACE)
                            HealthRiskRed.copy(alpha = 0.08f)
                        else
                            MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selectedMode == BackupManager.RestoreMode.REPLACE)
                            HealthRiskRed
                        else
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMode == BackupManager.RestoreMode.REPLACE,
                            onClick = { selectedMode = BackupManager.RestoreMode.REPLACE },
                            colors = RadioButtonDefaults.colors(selectedColor = HealthRiskRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Replace current vault",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.5.sp
                                )
                            )
                            Text(
                                text = "Overwrites all current items with this backup",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmRestore(selectedMode) },
                shape = Shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedMode == BackupManager.RestoreMode.REPLACE)
                        HealthRiskRed
                    else
                        TotalSecurityPrimary
                ),
                modifier = Modifier.testTag("btn_confirm_restore_mode")
            ) {
                Text(
                    text = "Restore Securely",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = Shapes.medium
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 13.5.sp
                )
            }
        },
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.surface
    )
}
