package com.example.adere.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MicroInteractionsDemo(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = "Micro-Interactions Showcase",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        
        // Copy Actions Demo
        item {
            DemoSection(
                title = "Copy Actions",
                description = "Interactive copy buttons with visual feedback"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnimatedCopyButton(
                            onCopy = { },
                            label = "Copy Password",
                            successMessage = "Password copied!"
                        )
                        Text(
                            text = "Copy Button",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VaultItemCopyButton(
                            value = "example@email.com",
                            label = "Email",
                            onCopy = { }
                        )
                        Text(
                            text = "Vault Copy",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        
        // Interactive Elements Demo
        item {
            var isFavorite by remember { mutableStateOf(false) }
            var isVisible by remember { mutableStateOf(false) }
            
            DemoSection(
                title = "Interactive Elements",
                description = "Animated buttons with micro-feedback"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnimatedFavoriteButton(
                            isFavorite = isFavorite,
                            onToggle = { isFavorite = !isFavorite }
                        )
                        Text(
                            text = "Favorite",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnimatedVisibilityToggle(
                            isVisible = isVisible,
                            onToggle = { isVisible = !isVisible }
                        )
                        Text(
                            text = "Visibility",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RotatingIconButton(
                            onClick = { },
                            icon = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                        Text(
                            text = "Rotating",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        
        // Bounce Buttons Demo
        item {
            DemoSection(
                title = "Bounce Buttons",
                description = "Satisfying bounce animation on interaction"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BounceButton(
                        onClick = { },
                        icon = Icons.Default.Add,
                        label = "Add Item",
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                    
                    BounceButton(
                        onClick = { },
                        icon = Icons.Default.Edit,
                        label = "Edit",
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        // Pressable Cards Demo
        item {
            DemoSection(
                title = "Pressable Cards",
                description = "Cards with satisfying press feedback"
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PressableCard(
                        onClick = { }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            EnhancedCategoryIcon(
                                category = com.example.adere.domain.model.VaultCategory.SOCIAL,
                                modifier = Modifier.size(40.dp)
                            )
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Social Account",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "user@example.com",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            
                            AnimatedCopyButton(
                                onCopy = { },
                                showLabel = false
                            )
                        }
                    }
                }
            }
        }
        
        // Quick Action Grid Demo
        item {
            val quickActions = remember {
                listOf(
                    QuickAction(
                        icon = Icons.Default.Person,
                        label = "Social",
                        color = Color(0xFF5E5CE6)
                    ) { },
                    QuickAction(
                        icon = Icons.Default.CreditCard,
                        label = "Cards",
                        color = Color(0xFF48C9B0)
                    ) { },
                    QuickAction(
                        icon = Icons.Default.Smartphone,
                        label = "Apps",
                        color = Color(0xFFF5BA31)
                    ) { },
                    QuickAction(
                        icon = Icons.Default.Security,
                        label = "Security",
                        color = Color(0xFFEF4444)
                    ) { }
                )
            }
            
            DemoSection(
                title = "Quick Action Grid",
                description = "Interactive grid with haptic feedback"
            ) {
                QuickActionGrid(
                    actions = quickActions,
                    columns = 2
                )
            }
        }
        
        // Swipe Actions Demo
        item {
            DemoSection(
                title = "Swipe Reveal Actions",
                description = "Action buttons revealed on swipe"
            ) {
                SwipeRevealActions(
                    onEdit = { },
                    onDelete = { },
                    onShare = { }
                )
            }
        }
        
        // Pulsing Indicators Demo
        item {
            var isActive1 by remember { mutableStateOf(true) }
            var isActive2 by remember { mutableStateOf(false) }
            
            DemoSection(
                title = "Pulsing Indicators",
                description = "Animated status indicators"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PulsingIndicator(
                                isActive = isActive1,
                                color = Color(0xFF10B981)
                            )
                            Text(
                                text = "Online",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        
                        BounceButton(
                            onClick = { isActive1 = !isActive1 },
                            icon = if (isActive1) Icons.Default.Refresh else Icons.Default.Add,
                            label = if (isActive1) "Stop" else "Start",
                            color = if (isActive1) Color(0xFFEF4444) else Color(0xFF10B981)
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PulsingIndicator(
                                isActive = isActive2,
                                color = Color(0xFFF59E0B)
                            )
                            Text(
                                text = "Syncing",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        
                        BounceButton(
                            onClick = { isActive2 = !isActive2 },
                            icon = if (isActive2) Icons.Default.Refresh else Icons.Default.Add,
                            label = if (isActive2) "Stop" else "Start",
                            color = if (isActive2) Color(0xFFEF4444) else Color(0xFFF59E0B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DemoSection(
    title: String,
    description: String,
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            
            content()
        }
    }
}