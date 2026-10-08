package com.example.wordcounter.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wordcounter.R
import com.example.wordcounter.calculator.TextMetricsCalculator
import com.example.wordcounter.data.TestSentenceProvider
import com.example.wordcounter.model.HistoryEntry
import com.example.wordcounter.model.TextMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI State for the Word Counter screen.
 */
data class WordCounterUiState(
    val inputText: String = "",
    val isInputError: Boolean = false,
    val currentMetrics: TextMetrics? = null,
    val sessionHistory: List<HistoryEntry> = emptyList(),
    val snackbarMessageResId: Int? = null
)

/**
 * ViewModel managing UI state, validation, test sentence generation,
 * metric calculation, and session history.
 */
class WordCounterViewModel(
    private val calculator: TextMetricsCalculator = TextMetricsCalculator(),
    private val testSentenceProvider: TestSentenceProvider = TestSentenceProvider()
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordCounterUiState())
    val uiState: StateFlow<WordCounterUiState> = _uiState.asStateFlow()

    /**
     * Updates the user's input text and resets the error flag if the user begins typing.
     */
    fun onInputTextChanged(newText: String) {
        _uiState.update { currentState ->
            currentState.copy(
                inputText = newText,
                isInputError = if (newText.isNotBlank()) false else currentState.isInputError
            )
        }
    }

    /**
     * Validates the input and calculates metrics if valid.
     * Prevents calculation and triggers notification when the input is blank.
     * Automatically prepends the new calculation to the session history.
     */
    fun calculateMetrics() {
        val currentText = _uiState.value.inputText

        if (currentText.isBlank()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isInputError = true,
                    currentMetrics = null,
                    snackbarMessageResId = R.string.error_empty_input
                )
            }
            return
        }

        val calculated = calculator.calculateMetrics(currentText)
        val newEntry = HistoryEntry(
            originalText = currentText,
            metrics = calculated
        )

        _uiState.update { currentState ->
            currentState.copy(
                isInputError = false,
                currentMetrics = calculated,
                // Add newest calculation to the top of the history list
                sessionHistory = listOf(newEntry) + currentState.sessionHistory,
                snackbarMessageResId = R.string.msg_calculation_success
            )
        }
    }

    /**
     * Generates a random realistic test sentence and replaces the input text.
     * Does NOT automatically add to history until the user calculates it.
     */
    fun generateTestSentence() {
        val sample = testSentenceProvider.getRandomSentence()
        _uiState.update { currentState ->
            currentState.copy(
                inputText = sample,
                isInputError = false,
                currentMetrics = null,
                snackbarMessageResId = R.string.msg_sentence_generated
            )
        }
    }

    /**
     * Clears all session calculation history entries and provides confirmation.
     */
    fun clearHistory() {
        _uiState.update { currentState ->
            currentState.copy(
                sessionHistory = emptyList(),
                snackbarMessageResId = R.string.msg_history_cleared
            )
        }
    }

    /**
     * Clears the current Snackbar message after it has been displayed.
     */
    fun onSnackbarDismissed() {
        _uiState.update { currentState ->
            currentState.copy(snackbarMessageResId = null)
        }
    }
}
