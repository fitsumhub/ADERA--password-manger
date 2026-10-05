package com.example.adere.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Flip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.utilities.HapticFeedbackHelper

/**
 * Premium 2026 interactive payment card with tap-to-flip animation.
 * Features EMV metallic chip, contactless icon, formatted digits, and secure CVV back.
 */
@Composable
fun PaymentCardView(
    cardNumber: String,
    cardHolder: String,
    cardExpiry: String,
    cardCvv: String,
    bankName: String = "",
    cardBrand: String = "",
    onCopy: (label: String, value: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isFlipped by remember { mutableStateOf(false) }
    var isNumberRevealed by remember { mutableStateOf(false) }
    var isCvvRevealed by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "card_flip"
    )

    val cleanNumber = cardNumber.replace(" ", "").replace("-", "")
    val detectedBrand = when {
        cardBrand.isNotBlank() -> cardBrand.uppercase()
        cleanNumber.startsWith("4") -> "VISA"
        cleanNumber.startsWith("51") || cleanNumber.startsWith("52") || cleanNumber.startsWith("53") || cleanNumber.startsWith("54") || cleanNumber.startsWith("55") -> "MASTERCARD"
        cleanNumber.startsWith("34") || cleanNumber.startsWith("37") -> "AMEX"
        cleanNumber.startsWith("6011") || cleanNumber.startsWith("65") -> "DISCOVER"
        else -> "DEBIT/CREDIT"
    }

    val formattedDisplayNumber = if (isNumberRevealed) {
        cleanNumber.chunked(4).joinToString(" ")
    } else {
        if (cleanNumber.length >= 4) {
            "•••• •••• •••• ${cleanNumber.takeLast(4)}"
        } else {
            "•••• •••• •••• ••••"
        }
    }

    val cardGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0F172A), // Dark Slate
            Color(0xFF1E293B),
            Color(0xFF0B192C)
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.586f) // Standard Credit Card ID-1 ratio (85.60 × 53.98 mm)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable {
                HapticFeedbackHelper.click(context)
                isFlipped = !isFlipped
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(cardGradient)
                .clip(RoundedCornerShape(18.dp))
        ) {
            if (rotation <= 90f) {
                // ── FRONT OF CARD ──────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 22.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Row: Bank name + Brand
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (bankName.isNotBlank()) bankName.uppercase() else "ADERE SECURE VAULT",
                            color = Color(0xFF94A3B8),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                fontSize = 11.sp
                            )
                        )

                        Text(
                            text = detectedBrand,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                fontSize = 16.sp
                            )
                        )
                    }

                    // Middle: EMV Chip + Contactless wave
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gold EMV Chip
                        Box(
                            modifier = Modifier
                                .size(width = 38.dp, height = 28.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFCD34D), Color(0xFFD97706), Color(0xFFF59E0B))
                                    )
                                )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            imageVector = Icons.Default.Nfc,
                            contentDescription = "Contactless",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Card Number Row with Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formattedDisplayNumber,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.2.sp,
                                fontSize = 17.5.sp
                            )
                        )

                        Row {
                            IconButton(
                                onClick = {
                                    HapticFeedbackHelper.tick(context)
                                    isNumberRevealed = !isNumberRevealed
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isNumberRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Reveal Card Number",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                            IconButton(
                                onClick = {
                                    onCopy("Card Number", cleanNumber)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Card Number",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Bottom Row: Cardholder name & Expiration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "CARDHOLDER",
                                color = Color(0xFF64748B),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (cardHolder.isNotBlank()) cardHolder.uppercase() else "VALUED MEMBER",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "EXPIRES",
                                color = Color(0xFF64748B),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (cardExpiry.isNotBlank()) cardExpiry else "••/••",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            } else {
                // ── BACK OF CARD (Rotated 180 deg) ──────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f }
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Magnetic Black Stripe
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .background(Color.Black)
                    )

                    // Signature & CVV Area
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp)
                    ) {
                        Text(
                            text = "SECURITY CODE (CVV)",
                            color = Color(0xFF94A3B8),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Signature stripe
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFE2E8F0))
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "AUTHORIZED SIGNATURE",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // CVV Box
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color.White,
                                modifier = Modifier
                                    .size(width = 65.dp, height = 34.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = if (isCvvRevealed) cardCvv else "•••",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    HapticFeedbackHelper.tick(context)
                                    isCvvRevealed = !isCvvRevealed
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCvvRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle CVV",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    onCopy("CVV", cardCvv)
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy CVV",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Disclaimer text
                    Text(
                        text = "This card is encrypted in your zero-knowledge Adere vault. Tap to flip.",
                        color = Color(0xFF64748B),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                        modifier = Modifier.padding(horizontal = 22.dp)
                    )
                }
            }
        }
    }
}

