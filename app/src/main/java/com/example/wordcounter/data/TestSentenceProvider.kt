package com.example.wordcounter.data

import kotlin.random.Random

/**
 * Provides a curated collection of realistic test sentences containing
 * a diverse mix of words, punctuation marks, and numbers.
 */
class TestSentenceProvider(
    private val random: Random = Random.Default
) {
    private var lastIndex: Int = -1

    val sampleSentences: List<String> = listOf(
        "In 2026, the lab tested 42 distinct algorithms across 3 continents: results were outstanding!",
        "Hello, world! Did you know that 3.14 is an approximation of pi?! Absolutely remarkable.",
        "The Apollo 11 mission reached the Moon in 1969; exactly 3 astronauts made history!",
        "Are you ready for the next test?! Room 101 has 15 workstations, 2 printers, and 4 servers.",
        "Flight 747 was delayed by 45 minutes; however, all 230 passengers arrived safely...",
        "She purchased 3 notebooks, 12 pens, and 1 backpack for a total of 85 dollars.",
        "Notice: Section 4, Paragraph 2 states that all 50 participants must register before 18:00!",
        "Wait... did the temperature really drop from 25 degrees down to -5 degrees in 2 hours?!"
    )

    /**
     * Returns a random test sentence from the curated collection,
     * ensuring consecutive calls do not return the same sentence.
     */
    fun getRandomSentence(): String {
        if (sampleSentences.size <= 1) return sampleSentences.firstOrNull() ?: ""
        var nextIndex = random.nextInt(sampleSentences.size)
        while (nextIndex == lastIndex) {
            nextIndex = random.nextInt(sampleSentences.size)
        }
        lastIndex = nextIndex
        return sampleSentences[nextIndex]
    }

    /**
     * Returns a deterministic sentence by index (useful for unit testing).
     */
    fun getSentenceAt(index: Int): String {
        return sampleSentences[index % sampleSentences.size]
    }
}
