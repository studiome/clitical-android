package org.studiomexx.clitical_android.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class DisclaimerScreensTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun readTermsRowExposesALocalizedOpenInBrowserActionLabel() {
        var locale by mutableStateOf(Locale.forLanguageTag("ja"))
        composeTestRule.setContent {
            IntendedUseDisclaimerScreen(locale = locale) {}
        }

        val japaneseAction = composeTestRule.onNode(hasText("利用規約・免責事項を読む"))
            .fetchSemanticsNode().config[SemanticsActions.OnClick]
        assertEquals("ブラウザで利用規約を開く", japaneseAction.label)

        locale = Locale.forLanguageTag("en")
        composeTestRule.waitForIdle()
        val englishAction = composeTestRule.onNode(hasText("Read the Terms of Use and disclaimer"))
            .fetchSemanticsNode().config[SemanticsActions.OnClick]
        assertEquals("Open terms of service in browser", englishAction.label)
    }
}
