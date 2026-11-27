package org.studiomexx.clitical_android

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class LanguageStoreTest {
    private val preferences = ApplicationProvider.getApplicationContext<Context>()
        .getSharedPreferences("language_store_test", Context.MODE_PRIVATE)
    private val originalLocale = Locale.getDefault()

    @Before
    fun clear() {
        preferences.edit().clear().commit()
    }

    @After
    fun restoreLocale() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun defaultsToJapaneseOnAJapaneseDevice() {
        Locale.setDefault(Locale.JAPAN)

        assertEquals("ja", LanguageStore(preferences).locale.language)
    }

    @Test
    fun defaultsToEnglishOnAnyOtherDevice() {
        Locale.setDefault(Locale.FRANCE)

        assertEquals("en", LanguageStore(preferences).locale.language)
    }

    @Test
    fun savedChoiceWinsOverTheDeviceLanguage() {
        preferences.edit().putString("app_language", "en").commit()
        Locale.setDefault(Locale.JAPAN)

        assertEquals("en", LanguageStore(preferences).locale.language)
    }

    @Test
    fun savedChoiceRoundTrips() {
        Locale.setDefault(Locale.FRANCE)

        LanguageStore(preferences).locale = Locale.forLanguageTag("ja")

        assertEquals("ja", LanguageStore(preferences).locale.language)
    }
}
