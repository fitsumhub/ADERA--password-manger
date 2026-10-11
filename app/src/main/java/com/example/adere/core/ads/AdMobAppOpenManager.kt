package com.example.adere.core.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import java.lang.ref.WeakReference

/**
 * Manages Format 6: App Open Ads with strict safety and zero-knowledge security.
 *
 * Rules:
 * - Disabled by default ([AdMobConfig.isAppOpenAdEnabled] = false) until user experience
 *   and security implications are verified.
 * - Suppressed whenever the vault is locked or during biometric/password authentication.
 * - Suppressed on cold startup directly into lock screen.
 */
class AdMobAppOpenManager(application: Application) : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private var currentActivityRef: WeakReference<Activity>? = null
    private val adMobManager = AdMobManager.getInstance(application)
    private var isVaultLocked: Boolean = true

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    /**
     * Updates whether the vault is currently locked.
     * When locked, App Open Ads are strictly suppressed.
     */
    fun setVaultLockedState(isLocked: Boolean) {
        this.isVaultLocked = isLocked
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        showAdIfAllowed()
    }

    private fun showAdIfAllowed() {
        val activity = currentActivityRef?.get() ?: return

        // Security Guardrail: Never show App Open Ads if feature disabled or vault locked
        if (!AdMobConfig.isAppOpenAdEnabled || isVaultLocked) {
            Log.d(TAG, "App Open Ad suppressed: isEnabled=${AdMobConfig.isAppOpenAdEnabled}, isVaultLocked=$isVaultLocked")
            return
        }

        adMobManager.showAppOpenAdIfAvailable(
            activity = activity,
            zone = AdSecurityZone.SAFE_ZONE,
            onAdDismissed = {
                Log.d(TAG, "App Open Ad dismissed or unavailable.")
            }
        )
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivityRef = WeakReference(activity)
    }
    override fun onActivityResumed(activity: Activity) {
        currentActivityRef = WeakReference(activity)
    }
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivityRef?.get() == activity) {
            currentActivityRef = null
        }
    }

    companion object {
        private const val TAG = "AdMobAppOpenManager"

        @Volatile
        private var instance: AdMobAppOpenManager? = null

        fun getInstance(application: Application): AdMobAppOpenManager {
            return instance ?: synchronized(this) {
                instance ?: AdMobAppOpenManager(application).also { instance = it }
            }
        }
    }
}
