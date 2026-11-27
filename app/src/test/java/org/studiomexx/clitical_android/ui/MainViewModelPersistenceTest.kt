package org.studiomexx.clitical_android.ui

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.studiomexx.clitical_android.LanguageStore
import java.util.Locale

/** What must survive a restart (language) or the process being killed in the background (answers). */
@RunWith(RobolectricTestRunner::class)
class MainViewModelPersistenceTest {
    private val preferences = ApplicationProvider.getApplicationContext<Context>()
        .getSharedPreferences("viewmodel_persistence_test", Context.MODE_PRIVATE)

    @Before
    fun clear() {
        preferences.edit().clear().commit()
    }

    @Test
    fun localeStartsFromTheStore() {
        preferences.edit().putString("app_language", "en").commit()

        val viewModel = MainViewModel(languageStore = LanguageStore(preferences))

        assertEquals("en", viewModel.locale.language)
    }

    @Test
    fun changingTheLocaleIsSaved() {
        val viewModel = MainViewModel(languageStore = LanguageStore(preferences))

        viewModel.locale = Locale.forLanguageTag("en")

        assertEquals("en", LanguageStore(preferences).locale.language)
        assertEquals("en", MainViewModel(languageStore = LanguageStore(preferences)).locale.language)
    }

    @Test
    fun answersAreRestoredFromTheSavedStateHandle() {
        val handle = SavedStateHandle()
        MainViewModel(savedStateHandle = handle).apply {
            ageText = "65"
            weightText = "50"
            updatePatientData { it.copy(isSmoking = true) }
        }

        val restored = MainViewModel(savedStateHandle = handle)

        assertEquals("65", restored.ageText)
        assertEquals("50", restored.weightText)
        assertTrue(restored.patientData.isSmoking)
    }

    @Test
    fun inputsAreWrittenThroughToTheHandle() {
        val handle = SavedStateHandle()
        val viewModel = MainViewModel(savedStateHandle = handle)

        viewModel.albText = "4.0"

        assertEquals("4.0", handle.get<String>("albText"))
    }
}
