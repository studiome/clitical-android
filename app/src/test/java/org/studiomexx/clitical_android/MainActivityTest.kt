package org.studiomexx.clitical_android

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.width
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Covers the AnimatedContent switch between the question form and the result screen. */
@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun acknowledgeFirstRunNoticeWhenPresent() {
        val notice = composeTestRule.onAllNodesWithText("内容を理解しました")
        if (notice.fetchSemanticsNodes().isNotEmpty()) {
            notice[0].performClick()
        }
    }

    // The form's lazy list; on a wide window the results pane scrolls too.
    private fun onForm() = composeTestRule.onNode(hasScrollToIndexAction())

    private fun fillField(label: String, text: String) {
        val matcher = hasSetTextAction() and hasContentDescription(label)
        onForm().performScrollToNode(matcher)
        composeTestRule.onNode(matcher).performTextInput(text)
    }

    private fun fillRequiredFieldsAndSubmit() {
        fillField("年齢 [歳]", "65")
        fillField("身長 [cm]", "150")
        fillField("体重 [kg]", "50")
        fillField("アルブミン値 [g/dl]", "4.0")
        onForm().performScrollToNode(hasText("リスク予測"))
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

    @Test
    fun navigationTabsHaveShortLabelsAndCompleteAccessibleNames() {
        composeTestRule.onNodeWithText("リスク").assertExists()
        composeTestRule.onNodeWithText("文献").assertExists()
        composeTestRule.onNodeWithText("設定").assertExists()
        composeTestRule.onNode(hasContentDescription("リスク評価タブ")).assertExists()
        composeTestRule.onNode(hasContentDescription("参考文献タブ")).assertExists()
        composeTestRule.onNode(hasContentDescription("設定タブ")).assertExists()
    }

    // No top app bar on the tabs: it only repeated the tab's name. Each screen opens with its
    // own heading instead, so it still announces where the user is.
    @Test
    fun tabsHaveNoTopAppBarButEachScreenOpensWithAHeading() {
        composeTestRule.onNodeWithText("リスク評価").assertDoesNotExist()
        composeTestRule.onNode(isHeading() and hasText("患者データ")).assertExists()

        composeTestRule.onNode(hasContentDescription("参考文献タブ")).performClick()
        composeTestRule.onNode(isHeading() and hasText("参考文献")).assertExists()

        composeTestRule.onNode(hasContentDescription("設定タブ")).performClick()
        composeTestRule.onNode(isHeading() and hasText("設定")).assertExists()
    }

    // Adaptive layout: a phone keeps the bottom bar; an unfolded foldable or a tablet gets a
    // navigation rail along the edge and a form that doesn't stretch across the whole window.

    private fun tabBounds(a11yLabel: String) =
        composeTestRule.onNode(hasContentDescription(a11yLabel)).getUnclippedBoundsInRoot()

    @Test
    fun compactWindowShowsTheTabsInABottomBar() {
        val risk = tabBounds("リスク評価タブ")
        val settings = tabBounds("設定タブ")

        assertEquals(risk.top, settings.top)
        assertNotEquals(risk.left, settings.left)
    }

    // Pixel Fold-class inner display.
    @Test
    @Config(qualifiers = "w673dp-h841dp")
    fun unfoldedWindowShowsTheTabsInANavigationRail() {
        val risk = tabBounds("リスク評価タブ")
        val settings = tabBounds("設定タブ")

        assertEquals(risk.left, settings.left)
        assertTrue(settings.top > risk.top)
    }

    @Test
    @Config(qualifiers = "w1000dp-h800dp")
    fun wideWindowKeepsTheFormAtAReadableWidth() {
        val predict = hasText("リスク予測") and hasClickAction()
        onForm().performScrollToNode(predict)

        val button = composeTestRule.onNode(predict).getUnclippedBoundsInRoot()

        assertTrue("button width ${button.width}", button.width <= 640.dp)
    }

    // Predict as the screen's primary action (M3 FAB): reachable without scrolling the long
    // form, above the bottom bar on a phone and in the rail's header when unfolded.

    // NavigationSuiteScaffold composes the primary action for both the bar and the rail
    // slots but only places the one in use, so match the placed one.
    private val isPlaced = SemanticsMatcher("is placed") { it.layoutInfo.isPlaced }

    private val predictFab = hasClickAction() and
        (hasText("リスク予測") or hasContentDescription("リスク予測")) and
        !hasAnyAncestor(hasScrollToIndexAction()) and isPlaced

    private fun fillRequiredFields() {
        fillField("年齢 [歳]", "65")
        fillField("身長 [cm]", "150")
        fillField("体重 [kg]", "50")
        fillField("アルブミン値 [g/dl]", "4.0")
    }

    @Test
    fun predictFabShowsTheResultWithoutScrollingToTheEndOfTheForm() {
        fillRequiredFields()

        composeTestRule.onNode(predictFab).performClick()

        composeTestRule.onNodeWithText("予測リスク").assertExists()
    }

    @Test
    fun predictFabWithMissingInputShowsTheError() {
        composeTestRule.onNode(predictFab).performClick()

        composeTestRule.onNodeWithText("数値入力を確認してください。").assertExists()
    }

    @Test
    fun predictFabIsOnlyOnTheRiskTab() {
        composeTestRule.onNode(predictFab).assertExists()

        composeTestRule.onNode(hasContentDescription("参考文献タブ")).performClick()

        composeTestRule.onNode(predictFab).assertDoesNotExist()
    }

    @Test
    @Config(qualifiers = "w1000dp-h800dp")
    fun unfoldedPredictFabInTheRailShowsTheResultBesideTheForm() {
        fillRequiredFields()

        composeTestRule.onNode(predictFab).performClick()

        composeTestRule.onNodeWithText("予測30日死亡・大切断率").assertExists()
    }

    // Like the iOS app in regular width: form and predicted risks side by side.

    private val ageField = hasSetTextAction() and hasContentDescription("年齢 [歳]")

    @Test
    @Config(qualifiers = "w1000dp-h800dp")
    fun wideWindowShowsAnEmptyResultsPaneBesideTheForm() {
        composeTestRule.onNode(ageField).assertExists()
        composeTestRule.onNodeWithText("患者データを入力すると、ここに予測リスクが表示されます。").assertExists()
    }

    @Test
    @Config(qualifiers = "w1000dp-h800dp")
    fun wideWindowShowsTheResultBesideTheFormInsteadOfReplacingIt() {
        fillRequiredFieldsAndSubmit()

        composeTestRule.onNodeWithText("予測30日死亡・大切断率").assertExists()
        // The form is still there to edit, scrolled down to the Predict button.
        onForm().performScrollToNode(ageField)
        composeTestRule.onNode(ageField).assertExists()
        // No full-screen result, so there's nothing to go back from.
        composeTestRule.onNode(hasContentDescription("戻る")).assertDoesNotExist()
    }

    @Test
    @Config(qualifiers = "w1000dp-h800dp")
    fun wideWindowClearsTheResultWhenTheInputChanges() {
        fillRequiredFieldsAndSubmit()

        fillField("年齢 [歳]", "1")

        composeTestRule.onNodeWithText("予測30日死亡・大切断率").assertDoesNotExist()
        composeTestRule.onNodeWithText("患者データを入力すると、ここに予測リスクが表示されます。").assertExists()
    }
}
