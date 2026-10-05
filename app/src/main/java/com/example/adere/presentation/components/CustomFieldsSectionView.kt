package com.example.adere.presentation.components
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.utilities.HapticFeedbackHelper
import com.example.ui.theme.TotalSecurityPrimary

/**
 * Renders custom secure key-value fields with automatic secret detection,
 * masking, and copy actions.
 */
@Composable
fun CustomFieldsSectionView(
    customFields: Map<String, String>,
    onCopy: (label: String, value: String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (customFields.isEmpty()) return

    val context = LocalContext.current
    val revealedStates = remember { mutableStateMapOf<String, Boolean>() }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Custom Fields",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            customFields.forEach { (key, value) ->
                val isSensitiveKey = isSecretField(key)
                val isRevealed = revealedStates[key] ?: (!isSensitiveKey)

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = key.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isRevealed) value else "••••••••••••",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = if (isSensitiveKey) FontFamily.Monospace else FontFamily.Default,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        Row {
                            if (isSensitiveKey) {
                                IconButton(onClick = {
                                    HapticFeedbackHelper.tick(context)
                                    revealedStates[key] = !isRevealed
                                }) {
                                    Icon(
                                        imageVector = if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle visibility",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            IconButton(onClick = { onCopy(key, value) }) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy field",
                                    tint = TotalSecurityPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun isSecretField(key: String): Boolean {
    val lower = key.lowercase()
    return lower.contains("pin") ||
            lower.contains("secret") ||
            lower.contains("pass") ||
            lower.contains("key") ||
            lower.contains("token") ||
            lower.contains("cvv") ||
            lower.contains("code") ||
            lower.contains("seed")
}

