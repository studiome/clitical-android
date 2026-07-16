package org.studiomexx.clitical_android.ui

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowToast
import org.studiomexx.clitical_android.model.Sex
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class QuestionFormTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun showForm(locale: String = "ja"): MainViewModel {
        val viewModel = MainViewModel().apply { this.locale = Locale.forLanguageTag(locale) }
        composeTestRule.setContent { QuestionForm(viewModel = viewModel) }
        return viewModel
    }

    /** Fills every field the validator requires, so only the field under test can fail it. */
    private fun MainViewModel.fillRequiredFields() {
        ageText = "65"
        heightText = "150"
        weightText = "50"
        albText = "4.0"
    }

    // The form is a LazyColumn, so a row only exists once scrolled into view.
    private fun scrollTo(matcher: SemanticsMatcher) =
        composeTestRule.onNode(hasScrollAction()).performScrollToNode(matcher)

    private fun rowLabelled(label: String) = hasText(label)

    // Each row is a ListItem holding its label and its control as siblings. ListItem
    // merges its descendants, so the two are only distinguishable in the unmerged tree.
    private fun onSwitchLabelled(label: String) =
        composeTestRule.onNode(isToggleable() and hasAnySibling(hasText(label)), useUnmergedTree = true)

    private fun onFieldLabelled(label: String) =
        composeTestRule.onNode(hasSetTextAction() and hasAnySibling(hasText(label)), useUnmergedTree = true)

    @Test
    fun rendersLabelsInTheSelectedLocale() {
        showForm(locale = "ja")

        composeTestRule.onNode(rowLabelled("基本情報")).assertExists()
        composeTestRule.onNode(rowLabelled("Basic Info")).assertDoesNotExist()
    }

    @Test
    fun rendersLabelsInEnglishWhenLocaleIsEnglish() {
        showForm(locale = "en")

        composeTestRule.onNode(rowLabelled("Basic Info")).assertExists()
        composeTestRule.onNode(rowLabelled("基本情報")).assertDoesNotExist()
    }

    @Test
    fun typingAgeUpdatesTheViewModel() {
        val viewModel = showForm()

        onFieldLabelled("年齢 [歳]").performTextInput("65")

        assertEquals("65", viewModel.ageText)
    }

    @Test
    fun rejectsNonNumericAge() {
        val viewModel = showForm()

        onFieldLabelled("年齢 [歳]").performTextInput("abc")

        assertEquals("", viewModel.ageText)
    }

    @Test
    fun rejectsAgeWithADecimalPoint() {
        val viewModel = showForm()

        onFieldLabelled("年齢 [歳]").performTextInput("6.5")

        assertEquals("", viewModel.ageText)
    }

    @Test
    fun acceptsDecimalHeight() {
        val viewModel = showForm()

        onFieldLabelled("身長 [cm]").performTextInput("165.5")

        assertEquals("165.5", viewModel.heightText)
    }

    @Test
    fun togglingASwitchUpdatesPatientData() {
        val viewModel = showForm()

        scrollTo(rowLabelled("喫煙歴"))
        onSwitchLabelled("喫煙歴").performClick()

        assertEquals(true, viewModel.patientData.isSmoking)
    }

    @Test
    fun switchesReflectPatientDataDefaults() {
        showForm()

        // hasAILesion defaults to true, hasFPLesion to false.
        scrollTo(rowLabelled("大動脈腸骨動脈領域病変"))
        onSwitchLabelled("大動脈腸骨動脈領域病変").assertIsOn()
        onSwitchLabelled("大腿膝窩領域病変").assertIsOff()
    }

    @Test
    fun selectingAnEnumOptionUpdatesPatientDataAndCollapsesTheRow() {
        val viewModel = showForm()
        assertEquals(Sex.FEMALE, viewModel.patientData.sex)

        // Collapsed, the row only shows the current selection, so the other option is absent.
        composeTestRule.onNode(rowLabelled("男性")).assertDoesNotExist()

        composeTestRule.onNode(rowLabelled("性別")).performClick()
        composeTestRule.onNode(rowLabelled("男性")).performClick()

        assertEquals(Sex.MALE, viewModel.patientData.sex)
        // Collapsed again: 男性 now shows as the selection, and 女性 is gone with the options.
        composeTestRule.onNode(rowLabelled("女性")).assertDoesNotExist()
    }

    @Test
    fun predictingWithValidInputProducesARisk() {
        val viewModel = showForm()
        viewModel.fillRequiredFields()

        scrollTo(rowLabelled("リスク解析実行"))
        composeTestRule.onNode(rowLabelled("リスク解析実行")).performClick()

        assertNotNull(viewModel.calculatedRisk)
        assertEquals(0, ShadowToast.shownToastCount())
    }

    @Test
    fun predictingWithEmptyFieldsShowsTheNumericInputError() {
        val viewModel = showForm()

        scrollTo(rowLabelled("リスク解析実行"))
        composeTestRule.onNode(rowLabelled("リスク解析実行")).performClick()

        assertNull(viewModel.calculatedRisk)
        assertEquals("数値入力を確認してください。", ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun predictingWithNoLesionSelectedShowsTheLesionError() {
        val viewModel = showForm()
        viewModel.fillRequiredFields()

        // hasAILesion defaults to true; clearing it leaves no lesion selected.
        scrollTo(rowLabelled("大動脈腸骨動脈領域病変"))
        onSwitchLabelled("大動脈腸骨動脈領域病変").performClick()
        scrollTo(rowLabelled("リスク解析実行"))
        composeTestRule.onNode(rowLabelled("リスク解析実行")).performClick()

        assertNull(viewModel.calculatedRisk)
        assertEquals("動脈病変の領域選択を確認してください。", ShadowToast.getTextOfLatestToast())
    }

    /** Regression: the Toast used to resolve via the system locale, ignoring the in-app switcher. */
    @Test
    fun errorToastFollowsTheInAppLocaleRatherThanTheSystemLocale() {
        Locale.setDefault(Locale.JAPAN)
        showForm(locale = "en")

        scrollTo(rowLabelled("Predict Risks"))
        composeTestRule.onNode(rowLabelled("Predict Risks")).performClick()

        assertEquals("Error! Missing some data at Number Form.", ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun resetClearsInputAndResult() {
        val viewModel = showForm()
        viewModel.fillRequiredFields()
        scrollTo(rowLabelled("喫煙歴"))
        onSwitchLabelled("喫煙歴").performClick()
        scrollTo(rowLabelled("リスク解析実行"))
        composeTestRule.onNode(rowLabelled("リスク解析実行")).performClick()
        assertNotNull(viewModel.calculatedRisk)

        scrollTo(rowLabelled("リセット"))
        composeTestRule.onNode(rowLabelled("リセット")).performClick()

        assertEquals("", viewModel.ageText)
        assertEquals(false, viewModel.patientData.isSmoking)
        assertNull(viewModel.calculatedRisk)
    }
}
