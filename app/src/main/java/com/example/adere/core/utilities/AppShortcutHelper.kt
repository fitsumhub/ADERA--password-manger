package com.example.adere.core.utilities

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import com.example.R

/**
 * Publishes and updates dynamic launcher shortcuts on Android 7.1+ (API 25+)
 * with the user's custom vault label.
 */
object AppShortcutHelper {
    fun updateAppShortcut(context: Context, customAppName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            try {
                val shortcutManager = context.getSystemService(ShortcutManager::class.java) ?: return
                val intent = Intent(context, com.example.MainActivity::class.java).apply {
                    action = Intent.ACTION_MAIN
                }
                val label = customAppName.trim().ifBlank { "Adera Password Manager" }
                val shortcut = ShortcutInfo.Builder(context, "custom_vault_shortcut")
                    .setShortLabel(label.take(25))
                    .setLongLabel(label)
                    .setIcon(Icon.createWithResource(context, R.drawable.ic_brand_safe))
                    .setIntent(intent)
                    .build()
                shortcutManager.dynamicShortcuts = listOf(shortcut)
            } catch (_: Exception) {
                // Suppressed
            }
        }
    }
}
