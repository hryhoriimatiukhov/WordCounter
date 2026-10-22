package com.example.wordcounter

import com.example.wordcounter.calculator.TextMetricsCalculator
import com.example.wordcounter.data.TestSentenceProvider
import com.example.wordcounter.model.TextMetrics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests verifying all rules and edge cases of [TextMetricsCalculator].
 * Covers 100% of methods, lines, and decision branches.
 */
class TextMetricsCalculatorTest {

    private lateinit var calculator: TextMetricsCalculator

    @Before
    fun setUp() {
        calculator = TextMetricsCalculator()
    }

    // -------------------------------------------------------------------------
    // Blank & Empty Inputs
    // -------------------------------------------------------------------------

    @Test
    fun testEmptyAndBlankStringsReturnZero() {
        val emptyResult = calculator.calculateMetrics("")
        assertEquals(TextMetrics(0, 0, 0, 0), emptyResult)

        val spacesResult = calculator.calculateMetrics("   ")
        assertEquals(TextMetrics(0, 0, 0, 0), spacesResult)

        val whitespaceResult = calculator.calculateMetrics(" \t \n \r ")
        assertEquals(TextMetrics(0, 0, 0, 0), whitespaceResult)

        assertEquals(0, calculator.countSentences(""))
        assertEquals(0, calculator.countSentences("   \n\t"))
        assertEquals(0, calculator.countPunctuationMarks(""))
        assertEquals(0, calculator.countPunctuationMarks("   \n\t"))

        val (words, numbers) = calculator.countWordsAndNumbers("")
        assertEquals(0, words)
        assertEquals(0, numbers)

        val (bWords, bNumbers) = calculator.countWordsAndNumbers("   \n\t")
        assertEquals(0, bWords)
        assertEquals(0, bNumbers)
    }

    // -------------------------------------------------------------------------
    // Sentence Counting Tests
    // -------------------------------------------------------------------------

    @Test
    fun testSentenceCounting_singleTerminator() {
        assertEquals(1, calculator.countSentences("This is a simple sentence."))
        assertEquals(1, calculator.countSentences("Look at this!"))
        assertEquals(1, calculator.countSentences("Where are you?"))
    }

