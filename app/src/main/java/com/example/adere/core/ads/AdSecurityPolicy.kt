package com.example.adere.core.ads

import android.util.Log

/**
 * Defines security zones for AdMob ad placement across the ADERA application.
 * 
 * ADERA is a password manager and crypto vault. Security and privacy must NEVER
 * be compromised for advertising.
 */
enum class AdSecurityZone {
    /**
     * Non-sensitive screens where advertisements may be displayed cleanly:
     * - Home / General Dashboard
     * - General Category Browsing
     * - Informational / About screens
     */
    SAFE_ZONE,

    /**
     * AD-FREE ZONE: Strictly prohibits any ads from loading or displaying.
     * Includes:
     * - Master Password creation and login
     * - Lock Screen & Biometric Authentication
     * - Password / Secret creation and editing
     * - Item Detail Screen (Revealing passwords, copying keys, viewing seed phrases)
     * - Security Audit & Security Health screens
     * - Backup & Restore operations
     * - Security Settings
     * - Privacy Settings
     */
    SENSITIVE_ZONE
}

/**
 * Enforces architectural policy preventing ads from appearing inside sensitive vault screens.
 */
object AdSecurityPolicy {
    private const val TAG = "AdSecurityPolicy"

    /**
     * Returns whether ads are permitted in the given [zone].
     */
    fun isAdAllowed(zone: AdSecurityZone): Boolean {
        return when (zone) {
            AdSecurityZone.SAFE_ZONE -> true
            AdSecurityZone.SENSITIVE_ZONE -> {
                Log.w(TAG, "Ad request blocked by AdSecurityPolicy: SENSITIVE_ZONE is an AD-FREE ZONE.")
                false
            }
        }
    }

    /**
     * Returns whether ads are permitted for the given screen string tag.
     */
    fun isAdAllowed(screenTag: String): Boolean {
        val zone = when (screenTag.uppercase()) {
            "DASHBOARD_HOME", "VAULT_LIST", "CATEGORY_ADD_TEMPLATE" -> AdSecurityZone.SAFE_ZONE
            else -> AdSecurityZone.SENSITIVE_ZONE
        }
        return isAdAllowed(zone)
    }
}
