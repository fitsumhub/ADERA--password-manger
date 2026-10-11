package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.adere.core.ads.AdMobConfig
import com.example.adere.core.ads.AdMobManager
import com.example.adere.core.ads.AdSecurityPolicy
import com.example.adere.core.ads.AdSecurityZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AdMobFormatsTest {

    @Test
    fun testAdMobConfigTestAdUnitIdsInDebug() {
        // In Debug build, official Google Test Ad Unit IDs must be returned for all formats
        assertNotNull(AdMobConfig.appId)
        assertNotNull(AdMobConfig.bannerAdUnitId)
        assertNotNull(AdMobConfig.interstitialAdUnitId)
        assertNotNull(AdMobConfig.rewardedInterstitialAdUnitId)
        assertNotNull(AdMobConfig.rewardedAdUnitId)
        assertNotNull(AdMobConfig.nativeAdUnitId)
        assertNotNull(AdMobConfig.appOpenAdUnitId)

        assertEquals("ca-app-pub-3940256099942544/6300978111", AdMobConfig.TEST_BANNER_AD_UNIT_ID)
        assertEquals("ca-app-pub-3940256099942544/1033173712", AdMobConfig.TEST_INTERSTITIAL_AD_UNIT_ID)
        assertEquals("ca-app-pub-3940256099942544/5354046379", AdMobConfig.TEST_REWARDED_INTERSTITIAL_AD_UNIT_ID)
        assertEquals("ca-app-pub-3940256099942544/5224354917", AdMobConfig.TEST_REWARDED_AD_UNIT_ID)
        assertEquals("ca-app-pub-3940256099942544/2247696110", AdMobConfig.TEST_NATIVE_AD_UNIT_ID)
        assertEquals("ca-app-pub-3940256099942544/9257390434", AdMobConfig.TEST_APP_OPEN_AD_UNIT_ID)
    }

    @Test
    fun testAppOpenAdDisabledByDefault() {
        // Format 6 (App Open Ads) MUST be disabled by default until security & UX verified
        assertFalse("App Open Ads must be disabled by default", AdMobConfig.isAppOpenAdEnabled)
    }

    @Test
    fun testAdSecurityPolicySafeZonePermitted() {
        assertTrue("Ads must be permitted in SAFE_ZONE", AdSecurityPolicy.isAdAllowed(AdSecurityZone.SAFE_ZONE))
        assertTrue("Ads must be permitted on Dashboard tag", AdSecurityPolicy.isAdAllowed("DASHBOARD_HOME"))
    }

    @Test
    fun testAdSecurityPolicySensitiveZoneProhibited() {
        assertFalse("Ads MUST be blocked in SENSITIVE_ZONE", AdSecurityPolicy.isAdAllowed(AdSecurityZone.SENSITIVE_ZONE))
        assertFalse("Ads MUST be blocked on Lock Screen", AdSecurityPolicy.isAdAllowed("LOCK_SCREEN"))
        assertFalse("Ads MUST be blocked on Item Detail", AdSecurityPolicy.isAdAllowed("ITEM_DETAIL"))
        assertFalse("Ads MUST be blocked on Add/Edit Item", AdSecurityPolicy.isAdAllowed("ADD_ITEM"))
        assertFalse("Ads MUST be blocked on Backup & Restore", AdSecurityPolicy.isAdAllowed("BACKUP_RESTORE"))
        assertFalse("Ads MUST be blocked on Recovery Key", AdSecurityPolicy.isAdAllowed("RECOVERY_KEY"))
    }

    @Test
    fun testAdMobManagerSingletonInitialization() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val manager = AdMobManager.getInstance(context)
        assertNotNull(manager)

        // Verify Ad Unit ID resolution
        assertNotNull(manager.getBannerAdUnitId())
        assertNotNull(manager.getInterstitialAdUnitId())
        assertNotNull(manager.getRewardedInterstitialAdUnitId())
        assertNotNull(manager.getRewardedAdUnitId())
        assertNotNull(manager.getNativeAdUnitId())
        assertNotNull(manager.getAppOpenAdUnitId())
    }
}
