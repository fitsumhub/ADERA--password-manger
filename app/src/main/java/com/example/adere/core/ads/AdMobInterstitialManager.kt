package com.example.adere.core.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Manages non-intrusive interstitial advertisements adhering to ADERA's frequency cap
 * and Ad-Free Security Zone policy.
 *
 * Rules:
 * - Minimum 10-minute interval between interstitial presentations.
 * - STRICTLY BANNED on all sensitive security, login, seed phrase, and backup screens.
 * - Always proceeds immediately if ad is unavailable or fails.
 */
object AdMobInterstitialManager {

    private const val TAG = "AdMobInterstitialManager"
    private const val MIN_INTERSTITIAL_INTERVAL_MS = 10 * 60 * 1000L // 10 minutes

    private var interstitialAd: InterstitialAd? = null
    private var lastShowTimestampMs: Long = 0L
    private var isLoading = false

    /**
     * Preloads an interstitial ad asynchronously if not already loaded or loading.
     */
    fun preload(context: Context) {
        if (interstitialAd != null || isLoading) return

        isLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            AdMobConfig.interstitialAdUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                    Log.d(TAG, "Interstitial ad preloaded successfully.")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                    Log.w(TAG, "Interstitial failed to load: ${error.message}")
                }
            }
        )
    }

    /**
     * Shows an interstitial ad if permitted by AdSecurityPolicy, frequency cap, and availability.
     * Always invokes [onDismissed] so user workflows are never blocked.
     */
    fun showIfReady(
        activity: Activity,
        screenTag: String,
        onDismissed: () -> Unit
    ) {
        // Enforce Ad-Free Security Zone policy
        if (!AdSecurityPolicy.isAdAllowed(screenTag)) {
            Log.d(TAG, "Interstitial suppressed: $screenTag is an AD-FREE ZONE.")
            onDismissed()
            return
        }

        val currentTime = System.currentTimeMillis()
        val timeSinceLastShow = currentTime - lastShowTimestampMs

        // Enforce 10-minute frequency cap
        if (timeSinceLastShow < MIN_INTERSTITIAL_INTERVAL_MS) {
            Log.d(TAG, "Interstitial suppressed by frequency cap (${timeSinceLastShow / 1000}s < 600s).")
            onDismissed()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    lastShowTimestampMs = System.currentTimeMillis()
                    preload(activity.applicationContext)
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    interstitialAd = null
                    Log.w(TAG, "Interstitial failed to show: ${error.message}")
                    onDismissed()
                }
            }
            ad.show(activity)
        } else {
            preload(activity.applicationContext)
            onDismissed()
        }
    }
}
