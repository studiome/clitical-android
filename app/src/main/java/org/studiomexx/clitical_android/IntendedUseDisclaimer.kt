package org.studiomexx.clitical_android

import android.content.SharedPreferences

/** Versioned record of the user's acknowledgement of the intended-use notice. */
object IntendedUseDisclaimer {
    const val STORAGE_KEY = "intended_use_disclaimer_version"
    const val CURRENT_VERSION = "2026-08"
}

/** Small synchronous store used at startup to avoid rendering the app briefly before the gate. */
class IntendedUseDisclaimerStore(private val preferences: SharedPreferences) {
    val acknowledgedVersion: String
        get() = preferences.getString(IntendedUseDisclaimer.STORAGE_KEY, "").orEmpty()

    val isAcknowledged: Boolean
        get() = acknowledgedVersion == IntendedUseDisclaimer.CURRENT_VERSION

    fun acknowledge() {
        preferences.edit()
            .putString(IntendedUseDisclaimer.STORAGE_KEY, IntendedUseDisclaimer.CURRENT_VERSION)
            .apply()
    }
}
