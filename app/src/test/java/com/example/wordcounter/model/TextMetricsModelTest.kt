package com.example.wordcounter.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for domain models [TextMetrics] and [HistoryEntry].
 * Ensures 100% function, constructor, and branch coverage for model data classes.
 */
class TextMetricsModelTest {

    @Test
    fun testTextMetrics_defaultConstructor() {
        val metrics = TextMetrics()
        assertEquals(0, metrics.sentences)
        assertEquals(0, metrics.words)
        assertEquals(0, metrics.punctuationMarks)
        assertEquals(0, metrics.numbers)
    }

    @Test
    fun testTextMetrics_customConstructorAndComponentDestructuring() {
        val metrics = TextMetrics(
            sentences = 3,
            words = 15,
            punctuationMarks = 4,
            numbers = 2
        )
        val (s, w, p, n) = metrics
        assertEquals(3, s)
        assertEquals(15, w)
        assertEquals(4, p)
        assertEquals(2, n)
    }

    @Test
    fun testTextMetrics_copyAndEquality() {
        val original = TextMetrics(1, 2, 3, 4)
        val copied = original.copy(words = 10)
        assertEquals(1, copied.sentences)
        assertEquals(10, copied.words)
        assertEquals(3, copied.punctuationMarks)
        assertEquals(4, copied.numbers)

        val identical = original.copy()
        assertEquals(original, identical)
        assertEquals(original.hashCode(), identical.hashCode())
        assertNotEquals(original, copied)
        assertFalse(original.equals("Not a TextMetrics"))
        assertTrue(original.toString().contains("sentences=1"))
    }

    @Test
    fun testHistoryEntry_defaultConstructorGeneratesIdAndTimestamp() {
        val before = System.currentTimeMillis()
        val entry = HistoryEntry(
            originalText = "Sample sentence.",
            metrics = TextMetrics(1, 2, 1, 0)
        )
        val after = System.currentTimeMillis()

        assertNotNull(entry.id)
        assertTrue(entry.id.isNotEmpty())
        assertTrue(entry.timestampMillis in before..after)
        assertEquals("Sample sentence.", entry.originalText)
        assertEquals(1, entry.metrics.sentences)
    }

    @Test
    fun testHistoryEntry_customConstructorAndComponentDestructuring() {
        val customId = "custom-test-id-123"
        val customTimestamp = 1700000000000L
        val metrics = TextMetrics(2, 5, 2, 1)

        val entry = HistoryEntry(
            id = customId,
            originalText = "Custom text",
            metrics = metrics,
            timestampMillis = customTimestamp
        )

        val (id, text, m, ts) = entry
        assertEquals(customId, id)
        assertEquals("Custom text", text)
        assertEquals(metrics, m)
        assertEquals(customTimestamp, ts)
    }

    @Test
    fun testHistoryEntry_formattedSummary() {
        val entry = HistoryEntry(
            originalText = "Testing 1, 2, 3!",
            metrics = TextMetrics(sentences = 1, words = 1, punctuationMarks = 3, numbers = 3)
        )

        val expected = "\"Testing 1, 2, 3!\" => 1 sentences, 1 words, 3 punctuation marks, 3 numbers"
        assertEquals(expected, entry.formattedSummary())

        val emptyMetricsEntry = HistoryEntry(
            originalText = "",
            metrics = TextMetrics(0, 0, 0, 0)
        )
        val expectedEmpty = "\"\" => 0 sentences, 0 words, 0 punctuation marks, 0 numbers"
        assertEquals(expectedEmpty, emptyMetricsEntry.formattedSummary())
    }

    @Test
    fun testHistoryEntry_copyAndEquality() {
        val metrics = TextMetrics(1, 2, 1, 0)
        val entry1 = HistoryEntry(
            id = "id-1",
            originalText = "Test",
            metrics = metrics,
            timestampMillis = 1000L
        )
        val entry2 = entry1.copy()
        assertEquals(entry1, entry2)
        assertEquals(entry1.hashCode(), entry2.hashCode())

        val entry3 = entry1.copy(originalText = "Updated")
        assertNotEquals(entry1, entry3)
        assertFalse(entry1.equals(null))
        assertFalse(entry1.equals("A String"))
        assertTrue(entry1.toString().contains("originalText=Test"))
    }
}
