package com.example.adere.core.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.adere.presentation.components.AdMobBanner as PresentationAdMobBanner

/**
 * Convenience string-tag overload for [PresentationAdMobBanner].
 * Automatically maps string screen tag to [AdSecurityZone] and enforces ad policies.
 */
@Suppress("unused")
@Composable
fun AdMobBanner(
    screenTag: String,
    modifier: Modifier = Modifier
) {
    val zone = if (AdSecurityPolicy.isAdAllowed(screenTag)) {
        AdSecurityZone.SAFE_ZONE
    } else {
        AdSecurityZone.SENSITIVE_ZONE
    }
    PresentationAdMobBanner(modifier = modifier, zone = zone)
}
