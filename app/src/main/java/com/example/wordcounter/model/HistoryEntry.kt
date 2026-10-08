package com.example.wordcounter.model

import java.util.UUID

/**
 * Represents a single calculation record in the session history.
 *
 * @property id Unique identifier for the history entry (guaranteed unique for Compose keys).
 * @property originalText The original raw text that was analyzed.
 * @property metrics The calculated metrics for the text.
 * @property timestampMillis Timestamp when the calculation took place.
 */
data class HistoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val originalText: String,
    val metrics: TextMetrics,
    val timestampMillis: Long = System.currentTimeMillis()
) {
    /**
     * Formats the history entry according to the required specification:
     * "Original text" => sentences, words, punctuation marks, numbers
     */
    fun formattedSummary(): String {
        return "\"$originalText\" => ${metrics.sentences} sentences, ${metrics.words} words, ${metrics.punctuationMarks} punctuation marks, ${metrics.numbers} numbers"
    }
}
