package com.example.adere.presentation.screens.settings

import android.content.ComponentName
import android.content.Context
import com.example.adere.service.AdereAutofillService

fun isAderaAutofillService(context: Context, flattenedComponentName: String?): Boolean {
    val configuredComponent = flattenedComponentName
        ?.let(ComponentName::unflattenFromString)
        ?: return false

    return configuredComponent == ComponentName(context, AdereAutofillService::class.java)
}
