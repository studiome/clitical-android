package org.studiomexx.clitical_android

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.robolectric.RuntimeEnvironment
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class IntendedUseDisclaimerTest {
    private val preferences = RuntimeEnvironment.getApplication()
        .getSharedPreferences("test_disclaimer", Context.MODE_PRIVATE)

    @Test
    fun unacknowledgedOrOldVersionMustShowTheGate() {
        preferences.edit().clear().commit()
        val store = IntendedUseDisclaimerStore(preferences)

        assertFalse(store.isAcknowledged)
        store.acknowledge()
        assertTrue(store.isAcknowledged)
        assertEquals(IntendedUseDisclaimer.CURRENT_VERSION, store.acknowledgedVersion)

        preferences.edit().putString(IntendedUseDisclaimer.STORAGE_KEY, "2026-07").commit()
        assertFalse(IntendedUseDisclaimerStore(preferences).isAcknowledged)
    }
}
