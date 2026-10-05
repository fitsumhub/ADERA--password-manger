package com.example.adere.presentation.screens.settings.backup.components
import com.example.ui.theme.Shapes
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.backup.BackupManager
import com.example.ui.theme.EmeraldPrimary

@Composable
fun RestoreSuccessDialog(
    restoredCount: Int,
    mode: BackupManager.RestoreMode,
    onDone: () -> Unit
) {
    val modeText = if (mode == BackupManager.RestoreMode.REPLACE) "Replaced vault completely" else "Merged with existing vault"

    AlertDialog(
        onDismissRequest = onDone,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(Shapes.medium)
                        .background(EmeraldPrimary.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Restored",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Vault Restored",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$restoredCount vault credentials were successfully restored and re-encrypted.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Mode: $modeText",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDone,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_restore_done")
            ) {
                Text(
                    text = "Done",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        },
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.surface
    )
}
