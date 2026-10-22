package com.example.wordcounter

import com.example.wordcounter.calculator.TextMetricsCalculator
import com.example.wordcounter.data.TestSentenceProvider
import com.example.wordcounter.model.HistoryEntry
import com.example.wordcounter.model.TextMetrics
import com.example.wordcounter.ui.viewmodel.WordCounterUiState
import com.example.wordcounter.ui.viewmodel.WordCounterViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [WordCounterViewModel] and [WordCounterUiState].
 * Covers 100% of methods, branch transitions, constructors, and UI state properties.
 */
class WordCounterViewModelTest {

    private lateinit var viewModel: WordCounterViewModel

    @Before
    fun setUp() {
        viewModel = WordCounterViewModel(
            calculator = TextMetricsCalculator(),
            testSentenceProvider = TestSentenceProvider()
        )
    }

    @Test
    fun testDefaultConstructorInitializesCorrectly() {
        val defaultVm = WordCounterViewModel()
        val state = defaultVm.uiState.value
        assertEquals("", state.inputText)
        assertFalse(state.isInputError)
        assertNull(state.currentMetrics)
        assertTrue(state.sessionHistory.isEmpty())
        assertNull(state.snackbarMessageResId)
    }

    @Test
    fun testInitialState_isEmpty() {
        val state = viewModel.uiState.value
        assertEquals("", state.inputText)
        assertFalse(state.isInputError)
        assertNull(state.currentMetrics)
        assertTrue(state.sessionHistory.isEmpty())
        assertNull(state.snackbarMessageResId)
    }

    @Test
    fun testOnInputTextChanged_resetsErrorWhenTypingNonBlank() {
        // Trigger error first
        viewModel.onInputTextChanged("   ")
        viewModel.calculateMetrics()
        assertTrue(viewModel.uiState.value.isInputError)

        // Type non-blank text
        viewModel.onInputTextChanged("Hello")
        val state = viewModel.uiState.value
        assertEquals("Hello", state.inputText)
        assertFalse("Typing non-blank text must clear the error flag", state.isInputError)
    }

    @Test
    fun testOnInputTextChanged_preservesErrorStateWhenTypingBlank() {
        // Trigger error first
        viewModel.calculateMetrics()
        assertTrue(viewModel.uiState.value.isInputError)

        // Change text to another blank string
        viewModel.onInputTextChanged("   \t")
        assertTrue("Typing blank text should keep error flag true if already in error", viewModel.uiState.value.isInputError)

        // Fresh VM: typing blank text when not in error should remain not in error
        val freshVm = WordCounterViewModel()
        freshVm.onInputTextChanged(" ")
        assertFalse(freshVm.uiState.value.isInputError)
    }

    @Test
    fun testEmptyInput_triggersErrorAndClearsMetrics() {
        viewModel.onInputTextChanged("   ")
        viewModel.calculateMetrics()

        val state = viewModel.uiState.value
        assertTrue(state.isInputError)
        assertNull(state.currentMetrics)
        assertTrue(state.sessionHistory.isEmpty())
        assertEquals(R.string.error_empty_input, state.snackbarMessageResId)
    }

    @Test
    fun testValidCalculation_updatesMetricsAndAddsHistoryWithUniqueId() {
        val input = "In 2026, the lab tested 42 distinct algorithms across 3 continents: results were outstanding!"
        viewModel.onInputTextChanged(input)
        viewModel.calculateMetrics()

        val state = viewModel.uiState.value
        assertFalse(state.isInputError)
        assertNotNull(state.currentMetrics)
        assertEquals(1, state.currentMetrics?.sentences)
        assertEquals(11, state.currentMetrics?.words)
        assertEquals(3, state.currentMetrics?.numbers)
        assertEquals(3, state.currentMetrics?.punctuationMarks)
        assertEquals(R.string.msg_calculation_success, state.snackbarMessageResId)

        assertEquals(1, state.sessionHistory.size)
        val entry = state.sessionHistory[0]
        assertEquals(input, entry.originalText)
        assertTrue(entry.id.isNotEmpty())
    }

