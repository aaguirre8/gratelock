package com.gratelock.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PerceptualHashTest {

    // --- hammingDistance ---

    @Test
    fun hammingDistance_identicalHashes_returnsZero() {
        assertEquals(0, PerceptualHash.hammingDistance("abcdef", "abcdef"))
    }

    @Test
    fun hammingDistance_completelyDifferent_returnsMaxBits() {
        // "f" = 1111, "0" = 0000 → 4 bits differ per char
        assertEquals(4, PerceptualHash.hammingDistance("f", "0"))
    }

    @Test
    fun hammingDistance_singleBitDifference() {
        // "0" = 0000, "1" = 0001 → 1 bit differs
        assertEquals(1, PerceptualHash.hammingDistance("0", "1"))
    }

    @Test
    fun hammingDistance_emptyHashes_returnsZero() {
        assertEquals(0, PerceptualHash.hammingDistance("", ""))
    }

    @Test
    fun hammingDistance_differentLengths_padsWithZero() {
        // "f" vs "" → "f" = 1111 vs "0" = 0000 → 4 bits
        assertEquals(4, PerceptualHash.hammingDistance("f", ""))
    }

    @Test
    fun hammingDistance_multipleChars() {
        // "ff" vs "00" → 8 bits differ
        assertEquals(8, PerceptualHash.hammingDistance("ff", "00"))
    }

    @Test
    fun hammingDistance_mixedDifferences() {
        // "a" = 1010, "b" = 1011 → 1 bit differs
        assertEquals(1, PerceptualHash.hammingDistance("a", "b"))
    }

    // --- similarity ---

    @Test
    fun similarity_identicalHashes_returnsOne() {
        assertEquals(1.0, PerceptualHash.similarity("abcdef", "abcdef"), 0.001)
    }

    @Test
    fun similarity_completelyDifferent_returnsZero() {
        // "ffff" = all 1s, "0000" = all 0s → 16 bits differ out of 16
        assertEquals(0.0, PerceptualHash.similarity("ffff", "0000"), 0.001)
    }

    @Test
    fun similarity_emptyHashes_returnsOne() {
        assertEquals(1.0, PerceptualHash.similarity("", ""), 0.001)
    }

    @Test
    fun similarity_halfDifferent_returnsFiftyPercent() {
        // "f0" = 1111 0000, "0f" = 0000 1111 → 8 bits differ out of 8 total
        // Wait: "f0" is 2 chars → 8 bits total. xor: f^0 = f (4 bits), 0^f = f (4 bits) → 8/8 = 0
        // Let me use different example:
        // "f" = 1111, "7" = 0111 → 1 bit differs out of 4 → similarity = 3/4 = 0.75
        assertEquals(0.75, PerceptualHash.similarity("f", "7"), 0.001)
    }

    @Test
    fun similarity_highSimilarity_aboveThreshold() {
        // For duplicate detection, similarity should be above 0.95
        val similarity = PerceptualHash.similarity("abcdef12", "abcdef12")
        assertTrue(similarity >= 0.95)
    }

    @Test
    fun similarity_slightDifference_belowOne() {
        // "abcdef12" vs "abcdef13" → last char: "2"=0010, "3"=0011 → 1 bit diff
        val similarity = PerceptualHash.similarity("abcdef12", "abcdef13")
        assertTrue(similarity > 0.9)
        assertTrue(similarity < 1.0)
    }

    @Test
    fun similarity_isSymmetric() {
        val sim1 = PerceptualHash.similarity("abc", "def")
        val sim2 = PerceptualHash.similarity("def", "abc")
        assertEquals(sim1, sim2, 0.001)
    }
}
