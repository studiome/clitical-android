package org.studiomexx.clitical_android.ui

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.onNodeWithText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
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

    // Both controls carry their own label, so they are reachable the same way a
    // screen reader reaches them: on the merged tree, by that label.
    private fun onSwitchLabelled(label: String) =
        composeTestRule.onNode(isToggleable() and hasText(label))

    private fun onFieldLabelled(label: String) =
        composeTestRule.onNode(hasSetTextAction() and hasContentDescription(label))

    // Accessibility: a control must carry its own label, otherwise a screen reader
    // announces an anonymous "switch" or "edit box" next to a separate line of text.

    @Test
    fun switchIsAnnouncedWithItsLabel() {
        showForm()

        scrollTo(rowLabelled("喫煙歴"))
        // Merged tree: what a screen reader actually focuses.
        composeTestRule.onNode(isToggleable() and hasText("喫煙歴")).assertExists()
    }

    @Test
    fun textFieldIsAnnouncedWithItsLabel() {
        showForm()

        composeTestRule.onNode(hasSetTextAction() and hasContentDescription("年齢 [歳]")).assertExists()
    }

    @Test
    fun enumOptionExposesItsSelectedState() {
        val viewModel = showForm()
        assertEquals(Sex.FEMALE, viewModel.patientData.sex)

        // Open the dropdown via its anchor field, the same way TalkBack reaches it.
        composeTestRule.onNode(hasContentDescription("性別") and hasClickAction()).performClick()

        // One selectable node per option, not a nested row-plus-radio pair.
        composeTestRule.onAllNodes(isSelectable() and hasText("男性")).assertCountEquals(1)
        // The current selection is exposed as selection state, not just as displayed text.
        composeTestRule.onNode(isSelectable() and hasText("女性")).assertIsSelected()
    }

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
        scrollTo(rowLabelled("大腿膝窩領域病変"))
        onSwitchLabelled("大腿膝窩領域病変").assertIsOff()
    }

    @Test
    fun selectingAnEnumOptionUpdatesPatientDataAndCollapsesTheRow() {
        val viewModel = showForm()
        assertEquals(Sex.FEMALE, viewModel.patientData.sex)

        // Collapsed, the dropdown menu isn't showing its options yet.
        composeTestRule.onNode(isSelectable() and hasText("男性")).assertDoesNotExist()

        composeTestRule.onNode(hasContentDescription("性別") and hasClickAction()).performClick()
        composeTestRule.onNode(isSelectable() and hasText("男性")).performClick()

        assertEquals(Sex.MALE, viewModel.patientData.sex)
        // Selecting an option collapses the menu again.
        composeTestRule.onNode(isSelectable() and hasText("男性")).assertDoesNotExist()
    }

    @Test
    fun predictingWithValidInputProducesARisk() {
        val viewModel = showForm()
        viewModel.fillRequiredFields()

        scrollTo(rowLabelled("リスク予測"))
        composeTestRule.onNode(rowLabelled("リスク予測")).performClick()

        assertNotNull(viewModel.calculatedRisk)
        composeTestRule.onNodeWithText("数値入力を確認してください。").assertDoesNotExist()
        composeTestRule.onNodeWithText("動脈病変の領域選択を確認してください。").assertDoesNotExist()
    }

    @Test
    fun predictingWithEmptyFieldsShowsTheNumericInputError() {
        val viewModel = showForm()

        scrollTo(rowLabelled("リスク予測"))
        composeTestRule.onNode(rowLabelled("リスク予測")).performClick()

        assertNull(viewModel.calculatedRisk)
        composeTestRule.onNodeWithText("数値入力を確認してください。").assertExists()
    }

    @Test
    fun predictingWithNoLesionSelectedShowsTheLesionError() {
        val viewModel = showForm()
        viewModel.fillRequiredFields()

        // hasAILesion defaults to true; clearing it leaves no lesion selected.
        scrollTo(rowLabelled("大動脈腸骨動脈領域病変"))
        onSwitchLabelled("大動脈腸骨動脈領域病変").performClick()
        scrollTo(rowLabelled("リスク予測"))
        composeTestRule.onNode(rowLabelled("リスク予測")).performClick()

        assertNull(viewModel.calculatedRisk)
        composeTestRule.onNodeWithText("動脈病変の領域選択を確認してください。").assertExists()
    }

    @Test
    fun predictingWithEmptyFieldsShowsPersistentFieldErrorAndFocusesFirstField() {
        showForm()

        scrollTo(rowLabelled("リスク予測"))
        composeTestRule.onNode(rowLabelled("リスク予測")).performClick()

        composeTestRule.onNodeWithText("年齢を入力してください。").assertExists()
        onFieldLabelled("年齢 [歳]").assertIsFocused()
    }

    @Test
    fun lesionValidationErrorRemainsVisibleAfterSubmit() {
        val viewModel = showForm()
        viewModel.fillRequiredFields()

        scrollTo(rowLabelled("大動脈腸骨動脈領域病変"))
        onSwitchLabelled("大動脈腸骨動脈領域病変").performClick()
        scrollTo(rowLabelled("リスク予測"))
        composeTestRule.onNode(rowLabelled("リスク予測")).performClick()

        composeTestRule.onNodeWithText("動脈病変を1つ以上選択してください。").assertExists()
    }

    /** Regression: the error message used to resolve via the system locale, ignoring the in-app switcher. */
    @Test
    fun errorSnackbarFollowsTheInAppLocaleRatherThanTheSystemLocale() {
        Locale.setDefault(Locale.JAPAN)
        showForm(locale = "en")

        scrollTo(rowLabelled("Predict Risks"))
        composeTestRule.onNode(rowLabelled("Predict Risks")).performClick()

        composeTestRule.onNodeWithText("Error! Missing some data at Number Form.").assertExists()
    }

    @Test
    fun resetClearsInputAndResult() {
        val viewModel = showForm()
        viewModel.fillRequiredFields()
        scrollTo(rowLabelled("喫煙歴"))
        onSwitchLabelled("喫煙歴").performClick()
        scrollTo(rowLabelled("リスク予測"))
        composeTestRule.onNode(rowLabelled("リスク予測")).performClick()
        assertNotNull(viewModel.calculatedRisk)

        scrollTo(rowLabelled("リセット"))
        composeTestRule.onNode(rowLabelled("リセット")).performClick()

        assertEquals("", viewModel.ageText)
        assertEquals(false, viewModel.patientData.isSmoking)
        assertNull(viewModel.calculatedRisk)
    }

    @Test
    fun switchRowShowsItsClinicalDescription() {
        showForm()

        scrollTo(rowLabelled("発熱"))
        composeTestRule.onNode(hasText("体温38℃以上", substring = true)).assertExists()
    }

    // Matches web's select-row.ts, which renders the description unconditionally.
    // The label/description pair is hidden from the semantics tree in favour of a single
    // merged contentDescription on the field (see fieldDescription), so we assert through that.
    @Test
    fun enumRowAlwaysShowsItsDescription() {
        showForm()

        scrollTo(hasContentDescription("慢性腎臓病", substring = true))
        composeTestRule.onNode(hasContentDescription("正常: 60以上", substring = true)).assertExists()
    }
}
