package com.example.adere.presentation.screens
import com.example.ui.theme.Shapes

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.crypto.PasswordGenerator
import com.example.adere.presentation.components.MasterRecoveryKeyCard
import com.example.adere.presentation.components.PasswordStrengthBar
import com.example.adere.presentation.components.TotalSecurityHeroIllustration
import com.example.ui.theme.CategoryAppsYellow
import com.example.ui.theme.CleanBorder
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.TotalSecurityPrimary
import com.example.ui.theme.TotalSecurityPrimaryContainer
import com.example.ui.theme.TotalSecurityPrimaryLight

private val CleanBg: Color
    @Composable get() = MaterialTheme.colorScheme.background
private val CleanSurface: Color
    @Composable get() = MaterialTheme.colorScheme.surface
private val CleanSurfaceVariant: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceVariant
private val TextDarkMuted: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
private val TextDarkPrimary: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface
private val TextDarkSecondary: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

private data class OnboardingSlideData(
    val categoryBadge: String,
    val title: String,
    val description: String,
    val chips: List<String>
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingAndSetupScreen(
    onSetupSuccess: () -> Unit,
    onSetupVault: (password: String, enableBiometrics: Boolean, (Result<Unit>) -> Unit) -> Unit,
    onGetActiveRecoveryKey: () -> Result<String> = { Result.failure(IllegalStateException()) }
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val hapticFeedback = LocalHapticFeedback.current

    var showSetupForm by remember { mutableStateOf(false) }
    var currentSlide by remember { mutableIntStateOf(0) }

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }
    var enableBiometrics by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var activeRecoveryKey by remember { mutableStateOf<String?>(null) }
    var userConfirmedSavedKey by remember { mutableStateOf(false) }

    val entropy = remember(password) { PasswordGenerator.calculateEntropy(password) }
    val strength = remember(password, entropy) { PasswordGenerator.evaluateStrength(entropy, password.length) }

    val scrollState = rememberScrollState()

    val slides = remember {
        listOf(
            OnboardingSlideData(
                categoryBadge = "MILITARY-GRADE VAULT",
                title = "Total Security &\nZero-Knowledge",
                description = "Secure all your passwords, cards, and crypto keys with hardware-backed AES-256-GCM encryption. Completely offline and private.",
                chips = listOf("🔒 AES-256-GCM", "🛡️ 100% Offline", "⚡ Zero Knowledge")
            ),
            OnboardingSlideData(
                categoryBadge = "HARDWARE BIOMETRICS",
                title = "Instant Fingerprint\n& Face Unlock",
                description = "Unlock your vault in milliseconds with hardware biometric sensors and Android Keystore protection. Plaintext keys are never stored on disk.",
                chips = listOf("👆 Fingerprint Auth", "👤 Face Unlock", "🔑 Keystore Backed")
            ),
            OnboardingSlideData(
                categoryBadge = "ADVANCED AUDITING",
                title = "Security Health\n& Built-In 2FA",
                description = "Detect weak and reused passwords instantly. Real-time password strength evaluation and offline two-factor TOTP codes.",
                chips = listOf("📊 Health Audit", "⏱️ TOTP Codes", "🛡️ Real-Time Audit")
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CleanBg)
    ) {
        AnimatedContent(
            targetState = showSetupForm,
            transitionSpec = {
                if (targetState) {
                    slideInHorizontally(initialOffsetX = { it }) + fadeIn() togetherWith
                            slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
                } else {
                    slideInHorizontally(initialOffsetX = { -it }) + fadeIn() togetherWith
                            slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                }
            },
            label = "setup_form_anim"
        ) { isFormVisible ->
            if (!isFormVisible) {
                // Onboarding Carousel View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Top App Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = Shapes.medium,
                            color = TotalSecurityPrimaryContainer,
                            border = BorderStroke(1.dp, TotalSecurityPrimary.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = TotalSecurityPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ADERE · አደራ",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TotalSecurityPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.6.sp,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        TextButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                showSetupForm = true
                            },
                            modifier = Modifier.testTag("onboarding_skip_button")
                        ) {
                            Text(
                                text = "Skip",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextDarkSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Animated Hero Illustration
                    TotalSecurityHeroIllustration(slideIndex = currentSlide)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Dynamic Slide Content
                    AnimatedContent(
                        targetState = currentSlide,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn() togetherWith
                                        slideOutHorizontally(targetOffsetX = { -it / 2 }) + fadeOut()
                            } else {
                                slideInHorizontally(initialOffsetX = { -it / 2 }) + fadeIn() togetherWith
                                        slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut()
                            }
                        },
                        label = "slide_content_anim"
                    ) { index ->
                        val slide = slides[index]
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Category Tag
                            Surface(
                                shape = Shapes.medium,
                                color = TotalSecurityPrimaryContainer
                            ) {
                                Text(
                                    text = slide.categoryBadge,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TotalSecurityPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.5.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Title
                            Text(
                                text = slide.title,
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextDarkPrimary,
                                    fontSize = 26.sp,
                                    lineHeight = 34.sp,
                                    letterSpacing = (-0.5).sp
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Subtitle description
                            Text(
                                text = slide.description,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextDarkSecondary,
                                    lineHeight = 22.sp,
                                    fontSize = 13.5.sp
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Feature chips
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                slide.chips.forEach { chipText ->
                                    Surface(
                                        shape = Shapes.medium,
                                        color = CleanSurface,
                                        border = BorderStroke(1.dp, CleanBorder),
                                        shadowElevation = 1.dp
                                    ) {
                                        Text(
                                            text = chipText,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = TextDarkPrimary,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.5.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // Animated Pill Page Indicator Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        slides.indices.forEach { index ->
                            val isSelected = currentSlide == index
                            val targetWidth by animateDpAsState(
                                targetValue = if (isSelected) 30.dp else 8.dp,
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                label = "dot_width"
                            )
                            val targetColor by animateColorAsState(
                                targetValue = if (isSelected) TotalSecurityPrimary else Color(0xFFD1D5DB),
                                animationSpec = tween(300),
                                label = "dot_color"
                            )

                            Box(
                                modifier = Modifier
                                    .size(width = targetWidth, height = 8.dp)
                                    .clip(Shapes.medium)
                                    .background(targetColor)
                                    .clickable {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        currentSlide = index
                                    }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Primary Action Button (Continue or Get Started)
                    Button(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (currentSlide < slides.size - 1) {
                                currentSlide++
                            } else {
                                showSetupForm = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(8.dp, Shapes.medium, spotColor = TotalSecurityPrimary)
                            .testTag("onboarding_get_started_button"),
                        shape = Shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TotalSecurityPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (currentSlide == slides.size - 1) "Create Secure Vault" else "Continue",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = if (currentSlide == slides.size - 1) Icons.Default.Lock else Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secondary existing vault restore link
                    Text(
                        text = "Have an existing vault backup? Restore",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextDarkSecondary,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier
                            .clickable {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                showSetupForm = true
                            }
                            .padding(8.dp)
                            .testTag("onboarding_existing_account_button")
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            } else {
                // Master Vault Creation Setup View
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    // Top Back & Title Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                showSetupForm = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextDarkPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Create Master Key",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextDarkPrimary,
                                    fontSize = 22.sp
                                )
                            )
                            Text(
                                text = "Your single key to encrypt the entire local vault",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextDarkSecondary, fontSize = 12.5.sp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Main Setup Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.08f)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CleanSurface),
                        border = BorderStroke(1.dp, CleanBorder)
                    ) {
                        Column(modifier = Modifier.padding(22.dp)) {
                            // Header badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(TotalSecurityPrimaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = TotalSecurityPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Master Password Setup",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextDarkPrimary,
                                                fontSize = 16.sp
                                            )
                                        )
                                        Text(
                                            text = "100k PBKDF2 · AES-256-GCM",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = EmeraldPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = TotalSecurityPrimaryContainer,
                                    border = BorderStroke(1.dp, TotalSecurityPrimary.copy(alpha = 0.25f))
                                ) {
                                    Text(
                                        text = "ZERO KNOWLEDGE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = TotalSecurityPrimary,
                                            fontSize = 9.5.sp,
                                            letterSpacing = 0.5.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Password Text Field
                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    errorMessage = null
                                },
                                label = { Text("Master Password") },
                                placeholder = { Text("At least 8 characters") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (password.isNotEmpty()) TotalSecurityPrimary else TextDarkMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                singleLine = true,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = null,
                                            tint = TextDarkSecondary
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = CleanSurfaceVariant,
                                    unfocusedContainerColor = CleanSurfaceVariant,
                                    focusedBorderColor = TotalSecurityPrimary,
                                    unfocusedBorderColor = CleanBorder,
                                    focusedTextColor = TextDarkPrimary,
                                    unfocusedTextColor = TextDarkPrimary
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("setup_master_password_input")
                            )

                            // Password Strength Bar
                            if (password.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                PasswordStrengthBar(strength = strength, entropyBits = entropy)

                                // Checklist chips wrapped dynamically
                                Spacer(modifier = Modifier.height(10.dp))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val is8Chars = password.length >= 8
                                    val hasUpper = password.any { it.isUpperCase() }
                                    val hasDigit = password.any { it.isDigit() }
                                    val hasSymbol = password.any { !it.isLetterOrDigit() }

                                    StrengthCheckChip(label = "8+ Chars", satisfied = is8Chars)
                                    StrengthCheckChip(label = "Uppercase", satisfied = hasUpper)
                                    StrengthCheckChip(label = "Numbers", satisfied = hasDigit)
                                    StrengthCheckChip(label = "Symbol", satisfied = hasSymbol)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Confirm Password Field
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                    errorMessage = null
                                },
                                label = { Text("Confirm Master Password") },
                                placeholder = { Text("Re-enter master password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (confirmPassword.isNotEmpty() && password == confirmPassword) EmeraldPrimary else TextDarkMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                singleLine = true,
                                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                        Icon(
                                            imageVector = if (confirmVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = null,
                                            tint = TextDarkSecondary
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = CleanSurfaceVariant,
                                    unfocusedContainerColor = CleanSurfaceVariant,
                                    focusedBorderColor = if (confirmPassword.isNotEmpty() && password == confirmPassword) EmeraldPrimary else TotalSecurityPrimary,
                                    unfocusedBorderColor = CleanBorder,
                                    focusedTextColor = TextDarkPrimary,
                                    unfocusedTextColor = TextDarkPrimary
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("setup_confirm_password_input")
                            )

                            // Match Indicator
                            if (confirmPassword.isNotEmpty()) {
                                val match = password == confirmPassword
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (match) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (match) EmeraldPrimary else HealthRiskRed,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (match) "Passwords match" else "Passwords do not match",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (match) EmeraldPrimary else HealthRiskRed,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Zero-Knowledge Security Notice Callout
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = CategoryAppsYellow.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, CategoryAppsYellow.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = TotalSecurityPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Important: ADERE is zero-knowledge. Your Master Password cannot be recovered if forgotten without your Master Recovery Key.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextDarkPrimary,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Biometrics Toggle Card
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = CleanSurfaceVariant,
                                border = BorderStroke(1.dp, CleanBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { enableBiometrics = !enableBiometrics }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(TotalSecurityPrimaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Fingerprint,
                                            contentDescription = null,
                                            tint = TotalSecurityPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Biometric Unlock",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TextDarkPrimary,
                                                fontSize = 14.5.sp
                                            )
                                        )
                                        Text(
                                            text = "Unlock with fingerprint or face recognition",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = TextDarkSecondary,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                    Switch(
                                        checked = enableBiometrics,
                                        onCheckedChange = { enableBiometrics = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = TotalSecurityPrimary
                                        )
                                    )
                                }
                            }

                            // Error Message
                            if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = HealthRiskRed.copy(alpha = 0.08f),
                                    border = BorderStroke(1.dp, HealthRiskRed.copy(alpha = 0.2f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = HealthRiskRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = errorMessage ?: "",
                                            color = HealthRiskRed,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Submit Button
                            Button(
                                onClick = {
                                    val trimmed = password.trim()
                                    if (trimmed.length < 8) {
                                        errorMessage = "Master password must be at least 8 characters."
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        return@Button
                                    }
                                    if (trimmed != confirmPassword.trim()) {
                                        errorMessage = "Passwords do not match."
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        return@Button
                                    }
                                    isProcessing = true
                                    errorMessage = null
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onSetupVault(trimmed, enableBiometrics) { result ->
                                        isProcessing = false
                                        if (result.isSuccess) {
                                            val keyRes = onGetActiveRecoveryKey()
                                            if (keyRes.isSuccess && !keyRes.getOrNull().isNullOrBlank()) {
                                                activeRecoveryKey = keyRes.getOrNull()
                                            } else {
                                                onSetupSuccess()
                                            }
                                        } else {
                                            errorMessage = result.exceptionOrNull()?.message ?: "Failed to initialize vault."
                                        }
                                    }
                                },
                                enabled = !isProcessing,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = TotalSecurityPrimary)
                                    .testTag("setup_initialize_button"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TotalSecurityPrimary,
                                    contentColor = Color.White
                                )
                            ) {
                                if (isProcessing) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Encrypt & Open Vault",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Master Recovery Key Dialog
        if (activeRecoveryKey != null) {
            AlertDialog(
                onDismissRequest = { /* Must explicitly acknowledge key */ },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(TotalSecurityPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = TotalSecurityPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = "Save Master Recovery Key",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextDarkPrimary
                        ),
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Save this 24-character key in a safe physical place. If you ever forget your Master Password, this key is the ONLY way to regain access to your encrypted vault.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDarkSecondary,
                                lineHeight = 18.sp
                            )
                        )

                        // Master Recovery Key Chunked Grid Card
                        MasterRecoveryKeyCard(
                            recoveryKey = activeRecoveryKey ?: "",
                            onCopyKey = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                clipboardManager.setText(AnnotatedString(activeRecoveryKey ?: ""))
                                Toast.makeText(context, "Recovery key copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        )

                        // Confirmation checkbox
                        Surface(
                            shape = Shapes.medium,
                            color = if (userConfirmedSavedKey) TotalSecurityPrimaryContainer.copy(alpha = 0.5f) else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    userConfirmedSavedKey = !userConfirmedSavedKey
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Checkbox(
                                    checked = userConfirmedSavedKey,
                                    onCheckedChange = { userConfirmedSavedKey = it },
                                    colors = CheckboxDefaults.colors(checkedColor = TotalSecurityPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "I have written down or safely saved this recovery key",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextDarkPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            activeRecoveryKey = null
                            onSetupSuccess()
                        },
                        enabled = userConfirmedSavedKey,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("setup_finish_vault_button"),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TotalSecurityPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Continue to Vault", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = CleanSurface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun StrengthCheckChip(
    label: String,
    satisfied: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = Shapes.medium,
        color = if (satisfied) EmeraldPrimary.copy(alpha = 0.12f) else CleanSurfaceVariant,
        border = BorderStroke(
            1.dp,
            if (satisfied) EmeraldPrimary.copy(alpha = 0.35f) else CleanBorder
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (satisfied) Icons.Default.Check else Icons.Default.Lock,
                contentDescription = null,
                tint = if (satisfied) EmeraldPrimary else TextDarkMuted,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = if (satisfied) FontWeight.Bold else FontWeight.Normal,
                    color = if (satisfied) EmeraldPrimary else TextDarkMuted
                )
            )
        }
    }
}
