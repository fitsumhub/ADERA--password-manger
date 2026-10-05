package com.example

import android.content.ComponentName
import com.example.adere.data.local.VaultConfigStore
import com.example.adere.domain.repository.VaultLockState
import com.example.adere.domain.repository.VaultSessionManager
import com.example.adere.presentation.screens.settings.isAderaAutofillService
import com.example.adere.service.AdereAutofillService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertThrows
import org.robolectric.RuntimeEnvironment
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SecuritySettingsTest {

    @Test
    fun autofillServiceStatusOnlyMatchesAderaProvider() {
        val context = RuntimeEnvironment.getApplication()
        val aderaService = ComponentName(context, AdereAutofillService::class.java)
        val anotherService = ComponentName("com.example.other", "com.example.other.AutofillService")

        assertTrue(isAderaAutofillService(context, aderaService.flattenToString()))
        assertFalse(isAderaAutofillService(context, anotherService.flattenToString()))
        assertFalse(isAderaAutofillService(context, "invalid-component"))
        assertFalse(isAderaAutofillService(context, null))
    }

    @Test
    fun autoLockOptionsPersistAndLockTheVaultAfterConfiguredInactivity() = kotlinx.coroutines.runBlocking {
        val context = RuntimeEnvironment.getApplication()
        val config = VaultConfigStore(context).apply { resetVault() }
        var now = 1_000_000L
        val session = VaultSessionManager(config, FakeBiometricKeyStore()) { now }
        val password = "AutoLockTestPass123!".toCharArray()

        assertTrue(session.initializeVault(password, enableBiometric = false).isSuccess)

        for (seconds in listOf(0, 30, 60, 300, 900, 3600)) {
            assertTrue(config.persistAutoLockSeconds(seconds))
            assertEquals(seconds, VaultConfigStore(context).autoLockSeconds)

            session.recordInteraction()
            if (seconds == 0) {
                session.checkAutoLock()
                assertEquals(VaultLockState.Unlocked, session.lockState.value)
                session.lock()
            } else {
                now += seconds * 1_000L
                session.checkAutoLock()
            }

            assertEquals(VaultLockState.Locked, session.lockState.value)
            assertNull(session.getActiveDek())

            if (seconds != 3600) {
                assertTrue(session.unlockWithMasterPassword(password).isSuccess)
            }
        }
    }

    @Test
    fun autoLockRejectsUnsupportedValuesWithoutChangingStoredSetting() {
        val config = VaultConfigStore(RuntimeEnvironment.getApplication()).apply { resetVault() }
        assertTrue(config.persistAutoLockSeconds(60))

        assertThrows(IllegalArgumentException::class.java) {
            config.persistAutoLockSeconds(45)
        }

        assertEquals(60, config.autoLockSeconds)
        assertFalse(45 in VaultConfigStore.AUTO_LOCK_OPTIONS_SECONDS)
    }
}
