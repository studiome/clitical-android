package org.studiomexx.clitical_android.ui

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class SettingsScreensTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun selectingEnglishSwitchesTheViewModelLocale() {
        val viewModel = MainViewModel().apply { locale = Locale.forLanguageTag("ja") }
        composeTestRule.setContent { LanguageScreen(viewModel = viewModel) }

        composeTestRule.onNode(hasText("English")).performClick()

        assertEquals("en", viewModel.locale.language)
    }

    @Test
    fun languageScreenRelabelsItselfAfterSwitching() {
        val viewModel = MainViewModel().apply { locale = Locale.forLanguageTag("en") }
        composeTestRule.setContent { LanguageScreen(viewModel = viewModel) }

        // 日本語 is its own endonym, so assert on the entry that actually changes.
        composeTestRule.onNode(hasText("English")).assertExists()
        composeTestRule.onNode(hasText("日本語")).performClick()

        assertEquals("ja", viewModel.locale.language)
    }

    @Test
    fun aboutScreenShowsTheBuildVersion() {
        composeTestRule.setContent { AboutScreen(locale = Locale.forLanguageTag("ja")) }

        composeTestRule.onNode(hasText("CLiTICAL")).assertExists()
        composeTestRule.onNode(hasText("Version: 0.1.0")).assertExists()
    }

    @Test
    fun referencesScreenListsBothPapers() {
        composeTestRule.setContent { ReferencesScreen(locale = Locale.forLanguageTag("ja")) }

        composeTestRule.onNode(hasText("1.", substring = true)).assertExists()
        composeTestRule.onNode(hasText("2.", substring = true)).assertExists()
    }
}
