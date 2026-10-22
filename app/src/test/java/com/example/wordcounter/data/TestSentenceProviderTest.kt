package com.example.wordcounter.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/**
 * Unit tests for [TestSentenceProvider].
 * Verifies sample sentence retrieval, deterministic indexing, and random non-repeating generation.
 */
class TestSentenceProviderTest {

    @Test
    fun testDefaultConstructorInitializesCorrectly() {
        val provider = TestSentenceProvider()
        assertEquals(8, provider.sampleSentences.size)
        for (sentence in provider.sampleSentences) {
            assertTrue(sentence.isNotBlank())
        }
    }

    @Test
    fun testGetSentenceAt_directIndices() {
        val provider = TestSentenceProvider()
        for (i in provider.sampleSentences.indices) {
            assertEquals(provider.sampleSentences[i], provider.getSentenceAt(i))
        }
    }

    @Test
    fun testGetSentenceAt_moduloWrapping() {
        val provider = TestSentenceProvider()
        val size = provider.sampleSentences.size
        assertEquals(provider.sampleSentences[0], provider.getSentenceAt(size))
        assertEquals(provider.sampleSentences[1], provider.getSentenceAt(size + 1))
        assertEquals(provider.sampleSentences[3], provider.getSentenceAt(size * 3 + 3))
    }

    @Test
    fun testGetRandomSentence_doesNotRepeatConcurrentlyAcrossMultipleCalls() {
        val provider = TestSentenceProvider()
        var previous = provider.getRandomSentence()
        assertTrue(provider.sampleSentences.contains(previous))

        // Call 50 times and assert consecutive sentences are never identical
        for (i in 1..50) {
            val current = provider.getRandomSentence()
            assertTrue(provider.sampleSentences.contains(current))
            assertNotEquals("Consecutive sentences should not be identical", previous, current)
            previous = current
        }
    }

    @Test
    fun testGetRandomSentence_handlesRepeatedRandomValues() {
        // Mock a Random instance that returns sequence [2, 2, 5]
        val sequence = mutableListOf(2, 2, 5)
        val mockRandom = object : Random() {
            override fun nextBits(bitCount: Int): Int = 0
            override fun nextInt(until: Int): Int {
                return if (sequence.isNotEmpty()) sequence.removeAt(0) else 0
            }
        }

        val provider = TestSentenceProvider(random = mockRandom)
        val first = provider.getRandomSentence() // picks 2
        assertEquals(provider.sampleSentences[2], first)

        // Second call: nextInt returns 2 (same as lastIndex), so loop executes again and picks 5
        val second = provider.getRandomSentence()
        assertEquals(provider.sampleSentences[5], second)
    }

    @Test
    fun testGetRandomSentence_withSingleOrEmptySampleSentences() {
        val provider = TestSentenceProvider()
        val field = TestSentenceProvider::class.java.getDeclaredField("sampleSentences")
        field.isAccessible = true

        // Test with empty list
        field.set(provider, emptyList<String>())
        assertEquals("", provider.getRandomSentence())

        // Test with single item list
        field.set(provider, listOf("Only sentence."))
        assertEquals("Only sentence.", provider.getRandomSentence())
    }
}
