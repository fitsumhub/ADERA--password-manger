package com.example.adere.presentation.components
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalThemeAccents
import kotlinx.coroutines.delay

enum class LoadingState {
    Idle,
    Loading,
    Success,
    Error
}

data class LoadingConfig(
    val showProgressIndicator: Boolean = true,
    val showMessage: Boolean = true,
    val enableRetry: Boolean = true,
    val autoHideSuccess: Boolean = true,
    val successDuration: Long = 2000L
)

@Composable
fun <T> LoadingStateWrapper(
    loadingState: LoadingState,
    data: T?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    config: LoadingConfig = LoadingConfig(),
    loadingMessage: String = "Loading...",
    successMessage: String = "Success!",
    errorMessage: String = "Something went wrong",
    emptyStateMessage: String = "No data available",
    loadingContent: @Composable () -> Unit = { DefaultLoadingContent(loadingMessage) },
    errorContent: @Composable () -> Unit = { 
        DefaultErrorContent(
            message = errorMessage,
            onRetry = if (config.enableRetry) onRetry else null
        )
    },
    successContent: @Composable () -> Unit = { DefaultSuccessContent(successMessage) },
    emptyContent: @Composable () -> Unit = { DefaultEmptyContent(emptyStateMessage) },
    content: @Composable (T) -> Unit
) {
    var showSuccess by remember { mutableStateOf(false) }
    
    LaunchedEffect(loadingState) {
        if (loadingState == LoadingState.Success && config.autoHideSuccess) {
            showSuccess = true
            delay(config.successDuration)
            showSuccess = false
        }
    }

    AnimatedContent(
        targetState = when {
            loadingState == LoadingState.Loading -> "loading"
            loadingState == LoadingState.Error -> "error"
            showSuccess -> "success"
            data == null -> "empty"
            else -> "content"
        },
        transitionSpec = {
            slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(400)
            ) + fadeIn(animationSpec = tween(400)) togetherWith
            slideOutVertically(
                targetOffsetY = { -it / 3 },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        },
        modifier = modifier,
        label = "loading_state_transition"
    ) { state ->
        when (state) {
            "loading" -> loadingContent()
            "error" -> errorContent()
            "success" -> successContent()
            "empty" -> emptyContent()
            "content" -> data?.let { content(it) }
        }
    }
}

@Composable
fun DefaultLoadingContent(
    message: String,
    modifier: Modifier = Modifier
) {
    val themeAccents = LocalThemeAccents.current
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PulsingLoadingIndicator(
            color = themeAccents.primary,
            modifier = Modifier.size(48.dp)
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun DefaultErrorContent(
    message: String,
    onRetry: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StateIcon(
            icon = Icons.Default.Error,
            color = Color(0xFFEF4444),
            backgroundColor = Color(0xFFEF4444).copy(alpha = 0.1f)
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        )
        
        onRetry?.let { retryAction ->
            Button(
                onClick = retryAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Retry",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun DefaultSuccessContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StateIcon(
            icon = Icons.Default.CheckCircle,
            color = Color(0xFF10B981),
            backgroundColor = Color(0xFF10B981).copy(alpha = 0.1f)
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun DefaultEmptyContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f)
                        )
                    )
                )
        )
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun StateIcon(
    icon: ImageVector,
    color: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(64.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
    }
}

// Specialized loading states for different scenarios
@Composable
fun VaultListLoadingState(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(itemCount) { index ->
            VaultItemSkeleton(
                animationDelay = index * 80L
            )
        }
    }
}

@Composable
fun SecurityHealthLoadingState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header skeleton
@Composable
fun SectionHeaderSkeleton(
    modifier: Modifier = Modifier
) {
    ShimmerEffect(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(width = 120.dp, height = 24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        )
    }
}
        
        // Health ring skeleton
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                ShimmerEffect {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(60.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    )
                }
            }
        }
        
        // Issues list skeleton
        repeat(3) { index ->
            VaultItemSkeleton(
                animationDelay = index * 100L
            )
        }
    }
}
