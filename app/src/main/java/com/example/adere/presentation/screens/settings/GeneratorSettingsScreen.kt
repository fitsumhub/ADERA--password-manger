package com.example.adere.presentation.screens.settings

import android.widget.Toast
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.crypto.PasswordGenerator
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.adere.presentation.screens.settings.components.SettingsSwitchItem
import com.example.ui.theme.HealthRiskRed
import com.example.ui.theme.SecurityOrange

@Composable
fun GeneratorSettingsScreen(
    generatorOptions: PasswordGenerator.GeneratorOptions,
    onUpdateGeneratorOptions: (PasswordGenerator.GeneratorOptions) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val primary = MaterialTheme.colorScheme.primary

    val sample = remember(generatorOptions) { PasswordGenerator.generate(generatorOptions) }
    val strengthColor = when (sample.strength) {
        PasswordGenerator.PasswordStrength.VERY_WEAK -> HealthRiskRed
        PasswordGenerator.PasswordStrength.WEAK      -> HealthRiskRed
        PasswordGenerator.PasswordStrength.MODERATE  -> Color(0xFFF59E0B)
        PasswordGenerator.PasswordStrength.STRONG    -> Color(0xFF10B981)
        PasswordGenerator.PasswordStrength.VERY_STRONG -> Color(0xFF10B981)
    }

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Password Generator",
                subtitle = "Default length, character sets & readability rules",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Live preview ──────────────────────────────────────────────────
            SettingsSectionTitle("Live Preview")

            Card(
                modifier = Modifier.fillMaxWidth().testTag("generator_sample_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = null,
                                    tint = primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Sample Password",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = strengthColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${sample.strength.label} · ${sample.entropyBits.toInt()} bits",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = strengthColor
                                ),
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Strength bar
                    val strengthFraction = when (sample.strength) {
                        PasswordGenerator.PasswordStrength.VERY_WEAK   -> 0.15f
                        PasswordGenerator.PasswordStrength.WEAK        -> 0.35f
                        PasswordGenerator.PasswordStrength.MODERATE    -> 0.60f
                        PasswordGenerator.PasswordStrength.STRONG      -> 0.80f
                        PasswordGenerator.PasswordStrength.VERY_STRONG -> 1.00f
                    }
                    androidx.compose.material3.LinearProgressIndicator(
                        progress = { strengthFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = strengthColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )

                    Text(
                        text = sample.password,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            // ── Length slider ─────────────────────────────────────────────────
            SettingsSectionTitle("Default Length")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Length",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                "${generatorOptions.length} characters",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = primary,
                                    fontSize = 13.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    var sliderVal by remember(generatorOptions.length) {
                        mutableFloatStateOf(generatorOptions.length.toFloat())
                    }

                    Slider(
                        value = sliderVal,
                        onValueChange = { sliderVal = it },
                        onValueChangeFinished = {
                            onUpdateGeneratorOptions(generatorOptions.copy(length = sliderVal.toInt()))
                        },
                        valueRange = 8f..64f,
                        steps = 55,
                        colors = SliderDefaults.colors(
                            thumbColor = primary,
                            activeTrackColor = primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.testTag("generator_length_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("8", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        Text("64", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }
                }
            }

            // ── Character sets ────────────────────────────────────────────────
            SettingsSectionTitle("Character Sets")

            SettingsSwitchItem(
                icon = Icons.Default.TextFields,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Uppercase (A–Z)",
                subtitle = "Include capital letters",
                checked = generatorOptions.includeUppercase,
                onCheckedChange = { checked ->
                    guard(generatorOptions, newUppercase = checked, onUpdate = onUpdateGeneratorOptions) {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                },
                testTag = "gen_switch_uppercase"
            )

            SettingsSwitchItem(
                icon = Icons.Default.Abc,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Lowercase (a–z)",
                subtitle = "Include small letters",
                checked = generatorOptions.includeLowercase,
                onCheckedChange = { checked ->
                    guard(generatorOptions, newLowercase = checked, onUpdate = onUpdateGeneratorOptions) {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                },
                testTag = "gen_switch_lowercase"
            )

            SettingsSwitchItem(
                icon = Icons.Default.Numbers,
                iconTint = Color(0xFF10B981),
                iconBg = Color(0xFF10B981).copy(alpha = 0.1f),
                title = "Digits (0–9)",
                subtitle = "Include numerical digits",
                checked = generatorOptions.includeDigits,
                onCheckedChange = { checked ->
                    guard(generatorOptions, newDigits = checked, onUpdate = onUpdateGeneratorOptions) {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                },
                testTag = "gen_switch_digits"
            )

            SettingsSwitchItem(
                icon = Icons.Default.Tag,
                iconTint = SecurityOrange,
                iconBg = SecurityOrange.copy(alpha = 0.1f),
                title = "Symbols (!@#\$%^&*)",
                subtitle = "Include punctuation and special characters",
                checked = generatorOptions.includeSymbols,
                onCheckedChange = { checked ->
                    guard(generatorOptions, newSymbols = checked, onUpdate = onUpdateGeneratorOptions) {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                },
                testTag = "gen_switch_symbols"
            )

            // ── Readability ───────────────────────────────────────────────────
            SettingsSectionTitle("Readability")

            SettingsSwitchItem(
                icon = Icons.Default.Psychology,
                iconTint = primary,
                iconBg = primary.copy(alpha = 0.1f),
                title = "Exclude Ambiguous Characters",
                subtitle = "Omit visually similar chars: 0, O, I, 1, l",
                checked = generatorOptions.excludeAmbiguous,
                onCheckedChange = {
                    onUpdateGeneratorOptions(generatorOptions.copy(excludeAmbiguous = it))
                },
                testTag = "gen_switch_ambiguous"
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

private fun guard(
    opts: PasswordGenerator.GeneratorOptions,
    newUppercase: Boolean = opts.includeUppercase,
    newLowercase: Boolean = opts.includeLowercase,
    newDigits: Boolean = opts.includeDigits,
    newSymbols: Boolean = opts.includeSymbols,
    onUpdate: (PasswordGenerator.GeneratorOptions) -> Unit,
    onError: (String) -> Unit
) {
    if (!newUppercase && !newLowercase && !newDigits && !newSymbols) {
        onError("At least one character type must remain enabled.")
        return
    }
    onUpdate(opts.copy(includeUppercase = newUppercase, includeLowercase = newLowercase, includeDigits = newDigits, includeSymbols = newSymbols))
}
