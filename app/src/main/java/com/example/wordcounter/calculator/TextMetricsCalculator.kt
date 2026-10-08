package com.example.wordcounter.calculator

import com.example.wordcounter.model.TextMetrics

/**
 * Reusable, standalone engine for analyzing and calculating text metrics.
 *
 * Rules:
 * - Sentences: Count a sentence when it ends with '.', '!', or '?'. Multiple consecutive
 *   sentence-ending punctuation marks such as "?!" count as one sentence.
 * - Words: Count lexical words separated by whitespace, ignoring punctuation surrounding the word.
 * - Numbers: Count each numeric token as one number, regardless of how many digits it contains.
 * - Punctuation marks: Count each punctuation character in the input text, including
 *   .,!?:;-()[]"' etc., excluding spaces, letters, or digits.
 */
class TextMetricsCalculator {

    /**
     * Analyzes the provided text and returns the calculated [TextMetrics].
     *
     * @param text The input string to analyze.
     * @return [TextMetrics] containing counts for sentences, words, punctuation marks, and numbers.
     */
    fun calculateMetrics(text: String): TextMetrics {
        if (text.isBlank()) {
            return TextMetrics(
                sentences = 0,
                words = 0,
                punctuationMarks = 0,
                numbers = 0
            )
        }

        val sentenceCount = countSentences(text)
        val punctuationCount = countPunctuationMarks(text)
        val (wordCount, numberCount) = countWordsAndNumbers(text)

        return TextMetrics(
            sentences = sentenceCount,
            words = wordCount,
            punctuationMarks = punctuationCount,
            numbers = numberCount
        )
    }

    /**
     * Counts sentences ending with '.', '!', or '?'.
     * Consecutive sentence-ending marks (e.g. "?!", "...", "???") count as a single sentence delimiter.
     * Decimals embedded between digits (e.g., "3.14") are not treated as sentence delimiters.
     * Punctuation-only segments without alphanumeric content are not counted as sentences.
     */
    fun countSentences(text: String): Int {
        if (text.isBlank()) return 0

        val sentenceRegex = Regex("""[.!?]+""")
        var count = 0
        var lastEnd = 0

        for (match in sentenceRegex.findAll(text)) {
            // Check if this match is a decimal separator embedded between digits (e.g. "3.14")
            if (match.value == ".") {
                val start = match.range.first
                val end = match.range.last
                val prevChar = if (start > 0) text[start - 1] else null
                val nextChar = if (end + 1 < text.length) text[end + 1] else null
                if (prevChar != null && prevChar.isDigit() && nextChar != null && nextChar.isDigit()) {
                    continue
                }
            }

            // A valid sentence must contain at least one alphanumeric character
            val segment = text.substring(lastEnd, match.range.first)
            if (segment.any { it.isLetter() || it.isDigit() }) {
                count++
                lastEnd = match.range.last + 1
            }
        }

        return count
    }

    /**
     * Counts all punctuation characters in the text.
     * Defined as any character that is not a whitespace, not a letter, and not a digit.
     */
    fun countPunctuationMarks(text: String): Int {
        return text.count { isPunctuation(it) }
    }

    /**
     * Analyzes whitespace-delimited tokens to count lexical words and numeric tokens.
     * Punctuation surrounding each token is stripped before evaluation.
     */
    fun countWordsAndNumbers(text: String): Pair<Int, Int> {
        if (text.isBlank()) return Pair(0, 0)

        val tokens = text.trim().split(Regex("""\s+""")).filter { it.isNotEmpty() }
        var wordCount = 0
        var numberCount = 0

        for (token in tokens) {
            val stripped = stripSurroundingPunctuation(token)
            if (stripped.isEmpty()) {
                continue
            }

            if (isNumericToken(stripped)) {
                numberCount++
            } else if (isLexicalWord(stripped)) {
                wordCount++
            }
        }

        return Pair(wordCount, numberCount)
    }

    /**
     * Checks if a character is considered a punctuation mark.
     * Any non-whitespace, non-alphanumeric character is treated as punctuation.
     */
    fun isPunctuation(c: Char): Boolean {
        return !c.isWhitespace() && !c.isLetter() && !c.isDigit()
    }

    /**
     * Strips leading and trailing punctuation characters from a token.
     */
    private fun stripSurroundingPunctuation(token: String): String {
        return token.trim { isPunctuation(it) }
    }

    /**
     * Determines whether a stripped token represents a discrete numeric value.
     * Supports:
     * - Integers and decimals (e.g., "2026", "42", "3.14", "3,14", "+42", "-42")
     * - Grouped numbers with thousand separators (e.g., "1,000,000", "10,000.50")
     * - Time representations (e.g., "18:00", "09:30:00")
     * - Common fractions (e.g., "3/4")
     */
    private fun isNumericToken(stripped: String): Boolean {
        if (stripped.isEmpty()) return false
        val numericRegex = Regex(
            """^[+-]?(?:\d+(?:[.,]\d+)?|\d{1,3}(?:,\d{3})+(?:\.\d+)?|\d{1,2}:\d{2}(?::\d{2})?|\d+/\d+)$"""
        )
        return stripped.matches(numericRegex)
    }

    /**
     * Determines whether a stripped token represents a lexical word.
     * A lexical word contains at least one letter.
     */
    private fun isLexicalWord(stripped: String): Boolean {
        return stripped.any { it.isLetter() }
    }
}
