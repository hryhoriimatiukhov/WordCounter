package com.example.wordcounter.model

/**
 * Data class representing the four calculated metrics for a given text.
 *
 * @property sentences Count of sentences ending with '.', '!', or '?' (consecutive marks count as one).
 * @property words Count of lexical words separated by whitespace (excluding surrounding punctuation).
 * @property punctuationMarks Count of individual punctuation characters.
 * @property numbers Count of discrete numeric tokens (e.g., 2026, 42).
 */
data class TextMetrics(
    val sentences: Int = 0,
    val words: Int = 0,
    val punctuationMarks: Int = 0,
    val numbers: Int = 0
)
