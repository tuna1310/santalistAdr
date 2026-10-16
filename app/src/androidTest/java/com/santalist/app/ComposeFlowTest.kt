package com.santalist.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposeFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun verifyThreeTabsNavigation() {
        // Verify default People tab
        composeTestRule.onNodeWithText("People").assertIsDisplayed()

        // Switch to Timeline tab
        composeTestRule.onNodeWithText("Timeline").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("All").assertIsDisplayed()

        // Switch to Budget tab
        composeTestRule.onNodeWithText("Budget").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Budget vs Planned").assertIsDisplayed()

        // Switch back to People tab
        composeTestRule.onNodeWithText("People").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun testAddPersonFlow() {
        // Click Add Person button
        composeTestRule.onNodeWithContentDescription("Add person").performClick()
        composeTestRule.waitForIdle()

        // Verify sheet opens
        composeTestRule.onNodeWithText("Add Person").assertIsDisplayed()

        // Input name
        composeTestRule.onNodeWithText("Name").performTextInput("Bob Test")

        // Input budget
        composeTestRule.onNodeWithText("Budget (VND)").performTextInput("1500000")

        // Save
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.waitForIdle()

        // Verify newly added person is on screen
        composeTestRule.onNodeWithText("Bob Test").assertIsDisplayed()
    }
}