    @Test
    fun testSentenceCounting_multipleSentencesWithVariousDelimiters() {
        val text = "First sentence. Second sentence! Third sentence? Fourth sentence."
        assertEquals(4, calculator.countSentences(text))
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
    fun testSentenceCounting_noTerminalPunctuation() {
        // Without ending '.', '!', or '?', sentences are 0 according to rule
        assertEquals(0, calculator.countSentences("This sentence has no ending mark"))
    }

    @Test
    fun testSentenceCounting_decimalNumbersDoNotSplitSentences() {
        val text = "Hello, world! Did you know that 3.14 is an approximation of pi?! Absolutely remarkable."
        assertEquals(3, calculator.countSentences(text))

        val singleWithDecimal = "The price is 19.99 dollars today."
        assertEquals(1, calculator.countSentences(singleWithDecimal))
    }

    @Test
    fun testSentenceCounting_decimalEdgeCases() {
        // Decimal at the very start of string: ".5 is half." -> start is 0
        assertEquals(1, calculator.countSentences(".5 is half."))

        // Dot followed by non-digit: "Version 1.a was released."
        assertEquals(2, calculator.countSentences("Version 1.a was released."))

        // Dot preceded by non-digit: "Section a.5 is complete."
        assertEquals(2, calculator.countSentences("Section a.5 is complete."))

        // Dot at end of string with digit: "Level 5." -> nextChar is null
        assertEquals(1, calculator.countSentences("Level 5."))

        // Consecutive dots like .. do not equal "."
        assertEquals(2, calculator.countSentences("Item 1..2 was updated."))
    }

    @Test
    fun testSentenceCounting_punctuationOnlyReturnsZero() {
        assertEquals(0, calculator.countSentences("..."))
        assertEquals(0, calculator.countSentences("?!?!"))
        assertEquals(0, calculator.countSentences("---"))
        assertEquals(0, calculator.countSentences("... ?! ... !"))
    }

    @Test
    fun testSentenceCounting_numericOnlySentences() {
        // A segment with digits is a valid sentence
        assertEquals(1, calculator.countSentences("2026."))
        assertEquals(2, calculator.countSentences("100! 200?"))
    }

    // -------------------------------------------------------------------------
    // Word and Number Counting Tests
    // -------------------------------------------------------------------------

    @Test
    fun testWordCounting_ignoresSurroundingPunctuation() {
        val text = "Hello, world!"
        val (words, numbers) = calculator.countWordsAndNumbers(text)
        assertEquals(2, words)
        assertEquals(0, numbers)
    }

    @Test
    fun testWordCounting_withParenthesesQuotesAndBrackets() {
        val text = "\"Quoted word\" and (parenthesized word) and [bracketed] here."
        val (words, numbers) = calculator.countWordsAndNumbers(text)
        assertEquals(8, words)
        assertEquals(0, numbers)
    }

    @Test
    fun testWordCounting_hyphenatedAndContractions() {
        val text = "It's a state-of-the-art solution, isn't it?"
        val (words, numbers) = calculator.countWordsAndNumbers(text)
        // Words: "It's", "a", "state-of-the-art", "solution", "isn't", "it" = 6 words
        assertEquals(6, words)
        assertEquals(0, numbers)
    }

    @Test
    fun testWordCounting_alphanumericTokensCountedAsWords() {
        // Tokens containing letters are counted as lexical words
        val text = "Apollo 11 reached Room101 with H2O and 2026."
        val (words, numbers) = calculator.countWordsAndNumbers(text)
        // Words: Apollo, reached, Room101, with, H2O, and = 6 words
        // Numbers: 11, 2026 = 2 numbers
        assertEquals(6, words)
        assertEquals(2, numbers)
    }

    @Test
    fun testNumberCounting_integersAndDecimals() {
        val text = "Counts: 0, 42, 2026, 3.14, and 3,14."
        val (words, numbers) = calculator.countWordsAndNumbers(text)
        // Numbers: 0, 42, 2026, 3.14, 3,14 = 5 numbers
        assertEquals(5, numbers)
        // Words: Counts, and = 2 words
        assertEquals(2, words)
    }

    @Test
    fun testNumberCounting_signedNumbers() {
        val text = "Temperature changed from +25 to -5 degrees."
        val (words, numbers) = calculator.countWordsAndNumbers(text)
        // Numbers: +25, -5 = 2 numbers
        assertEquals(2, numbers)
        // Words: Temperature, changed, from, to, degrees = 5 words
        assertEquals(5, words)
    }

    @Test
    fun testNumberCounting_groupedNumbersTimesAndFractions() {
        val text = "At 18:00 or 09:30:00, 1,000,000 users downloaded 10,000.50 files in 3/4 hour."
        val (words, numbers) = calculator.countWordsAndNumbers(text)
        // Numbers: 18:00, 09:30:00, 1,000,000, 10,000.50, 3/4 = 5 numbers
        assertEquals(5, numbers)
        // Words: At, or, users, downloaded, files, in, hour = 7 words
        assertEquals(7, words)
    }

    @Test
    fun testWordAndNumberCounting_purePunctuationTokensSkipped() {
        val text = "Word --- another *** word ..."
        val (words, numbers) = calculator.countWordsAndNumbers(text)
        assertEquals(3, words) // Word, another, word
        assertEquals(0, numbers)
    }

    @Test
    fun testWordAndNumberCounting_nonLexicalNonNumericTokens() {
        // A token like "12#34" has no letters and does not match the numeric regex
        val text = "Valid word 12#34 42"
        val (words, numbers) = calculator.countWordsAndNumbers(text)
        assertEquals(2, words) // Valid, word
        assertEquals(1, numbers) // 42 (12#34 is neither)
    }

    // -------------------------------------------------------------------------
    // Punctuation Marks Counting Tests
    // -------------------------------------------------------------------------

    @Test
    fun testIsPunctuation_characterTypes() {
        // Alphanumerics and spaces are NOT punctuation
        assertFalse(calculator.isPunctuation('a'))
        assertFalse(calculator.isPunctuation('Z'))
        assertFalse(calculator.isPunctuation('0'))
        assertFalse(calculator.isPunctuation('9'))
        assertFalse(calculator.isPunctuation(' '))
        assertFalse(calculator.isPunctuation('\t'))
        assertFalse(calculator.isPunctuation('\n'))

        // Punctuation and symbols ARE punctuation
        assertTrue(calculator.isPunctuation('.'))
        assertTrue(calculator.isPunctuation(','))
        assertTrue(calculator.isPunctuation('!'))
        assertTrue(calculator.isPunctuation('?'))
        assertTrue(calculator.isPunctuation(':'))
        assertTrue(calculator.isPunctuation(';'))
        assertTrue(calculator.isPunctuation('-'))
        assertTrue(calculator.isPunctuation('('))
        assertTrue(calculator.isPunctuation(')'))
        assertTrue(calculator.isPunctuation('['))
        assertTrue(calculator.isPunctuation(']'))
        assertTrue(calculator.isPunctuation('{'))
        assertTrue(calculator.isPunctuation('}'))
        assertTrue(calculator.isPunctuation('"'))
        assertTrue(calculator.isPunctuation('\''))
        assertTrue(calculator.isPunctuation('/'))
        assertTrue(calculator.isPunctuation('\\'))
        assertTrue(calculator.isPunctuation('@'))
        assertTrue(calculator.isPunctuation('#'))
        assertTrue(calculator.isPunctuation('$'))
        assertTrue(calculator.isPunctuation('%'))
        assertTrue(calculator.isPunctuation('^'))
        assertTrue(calculator.isPunctuation('&'))
        assertTrue(calculator.isPunctuation('*'))
        assertTrue(calculator.isPunctuation('_'))
        assertTrue(calculator.isPunctuation('+'))
        assertTrue(calculator.isPunctuation('='))
        assertTrue(calculator.isPunctuation('<'))
        assertTrue(calculator.isPunctuation('>'))
        assertTrue(calculator.isPunctuation('~'))
    }

    @Test
    fun testCountPunctuationMarks_accurateCount() {
        val sample = ".,!?:;-()[]\"'"
        assertEquals(13, calculator.countPunctuationMarks(sample))

        val text = "Hello, world! 2026."
        // ',' '!' '.' = 3
        assertEquals(3, calculator.countPunctuationMarks(text))

        val textWithSymbols = "Price is $100 & rate is 5%!"
        // '$', '&', '%', '!' = 4
        assertEquals(4, calculator.countPunctuationMarks(textWithSymbols))

        assertEquals(0, calculator.countPunctuationMarks("No punctuation here 12345"))
    }

    // -------------------------------------------------------------------------
    // Curated Sample Sentences Consistency
    // -------------------------------------------------------------------------

    @Test
    fun testCuratedSampleSentences_deterministicCalculations() {
        val provider = TestSentenceProvider()

        // Sentence 0: "In 2026, the lab tested 42 distinct algorithms across 3 continents: results were outstanding!"
        val s0 = calculator.calculateMetrics(provider.getSentenceAt(0))
        assertEquals(1, s0.sentences)
        assertEquals(11, s0.words)
        assertEquals(3, s0.numbers)
        assertEquals(3, s0.punctuationMarks)

        // Sentence 1: "Hello, world! Did you know that 3.14 is an approximation of pi?! Absolutely remarkable."
        val s1 = calculator.calculateMetrics(provider.getSentenceAt(1))
        assertEquals(3, s1.sentences)
        assertEquals(13, s1.words)
        assertEquals(1, s1.numbers)
        assertEquals(6, s1.punctuationMarks)

        // Sentence 2: "The Apollo 11 mission reached the Moon in 1969; exactly 3 astronauts made history!"
        val s2 = calculator.calculateMetrics(provider.getSentenceAt(2))
        assertEquals(1, s2.sentences)
        assertEquals(11, s2.words)
        assertEquals(3, s2.numbers)
        assertEquals(2, s2.punctuationMarks)

        // Sentence 3: "Are you ready for the next test?! Room 101 has 15 workstations, 2 printers, and 4 servers."
        val s3 = calculator.calculateMetrics(provider.getSentenceAt(3))
        assertEquals(2, s3.sentences)
        assertEquals(13, s3.words)
        assertEquals(4, s3.numbers)
        assertEquals(5, s3.punctuationMarks)

        // Sentence 4: "Flight 747 was delayed by 45 minutes; however, all 230 passengers arrived safely..."
        val s4 = calculator.calculateMetrics(provider.getSentenceAt(4))
        assertEquals(1, s4.sentences)
        assertEquals(10, s4.words)
        assertEquals(3, s4.numbers)
        assertEquals(5, s4.punctuationMarks)

        // Sentence 5: "She purchased 3 notebooks, 12 pens, and 1 backpack for a total of 85 dollars."
        val s5 = calculator.calculateMetrics(provider.getSentenceAt(5))
        assertEquals(1, s5.sentences)
        assertEquals(11, s5.words)
        assertEquals(4, s5.numbers)
        assertEquals(3, s5.punctuationMarks)

        // Sentence 6: "Notice: Section 4, Paragraph 2 states that all 50 participants must register before 18:00!"
        val s6 = calculator.calculateMetrics(provider.getSentenceAt(6))
        assertEquals(1, s6.sentences)
        assertEquals(10, s6.words)
        assertEquals(4, s6.numbers)
        assertEquals(4, s6.punctuationMarks)

        // Sentence 7: "Wait... did the temperature really drop from 25 degrees down to -5 degrees in 2 hours?!"
        val s7 = calculator.calculateMetrics(provider.getSentenceAt(7))
        assertEquals(2, s7.sentences)
        assertEquals(13, s7.words)
        assertEquals(3, s7.numbers)
        assertEquals(6, s7.punctuationMarks)
    }

    // -------------------------------------------------------------------------
    // Private Helpers Direct Testing via Reflection
    // -------------------------------------------------------------------------

    @Test
    fun testPrivateHelpers_directInvocation() {
        val isNumericMethod = TextMetricsCalculator::class.java.getDeclaredMethod("isNumericToken", String::class.java)
        isNumericMethod.isAccessible = true
        // Test empty string guard
        assertFalse(isNumericMethod.invoke(calculator, "") as Boolean)
        assertTrue(isNumericMethod.invoke(calculator, "42") as Boolean)

        val isLexicalMethod = TextMetricsCalculator::class.java.getDeclaredMethod("isLexicalWord", String::class.java)
        isLexicalMethod.isAccessible = true
        assertFalse(isLexicalMethod.invoke(calculator, "") as Boolean)
        assertTrue(isLexicalMethod.invoke(calculator, "word") as Boolean)

        val stripMethod = TextMetricsCalculator::class.java.getDeclaredMethod("stripSurroundingPunctuation", String::class.java)
        stripMethod.isAccessible = true
        assertEquals("test", stripMethod.invoke(calculator, "...test!"))
        assertEquals("", stripMethod.invoke(calculator, "..."))
    }
}

