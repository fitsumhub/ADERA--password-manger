package com.example.adere.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll

/**
 * Enhanced pull-to-refresh wrapper that can be used with any content
 */
@Composable
fun SimplePullRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    EnhancedPullToRefresh(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        content = content
    )
}

/**
 * Pull-to-refresh wrapper specifically designed for LazyColumn content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullRefreshLazyColumn(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()
    
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            pullToRefreshState.startRefresh()
        } else {
            pullToRefreshState.endRefresh()
        }
    }
    
    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing) {
            onRefresh()
        }
    }
    
    Box(
        modifier = modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            content = content
        )
        
        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

/**
 * Custom animated pull refresh for vault items with enhanced visual feedback
 */
@Composable
fun VaultPullRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    EnhancedPullToRefresh(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        refreshThreshold = 100.dp,
        maxPullDistance = 150.dp,
        modifier = modifier,
        content = content
    )
}

/**
 * Quick access pull refresh with haptic feedback for security actions
 */
@Composable
fun SecurityPullRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    EnhancedPullToRefresh(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        refreshThreshold = 80.dp,
        maxPullDistance = 120.dp,
        modifier = modifier,
        content = content
    )
}