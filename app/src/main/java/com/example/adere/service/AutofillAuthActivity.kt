package com.example.adere.service

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.autofill.Dataset
import android.view.autofill.AutofillId
import android.view.autofill.AutofillManager
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.R
import com.example.adere.AdereApplication
import com.example.adere.core.utilities.HapticFeedbackHelper
import com.example.adere.domain.model.VaultItem
import com.example.adere.domain.repository.VaultLockState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Trampoline activity for authenticated autofill requests.
 * Prompts user for Biometrics or Master Password, decrypts requested match,
 * and passes the Dataset back to the calling app/browser.
 */
@RequiresApi(Build.VERSION_CODES.O)
class AutofillAuthActivity : FragmentActivity() {

    companion object {
        const val EXTRA_WEB_DOMAIN = "extra_web_domain"
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_USERNAME_ID = "extra_username_id"
        const val EXTRA_PASSWORD_ID = "extra_password_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = applicationContext as? AdereApplication
        val container = app?.container
        if (container == null) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        val sessionManager = container.sessionManager

        // If already unlocked, fulfill immediately
        if (sessionManager.lockState.value == VaultLockState.Unlocked) {
            fulfillAutofill(container)
            return
        }

        // Trigger Biometric verification
        val executor = ContextCompat.getMainExecutor(this)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Adera Vault")
            .setSubtitle("Authenticate with biometrics to autofill your credentials")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    HapticFeedbackHelper.confirm(this@AutofillAuthActivity)
                    CoroutineScope(Dispatchers.Main).launch {
                        val unlockRes = sessionManager.unlockWithBiometricAuth()
                        if (unlockRes.isSuccess) {
                            fulfillAutofill(container)
                        } else {
                            Toast.makeText(this@AutofillAuthActivity, "Authentication failed", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_CANCELED)
                            finish()
                        }
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    HapticFeedbackHelper.error(this@AutofillAuthActivity)
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    HapticFeedbackHelper.error(this@AutofillAuthActivity)
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }

    private fun fulfillAutofill(container: com.example.adere.core.common.AdereAppContainer) {
        val webDomain = intent.getStringExtra(EXTRA_WEB_DOMAIN)
        val packageNameExtra = intent.getStringExtra(EXTRA_PACKAGE_NAME)
        val usernameId: AutofillId? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_USERNAME_ID, AutofillId::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_USERNAME_ID)
        }

        val passwordId: AutofillId? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_PASSWORD_ID, AutofillId::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_PASSWORD_ID)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val allItems = container.vaultRepository.getAllDecryptedItems()
            val cleanDomain = webDomain?.replace("www.", "")?.lowercase()
            val cleanPkg = packageNameExtra?.lowercase()

            val bestMatch: VaultItem? = allItems.firstOrNull { item ->
                val itemUrl = item.payload.url.lowercase().replace("https://", "").replace("http://", "").replace("www.", "")
                val domainPrefix = if (itemUrl.contains("/")) itemUrl.substringBefore("/") else itemUrl
                val domainMatches = !cleanDomain.isNullOrBlank() &&
                    (domainPrefix == cleanDomain || domainPrefix.endsWith(".$cleanDomain") ||
                        cleanDomain.endsWith(".$domainPrefix"))
                domainMatches && item.payload.password.isNotBlank()
            }

            if (bestMatch != null) {
                val presentation = RemoteViews(packageName, R.layout.autofill_dataset_item).apply {
                    setTextViewText(R.id.autofill_title, bestMatch.title)
                    setTextViewText(R.id.autofill_subtitle, bestMatch.username)
                }

                val datasetBuilder = Dataset.Builder(presentation)
                if (usernameId != null && bestMatch.username.isNotBlank()) {
                    datasetBuilder.setValue(usernameId, AutofillValue.forText(bestMatch.username))
                }
                if (passwordId != null && bestMatch.payload.password.isNotBlank()) {
                    datasetBuilder.setValue(passwordId, AutofillValue.forText(bestMatch.payload.password))
                }

                val replyIntent = Intent().apply {
                    putExtra(AutofillManager.EXTRA_AUTHENTICATION_RESULT, datasetBuilder.build())
                }
                setResult(Activity.RESULT_OK, replyIntent)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }
            finish()
        }
    }
}
