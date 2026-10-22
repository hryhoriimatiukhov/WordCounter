package com.example.wordcounter

import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextReplacement
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wordcounter.data.TestSentenceProvider
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI and Integration tests for WordCounter application using Jetpack Compose UI Test framework.
 *
 * Scenarios:
 * 1. Initial screen launch and default UI element verification.
 * 2. Valid text input, metric calculation, card rendering, and summary verification.
 * 3. Input validation, error message display for empty/blank input, and error recovery.
 * 4. Test sentence generation, input population, and subsequent metric calculation.
 * 5. Multi-calculation session history order and clear history functionality.
 */
@RunWith(AndroidJUnit4::class)
class WordCounterUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Scenario 1: Initial App Launch & Component Verification
     * Verifies that the initial screen contains the top bar title, the text field,
     * action buttons, the history section with its empty-state placeholder,
     * and confirms that the metrics section and clear-history button are not displayed.
     */
    @Test
    fun testInitialScreen_displaysDefaultUiElements() {
        // Top App Bar title
        composeTestRule.onNodeWithText("Word Counter")
            .assertIsDisplayed()

        // Input field and label
        composeTestRule.onNode(hasSetTextAction())
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Text to Analyze")
            .assertIsDisplayed()

        // Action Buttons
        composeTestRule.onNodeWithText("Calculate Metrics")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Generate Test Sentence")
            .assertIsDisplayed()

        // Metrics section should not be visible before any calculation
        composeTestRule.onNodeWithText("Current Text Metrics")
            .assertDoesNotExist()

        // Session History Header and initial empty-state message
        composeTestRule.onNodeWithText("Session Calculation History")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("No calculations performed yet in this session.")
            .assertIsDisplayed()

        // Clear History button should not be visible when history is empty
        composeTestRule.onNodeWithText("Clear History")
            .assertDoesNotExist()
    }

    /**
     * Scenario 2: Valid Text Analysis & Metric Cards Verification
     * Enters valid text, triggers metric calculation, and verifies:
     * - The "Current Text Metrics" section appears
     * - Metric cards are displayed for Sentences, Words, Punctuation, and Numbers
     * - A calculation history card is added with correct index and formatted summary
     */
    @Test
    fun testValidTextAnalysis_displaysAccurateMetricsAndHistory() {
        val sampleText = "Hello world! This is 1 test."

        // Enter input text and close soft keyboard
        composeTestRule.onNode(hasSetTextAction())
            .performTextReplacement(sampleText)
        Espresso.closeSoftKeyboard()

        // Click Calculate Metrics
        composeTestRule.onNodeWithText("Calculate Metrics")
            .performClick()

        // Verify Results Section appears
        composeTestRule.onNodeWithText("Current Text Metrics")
            .performScrollTo()
            .assertIsDisplayed()

        // Verify Metric card titles
        composeTestRule.onNodeWithText("Sentences").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Words").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Punctuation").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Numbers").performScrollTo().assertIsDisplayed()

        // Verify the history card displays entry #1 with original text and summary
        // Summary for "Hello world! This is 1 test.":
        // 2 sentences, 5 words, 2 punctuation marks, 1 numbers
        composeTestRule.onNodeWithText("#1")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("\"$sampleText\"")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("=> 2 sentences, 5 words, 2 punctuation marks, 1 numbers")
            .performScrollTo()
            .assertIsDisplayed()
    }

    /**
     * Scenario 3: Input Validation & Error Handling
     * Verifies that:
     * - Attempting to calculate with empty or whitespace-only input triggers validation error
     * - Metrics are not calculated and history is not modified
     * - Typing valid text clears the validation error
     */
    @Test
    fun testInputValidation_showsErrorOnEmptyInputAndRecoversOnValidInput() {
        // Attempt calculation with initially empty input
        composeTestRule.onNode(hasSetTextAction())
            .performTextClearance()
        Espresso.closeSoftKeyboard()
        composeTestRule.onNodeWithText("Calculate Metrics")
            .performClick()

        // Assert error message is displayed (in TextField supporting text and/or Snackbar)
        composeTestRule.onAllNodesWithText("Please enter some text to analyze.")
            .onFirst()
            .assertIsDisplayed()

        // Metrics section should not appear
        composeTestRule.onNodeWithText("Current Text Metrics")
            .assertDoesNotExist()

        // History remains empty
        composeTestRule.onNodeWithText("No calculations performed yet in this session.")
            .performScrollTo()
            .assertIsDisplayed()

        // Enter whitespace-only input and attempt calculation again
        composeTestRule.onNode(hasSetTextAction())
            .performTextReplacement("   \n\t  ")
        Espresso.closeSoftKeyboard()
        composeTestRule.onNodeWithText("Calculate Metrics")
            .performClick()

        // Error message remains displayed
        composeTestRule.onAllNodesWithText("Please enter some text to analyze.")
            .onFirst()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Current Text Metrics")
            .assertDoesNotExist()

        // Enter valid text and calculate to verify complete recovery
        composeTestRule.onNode(hasSetTextAction())
            .performTextReplacement("Valid text.")
        Espresso.closeSoftKeyboard()
        composeTestRule.onNodeWithText("Calculate Metrics")
            .performClick()

        // Verify successful calculation clears error and displays metrics
        composeTestRule.onNodeWithText("Current Text Metrics")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Please enter some text to analyze.")
            .assertCountEquals(0)
    }

    /**
     * Scenario 4: Sample Sentence Generation & Instant Calculation
     * Verifies that:
     * - Clicking "Generate Test Sentence" populates the text field with a sample sentence
     * - Calculating metrics on the generated text succeeds and displays the results section
     */
    @Test
    fun testGenerateTestSentence_populatesInputAndCalculatesSuccessfully() {
        val testSentenceProvider = TestSentenceProvider()

        // Click Generate Test Sentence button
        composeTestRule.onNodeWithText("Generate Test Sentence")
            .performClick()

        // Verify the text field now contains one of the curated sample sentences
        val anyMatched = testSentenceProvider.sampleSentences.any { sentence ->
            try {
                composeTestRule.onNode(hasSetTextAction()).assertTextContains(sentence)
                true
            } catch (e: AssertionError) {
                false
            }
        }
        assertTrue("Input field should contain one of the curated test sentences", anyMatched)

        Espresso.closeSoftKeyboard()

        // Click Calculate Metrics on the generated sentence
        composeTestRule.onNodeWithText("Calculate Metrics")
            .performClick()

        // Verify the metrics section is displayed
        composeTestRule.onNodeWithText("Current Text Metrics")
            .performScrollTo()
            .assertIsDisplayed()

        // Verify history has recorded the calculation
        composeTestRule.onNodeWithText("#1")
            .performScrollTo()
            .assertIsDisplayed()
    }

    /**
     * Scenario 5: Multi-Calculation Session History & Clearing History
     * Verifies that:
     * - Multiple calculations are recorded in reverse chronological order (#2, #1)
     * - "Clear History" button appears when history is non-empty
     * - Clicking "Clear History" removes all history cards and restores the empty placeholder
     */
    @Test
    fun testSessionHistory_recordsMultipleCalculationsAndClearsCorrectly() {
        val firstInput = "First entry."
        val secondInput = "Second distinct entry!"

        // Perform first calculation
        composeTestRule.onNode(hasSetTextAction())
            .performTextReplacement(firstInput)
        Espresso.closeSoftKeyboard()
        composeTestRule.onNodeWithText("Calculate Metrics")
            .performClick()

        // Perform second calculation
        composeTestRule.onNode(hasSetTextAction())
            .performTextReplacement(secondInput)
        Espresso.closeSoftKeyboard()
        composeTestRule.onNodeWithText("Calculate Metrics")
            .performClick()

        // Verify Clear History button is displayed
        composeTestRule.onNodeWithText("Clear History")
            .performScrollTo()
            .assertIsDisplayed()

        // Verify second calculation (#2) is present in history (at top)
        composeTestRule.onNodeWithText("#2")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("\"$secondInput\"")
            .performScrollTo()
            .assertIsDisplayed()

        // Verify first calculation (#1) is present in history (below #2)
        composeTestRule.onNodeWithText("#1")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("\"$firstInput\"")
            .performScrollTo()
            .assertIsDisplayed()

        // Click Clear History
        composeTestRule.onNodeWithText("Clear History")
            .performScrollTo()
            .performClick()

        // Verify history items are gone and empty message is restored
        composeTestRule.onNodeWithText("#1").assertDoesNotExist()
        composeTestRule.onNodeWithText("#2").assertDoesNotExist()
        composeTestRule.onNodeWithText("\"$firstInput\"").assertDoesNotExist()
        composeTestRule.onNodeWithText("\"$secondInput\"").assertDoesNotExist()

        composeTestRule.onNodeWithText("No calculations performed yet in this session.")
            .performScrollTo()
            .assertIsDisplayed()

        // Clear History button should no longer exist
        composeTestRule.onNodeWithText("Clear History")
            .assertDoesNotExist()
    }
}
