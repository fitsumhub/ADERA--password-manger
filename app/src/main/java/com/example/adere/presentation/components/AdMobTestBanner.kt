package com.example.adere.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.adere.core.ads.AdSecurityZone

/**
 * Backward compatibility alias for [AdMobBanner].
 */
@Composable
fun AdMobTestBanner(modifier: Modifier = Modifier) {
    AdMobBanner(
        modifier = modifier,
        zone = AdSecurityZone.SAFE_ZONE
    )
}
