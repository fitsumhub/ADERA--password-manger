package com.example.adere.presentation.components
import androidx.compose.foundation.shape.CircleShape

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalThemeAccents
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BiometricUnlockDemo(
    modifier: Modifier = Modifier
) {
    val themeAccents = LocalThemeAccents.current
    val coroutineScope = rememberCoroutineScope()
    
    var currentState by remember { mutableStateOf(BiometricState.Idle) }
    var isFullScreenDemo by remember { mutableStateOf(false) }

    if (isFullScreenDemo) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            EnhancedBiometricUnlockScreen(
                biometricState = currentState,
                onBiometricClick = {
                    coroutineScope.launch {
                        currentState = BiometricState.Scanning
                        delay(2000)
                        currentState = if ((0..1).random() == 0) {
                            BiometricState.Success
                        } else {
                            BiometricState.Error
                        }
                        delay(2000)
                        currentState = BiometricState.Idle
                    }
                },
                onPasswordClick = {
                    isFullScreenDemo = false
                    currentState = BiometricState.Idle
                }
            )
        }
    } else {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Biometric Unlock Demo",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
                
                Text(
                    text = "Experience the enhanced biometric unlock with beautiful animations and visual feedback.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                )
                
                // State Control Buttons
                Text(
                    text = "Test Different States:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StateButton(
                        text = "Idle",
                        isActive = currentState == BiometricState.Idle,
                        onClick = { currentState = BiometricState.Idle },
                        modifier = Modifier.weight(1f)
                    )
                    
                    StateButton(
                        text = "Scanning",
                        isActive = currentState == BiometricState.Scanning,
                        onClick = { currentState = BiometricState.Scanning },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StateButton(
                        text = "Success",
                        isActive = currentState == BiometricState.Success,
                        onClick = { currentState = BiometricState.Success },
                        modifier = Modifier.weight(1f)
                    )
                    
                    StateButton(
                        text = "Error",
                        isActive = currentState == BiometricState.Error,
                        onClick = { currentState = BiometricState.Error },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                StateButton(
                    text = "Unavailable",
                    isActive = currentState == BiometricState.Unavailable,
                    onClick = { currentState = BiometricState.Unavailable },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Mini Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.surface
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                AnimatedBiometricIcon(
                    state = currentState,
                    onClick = {
                        coroutineScope.launch {
                            currentState = BiometricState.Scanning
                            delay(1500)
                            currentState = BiometricState.Success
                            delay(1500)
                            currentState = BiometricState.Idle
                        }
                    },
                    modifier = Modifier.padding(120.dp)
                )
                }
                
                // Full Screen Demo Button
                Button(
                    onClick = { isFullScreenDemo = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeAccents.primary
                    ),
                    shape = CircleShape,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Launch Full Screen Demo",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun StateButton(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themeAccents = LocalThemeAccents.current
    
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) {
                themeAccents.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = if (isActive) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 12.sp
            )
        )
    }
}

// Specialized biometric components for different use cases
@Composable
fun CompactBiometricPrompt(
    state: BiometricState,
    onBiometricClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnimatedBiometricIcon(
                state = state,
                onClick = onBiometricClick,
                modifier = Modifier.padding(48.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Quick Unlock",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                
                Text(
                    text = when (state) {
                        BiometricState.Idle -> "Touch to unlock"
                        BiometricState.Scanning -> "Scanning..."
                        BiometricState.Success -> "Success!"
                        BiometricState.Error -> "Try again"
                        BiometricState.Unavailable -> "Not available"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
