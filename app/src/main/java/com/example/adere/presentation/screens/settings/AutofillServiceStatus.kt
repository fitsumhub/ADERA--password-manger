package com.example.adere.presentation.screens.settings

import android.content.ComponentName
import android.content.Context
import android.os.Build
import com.example.adere.service.AdereAutofillService

fun isAderaAutofillService(context: Context, flattenedComponentName: String?): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        return false
    }
    val configuredComponent = flattenedComponentName
        ?.let(ComponentName::unflattenFromString)
        ?: return false

    return configuredComponent == ComponentName(context, AdereAutofillService::class.java)
}
