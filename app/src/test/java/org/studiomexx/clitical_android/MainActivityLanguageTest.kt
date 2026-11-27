package org.studiomexx.clitical_android

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Nothing saved and Robolectric's English device: the app follows the device, not Japanese. */
@RunWith(RobolectricTestRunner::class)
class MainActivityLanguageTest {

    @get:Rule(order = 0)
    val nothingSaved = object : ExternalResource() {
        override fun before() {
            ApplicationProvider.getApplicationContext<Context>()
                .getSharedPreferences("clitical_preferences", Context.MODE_PRIVATE)
                .edit().clear().commit()
        }
    }

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun startsInEnglishWhenNothingIsSavedOnAnEnglishDevice() {
        val notice = composeTestRule.onAllNodesWithText("I understand")
        if (notice.fetchSemanticsNodes().isNotEmpty()) notice[0].performClick()

        composeTestRule.onNode(hasContentDescription("Settings tab")).assertExists()
        composeTestRule.onAllNodes(hasContentDescription("設定タブ")).assertCountEquals(0)
    }
}
