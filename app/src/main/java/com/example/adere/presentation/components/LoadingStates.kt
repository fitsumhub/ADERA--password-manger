package com.example.adere.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalThemeAccents
import kotlinx.coroutines.delay

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    Box(
        modifier = modifier.graphicsLayer(alpha = alpha)
    ) {
        content()
    }
}

@Composable
fun PulsingLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0.5f) }

    LaunchedEffect(Unit) {
        while (true) {
            scale.animateTo(1.2f, animationSpec = tween(800))
            scale.animateTo(0.8f, animationSpec = tween(800))
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            alpha.animateTo(1f, animationSpec = tween(800))
            alpha.animateTo(0.3f, animationSpec = tween(800))
        }
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .scale(scale.value)
            .clip(CircleShape)
            .background(color.copy(alpha = alpha.value)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}

@Composable
fun VaultItemSkeleton(
    modifier: Modifier = Modifier,
    animationDelay: Long = 0L
) {
    val themeAccents = LocalThemeAccents.current
    val scale = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        delay(animationDelay)
        scale.animateTo(1f, animationSpec = tween(300))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale.value),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon skeleton
            ShimmerEffect {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                                )
                            )
                        )
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Content skeleton
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Title skeleton
                ShimmerEffect {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    )
                }
                
                // Username skeleton
                ShimmerEffect {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    )
                }
                
                // URL skeleton
                ShimmerEffect {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                    )
                }
            }
            
            // Action buttons skeleton
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(2) {
                    ShimmerEffect {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardSkeleton(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            
            // Header skeleton
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerEffect {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(2) {
                        ShimmerEffect {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            )
                        }
                    }
                }
            }
        }
        
        // Security card skeleton
        item {
            SecurityCardSkeleton()
        }
        
        // Search bar skeleton
        item {
            SearchBarSkeleton()
        }
        
        // Section header skeleton
        item {
            SectionHeaderSkeleton()
        }
        
        // Category tiles skeleton
        item {
            CategoryTilesSkeleton()
        }
        
        // Another section header
        item {
            SectionHeaderSkeleton()
        }
        
        // Vault items skeleton
        items(5) { index ->
            VaultItemSkeleton(
                animationDelay = index * 100L
            )
        }
    }
}
@Composable
private fun SecurityCardSkeleton() {
    ShimmerEffect {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
                
                Spacer(modifier = Modifier.width(14.dp))
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(18.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBarSkeleton() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerEffect {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
        
        Spacer(modifier = Modifier.width(10.dp))
        
        ShimmerEffect {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            )
        }
    }
}

@Composable
private fun SectionHeaderSkeleton() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShimmerEffect {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            )
        }
        
        ShimmerEffect {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            )
        }
    }
}

@Composable
private fun CategoryTilesSkeleton() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(3) { index ->
            ShimmerEffect {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                    )
                ) {
                    Box(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun LoadingSpinner(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    size: Int = 3
) {
    val themeAccents = LocalThemeAccents.current
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(size) { index ->
            val scale = remember { Animatable(0.5f) }
            
            LaunchedEffect(Unit) {
                delay(index * 150L)
                while (true) {
                    scale.animateTo(1f, animationSpec = tween(400))
                    scale.animateTo(0.5f, animationSpec = tween(400))
                    delay(200)
                }
            }
            
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(scale.value)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun ProgressiveLoadingCard(
    isLoading: Boolean,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit = { VaultItemSkeleton() }
) {
    val scale = remember { Animatable(if (isLoading) 1f else 0f) }
    val contentScale = remember { Animatable(if (isLoading) 0f else 1f) }
    
    LaunchedEffect(isLoading) {
        if (isLoading) {
            contentScale.animateTo(0f, animationSpec = tween(200))
            scale.animateTo(1f, animationSpec = tween(300))
        } else {
            scale.animateTo(0f, animationSpec = tween(200))
            contentScale.animateTo(1f, animationSpec = tween(300))
        }
    }
    
    Box(modifier = modifier) {
        if (scale.value > 0f) {
            Box(modifier = Modifier.scale(scale.value)) {
                loadingContent()
            }
        }
        
        if (contentScale.value > 0f) {
            Box(modifier = Modifier.scale(contentScale.value)) {
                content()
            }
        }
    }
}

@Composable
fun BiometricLoadingState(
    modifier: Modifier = Modifier,
    isAuthenticating: Boolean = true
) {
    val themeAccents = LocalThemeAccents.current
    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(0.8f) }
    
    LaunchedEffect(isAuthenticating) {
        if (isAuthenticating) {
            while (isAuthenticating) {
                rotation.animateTo(360f, animationSpec = tween(2000, easing = LinearEasing))
                rotation.snapTo(0f)
            }
        }
    }
    
    LaunchedEffect(Unit) {
        while (true) {
            scale.animateTo(1.1f, animationSpec = tween(1000))
            scale.animateTo(0.8f, animationSpec = tween(1000))
        }
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .scale(scale.value)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            themeAccents.primary.copy(alpha = 0.2f),
                            themeAccents.primary.copy(alpha = 0.05f)
                        )
                    )
                )
                .graphicsLayer(rotationZ = rotation.value),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(themeAccents.primary)
            )
        }
        
        LoadingSpinner(
            color = themeAccents.primary,
            size = 3
        )
    }
}