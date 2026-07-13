package org.studiomexx.clitical_android

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.studiomexx.clitical_android.ui.QuestionForm
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class QuestionFormTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSexSelectionAndWizardFlow() {
        val viewModel = MainViewModel().apply {
            locale = Locale("ja") // Fix locale to Japanese for testing
        }

        composeTestRule.setContent {
            QuestionForm(viewModel = viewModel)
        }

        // 1. Instruction Screen
        composeTestRule.onNodeWithText("はじめに").assertExists()
        composeTestRule.onNodeWithText("ご使用前に説明をお読みください。").assertExists()
        
        // Tap Next
        composeTestRule.onNodeWithText("次へ").performClick()

        // 2. Sex Selection Screen
        composeTestRule.onNodeWithText("性別").assertExists()
        composeTestRule.onNodeWithText("女性").assertExists()
        composeTestRule.onNodeWithText("男性").assertExists()

        // Initial sex is FEMALE
        assertEquals(Sex.FEMALE, viewModel.sex)

        // Select Male
        composeTestRule.onNodeWithText("男性").performClick()
        assertEquals(Sex.MALE, viewModel.sex)

        // Tap Next
        composeTestRule.onNodeWithText("次へ").performClick()

        // 3. Age Input Screen
        composeTestRule.onNodeWithText("年齢 [歳]").assertExists()
        
        // Tap Next without input (should not advance and stay on Age screen)
        composeTestRule.onNodeWithText("次へ").performClick()
        composeTestRule.onNodeWithText("年齢 [歳]").assertExists()
        
        // Input Age
        composeTestRule.onNodeWithText("年齢 [歳]").performTextInput("65")
        
        // Tap Next (should advance to Height screen)
        composeTestRule.onNodeWithText("次へ").performClick()
        composeTestRule.onNodeWithText("身長 [cm]").assertExists()
        assertEquals("65", viewModel.ageText)
    }
}
