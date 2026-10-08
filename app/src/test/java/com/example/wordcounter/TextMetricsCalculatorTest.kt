package com.example.wordcounter

import com.example.wordcounter.calculator.TextMetricsCalculator
import com.example.wordcounter.model.HistoryEntry
import com.example.wordcounter.model.TextMetrics
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests verifying all rules of the TextMetricsCalculator and HistoryEntry formatting.
 */
class TextMetricsCalculatorTest {

    private lateinit var calculator: TextMetricsCalculator

    @Before
    fun setUp() {
        calculator = TextMetricsCalculator()
    }

    @Test
    fun testEmptyAndBlankStringsReturnZero() {
        val emptyResult = calculator.calculateMetrics("")
        assertEquals(TextMetrics(0, 0, 0, 0), emptyResult)

        val blankResult = calculator.calculateMetrics("   \n\t  ")
        assertEquals(TextMetrics(0, 0, 0, 0), blankResult)
    }

    @Test
    fun testSentenceCounting_singleTerminator() {
        assertEquals(1, calculator.countSentences("This is a simple sentence."))
        assertEquals(1, calculator.countSentences("Look at this!"))
        assertEquals(1, calculator.countSentences("Where are you?"))
    }

    @Test
    fun testSentenceCounting_consecutivePunctuationMarksCountAsOne() {
        // "Multiple consecutive sentence-ending punctuation marks such as ?! count as one sentence."
        val text = "Really?! Are you sure?? Yes..."
        assertEquals(3, calculator.countSentences(text))

        val singleMulti = "What?!?!?"
        assertEquals(1, calculator.countSentences(singleMulti))
    }

    @Test
    fun testWordCounting_ignoresSurroundingPunctuation() {
        // "Count lexical words separated by whitespace. Ignore punctuation surrounding the word.
        // Example: Hello, world! = 2 words."
        val text = "Hello, world!"
        val (words, numbers) = calculator.countWordsAndNumbers(text)
        assertEquals(2, words)
        assertEquals(0, numbers)
    }

    @Test
    fun testWordCounting_withParenthesesAndQuotes() {
        val text = "\"Quoted word\" and (parenthesized word) here."
        val (words, _) = calculator.countWordsAndNumbers(text)
        // Words: "Quoted", "word", "and", "parenthesized", "word", "here" = 6 words
        assertEquals(6, words)
    }

    @Test
    fun testNumberCounting_countsDiscreteNumericTokensRegardlessOfDigits() {
        // "Count each numeric token as one number, regardless of how many digits it contains.
        // Example: 2026 = 1 number, 42 = 1 number."
        val text = "In 2026, there are 42 items and 7 categories."
        val (words, numbers) = calculator.countWordsAndNumbers(text)
        assertEquals(6, words) // In, there, are, items, and, categories
        assertEquals(3, numbers) // 2026, 42, 7
    }

    @Test
    fun testNumberCounting_decimalNumbers() {
        val text = "Pi is approximately 3.14 or 3.14159."
        val (words, numbers) = calculator.countWordsAndNumbers(text)
        assertEquals(4, words) // Pi, is, approximately, or
        assertEquals(2, numbers) // 3.14, 3.14159
        // Sentences must also not split on decimal dots
        assertEquals(1, calculator.countSentences(text))
    }

    @Test
    fun testSentenceCounting_decimalNumbersDoNotSplitSentences() {
        val text = "Hello, world! Did you know that 3.14 is an approximation of pi?! Absolutely remarkable."
        // 1: "Hello, world!"
        // 2: "Did you know that 3.14 is an approximation of pi?!" (3.14 decimal dot ignored)
        // 3: "Absolutely remarkable."
        assertEquals(3, calculator.countSentences(text))
    }

    @Test
    fun testSentenceCounting_punctuationOnlyReturnsZero() {
        assertEquals(0, calculator.countSentences("..."))
        assertEquals(0, calculator.countSentences("?!?!"))
        assertEquals(0, calculator.countSentences("---"))
    }

    @Test
    fun testNumberCounting_groupedNumbersAndTimes() {
        val text = "Meeting is at 18:00 for 1,000,000 users with -5 errors."
        val (words, numbers) = calculator.countWordsAndNumbers(text)
        // Numbers: 18:00, 1,000,000, -5 = 3 numbers
        assertEquals(3, numbers)
        // Words: Meeting, is, at, for, users, with, errors = 7 words
        assertEquals(7, words)
    }

    @Test
    fun testPunctuationMarksCounting_countsEveryPunctuationChar() {
        // "Count each punctuation character in the input text, including . , ! ? : ; - ( ) [ ] " ', etc.
        // Do not count spaces, letters, or digits."
        val sample = ".,!?:;-()[]\"'"
        assertEquals(13, calculator.countPunctuationMarks(sample))

        val text = "Hello, world! 2026."
        // Punctuation characters: ',' and '!' and '.' = 3
        assertEquals(3, calculator.countPunctuationMarks(text))
    }

    @Test
    fun testComprehensiveAnalysis() {
        val text = "In 2026, the lab tested 42 distinct algorithms across 3 continents: results were outstanding!"
        val result = calculator.calculateMetrics(text)

        // Sentences: 1 (ends with '!')
        assertEquals(1, result.sentences)
        // Words: "In", "the", "lab", "tested", "distinct", "algorithms", "across", "continents", "results", "were", "outstanding" = 11 words
        assertEquals(11, result.words)
        // Numbers: "2026", "42", "3" = 3 numbers
        assertEquals(3, result.numbers)
        // Punctuation: ',' ':' '!' = 3 punctuation marks
        assertEquals(3, result.punctuationMarks)
    }

    @Test
    fun testComprehensiveAnalysis_sampleSentenceWithDecimals() {
        val text = "Hello, world! Did you know that 3.14 is an approximation of pi?! Absolutely remarkable."
        val result = calculator.calculateMetrics(text)

        assertEquals(3, result.sentences)
        assertEquals(13, result.words)
        assertEquals(1, result.numbers) // 3.14
        assertEquals(6, result.punctuationMarks) // ',', '!', '.', '?', '!', '.'
    }

    @Test
    fun testHistoryEntryFormatting() {
        val entry = HistoryEntry(
            originalText = "Hello, world!",
            metrics = TextMetrics(
                sentences = 1,
                words = 2,
                punctuationMarks = 2,
                numbers = 0
            )
        )
        val expected = "\"Hello, world!\" => 1 sentences, 2 words, 2 punctuation marks, 0 numbers"
        assertEquals(expected, entry.formattedSummary())
    }
}
