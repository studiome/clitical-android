package org.studiomexx.clitical_android

import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.Locale

/** Where the in-app language choice is kept, in the same preferences file as the notice. */
object AppLanguage {
    const val PREFERENCES_NAME = "clitical_preferences"
    const val STORAGE_KEY = "app_language"
}

/** Synchronous like [IntendedUseDisclaimerStore], so the first frame is already in the right language. */
class LanguageStore(private val preferences: SharedPreferences) {
    /**
     * The saved choice; before one is made, the device language if it's Japanese, else English
     * (the app only ships those two).
     */
    var locale: Locale
        get() {
            val tag = preferences.getString(AppLanguage.STORAGE_KEY, null)
                ?: if (Locale.getDefault().language == "ja") "ja" else "en"
            return Locale.forLanguageTag(tag)
        }
        set(value) {
            preferences.edit { putString(AppLanguage.STORAGE_KEY, value.toLanguageTag()) }
        }
}
