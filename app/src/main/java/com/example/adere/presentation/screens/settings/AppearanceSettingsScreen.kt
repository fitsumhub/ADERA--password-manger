package com.example.adere.presentation.screens.settings
import com.example.ui.theme.Shapes

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.presentation.screens.settings.components.SettingsSectionTitle
import com.example.adere.presentation.screens.settings.components.SettingsSubHeader
import com.example.ui.theme.VaultThemePalette

@Composable
fun AppearanceSettingsScreen(
    currentPalette: VaultThemePalette,
    onSelectThemePalette: (VaultThemePalette) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            SettingsSubHeader(
                title = "Appearance & Themes",
                subtitle = "Choose your vault's color palette & visual atmosphere",
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

            Spacer(modifier = Modifier.height(80.dp))
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
