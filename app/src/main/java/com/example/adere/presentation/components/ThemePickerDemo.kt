package com.example.adere.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.presentation.theme.AccessibilityPreferences
import com.example.adere.presentation.theme.ColorBlindnessType
import com.example.adere.presentation.theme.ContrastLevel
import com.example.adere.presentation.theme.EnhancedAdereTheme
import com.example.adere.presentation.theme.LocalEnhancedTheme
import com.example.ui.theme.VaultThemePalette

@Composable
fun ThemePickerDemo(
    modifier: Modifier = Modifier
) {
    var selectedPalette by remember { mutableStateOf(VaultThemePalette.TOTAL_SECURITY) }
    var isDarkTheme by remember { mutableStateOf(false) }
    var contrastLevel by remember { mutableStateOf(ContrastLevel.STANDARD) }
    var colorBlindnessType by remember { mutableStateOf(ColorBlindnessType.NONE) }
    var enhanceFocus by remember { mutableStateOf(false) }
    var animateTransitions by remember { mutableStateOf(true) }
    
    val accessibilityPrefs = AccessibilityPreferences(
        contrastLevel = contrastLevel,
        colorBlindnessType = colorBlindnessType,
        enhanceFocus = enhanceFocus
    )

    EnhancedAdereTheme(
        palette = selectedPalette,
        accessibilityPrefs = accessibilityPrefs,
        isDarkTheme = isDarkTheme,
        animateTransitions = animateTransitions
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Text(
                    text = "Enhanced Theme System",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            // Theme Palette Selector
            item {
                ThemeSection(
                    title = "Theme Palette",
                    icon = Icons.Default.Palette
                ) {
                    ThemePaletteGrid(
                        selectedPalette = selectedPalette,
                        onPaletteSelected = { selectedPalette = it }
                    )
                }
            }
            
            // Basic Theme Settings
            item {
                ThemeSection(
                    title = "Basic Settings",
                    icon = Icons.Default.Contrast
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SettingRow(
                            label = "Dark Theme",
                            description = "Switch between light and dark modes"
                        ) {
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { isDarkTheme = it }
                            )
                        }
                        
                        SettingRow(
                            label = "Animate Transitions",
                            description = "Smooth color transitions when changing themes"
                        ) {
                            Switch(
                                checked = animateTransitions,
                                onCheckedChange = { animateTransitions = it }
                            )
                        }
                    }
                }
            }
            
            // Accessibility Settings
            item {
                ThemeSection(
                    title = "Accessibility",
                    icon = Icons.Default.Visibility
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ContrastLevelSelector(
                            selectedLevel = contrastLevel,
                            onLevelSelected = { contrastLevel = it }
                        )
                        
                        ColorBlindnessSelector(
                            selectedType = colorBlindnessType,
                            onTypeSelected = { colorBlindnessType = it }
                        )
                        
                        SettingRow(
                            label = "Enhanced Focus",
                            description = "Stronger focus indicators for better accessibility"
                        ) {
                            Switch(
                                checked = enhanceFocus,
                                onCheckedChange = { enhanceFocus = it }
                            )
                        }
                    }
                }
            }
            
            // Theme Preview
            item {
                ThemeSection(
                    title = "Preview",
                    icon = Icons.Default.Visibility
                ) {
                    ThemePreviewGrid()
                }
            }
        }
    }
}

@Composable
private fun ThemeSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))
            
            content()
        }
    }
}

@Composable
private fun ThemePaletteGrid(
    selectedPalette: VaultThemePalette,
    onPaletteSelected: (VaultThemePalette) -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        VaultThemePalette.values().forEach { palette ->
            ThemePaletteCard(
                palette = palette,
                isSelected = palette == selectedPalette,
                onClick = {
                    hapticFeedback.performHapticFeedback(
                        androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                    )
                    onPaletteSelected(palette)
                }
            )
        }
    }
}

@Composable
private fun ThemePaletteCard(
    palette: VaultThemePalette,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        },
        animationSpec = tween(300),
        label = "border_color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Color preview circles
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(palette.primaryColor)
                )
                
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(palette.surfaceColor)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                )
                
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(palette.bgColor)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = palette.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                
                Text(
                    text = palette.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ContrastLevelSelector(
    selectedLevel: ContrastLevel,
    onLevelSelected: (ContrastLevel) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Contrast Level",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ContrastLevel.values().forEach { level ->
                ContrastChip(
                    label = level.name.lowercase().replaceFirstChar { it.uppercase() },
                    isSelected = level == selectedLevel,
                    onClick = { onLevelSelected(level) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ColorBlindnessSelector(
    selectedType: ColorBlindnessType,
    onTypeSelected: (ColorBlindnessType) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Color Vision",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ColorBlindnessType.values().forEach { type ->
                val label = when (type) {
                    ColorBlindnessType.NONE -> "Normal Vision"
                    ColorBlindnessType.PROTANOPIA -> "Protanopia (Red-blind)"
                    ColorBlindnessType.DEUTERANOPIA -> "Deuteranopia (Green-blind)"
                    ColorBlindnessType.TRITANOPIA -> "Tritanopia (Blue-blind)"
                    ColorBlindnessType.MONOCHROMACY -> "Monochromacy"
                }
                
                ContrastChip(
                    label = label,
                    isSelected = type == selectedType,
                    onClick = { onTypeSelected(type) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun ContrastChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(200),
        label = "chip_background"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(200),
        label = "chip_text"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = textColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}

@Composable
private fun SettingRow(
    label: String,
    description: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        trailing()
    }
}

@Composable
private fun ThemePreviewGrid() {
    val enhancedTheme = LocalEnhancedTheme.current
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Color palette preview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColorPreviewCard("Primary", enhancedTheme.primary, Modifier.weight(1f))
            ColorPreviewCard("Secondary", enhancedTheme.secondary, Modifier.weight(1f))
            ColorPreviewCard("Success", enhancedTheme.success, Modifier.weight(1f))
            ColorPreviewCard("Error", enhancedTheme.error, Modifier.weight(1f))
        }
        
        // Component preview
        EnhancedVaultItemCard(
            item = createSampleVaultItem(),
            onClick = { },
            onCopyClick = { },
            onFavoriteClick = { }
        )
    }
}

@Composable
private fun ColorPreviewCard(
    name: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
        )
        
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 10.sp
            )
        )
    }
}

private fun createSampleVaultItem() = com.example.adere.domain.model.VaultItem(
    id = "preview",
    title = "Sample Account",
    username = "user@example.com",
    category = com.example.adere.domain.model.VaultCategory.SOCIAL,
    payload = com.example.adere.domain.model.VaultItemPayload(
        password = "SecurePassword123!",
        url = "https://example.com"
    ),
    isFavorite = true,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis(),
    passwordLastChanged = System.currentTimeMillis()
)
