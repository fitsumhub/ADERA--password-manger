package com.example.adere.core.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

/**
 * Manages privacy consent choices using Google's User Messaging Platform (UMP) SDK.
 * Ensures GDPR / EEA / UK privacy compliance for AdMob requests.
 */
class ConsentManager(context: Context) {
    private val appContext = context.applicationContext
    private val consentInformation: ConsentInformation = UserMessagingPlatform.getConsentInformation(appContext)

    fun interface OnConsentGatheringCompleteListener {
        fun onConsentGatheringComplete(canRequestAds: Boolean)
    }

    /**
     * Checks if ads can be requested based on the user's consent choices.
     */
    fun canRequestAds(): Boolean {
        return consentInformation.canRequestAds()
    }

    /**
     * Checks if privacy options form is required for the user (EEA/UK compliance).
     */
    fun isPrivacyOptionsRequired(): Boolean {
        return consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }

    /**
     * Helper method to request consent information update and present consent form if required.
     */
    fun gatherConsent(
        activity: Activity,
        isDebug: Boolean = false,
        testDeviceHashedId: String? = null,
        onComplete: OnConsentGatheringCompleteListener
    ) {
        val paramsBuilder = ConsentRequestParameters.Builder()

        if (isDebug) {
            val debugSettingsBuilder = ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            if (!testDeviceHashedId.isNullOrBlank()) {
                debugSettingsBuilder.addTestDeviceHashedId(testDeviceHashedId)
            }
            paramsBuilder.setConsentDebugSettings(debugSettingsBuilder.build())
        }

        val params = paramsBuilder.build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        Log.w(TAG, "Consent form error: ${formError.errorCode}: ${formError.message}")
                    }
                    onComplete.onConsentGatheringComplete(canRequestAds())
                }
            },
            { requestConsentError ->
                Log.w(TAG, "Consent info update failed: ${requestConsentError.errorCode}: ${requestConsentError.message}")
                onComplete.onConsentGatheringComplete(canRequestAds())
            }
        )
    }

    /**
     * Shows the privacy options form allowing users to modify or revoke consent choices.
     */
    fun showPrivacyOptionsForm(activity: Activity, onDismiss: (Boolean) -> Unit) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            if (formError != null) {
                Log.w(TAG, "Privacy options form error: ${formError.message}")
                onDismiss(false)
            } else {
                onDismiss(true)
            }
        }
    }

    companion object {
        private const val TAG = "ConsentManager"

        @Volatile
        private var instance: ConsentManager? = null

        fun getInstance(context: Context): ConsentManager {
            return instance ?: synchronized(this) {
                instance ?: ConsentManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
