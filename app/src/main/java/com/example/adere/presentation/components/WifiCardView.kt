package com.example.adere.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.utilities.HapticFeedbackHelper
import com.example.adere.core.utilities.WifiQrCodeGenerator
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TotalSecurityPrimary

@Composable
fun WifiCardView(
    ssid: String,
    password: String,
    securityType: String,
    onCopy: (label: String, value: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPasswordRevealed by remember { mutableStateOf(false) }
    var showQrModal by remember { mutableStateOf(false) }

    val cleanSecType = if (securityType.isNotBlank()) securityType else "WPA2/WPA3"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row: Wi-Fi Icon & Network Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0D9488).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Wifi,
                            contentDescription = "Wi-Fi",
                            tint = Color(0xFF0D9488),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = ssid.ifBlank { "Wi-Fi Network" },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 17.sp
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = cleanSecType,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        HapticFeedbackHelper.tick(context)
                        showQrModal = true
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TotalSecurityPrimary.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Rounded.QrCode2,
                        contentDescription = "Show QR Code",
                        tint = TotalSecurityPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SSID Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SSID / NETWORK NAME",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = ssid.ifBlank { "—" },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
                IconButton(
                    onClick = {
                        HapticFeedbackHelper.confirm(context)
                        onCopy("SSID", ssid)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy SSID",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Password Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "WI-FI PASSWORD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = if (isPasswordRevealed) password else "••••••••••••",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = if (isPasswordRevealed) 0.sp else 2.sp
                        )
                    )
                }

                Row {
                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.tick(context)
                            isPasswordRevealed = !isPasswordRevealed
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isPasswordRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle password visibility",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.confirm(context)
                            onCopy("Wi-Fi Password", password)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy password",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Share QR Code Button
            Button(
                onClick = {
                    HapticFeedbackHelper.tick(context)
                    showQrModal = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0D9488)
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.QrCode2,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Share with QR Code",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }

    // Offline QR Code Dialog
    if (showQrModal) {
        val qrPayload = remember(ssid, password, cleanSecType) {
            WifiQrCodeGenerator.formatWifiString(ssid, password, cleanSecType)
        }
        val qrBitmap: Bitmap? = remember(qrPayload) {
            try {
                WifiQrCodeGenerator.generateQrBitmap(qrPayload, 512)
            } catch (_: Exception) {
                null
            }
        }

        AlertDialog(
            onDismissRequest = { showQrModal = false },
            title = {
                Text(
                    text = "Scan to Connect",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Point another phone's camera at this QR code to connect to '$ssid' instantly without typing the password.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (qrBitmap != null) {
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "Wi-Fi QR Code",
                                modifier = Modifier.size(216.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "Could not generate QR code",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "100% Offline • Zero Network Transmission",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showQrModal = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Done")
                }
            },
            shape = CircleShape
        )
    }
}