    @Test
    fun testMultipleCalculations_prependsToSessionHistory() {
        viewModel.onInputTextChanged("First sentence.")
        viewModel.calculateMetrics()

        viewModel.onInputTextChanged("Second sentence!")
        viewModel.calculateMetrics()

        val state = viewModel.uiState.value
        assertEquals(2, state.sessionHistory.size)
        // Most recent calculation must be at index 0 (LIFO order)
        assertEquals("Second sentence!", state.sessionHistory[0].originalText)
        assertEquals("First sentence.", state.sessionHistory[1].originalText)
    }

    @Test
    fun testEmptyInputAfterCalculation_clearsCurrentMetrics() {
        viewModel.onInputTextChanged("Hello, world!")
        viewModel.calculateMetrics()
        assertNotNull(viewModel.uiState.value.currentMetrics)

        viewModel.onInputTextChanged("")
        viewModel.calculateMetrics()

        val state = viewModel.uiState.value
        assertTrue(state.isInputError)
        assertNull(state.currentMetrics) // Stale metrics must be cleared
    }

    @Test
    fun testGenerateTestSentence_clearsStaleMetricsAndSetsSnackbar() {
        viewModel.onInputTextChanged("Old text.")
        viewModel.calculateMetrics()
        assertNotNull(viewModel.uiState.value.currentMetrics)

        viewModel.generateTestSentence()

        val state = viewModel.uiState.value
        assertTrue(state.inputText.isNotBlank())
        assertNull(state.currentMetrics) // Must be cleared until user calculates
        assertFalse(state.isInputError)
        assertEquals(R.string.msg_sentence_generated, state.snackbarMessageResId)
    }

    @Test
    fun testClearHistory_clearsEntriesAndSetsSnackbar() {
        viewModel.onInputTextChanged("First entry.")
        viewModel.calculateMetrics()
        viewModel.onInputTextChanged("Second entry.")
        viewModel.calculateMetrics()

        assertEquals(2, viewModel.uiState.value.sessionHistory.size)

        viewModel.clearHistory()
        assertTrue(viewModel.uiState.value.sessionHistory.isEmpty())
        assertEquals(R.string.msg_history_cleared, viewModel.uiState.value.snackbarMessageResId)
    }

    @Test
    fun testOnSnackbarDismissed_clearsMessage() {
        viewModel.onInputTextChanged("")
        viewModel.calculateMetrics()
        assertEquals(R.string.error_empty_input, viewModel.uiState.value.snackbarMessageResId)

        viewModel.onSnackbarDismissed()
        assertNull(viewModel.uiState.value.snackbarMessageResId)
    }

    // -------------------------------------------------------------------------
    // WordCounterUiState Data Class Tests
    // -------------------------------------------------------------------------

    @Test
    fun testWordCounterUiState_copyAndEquality() {
        val state1 = WordCounterUiState(
            inputText = "Sample",
            isInputError = false,
            currentMetrics = TextMetrics(1, 1, 1, 0),
            sessionHistory = listOf(HistoryEntry(originalText = "Sample", metrics = TextMetrics(1, 1, 1, 0))),
            snackbarMessageResId = 123
        )
        val state2 = state1.copy()
        assertEquals(state1, state2)
        assertEquals(state1.hashCode(), state2.hashCode())

        val state3 = state1.copy(inputText = "Different")
        assertNotEquals(state1, state3)
        assertFalse(state1.equals(null))
        assertFalse(state1.equals("Other type"))
        assertTrue(state1.toString().contains("Sample"))
    }

    @Test
    fun testWordCounterUiState_componentDestructuring() {
        val metrics = TextMetrics(1, 2, 3, 4)
        val history = listOf(HistoryEntry(originalText = "Test", metrics = metrics))
        val state = WordCounterUiState(
            inputText = "Input",
            isInputError = true,
            currentMetrics = metrics,
            sessionHistory = history,
            snackbarMessageResId = 999
        )

        val (input, isError, currentM, hist, snackbar) = state
        assertEquals("Input", input)
        assertTrue(isError)
        assertEquals(metrics, currentM)
        assertEquals(history, hist)
        assertEquals(999, snackbar)
    }
}
