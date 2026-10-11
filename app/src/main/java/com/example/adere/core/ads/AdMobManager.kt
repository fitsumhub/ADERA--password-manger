package com.example.adere.core.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Centralized AdMob manager supporting all 6 official formats:
 * 1. Banner Ads
 * 2. Interstitial Ads
 * 3. Rewarded Interstitial Ads
 * 4. Rewarded Ads
 * 5. Native Advanced Ads
 * 6. App Open Ads (Disabled by default)
 *
 * Enforces [AdSecurityPolicy], frequency capping, UMP consent verification,
 * and zero-knowledge data privacy.
 */
class AdMobManager private constructor(context: Context) {
    private val appContext = context.applicationContext
    private val isInitialized = AtomicBoolean(false)
    private val consentManager = ConsentManager.getInstance(appContext)

    // Format 2: Interstitial
    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false
    private var lastInterstitialShowTimeMs: Long = 0
    private var interstitialShowCountInSession: Int = 0

    // Format 3: Rewarded Interstitial
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var isRewardedInterstitialLoading = false

    // Format 4: Rewarded
    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false

    // Format 6: App Open Ad (Disabled by default)
    private var appOpenAd: AppOpenAd? = null
    private var isAppOpenLoading = false

    companion object {
        private const val TAG = "AdMobManager"

        // Interstitial Frequency Capping Rules:
        // - Minimum 10 minutes (600,000 ms) between interstitials
        // - Maximum 2 interstitials per user session
        private const val MIN_INTERSTITIAL_INTERVAL_MS = 600_000L
        private const val MAX_INTERSTITIALS_PER_SESSION = 2

        @Volatile
        private var instance: AdMobManager? = null

        fun getInstance(context: Context): AdMobManager {
            return instance ?: synchronized(this) {
                instance ?: AdMobManager(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * Initializes the AdMob SDK safely and asynchronously.
     */
    fun initialize(onComplete: () -> Unit = {}) {
        if (isInitialized.getAndSet(true)) {
            onComplete()
            return
        }

        try {
            MobileAds.initialize(appContext) { initializationStatus ->
                Log.d(TAG, "AdMob initialized: ${initializationStatus.adapterStatusMap}")
                onComplete()
            }
        } catch (e: Exception) {
            Log.e(TAG, "AdMob initialization failed: ${e.message}")
            onComplete()
        }
    }

    // ── Format 1: Banner Ads ────────────────────────────────────────────────

    fun getBannerAdUnitId(): String = AdMobConfig.bannerAdUnitId

    // ── Format 2: Interstitial Ads ──────────────────────────────────────────

    fun getInterstitialAdUnitId(): String = AdMobConfig.interstitialAdUnitId

    fun preloadInterstitial() {
        if (!consentManager.canRequestAds() || isInterstitialLoading || interstitialAd != null) {
            return
        }

        isInterstitialLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            appContext,
            getInterstitialAdUnitId(),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "Interstitial ad preloaded successfully.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                    Log.w(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    fun showInterstitialIfAllowed(
        activity: Activity,
        zone: AdSecurityZone,
        onAdDismissed: () -> Unit
    ) {
        if (!AdSecurityPolicy.isAdAllowed(zone)) {
            Log.w(TAG, "Interstitial show blocked: Security policy prohibits ads in $zone")
            onAdDismissed()
            return
        }

        val currentTime = System.currentTimeMillis()
        if (interstitialShowCountInSession >= MAX_INTERSTITIALS_PER_SESSION) {
            Log.i(TAG, "Interstitial skipped: Session cap reached.")
            onAdDismissed()
            return
        }

        if (currentTime - lastInterstitialShowTimeMs < MIN_INTERSTITIAL_INTERVAL_MS) {
            Log.i(TAG, "Interstitial skipped: Minimum time interval not elapsed.")
            onAdDismissed()
            return
        }

        val ad = interstitialAd
        if (ad != null && consentManager.canRequestAds()) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    lastInterstitialShowTimeMs = System.currentTimeMillis()
                    interstitialShowCountInSession++
                    onAdDismissed()
                    preloadInterstitial()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    Log.w(TAG, "Interstitial failed to show: ${adError.message}")
                    onAdDismissed()
                    preloadInterstitial()
                }
            }
            ad.show(activity)
        } else {
            onAdDismissed()
            preloadInterstitial()
        }
    }

    // ── Format 3: Rewarded Interstitial Ads ──────────────────────────────────

    fun getRewardedInterstitialAdUnitId(): String = AdMobConfig.rewardedInterstitialAdUnitId

    fun preloadRewardedInterstitialAd() {
        if (!consentManager.canRequestAds() || isRewardedInterstitialLoading || rewardedInterstitialAd != null) {
            return
        }

        isRewardedInterstitialLoading = true
        val adRequest = AdRequest.Builder().build()

        RewardedInterstitialAd.load(
            appContext,
            getRewardedInterstitialAdUnitId(),
            adRequest,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    rewardedInterstitialAd = ad
                    isRewardedInterstitialLoading = false
                    Log.d(TAG, "Rewarded Interstitial ad loaded successfully.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedInterstitialAd = null
                    isRewardedInterstitialLoading = false
                    Log.w(TAG, "Rewarded Interstitial ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    fun showRewardedInterstitialAdIfAvailable(
        activity: Activity,
        zone: AdSecurityZone,
        onUserEarnedReward: () -> Unit,
        onAdDismissed: () -> Unit
    ) {
        if (!AdSecurityPolicy.isAdAllowed(zone)) {
            Log.w(TAG, "Rewarded Interstitial ad blocked: Security policy prohibits ads in $zone")
            onAdDismissed()
            return
        }

        val ad = rewardedInterstitialAd
        if (ad != null && consentManager.canRequestAds()) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedInterstitialAd = null
                    onAdDismissed()
                    preloadRewardedInterstitialAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedInterstitialAd = null
                    Log.w(TAG, "Rewarded Interstitial ad failed to show: ${adError.message}")
                    onAdDismissed()
                    preloadRewardedInterstitialAd()
                }
            }

            ad.show(activity) { rewardItem ->
                Log.d(TAG, "User earned rewarded interstitial reward: ${rewardItem.amount} ${rewardItem.type}")
                onUserEarnedReward()
            }
        } else {
            onAdDismissed()
            preloadRewardedInterstitialAd()
        }
    }

    // ── Format 4: Rewarded Ads ────────────────────────────────────────────────

    fun getRewardedAdUnitId(): String = AdMobConfig.rewardedAdUnitId

    fun preloadRewardedAd() {
        if (!consentManager.canRequestAds() || isRewardedLoading || rewardedAd != null) {
            return
        }

        isRewardedLoading = true
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            appContext,
            getRewardedAdUnitId(),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false
                    Log.d(TAG, "Rewarded ad loaded successfully.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedAd = null
                    isRewardedLoading = false
                    Log.w(TAG, "Rewarded ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    fun showRewardedAdIfAvailable(
        activity: Activity,
        zone: AdSecurityZone,
        onUserEarnedReward: () -> Unit,
        onAdDismissed: () -> Unit
    ) {
        if (!AdSecurityPolicy.isAdAllowed(zone)) {
            Log.w(TAG, "Rewarded ad blocked: Security policy prohibits ads in $zone")
            onAdDismissed()
            return
        }

        val ad = rewardedAd
        if (ad != null && consentManager.canRequestAds()) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    onAdDismissed()
                    preloadRewardedAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    Log.w(TAG, "Rewarded ad failed to show: ${adError.message}")
                    onAdDismissed()
                    preloadRewardedAd()
                }
            }

            ad.show(activity) { rewardItem ->
                Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onUserEarnedReward()
            }
        } else {
            onAdDismissed()
            preloadRewardedAd()
        }
    }

