package com.example.adere

import android.app.Application
import android.util.Log
import com.example.adere.core.ads.AdMobManager
import com.example.adere.core.common.AdereAppContainer

class AdereApplication : Application() {
    lateinit var container: AdereAppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        // Implement a robust global uncaught exception handler to ensure the app never crashes abruptly
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("AdereApplication", "FATAL EXCEPTION caught on thread ${thread.name}: ${throwable.message}", throwable)
            try {
                if (::container.isInitialized) {
                    container.sessionManager.lock()
                }
            } catch (e: Exception) {
                Log.e("AdereApplication", "Error locking vault in crash handler", e)
            }

            try {
                defaultHandler?.uncaughtException(thread, throwable)
            } catch (e: Exception) {
                Log.e("AdereApplication", "Error in default uncaught exception handler", e)
            }
        }

        try {
            container = AdereAppContainer(this)
        } catch (e: Exception) {
            Log.e("AdereApplication", "Failed to initialize AdereAppContainer", e)
        }
        
        // Initialize AdMob asynchronously and safely
        try {
            val adMob = AdMobManager.getInstance(this)
            adMob.initialize {
                adMob.preloadInterstitial()
                adMob.preloadRewardedAd()
                adMob.preloadRewardedInterstitialAd()
            }
            com.example.adere.core.ads.AdMobAppOpenManager.getInstance(this)
        } catch (e: Exception) {
            Log.e("AdereApplication", "Failed to initialize AdMob", e)
        }
    }
}

