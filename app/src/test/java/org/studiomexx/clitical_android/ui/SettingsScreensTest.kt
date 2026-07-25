package org.studiomexx.clitical_android.ui

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.core.net.toUri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class SettingsScreensTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun selectingEnglishSwitchesTheViewModelLocale() {
        val viewModel = MainViewModel().apply { locale = Locale.forLanguageTag("ja") }
        composeTestRule.setContent { SettingsScreen(viewModel = viewModel) }

        composeTestRule.onNode(hasText("English")).performClick()

        assertEquals("en", viewModel.locale.language)
    }

    @Test
    fun settingsScreenRelabelsItselfAfterSwitchingLanguage() {
        val viewModel = MainViewModel().apply { locale = Locale.forLanguageTag("en") }
        composeTestRule.setContent { SettingsScreen(viewModel = viewModel) }

        // 日本語 is its own endonym, so assert on the entry that actually changes.
        composeTestRule.onNode(hasText("English")).assertExists()
        composeTestRule.onNode(hasText("日本語")).performClick()

        assertEquals("ja", viewModel.locale.language)
    }

    @Test
    fun settingsScreenShowsTheBuildVersion() {
        val viewModel = MainViewModel().apply { locale = Locale.forLanguageTag("ja") }
        composeTestRule.setContent { SettingsScreen(viewModel = viewModel) }

        // Matching clitical-ios, the row shows the bare version number with no
        // "Version:" label, so the About row has nothing left to translate. The
        // row is one node so the number is announced with the app it belongs to.
        composeTestRule.onNode(hasText("CLiTICAL") and hasText("0.1.0")).assertExists()
        composeTestRule.onNode(hasText("Version: 0.1.0")).assertDoesNotExist()
    }

    @Test
    fun referencesScreenListsBothPapers() {
        composeTestRule.setContent { ReferencesScreen(locale = Locale.forLanguageTag("ja")) }

        composeTestRule.onNode(hasText("1.", substring = true)).assertExists()
        composeTestRule.onNode(hasText("2.", substring = true)).assertExists()
    }

    @Test
    fun referencesScreenOpensFirstPaperInAnInAppBrowser() {
        composeTestRule.setContent { ReferencesScreen(locale = Locale.forLanguageTag("ja")) }

        composeTestRule.onNode(hasText("1.", substring = true)).performClick()

        val intent = shadowOf(composeTestRule.activity).nextStartedActivity
        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals("https://doi.org/10.1093/bjs/znab036".toUri(), intent.data)
        assertTrue(intent.hasExtra(CustomTabsIntent.EXTRA_SESSION))
    }

    @Test
    fun referencesScreenOpensSecondPaperInAnInAppBrowser() {
        composeTestRule.setContent { ReferencesScreen(locale = Locale.forLanguageTag("ja")) }

        composeTestRule.onNode(hasText("2.", substring = true)).performClick()

        val intent = shadowOf(composeTestRule.activity).nextStartedActivity
        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals("https://doi.org/10.1016/j.ejvs.2022.05.038".toUri(), intent.data)
        assertTrue(intent.hasExtra(CustomTabsIntent.EXTRA_SESSION))
    }

    @Test
    fun settingsScreenOpensTermsOfServiceInAnInAppBrowser() {
        val viewModel = MainViewModel().apply { locale = Locale.forLanguageTag("ja") }
        composeTestRule.setContent { SettingsScreen(viewModel = viewModel) }

        composeTestRule.onNode(hasText("利用規約")).performClick()

        val intent = shadowOf(composeTestRule.activity).nextStartedActivity
        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals("https://studiome.github.io/clti_risk/".toUri(), intent.data)
        assertTrue(intent.hasExtra(CustomTabsIntent.EXTRA_SESSION))
    }
}