    // ── Format 5: Native Advanced Ads ─────────────────────────────────────────

    fun getNativeAdUnitId(): String = AdMobConfig.nativeAdUnitId

    fun loadNativeAd(
        context: Context,
        zone: AdSecurityZone,
        onNativeAdLoaded: (NativeAd) -> Unit,
        onAdFailedToLoad: (LoadAdError) -> Unit
    ) {
        if (!AdSecurityPolicy.isAdAllowed(zone) || !consentManager.canRequestAds()) {
            Log.w(TAG, "Native ad request blocked by policy or missing consent.")
            return
        }

        val builder = AdLoader.Builder(context, getNativeAdUnitId())
        builder.forNativeAd { nativeAd ->
            Log.d(TAG, "Native ad loaded successfully.")
            onNativeAdLoaded(nativeAd)
        }

        val videoOptions = com.google.android.gms.ads.VideoOptions.Builder()
            .setStartMuted(true)
            .build()

        val adOptions = NativeAdOptions.Builder()
            .setVideoOptions(videoOptions)
            .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : com.google.android.gms.ads.AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.w(TAG, "Native ad failed to load: ${loadAdError.message}")
                onAdFailedToLoad(loadAdError)
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    // ── Format 6: App Open Ads (Disabled by default) ──────────────────────────

    fun getAppOpenAdUnitId(): String = AdMobConfig.appOpenAdUnitId

    fun preloadAppOpenAd() {
        if (!AdMobConfig.isAppOpenAdEnabled || !consentManager.canRequestAds() || isAppOpenLoading || appOpenAd != null) {
            return
        }

        isAppOpenLoading = true
        val adRequest = AdRequest.Builder().build()

        AppOpenAd.load(
            appContext,
            getAppOpenAdUnitId(),
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isAppOpenLoading = false
                    Log.d(TAG, "App Open ad preloaded successfully.")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    appOpenAd = null
                    isAppOpenLoading = false
                    Log.w(TAG, "App Open ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    fun showAppOpenAdIfAvailable(
        activity: Activity,
        zone: AdSecurityZone,
        onAdDismissed: () -> Unit
    ) {
        if (!AdMobConfig.isAppOpenAdEnabled || !AdSecurityPolicy.isAdAllowed(zone)) {
            Log.d(TAG, "App Open Ad suppressed: Feature disabled by default or in sensitive zone.")
            onAdDismissed()
            return
        }

        val ad = appOpenAd
        if (ad != null && consentManager.canRequestAds()) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    onAdDismissed()
                    preloadAppOpenAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    Log.w(TAG, "App Open ad failed to show: ${adError.message}")
                    onAdDismissed()
                    preloadAppOpenAd()
                }
            }
            ad.show(activity)
        } else {
            onAdDismissed()
            preloadAppOpenAd()
        }
    }
}
