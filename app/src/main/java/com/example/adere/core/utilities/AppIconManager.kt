package com.example.adere.core.utilities

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.DrawableRes
import com.example.R

/**
 * Manages custom launcher activity-aliases and preset icon switching.
 * Uses Package Manager's setComponentEnabledSetting with DONT_KILL_APP
 * to safely switch home screen icons and launcher labels on Android OS launcher.
 */
object AppIconManager {

    enum class AppIconPreset(
        val id: String,
        val title: String,
        val aliasClassName: String,
        @param:DrawableRes val previewIconRes: Int
    ) {
        DEFAULT("default", "Adera Default", "com.example.MainActivityDefault", R.drawable.ic_launcher_foreground),
        CALCULATOR("calculator", "My Calculator", "com.example.MainActivityCalculator", R.drawable.ic_brand_safe),
        NOTES("notes", "Notes & Tasks", "com.example.MainActivityNotes", R.drawable.ic_brand_seedphrase),
        UTILS("utils", "System Utilities", "com.example.MainActivityUtils", R.drawable.ic_brand_ledger),
        VAULT("vault", "Secure Vault", "com.example.MainActivityVault", R.drawable.ic_brand_metamask);

        companion object {
            fun fromId(id: String): AppIconPreset = entries.firstOrNull { it.id == id } ?: DEFAULT
        }
    }

    fun applyAppIcon(context: Context, preset: AppIconPreset) {
        val pm = context.packageManager
        val packageName = context.packageName

        try {
            // First: Enable target preset to ensure a launcher component is ALWAYS active
            val targetCompName = ComponentName(packageName, preset.aliasClassName)
            if (pm.getComponentEnabledSetting(targetCompName) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                pm.setComponentEnabledSetting(
                    targetCompName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            }

            // Second: Disable all non-target presets
            AppIconPreset.entries.filter { it != preset }.forEach { other ->
                val otherCompName = ComponentName(packageName, other.aliasClassName)
                try {
                    if (pm.getComponentEnabledSetting(otherCompName) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                        pm.setComponentEnabledSetting(
                            otherCompName,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP
                        )
                    }
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // Safe fallback: never crash activity if OEM launcher rejects component toggle
        }
    }
}
