package org.studiomexx.clitical_android.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.studiomexx.clitical_android.model.PatientData
import org.studiomexx.clitical_android.model.PatientRisk
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class ResultScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /** Matches PatientRiskTest.normalCase, whose expected values are asserted there. */
    private val normalCase = PatientData(age = 65, weight = 50.0, height = 1.50, alb = 4.0)

    private fun showResult(
        data: PatientData = normalCase,
        locale: String = "ja",
        onBack: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            ResultScreen(
                risk = PatientRisk(data),
                onBack = onBack,
                locale = Locale.forLanguageTag(locale)
            )
        }
    }

    @Test
    fun rendersGnriAndItsRiskLabel() {
        showResult()

        composeTestRule.onNode(hasText("101.3")).assertExists()
        composeTestRule.onNode(hasText("リスクなし")).assertExists()
    }

    @Test
    fun formatsShortTermRisksAsPercentages() {
        showResult()

        // PatientRiskTest pins these to 0.013 and 0.032.
        composeTestRule.onNode(hasText("1.3%")).assertExists()
        composeTestRule.onNode(hasText("3.2%")).assertExists()
    }

    @Test
    fun formatsSurvivalRisksAsWholePercentages() {
        showResult()

        // PatientRiskTest pins these to 0.92 and 0.88.
        composeTestRule.onNode(hasText("92%")).assertExists()
        composeTestRule.onNode(hasText("88%")).assertExists()
    }

    @Test
    fun rendersInTheSelectedLocale() {
        showResult(locale = "en")

        composeTestRule.onNode(hasText("No Risk")).assertExists()
        composeTestRule.onNode(hasText("リスクなし")).assertDoesNotExist()
    }

    @Test
    fun showsNotAvailableForEveryValueThatCannotBeComputed() {
        // A zero height makes GNRI, and everything derived from it, NaN.
        showResult(data = normalCase.copy(height = 0.0))

        // Five card values plus the OS and GNRI risk labels all degrade to N/A.
        composeTestRule.onAllNodes(hasText("該当なし")).assertCountEquals(7)
    }

    @Test
    fun backButtonInvokesTheCallback() {
        var backCount = 0
        showResult(onBack = { backCount++ })

        composeTestRule.onNodeWithContentDescription("戻る").performClick()

        assertEquals(1, backCount)
    }
}
