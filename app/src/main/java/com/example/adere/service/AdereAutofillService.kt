package com.example.adere.service

import android.app.PendingIntent
import android.app.assist.AssistStructure
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillContext
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveInfo
import android.service.autofill.SaveRequest
import android.text.InputType
import android.view.View
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.example.R
import com.example.adere.AdereApplication
import com.example.adere.domain.model.VaultItem
import com.example.adere.domain.repository.VaultLockState
import com.example.adere.core.utilities.AppErrorHandler
import com.example.adere.core.utilities.AppErrorHandler.Operation
import kotlinx.coroutines.runBlocking

/**
 * Android System Autofill Service for Adere.
 *
 * Provides inline, zero-knowledge credential autofill across Android native apps
 * and mobile browsers (Chrome, Brave, Firefox, etc.) with hardware biometric verification.
 */
@RequiresApi(Build.VERSION_CODES.O)
class AdereAutofillService : AutofillService() {

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val fillContexts: List<FillContext> = request.fillContexts
        if (fillContexts.isEmpty()) {
            callback.onSuccess(null)
            return
        }

        val structure = fillContexts.last().structure
        val parsedFields = parseStructure(structure)

        if (parsedFields.passwordId == null && parsedFields.usernameId == null) {
            callback.onSuccess(null)
            return
        }

        val app = applicationContext as? AdereApplication
        val container = app?.container
        if (container == null) {
            callback.onSuccess(null)
            return
        }

        val lockState = container.sessionManager.lockState.value

        if (lockState == VaultLockState.Locked || lockState == VaultLockState.Uninitialized) {
            // Vault is locked: Offer authenticated unlock prompt
            val authIntent = Intent(this, AutofillAuthActivity::class.java).apply {
                putExtra(AutofillAuthActivity.EXTRA_WEB_DOMAIN, parsedFields.webDomain)
                putExtra(AutofillAuthActivity.EXTRA_PACKAGE_NAME, parsedFields.packageName)
                if (parsedFields.usernameId != null) {
                    putExtra(AutofillAuthActivity.EXTRA_USERNAME_ID, parsedFields.usernameId)
                }
                if (parsedFields.passwordId != null) {
                    putExtra(AutofillAuthActivity.EXTRA_PASSWORD_ID, parsedFields.passwordId)
                }
            }

            val pendingIntent = PendingIntent.getActivity(
                this,
                1001,
                authIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            val authPresentation = RemoteViews(packageName, R.layout.autofill_dataset_item).apply {
                setTextViewText(R.id.autofill_title, "Unlock Adera to Autofill")
                setTextViewText(R.id.autofill_subtitle, "Touch with fingerprint or master password")
            }

            val targetIds = listOfNotNull(parsedFields.usernameId, parsedFields.passwordId).toTypedArray()
            val response = FillResponse.Builder()
                .setAuthentication(targetIds, pendingIntent.intentSender, authPresentation)
                .build()

            callback.onSuccess(response)
            return
        }

        // Vault is Unlocked: Fetch matching credentials
        val allItems: List<VaultItem> = try {
            runBlocking { container.vaultRepository.getAllDecryptedItems() }
        } catch (error: Exception) {
            callback.onFailure(AppErrorHandler.getMessage(error, Operation.GENERAL_SECURITY))
            return
        }

        val matchingItems = findMatchingItems(
            items = allItems,
            webDomain = parsedFields.webDomain
        )

        if (matchingItems.isEmpty()) {
            callback.onSuccess(null)
            return
        }

        val responseBuilder = FillResponse.Builder()

        for (item in matchingItems.take(4)) {
            val presentation = RemoteViews(packageName, R.layout.autofill_dataset_item).apply {
                setTextViewText(R.id.autofill_title, item.title)
                setTextViewText(
                    R.id.autofill_subtitle,
                    if (item.username.isNotBlank()) item.username else "Adera Password"
                )
            }

            val datasetBuilder = Dataset.Builder(presentation)

            val uId = parsedFields.usernameId
            if (uId != null && item.username.isNotBlank()) {
                datasetBuilder.setValue(
                    uId,
                    AutofillValue.forText(item.username)
                )
            }

            val pId = parsedFields.passwordId
            if (pId != null && item.payload.password.isNotBlank()) {
                datasetBuilder.setValue(
                    pId,
                    AutofillValue.forText(item.payload.password)
                )
            }

            responseBuilder.addDataset(datasetBuilder.build())
        }

        callback.onSuccess(responseBuilder.build())
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        callback.onSuccess()
    }

    private data class ParsedFields(
        var usernameId: AutofillId? = null,
        var passwordId: AutofillId? = null,
        var webDomain: String? = null,
        var packageName: String? = null
    )

    private fun parseStructure(structure: AssistStructure): ParsedFields {
        val result = ParsedFields()
        val nodeCount = structure.windowNodeCount
        for (i in 0 until nodeCount) {
            val windowNode = structure.getWindowNodeAt(i)
            val rootNode = windowNode.rootViewNode
            parseNode(rootNode, result)
        }
        return result
    }

    private fun parseNode(node: AssistStructure.ViewNode, result: ParsedFields) {
        val hints = node.autofillHints
        val webDomain = node.webDomain
        if (!webDomain.isNullOrBlank()) {
            result.webDomain = webDomain
        }
        if (result.packageName.isNullOrBlank() && !node.idPackage.isNullOrBlank()) {
            result.packageName = node.idPackage
        }

        if (hints != null) {
            for (hint in hints) {
                when (hint) {
                    View.AUTOFILL_HINT_PASSWORD -> result.passwordId = node.autofillId
                    View.AUTOFILL_HINT_USERNAME, View.AUTOFILL_HINT_EMAIL_ADDRESS -> result.usernameId = node.autofillId
                }
            }
        }

        val inputType = node.inputType
        if (result.passwordId == null && isPasswordInputType(inputType)) {
            result.passwordId = node.autofillId
        }

        val childCount = node.childCount
        for (i in 0 until childCount) {
            parseNode(node.getChildAt(i), result)
        }
    }

    private fun isPasswordInputType(inputType: Int): Boolean {
        val variation = inputType and (InputType.TYPE_MASK_CLASS or InputType.TYPE_MASK_VARIATION)
        return variation == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) ||
                variation == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD) ||
                variation == (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD)
    }

    private fun findMatchingItems(
        items: List<VaultItem>,
        webDomain: String?
    ): List<VaultItem> {
        val cleanDomain = webDomain?.replace("www.", "")?.lowercase()

        return items.filter { item ->
            val itemUrl = item.payload.url.lowercase().replace("https://", "").replace("http://", "").replace("www.", "")
            val domainPrefix = if (itemUrl.contains("/")) itemUrl.substringBefore("/") else itemUrl
            val domainMatch = !cleanDomain.isNullOrBlank() &&
                (domainPrefix == cleanDomain || domainPrefix.endsWith(".$cleanDomain") ||
                    cleanDomain.endsWith(".$domainPrefix"))
            domainMatch && item.payload.password.isNotBlank()
        }
    }
}
