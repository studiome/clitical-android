package org.studiomexx.clitical_android

import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Covers the AnimatedContent switch between the question form and the result screen. */
@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun fillField(label: String, text: String) {
        val matcher = hasSetTextAction() and hasContentDescription(label)
        composeTestRule.onNode(hasScrollAction()).performScrollToNode(matcher)
        composeTestRule.onNode(matcher).performTextInput(text)
    }

    private fun fillRequiredFieldsAndSubmit() {
        fillField("年齢 [歳]", "65")
        fillField("身長 [cm]", "150")
        fillField("体重 [kg]", "50")
        fillField("アルブミン値 [g/dl]", "4.0")
        composeTestRule.onNode(hasScrollAction()).performScrollToNode(hasText("リスク予測"))
        composeTestRule.onNodeWithText("リスク予測").performClick()
    }

    @Test
    fun submittingValidInputsNavigatesToTheResultScreen() {
        fillRequiredFieldsAndSubmit()

        composeTestRule.onNodeWithText("予測リスク").assertExists()
    }

    @Test
    fun backNavigatesFromTheResultScreenToTheForm() {
        fillRequiredFieldsAndSubmit()
        composeTestRule.onNodeWithText("予測リスク").assertExists()

        composeTestRule.activity.onBackPressedDispatcher.onBackPressed()

        // The result screen has no age field, so its reappearance confirms we're back on the form.
        composeTestRule.onNode(hasSetTextAction() and hasContentDescription("年齢 [歳]")).assertExists()
    }
}
