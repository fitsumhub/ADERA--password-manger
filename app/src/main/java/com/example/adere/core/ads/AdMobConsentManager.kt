package com.example.adere.core.ads

import android.app.Activity
import android.util.Log
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

/**
 * Manages privacy consent via Google's User Messaging Platform (UMP) SDK
 * in compliance with GDPR, EEA, UK, and CCPA regulations.
 */
class AdMobConsentManager private constructor() {

    companion object {
        private const val TAG = "AdMobConsentManager"

        @Volatile
        private var instance: AdMobConsentManager? = null

        fun getInstance(): AdMobConsentManager {
            return instance ?: synchronized(this) {
                instance ?: AdMobConsentManager().also { instance = it }
            }
        }
    }

    /**
     * Initializes UMP consent request and presents consent form if required.
     * Operates asynchronously without blocking app launch or vault operations.
     */
    fun initializeAndRequestConsent(
        activity: Activity,
        onConsentCompleted: (canRequestAds: Boolean) -> Unit
    ) {
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        Log.w(TAG, "Consent form error: ${formError.errorCode} - ${formError.message}")
                    }
                    val canRequest = consentInformation.canRequestAds()
                    onConsentCompleted(canRequest)
                }
            },
            { requestError ->
                Log.w(TAG, "Consent info update failed: ${requestError.errorCode} - ${requestError.message}")
                onConsentCompleted(consentInformation.canRequestAds())
            }
        )
    }

    /**
     * Shows the privacy options form allowing users to modify ad choices anytime.
     */
    fun showPrivacyOptionsForm(activity: Activity, onDismiss: () -> Unit) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            if (formError != null) {
                Log.w(TAG, "Privacy options form error: ${formError.message}")
            }
            onDismiss()
        }
    }

    /**
     * Checks if privacy choices form is required to be offered in Settings.
     */
    fun isPrivacyOptionsRequired(activity: Activity): Boolean {
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        return consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }
}
