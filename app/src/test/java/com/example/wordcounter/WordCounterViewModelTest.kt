package com.example.wordcounter

import com.example.wordcounter.calculator.TextMetricsCalculator
import com.example.wordcounter.data.TestSentenceProvider
import com.example.wordcounter.ui.viewmodel.WordCounterViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

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
    fun testInitialState_isEmpty() {
        val state = viewModel.uiState.value
        assertEquals("", state.inputText)
        assertFalse(state.isInputError)
        assertNull(state.currentMetrics)
        assertTrue(state.sessionHistory.isEmpty())
        assertNull(state.snackbarMessageResId)
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

        assertEquals(1, state.sessionHistory.size)
        val entry = state.sessionHistory[0]
        assertEquals(input, entry.originalText)
        assertTrue(entry.id.isNotEmpty())
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
    fun testGenerateTestSentence_clearsStaleMetrics() {
        viewModel.onInputTextChanged("Old text.")
        viewModel.calculateMetrics()
        assertNotNull(viewModel.uiState.value.currentMetrics)

        viewModel.generateTestSentence()

        val state = viewModel.uiState.value
        assertTrue(state.inputText.isNotBlank())
        assertNull(state.currentMetrics) // Must be cleared until user calculates
        assertFalse(state.isInputError)
    }

    @Test
    fun testClearHistory_clearsEntries() {
        viewModel.onInputTextChanged("First entry.")
        viewModel.calculateMetrics()
        viewModel.onInputTextChanged("Second entry.")
        viewModel.calculateMetrics()

        assertEquals(2, viewModel.uiState.value.sessionHistory.size)

        viewModel.clearHistory()
        assertTrue(viewModel.uiState.value.sessionHistory.isEmpty())
        assertEquals(R.string.msg_history_cleared, viewModel.uiState.value.snackbarMessageResId)
    }
}
