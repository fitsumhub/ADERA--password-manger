package com.example.adere.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.adere.core.ads.AdMobManager
import com.example.adere.core.ads.AdSecurityPolicy
import com.example.adere.core.ads.AdSecurityZone
import com.example.adere.core.ads.ConsentManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

private const val TAG = "AdMobBanner"

/**
 * Production-ready, privacy-conscious, lifecycle-safe AdMob Banner Composable.
 * 
 * Enforces [AdSecurityPolicy] so that ads NEVER appear inside sensitive screens.
 */
@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    zone: AdSecurityZone = AdSecurityZone.SAFE_ZONE
) {
    // Architectural Guardrail: If zone is SENSITIVE_ZONE, block ad completely.
    if (!AdSecurityPolicy.isAdAllowed(zone)) {
        return
    }

    val context = LocalContext.current
    val consentManager = remember { ConsentManager.getInstance(context) }
    val adMobManager = remember { AdMobManager.getInstance(context) }

    // If UMP consent is missing or user opted out, do not display ad
    if (!consentManager.canRequestAds()) {
        return
    }

    var isAdLoadedSuccessfully by remember { mutableStateOf(false) }
    var isAdFailedToLoad by remember { mutableStateOf(false) }

    if (isAdFailedToLoad) {
        // Hide gracefully on failure without affecting surrounding layout
        return
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        val widthDp = this.maxWidth
        val widthPx = widthDp.value.toInt()
        val adSize = remember(widthPx) {
            val safeWidth = if (widthPx > 0) widthPx else 320
            @Suppress("DEPRECATION")
            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, safeWidth)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Visual Ad Disclosure Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "ADVERTISEMENT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                )
            }

            key(adSize) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(adSize.height.dp),
                    factory = { viewContext ->
                        AdView(viewContext).apply {
                            setAdSize(adSize)
                            adUnitId = adMobManager.getBannerAdUnitId()
                            adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    isAdLoadedSuccessfully = true
                                    isAdFailedToLoad = false
                                }

                                override fun onAdFailedToLoad(error: LoadAdError) {
                                    Log.w(TAG, "AdMob banner load failed: ${error.message} (code: ${error.code})")
                                    isAdFailedToLoad = true
                                    isAdLoadedSuccessfully = false
                                }
                            }
                            loadAd(AdRequest.Builder().build())
                        }
                    },
                    onRelease = AdView::destroy
                )
            }
        }
    }
}
