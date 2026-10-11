package com.example.adere.core.ads

import com.example.BuildConfig

/**
 * Centralized AdMob Configuration managing all 6 ad formats with automatic separation
 * between DEBUG (Google Official Test IDs) and RELEASE (Production Ad Unit IDs for pub-2695951066960188).
 */
object AdMobConfig {

    const val PUBLISHER_ID = "pub-2695951066960188"

    // Google Official Test Ad Unit IDs (For Development)
    const val TEST_ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713"
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_REWARDED_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/5354046379"
    const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    const val TEST_NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
    const val TEST_APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257390434"

    // Production Ad Unit IDs for Publisher pub-2695951066960188
    private const val PROD_APP_ID = "ca-app-pub-2695951066960188~3347511713"
    private const val PROD_BANNER_AD_UNIT_ID = "ca-app-pub-2695951066960188/6300978111"
    private const val PROD_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-2695951066960188/1033173712"
    private const val PROD_REWARDED_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-2695951066960188/5354046379"
    private const val PROD_REWARDED_AD_UNIT_ID = "ca-app-pub-2695951066960188/5224354917"
    private const val PROD_NATIVE_AD_UNIT_ID = "ca-app-pub-2695951066960188/2247696110"
    private const val PROD_APP_OPEN_AD_UNIT_ID = "ca-app-pub-2695951066960188/9257390434"

    /**
     * App Open Ads Feature Flag: Disabled by default for security and user experience verification.
     */
    var isAppOpenAdEnabled: Boolean = false

    val appId: String
        get() = if (BuildConfig.DEBUG) TEST_ADMOB_APP_ID else PROD_APP_ID

    val bannerAdUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_BANNER_AD_UNIT_ID else PROD_BANNER_AD_UNIT_ID

    val interstitialAdUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_INTERSTITIAL_AD_UNIT_ID else PROD_INTERSTITIAL_AD_UNIT_ID

    val rewardedInterstitialAdUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_REWARDED_INTERSTITIAL_AD_UNIT_ID else PROD_REWARDED_INTERSTITIAL_AD_UNIT_ID

    val rewardedAdUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_REWARDED_AD_UNIT_ID else PROD_REWARDED_AD_UNIT_ID

    val nativeAdUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_NATIVE_AD_UNIT_ID else PROD_NATIVE_AD_UNIT_ID

    val appOpenAdUnitId: String
        get() = if (BuildConfig.DEBUG) TEST_APP_OPEN_AD_UNIT_ID else PROD_APP_OPEN_AD_UNIT_ID
}
