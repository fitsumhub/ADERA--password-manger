package com.example.adere.presentation.screens
import com.example.ui.theme.Shapes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.core.utilities.HapticFeedbackHelper
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import com.example.adere.presentation.components.BrandIconHelper
import com.example.adere.presentation.components.VaultItemAvatar
import com.example.ui.theme.CategoryAppsYellow

/**
 * Dedicated Category Detail Screen displaying all saved credentials belonging to a category.
 *
 * Flow:
 * - Top shows: Category name, number of saved items, search input, and "+ Add" button.
 * - Immediately displays all saved items belonging to the category.
 * - Tapping an item opens its password/details securely in ItemDetailScreen.
 * - Tapping "+ Add" opens AddEditItemScreen with this category automatically pre-selected.
 * - Smart Empty State: "No passwords yet", "Add your first [Category Name] password", "[ + Add Password ]".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    category: VaultCategory,
    items: List<VaultItem>,
    onBackClick: () -> Unit,
    onItemClick: (String) -> Unit,
    onAddItemClick: (VaultCategory) -> Unit,
    onQuickCopy: (VaultItem) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    onLockClick: () -> Unit
) {
    val context = LocalContext.current
    val categoryColor = remember(category) { BrandIconHelper.getCategoryColor(category) }
    val categoryEmoji = remember(category) { BrandIconHelper.getCategoryEmoji(category) }

    var searchQuery by remember { mutableStateOf("") }

    val categoryItems = remember(items, category) {
        items.filter { it.category == category }
    }

    val displayedItems = remember(categoryItems, searchQuery) {
        if (searchQuery.isBlank()) {
            categoryItems
        } else {
            categoryItems.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.username.contains(searchQuery, ignoreCase = true) ||
                it.payload.url.contains(searchQuery, ignoreCase = true) ||
                it.payload.notes.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(Shapes.medium)
                                .background(categoryColor.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = categoryEmoji, fontSize = 18.sp)
                        }
                        Column {
                            Text(
                                text = category.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "${categoryItems.size} saved item${if (categoryItems.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.click(context)
                            onBackClick()
                        },
                        modifier = Modifier.testTag("category_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // + Add Quick Header Button
                    Button(
                        onClick = {
                            HapticFeedbackHelper.confirm(context)
                            onAddItemClick(category)
                        },
                        shape = Shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = categoryColor,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("category_top_add_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+ Add",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = {
                            HapticFeedbackHelper.click(context)
                            onLockClick()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock Vault",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (categoryItems.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = {
                        HapticFeedbackHelper.confirm(context)
                        onAddItemClick(category)
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add ${category.title}", fontWeight = FontWeight.Bold) },
                    containerColor = categoryColor,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("category_fab_add")
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Search & Filter Bar ──────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search in ${category.title}...",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = categoryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = Shapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = categoryColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("category_search_input")
                )
            }

            // ── Section Title & Counter ──────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (searchQuery.isNotBlank()) {
                            "Matching \"$searchQuery\" (${displayedItems.size})"
                        } else {
                            "Saved Entries (${categoryItems.size})"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        )
                    )

                    Surface(
                        shape = Shapes.medium,
                        color = categoryColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${category.title} ✓",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = categoryColor,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // ── Items List ───────────────────────────────────────────────────
            if (displayedItems.isNotEmpty()) {
                items(displayedItems, key = { it.id }) { item ->
                    CategoryItemCard(
                        item = item,
                        accentColor = categoryColor,
                        onClick = { onItemClick(item.id) },
                        onCopy = { onQuickCopy(item) },
                        onToggleFavorite = { onToggleFavorite(item.id, !item.isFavorite) }
                    )
                }
            } else if (categoryItems.isEmpty()) {
                // ── Smart Empty State ────────────────────────────────────────
                item {
                    SmartCategoryEmptyState(
                        category = category,
                        categoryEmoji = categoryEmoji,
                        categoryColor = categoryColor,
                        onAddPasswordClick = {
                            HapticFeedbackHelper.confirm(context)
                            onAddItemClick(category)
                        }
                    )
                }
            } else {
                // ── Search yielded no results ─────────────────────────────────
                item {
                    Surface(
modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = Shapes.medium,
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "No matching passwords found",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "No entries matching \"$searchQuery\" in ${category.title}.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            )
                            Button(
                                onClick = { searchQuery = "" },
                                shape = Shapes.medium,
                                colors = ButtonDefaults.buttonColors(containerColor = categoryColor)
                            ) {
                                Text("Clear Search")
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

/**
 * Smart empty state adhering to required UX:
 * Shows:
 * "No passwords yet"
 * "Add your first [Category Name] password"
 * [ + Add Password ]
 */
@Composable
private fun SmartCategoryEmptyState(
    category: VaultCategory,
    categoryEmoji: String,
    categoryColor: Color,
    onAddPasswordClick: () -> Unit
) {
    Surface(
modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp),
        shape = Shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = categoryEmoji, fontSize = 34.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "No passwords yet",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Add your first ${category.title} password",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onAddPasswordClick,
                shape = Shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = categoryColor,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
                modifier = Modifier.testTag("empty_add_password_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "+ Add Password",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

/**
 * Clean, secure card displaying a single credential item belonging to this category.
 * Never exposes plaintext secrets directly on this screen.
 */
@Composable
private fun CategoryItemCard(
    item: VaultItem,
    accentColor: Color,
    onClick: () -> Unit,
    onCopy: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Surface(
modifier = Modifier
            .fillMaxWidth()
            .clip(Shapes.medium)
            .clickable(onClick = onClick)
            .testTag("category_item_${item.id}"),
        shape = Shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            VaultItemAvatar(
                title = item.title,
                category = item.category,
                url = item.payload.url,
                size = 44.dp,
                iconSize = 22.dp,
                cornerRadius = 12.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.username.ifBlank {
                        item.payload.url.ifBlank { "Encrypted Secret" }
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Favorite star button
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Favorite",
                    tint = if (item.isFavorite) CategoryAppsYellow else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(19.dp)
                )
            }

            // Quick Copy Secret Button
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Secret",
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

