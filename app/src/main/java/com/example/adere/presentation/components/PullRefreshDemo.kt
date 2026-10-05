package com.example.adere.presentation.components
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.adere.domain.model.VaultCategory
import com.example.adere.domain.model.VaultItem
import com.example.adere.domain.model.VaultItemPayload
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun PullRefreshDemo(
    modifier: Modifier = Modifier
) {
    var isRefreshing by remember { mutableStateOf(false) }
    var items by remember { mutableStateOf(generateSampleItems()) }
    val coroutineScope = rememberCoroutineScope()

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
                .padding(20.dp)
        ) {
            Text(
                text = "Pull to Refresh Demo",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
            
            Text(
                text = "Pull down to refresh the vault items",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Button(
                onClick = {
                    coroutineScope.launch {
                        isRefreshing = true
                        delay(2000) // Simulate network call
                        items = generateSampleItems()
                        isRefreshing = false
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Trigger Refresh Manually")
            }
        }
    }
    
    VaultPullRefresh(
        isRefreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                isRefreshing = true
                delay(2000) // Simulate network call
                items = generateSampleItems()
                isRefreshing = false
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.id }) { item ->
                EnhancedVaultItemCard(
                    item = item,
                    onClick = { },
                    onCopyClick = { },
                    onFavoriteClick = { }
                )
            }
        }
    }
}

private fun generateSampleItems(): List<VaultItem> {
    val categories = listOf(
        VaultCategory.SOCIAL to "Social Account",
        VaultCategory.WEBSITE to "Website",
        VaultCategory.BANKING to "Banking Card",
        VaultCategory.NOTES to "Secure Note"
    )
    
    return categories.mapIndexed { index, (category, prefix) ->
        VaultItem(
            id = UUID.randomUUID().toString(),
            title = "$prefix ${index + 1}",
            username = "user${index + 1}@example.com",
            category = category,
            payload = VaultItemPayload(
                password = "SecurePassword${index + 1}!",
                url = "https://example${index + 1}.com"
            ),
            isFavorite = index % 3 == 0,
            createdAt = System.currentTimeMillis() - (index * 86400000L), // Different days
            updatedAt = System.currentTimeMillis(),
            passwordLastChanged = System.currentTimeMillis() - (index * 86400000L)
        )
    }
}
