package com.example.adere.presentation.screens.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.adere.core.ads.AdMobManager
import com.example.adere.core.ads.AdSecurityZone
import com.example.adere.presentation.components.AdMobRewardedDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.utilities.AppIconManager
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.ui.theme.Shapes
import com.example.ui.theme.VaultThemePalette

@Composable
fun AppearanceSettingsScreen(
    currentPalette: VaultThemePalette,
    customAppName: String = "ADERA",
    selectedAppIcon: String = "default",
    onSelectThemePalette: (VaultThemePalette) -> Unit,
    onSetCustomAppName: (String) -> Unit = {},
    onSetSelectedAppIcon: (AppIconManager.AppIconPreset) -> Unit = {},
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Appearance & Identity",
                subtitle = "Customize app theme color palette & home screen launcher icon",
                onBackClick = onBackClick,
                trailingAction = {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Appearance settings applied!", Toast.LENGTH_SHORT).show()
                            onBackClick()
                        },
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Done",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Done", fontWeight = FontWeight.Bold)
                    }
                }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Active Identity Summary Banner ──────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = Shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val activePreset = AppIconManager.AppIconPreset.fromId(selectedAppIcon)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(Shapes.medium)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = activePreset.previewIconRes),
                            contentDescription = activePreset.title,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = customAppName.ifBlank { "ADERA" },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Theme: ${currentPalette.title} · Icon: ${activePreset.title}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Surface(
                        shape = Shapes.medium,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Active",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // ── Vault Theme Palette ────────────────────────────────────────────
            SettingsSectionTitle("Vault Color Palettes")

            VaultThemePalette.entries.forEach { palette ->
                ThemePaletteCard(
                    palette = palette,
                    isSelected = currentPalette == palette,
                    onSelect = {
                        onSelectThemePalette(palette)
                        Toast.makeText(context, "${palette.title} theme applied", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // ── Custom App Identity: Icon ──────────────────────────────────────
            SettingsSectionTitle("Home Screen App Icon")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = Shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "App Icon Selector",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Choose the icon ADERA uses on your home screen launcher.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppIconManager.AppIconPreset.entries.forEach { preset ->
                            val isSelected = selectedAppIcon.equals(preset.id, ignoreCase = true)
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(Shapes.medium)
                                    .clickable {
                                        onSetSelectedAppIcon(preset)
                                        Toast.makeText(context, "Launcher icon set to ${preset.title}", Toast.LENGTH_SHORT).show()
                                    },
                                shape = Shapes.medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(id = preset.previewIconRes),
                                        contentDescription = preset.title,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = preset.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isSelected) {
                                        Icon(
                                            Icons.Rounded.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val defaultPreset = AppIconManager.AppIconPreset.DEFAULT
                            onSetSelectedAppIcon(defaultPreset)
                            Toast.makeText(context, "App icon reset to Default ADERA", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Reset to ADERA Default Icon")
                    }
                }
            }

            // ── Optional Rewarded Support Card (Formats 3 & 4) ────────────────
            var showRewardedChoiceDialog by remember { mutableStateOf(false) }

            SettingsSectionTitle("Optional Cosmetic Support")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = Shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Preview Special Themes",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Optionally watch a short ad to preview cosmetic theme swatches. 100% optional.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedButton(
                        onClick = { showRewardedChoiceDialog = true }
                    ) {
                        Text("Watch Ad")
                    }
                }
            }

            if (showRewardedChoiceDialog) {
                AdMobRewardedDialog(
                    title = "Preview Cosmetic Themes?",
                    description = "Watch a short rewarded video ad to preview custom palette swatches? This is completely optional and does not affect your vault security.",
                    rewardTitle = "Theme Palette Preview",
                    onWatchAd = {
                        val activity = context as? android.app.Activity
                        if (activity != null) {
                            AdMobManager.getInstance(context).showRewardedAdIfAvailable(
                                activity = activity,
                                zone = AdSecurityZone.SAFE_ZONE,
                                onUserEarnedReward = {
                                    Toast.makeText(context, "Cosmetic theme preview unlocked!", Toast.LENGTH_LONG).show()
                                },
                                onAdDismissed = {}
                            )
                        }
                    },
                    onDismiss = { showRewardedChoiceDialog = false }
                )
            }

            // ── Prominent Done / Apply Action ─────────────────────────────────
            Button(
                onClick = {
                    Toast.makeText(context, "Appearance settings applied successfully!", Toast.LENGTH_SHORT).show()
                    onBackClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_done_appearance"),
                shape = Shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Done & Apply Settings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ThemePaletteCard(
    palette: VaultThemePalette,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val isLight = palette == VaultThemePalette.TOTAL_SECURITY
    val textPrimary = if (isLight) Color(0xFF1E293B) else Color.White
    val textSecondary = if (isLight) Color(0xFF64748B) else Color(0xFFCBD5E1)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes.medium)
            .clickable(onClick = onSelect)
            .then(
                if (isSelected)
                    Modifier.border(2.dp, palette.primaryColor, Shapes.medium)
                else
                    Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), Shapes.medium)
            )
            .testTag("theme_card_${palette.name.lowercase()}"),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = palette.surfaceColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color swatch circle
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(palette.primaryColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = palette.surfaceColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Text(
                            text = palette.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = textPrimary
                            )
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Active",
                                tint = palette.primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = palette.subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    )
                }

                if (isSelected) {
                    Surface(
                        shape = Shapes.medium,
                        color = palette.primaryColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Active",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = palette.primaryColor
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Swatch Row Preview
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ColorDot(palette.primaryColor)
                ColorDot(palette.surfaceColor, border = true)
                ColorDot(palette.bgColor, border = true)
            }
        }
    }
}

@Composable
private fun ColorDot(color: Color, border: Boolean = false) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(color)
            .then(
                if (border) Modifier.border(1.dp, Color.Gray.copy(alpha = 0.4f), CircleShape)
                else Modifier
            )
    )
}
