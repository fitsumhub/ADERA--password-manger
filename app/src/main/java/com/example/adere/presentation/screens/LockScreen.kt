package com.example.adere.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import android.widget.Toast
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.adere.core.crypto.RecoveryKeyManager
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.Shapes
import kotlinx.coroutines.launch

private val CleanBg: Color
    @Composable get() = MaterialTheme.colorScheme.background
private val CleanSurface: Color
    @Composable get() = MaterialTheme.colorScheme.surface
private val CleanSurfaceVariant: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceVariant
private val TextDarkMuted: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
private val TextDarkPrimary: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface
private val TextDarkSecondary: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
private val PrimaryAccent: Color
    @Composable get() = MaterialTheme.colorScheme.primary
private val PrimaryContainerAccent: Color
    @Composable get() = MaterialTheme.colorScheme.primaryContainer
private val OnPrimaryAccent: Color
    @Composable get() = MaterialTheme.colorScheme.onPrimary

@Composable
fun LockScreen(
    isBiometricAvailable: Boolean,
    customAppName: String = "ADERA",
    onTriggerBiometrics: () -> Unit,
    onUnlockWithPassword: (String, (Result<Unit>) -> Unit) -> Unit,
    onRecoverWithKey: (recoveryKey: String, newPassword: String, (Result<Unit>) -> Unit) -> Unit = { _, _, _ -> },
) {
    val hapticFeedback = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(value = false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isAuthenticating by remember { mutableStateOf(value = false) }

    // Recovery State
    var showRecoveryDialog by remember { mutableStateOf(value = false) }
    var recoveryKeyInput by remember { mutableStateOf("") }
    var recoveryNewPassword by remember { mutableStateOf("") }
    var recoveryConfirmPassword by remember { mutableStateOf("") }
    var recoveryPasswordVisible by remember { mutableStateOf(value = false) }
    var recoveryErrorMessage by remember { mutableStateOf<String?>(null) }
    var isRecovering by remember { mutableStateOf(value = false) }

    // Error shake animation
    val shakeOffset = remember { Animatable(0f) }

    // Breathing pulse animation for ambient security glow
    val infiniteTransition = rememberInfiniteTransition(label = "lock_ambient_anim")
    val pulseAura by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAura",
    )

    // Trigger biometrics automatically on entry if available
    if (isBiometricAvailable) {
        LaunchedEffect(Unit) {
            onTriggerBiometrics()
        }
    }

    fun submitPassword() {
        val trimmed = password.trim()
        if (trimmed.isBlank()) {
            errorMessage = "Please enter your master password."
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            coroutineScope.launch {
                shakeOffset.animateTo(-12f, tween(40))
                shakeOffset.animateTo(12f, tween(40))
                shakeOffset.animateTo(-8f, tween(40))
                shakeOffset.animateTo(8f, tween(40))
                shakeOffset.animateTo(0f, tween(40))
            }
            return
        }

        isAuthenticating = true
        errorMessage = null
        onUnlockWithPassword(trimmed) { result ->
            isAuthenticating = false
            if (result.isFailure) {
                errorMessage = result.exceptionOrNull()?.message ?: "Incorrect master password."
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                coroutineScope.launch {
                    shakeOffset.animateTo(-14f, tween(45))
                    shakeOffset.animateTo(14f, tween(45))
                    shakeOffset.animateTo(-10f, tween(45))
                    shakeOffset.animateTo(10f, tween(45))
                    shakeOffset.animateTo(-4f, tween(45))
                    shakeOffset.animateTo(0f, tween(45))
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CleanBg),
    ) {
        // Ambient soft atmospheric glowing rings in background
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(360.dp)
                .scale(pulseAura)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PrimaryAccent.copy(alpha = 0.12f),
                            PrimaryAccent.copy(alpha = 0.04f),
                            Color.Transparent,
                        ),
                    ),
                    shape = Shapes.large,
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Security Badge Pill with pulse indicator
            Surface(
                shape = Shapes.medium,
                color = PrimaryContainerAccent,
                shadowElevation = 0.dp,
                border = BorderStroke(1.dp, PrimaryAccent.copy(alpha = 0.25f)),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(EmeraldPrimary),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AES-256 ZERO-KNOWLEDGE VAULT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PrimaryAccent,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp,
                            fontSize = 10.5.sp,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Professional Elevated Security Emblem
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .shadow(16.dp, CircleShape, spotColor = PrimaryAccent.copy(alpha = 0.35f))
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                PrimaryAccent.copy(alpha = 0.8f),
                                PrimaryAccent,
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .clip(CircleShape)
                        .background(CleanSurface),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "ADERE Security Logo",
                        modifier = Modifier.size(76.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title & Subtitle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = customAppName.ifBlank { "Welcome Back" },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDarkPrimary,
                        fontSize = 28.sp,
                        letterSpacing = (-0.5).sp,
                    ),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = Shapes.medium,
                    color = PrimaryContainerAccent,
                ) {
                    Text(
                        text = "አደራ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryAccent,
                            fontSize = 11.sp,
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Authenticate to decrypt your hardware-protected local vault",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextDarkSecondary,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp,
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Main Authentication Card with Shake Animation on Error
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationX = shakeOffset.value
                    }
                    .shadow(
                        elevation = 8.dp,
                        shape = Shapes.large,
                        spotColor = Color.Black.copy(alpha = 0.08f),
                    ),
                shape = Shapes.large,
                colors = CardDefaults.cardColors(containerColor = CleanSurface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Biometric Unlock Trigger (Hero CTA if available)
                    if (isBiometricAvailable) {
                        Surface(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                onTriggerBiometrics()
                            },
                            shape = Shapes.medium,
                            color = PrimaryContainerAccent,
                            border = BorderStroke(1.dp, PrimaryAccent.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("unlock_biometric_button"),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 14.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryAccent),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = "Biometric Icon",
                                        tint = OnPrimaryAccent,
                                        modifier = Modifier.size(22.dp),
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Unlock with Biometrics",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = PrimaryAccent,
                                        ),
                                    )
                                    Text(
                                        text = "Touch sensor for instant access",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextDarkSecondary,
                                            fontSize = 11.5.sp,
                                        ),
                                    )
                                }
                            }
                        }

                        // Elegant Divider
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            )
                            Text(
                                text = "OR USE MASTER PASSWORD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextDarkMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.6.sp,
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp),
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            )
                        }
                    }

                    // Master Password Text Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (errorMessage != null) errorMessage = null
                        },
                        label = { Text("Master Password") },
                        placeholder = { Text("Enter master password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (password.isNotEmpty()) PrimaryAccent else TextDarkMuted,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { submitPassword() },
                        ),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = TextDarkSecondary,
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CleanSurfaceVariant,
                            unfocusedContainerColor = CleanSurfaceVariant,
                            focusedBorderColor = PrimaryAccent,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedTextColor = TextDarkPrimary,
                            unfocusedTextColor = TextDarkPrimary,
                            cursorColor = PrimaryAccent,
                        ),
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("lock_screen_password_input"),
                    )

                    // Error Notification Surface
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        Surface(
                            shape = Shapes.medium,
                            color = HealthRiskRed.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, HealthRiskRed.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = HealthRiskRed,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage ?: "",
                                    color = HealthRiskRed,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.5.sp,
                                    ),
                                )
                            }
                        }
                    }

                    // Unlock Button
                    Button(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            submitPassword()
                        },
                        enabled = !isAuthenticating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(
                                elevation = if (password.isNotEmpty()) 6.dp else 0.dp,
                                shape = Shapes.medium,
                                spotColor = PrimaryAccent,
                            )
                            .testTag("lock_screen_unlock_button"),
                        shape = Shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryAccent,
                            contentColor = OnPrimaryAccent,
                            disabledContainerColor = PrimaryAccent.copy(alpha = 0.6f),
                        ),
                    ) {
                        if (isAuthenticating) {
                            CircularProgressIndicator(
                                color = OnPrimaryAccent,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp,
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = OnPrimaryAccent,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Unlock Vault",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = OnPrimaryAccent,
                                    ),
                                )
                            }
                        }
                    }

                    // Recovery Key Option
                    OutlinedButton(
                        onClick = {
                            recoveryErrorMessage = null
                            recoveryKeyInput = ""
                            recoveryNewPassword = ""
                            recoveryConfirmPassword = ""
                            showRecoveryDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("lock_screen_recover_button"),
                        shape = Shapes.medium,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PrimaryAccent,
                        ),
                        border = BorderStroke(1.dp, PrimaryAccent.copy(alpha = 0.35f)),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = null,
                                tint = PrimaryAccent,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Use Master Recovery Key",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = PrimaryAccent,
                                ),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Footer Security Guarantee
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 12.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = TextDarkMuted,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "100% Offline · Local Android Keystore · Zero Telemetry",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextDarkMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Master Recovery Key Dialog
        if (showRecoveryDialog) {
            AlertDialog(
                onDismissRequest = { if (!isRecovering) showRecoveryDialog = false },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainerAccent),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = PrimaryAccent,
                            modifier = Modifier.size(26.dp),
                        )
                    }
                },
                title = {
                    Text(
                        text = "Recover Encrypted Vault",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkPrimary,
                        ),
                        textAlign = TextAlign.Center,
                    )
                },
                text = {
                    val context = LocalContext.current
                    val clipboardManager = LocalClipboardManager.current
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Text(
                            text = "Enter your 24-character Master Recovery Key to restore full access and set a new Master Password without losing your encrypted records.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDarkSecondary,
                                lineHeight = 18.sp,
                            ),
                        )

                        // Formatted Recovery Key Input
                        OutlinedTextField(
                            value = recoveryKeyInput,
                            onValueChange = { input ->
                                val raw = input.replace("-", "").replace(" ", "").uppercase()
                                recoveryKeyInput = raw.chunked(4).joinToString("-").take(29)
                                recoveryErrorMessage = null
                            },
                            label = { Text("Master Recovery Key") },
                            placeholder = { Text("XXXX-XXXX-XXXX-XXXX-XXXX-XXXX") },
                            trailingIcon = {
                                IconButton(onClick = {
                                    val pasted = clipboardManager.getText()?.text ?: ""
                                    if (pasted.isNotBlank()) {
                                        val normalized = RecoveryKeyManager.normalizeKey(pasted)
                                        if (RecoveryKeyManager.isValidKeyFormat(normalized)) {
                                            recoveryKeyInput = RecoveryKeyManager.formatKey(normalized)
                                            recoveryErrorMessage = null
                                            Toast.makeText(context, "Recovery key pasted from clipboard", Toast.LENGTH_SHORT).show()
                                        } else {
                                            recoveryErrorMessage = "Clipboard text is not a valid 24-character Recovery Key."
                                            Toast.makeText(context, "Clipboard text is not a valid 24-character Recovery Key.", Toast.LENGTH_LONG).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ContentPaste,
                                        contentDescription = "Paste Recovery Key",
                                        tint = PrimaryAccent
                                    )
                                }
                            },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CleanSurfaceVariant,
                                unfocusedContainerColor = CleanSurfaceVariant,
                                focusedBorderColor = PrimaryAccent,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                focusedTextColor = TextDarkPrimary,
                                unfocusedTextColor = TextDarkPrimary,
                            ),
                            shape = Shapes.large,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recovery_key_input"),
                        )

                        // New Password Input
                        OutlinedTextField(
                            value = recoveryNewPassword,
                            onValueChange = {
                                recoveryNewPassword = it
                                recoveryErrorMessage = null
                            },
                            label = { Text("New Master Password") },
                            placeholder = { Text("Min 8 characters") },
                            singleLine = true,
                            visualTransformation = if (recoveryPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { recoveryPasswordVisible = !recoveryPasswordVisible }) {
                                    Icon(
                                        imageVector = if (recoveryPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = null,
                                        tint = TextDarkSecondary,
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CleanSurfaceVariant,
                                unfocusedContainerColor = CleanSurfaceVariant,
                                focusedBorderColor = PrimaryAccent,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                focusedTextColor = TextDarkPrimary,
                                unfocusedTextColor = TextDarkPrimary,
                            ),
                            shape = Shapes.large,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recovery_new_password_input"),
                        )

                        // Confirm Password Input
                        OutlinedTextField(
                            value = recoveryConfirmPassword,
                            onValueChange = {
                                recoveryConfirmPassword = it
                                recoveryErrorMessage = null
                            },
                            label = { Text("Confirm New Password") },
                            singleLine = true,
                            visualTransformation = if (recoveryPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = CleanSurfaceVariant,
                                unfocusedContainerColor = CleanSurfaceVariant,
                                focusedBorderColor = PrimaryAccent,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                focusedTextColor = TextDarkPrimary,
                                unfocusedTextColor = TextDarkPrimary,
                            ),
                            shape = Shapes.large,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("recovery_confirm_password_input"),
                        )

                        if (recoveryErrorMessage != null) {
                            Surface(
                                shape = Shapes.medium,
                                color = HealthRiskRed.copy(alpha = 0.1f),
                                border = BorderStroke(1.dp, HealthRiskRed.copy(alpha = 0.2f)),
                            ) {
                                Text(
                                    text = recoveryErrorMessage ?: "",
                                    color = HealthRiskRed,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    modifier = Modifier.padding(10.dp),
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val cleanKey = recoveryKeyInput.replace("-", "").replace(" ", "").trim()
                            if (cleanKey.length != 24) {
                                recoveryErrorMessage = "Recovery key must be 24 characters long."
                                return@Button
                            }
                            val newPass = recoveryNewPassword.trim()
                            if (newPass.length < 8) {
                                recoveryErrorMessage = "New password must be at least 8 characters."
                                return@Button
                            }
                            if (newPass != recoveryConfirmPassword.trim()) {
                                recoveryErrorMessage = "Passwords do not match."
                                return@Button
                            }

                            isRecovering = true
                            recoveryErrorMessage = null
                            onRecoverWithKey(cleanKey, newPass) { res ->
                                isRecovering = false
                                if (res.isSuccess) {
                                    showRecoveryDialog = false
                                } else {
                                    recoveryErrorMessage = res.exceptionOrNull()?.message ?: "Recovery failed. Verify your recovery key."
                                }
                            }
                        },
                        enabled = !isRecovering,
                        modifier = Modifier.testTag("recovery_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                        shape = Shapes.large,
                    ) {
                        if (isRecovering) {
                            CircularProgressIndicator(
                                color = OnPrimaryAccent,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text("Recover & Unlock", color = OnPrimaryAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showRecoveryDialog = false },
                        enabled = !isRecovering,
                    ) {
                        Text("Cancel", color = TextDarkSecondary)
                    }
                },
                containerColor = CleanSurface,
                shape = Shapes.large,
            )
        }
    }
}
