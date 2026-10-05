package com.example.adere.core.utilities

import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.UserNotAuthenticatedException
import android.util.Log
import com.example.BuildConfig

object AppErrorHandler {

    enum class Operation {
        BIOMETRIC_ENABLE,
        BIOMETRIC_UNLOCK,
        AUTO_LOCK,
        RECOVERY_KEY,
        GENERAL_SECURITY
    }

    fun getMessage(error: Throwable?, operation: Operation): String {
        logSafely(error, operation)

        val raw = error?.localizedMessage ?: error?.message
        val isSafeUserMessage = !raw.isNullOrBlank() && 
            raw != "null" && 
            !raw.contains("Exception") &&
            !raw.contains("@") &&
            raw.length <= 120

        return when {
            error is KeyPermanentlyInvalidatedException ->
                "Biometric credentials changed. Unlock with your master password, then set up biometrics again."
            error is UserNotAuthenticatedException ->
                "Biometric authentication is required to complete this action."
            error is IllegalStateException && raw?.contains("unlocked", ignoreCase = true) == true ->
                "Vault must be unlocked to modify security settings."
            error is IllegalArgumentException && operation == Operation.AUTO_LOCK ->
                "Choose one of the available auto-lock intervals."
            operation == Operation.BIOMETRIC_ENABLE ->
                if (isSafeUserMessage) raw
                else "Unable to enable biometric authentication. Please check that biometrics are configured on this device and try again."
            operation == Operation.BIOMETRIC_UNLOCK ->
                "Unable to authenticate with biometrics. Please unlock with your master password."
            operation == Operation.AUTO_LOCK ->
                if (isSafeUserMessage) raw
                else "Unable to change auto-lock settings. Please try again."
            operation == Operation.RECOVERY_KEY ->
                if (isSafeUserMessage) raw
                else "Unable to update the recovery key. Please try again."
            else ->
                if (isSafeUserMessage) raw
                else "Unable to update security settings. Something went wrong while applying this setting. Please try again."
        }
    }

    private fun logSafely(error: Throwable?, operation: Operation) {
        if (!BuildConfig.DEBUG) return

        val exceptionTypes = generateSequence(error) { it.cause }
            .take(5)
            .joinToString(" <- ") { it.javaClass.name }
            .ifBlank { "unknown" }
        Log.e("AppErrorHandler", "$operation failed ($exceptionTypes)")
    }
}
