package com.example.adere.presentation.screens.settings.backup.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.EmeraldPrimary

enum class BackupStepState {
    PENDING,
    RUNNING,
    COMPLETED,
    ERROR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupProgressDialog(
    preparingStep: BackupStepState,
    encryptingStep: BackupStepState,
    creatingStep: BackupStepState,
    verifyingStep: BackupStepState,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        onDismissRequest = { /* Non-dismissible while processing */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        modifier = modifier
    ) {
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Securing your vault...",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 19.sp
                    )
                )

                Text(
                    text = "Please keep Adere open while data is encrypted",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ProgressStepRow(title = "Preparing vault items", state = preparingStep)
                    ProgressStepRow(title = "Encrypting data with AES-256-GCM", state = encryptingStep)
                    ProgressStepRow(title = "Creating secure backup package", state = creatingStep)
                    ProgressStepRow(title = "Verifying package integrity", state = verifyingStep)
                }
            }
        }
    }
}

@Composable
private fun ProgressStepRow(
    title: String,
    state: BackupStepState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (state) {
            BackupStepState.COMPLETED -> {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Done",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            BackupStepState.RUNNING -> {
                CircularProgressIndicator(
                    strokeWidth = 2.5.dp,
                    color = EmeraldPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            BackupStepState.PENDING -> {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                )
            }
            BackupStepState.ERROR -> {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.8f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (state == BackupStepState.RUNNING) FontWeight.SemiBold else FontWeight.Normal,
                color = when (state) {
                    BackupStepState.COMPLETED -> MaterialTheme.colorScheme.onSurface
                    BackupStepState.RUNNING -> EmeraldPrimary
                    BackupStepState.PENDING -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    BackupStepState.ERROR -> MaterialTheme.colorScheme.error
                },
                fontSize = 14.sp
            )
        )
    }
}

