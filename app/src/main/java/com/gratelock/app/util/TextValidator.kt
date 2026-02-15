package com.gratelock.app.util

/**
 * Validates typed gratitude entries for gibberish, duplicates, and minimum content.
 *
 * Detects common patterns of low-effort input: repeated characters,
 * keyboard mashing, sequential characters, and word repetition.
 * Uses Levenshtein distance for cross-field duplicate detection.
 */
object TextValidator {

    private const val MIN_CHARS = 10
    private const val MAX_CHARS = 200
    private const val MIN_DISTINCT_WORDS = 2
    private const val REPEATED_CHAR_THRESHOLD = 4
    private const val WORD_REPETITION_RATIO = 0.7
    private const val DUPLICATE_SIMILARITY_THRESHOLD = 0.8

    private val KEYBOARD_PATTERNS = listOf(
        "qwert", "asdfg", "zxcvb", "qazwsx", "poiuy", "lkjhg", "mnbvc",
        "12345", "67890", "abcde", "fghij", "klmno", "pqrst", "uvwxy",
    )

    /**
     * Validates a single gratitude field's text content.
     *
     * @param text The text to validate.
     * @return `null` if valid, or a warm error message string if invalid.
     */
    fun validateField(text: String): String? {
        val trimmed = text.trim()

        if (trimmed.length < MIN_CHARS) {
            return "Could you share a bit more? At least a few words would be wonderful."
        }

        if (trimmed.length > MAX_CHARS) {
            return "That's a lot of gratitude! Could you keep it under $MAX_CHARS characters?"
        }

        if (hasRepeatedCharacters(trimmed)) {
            return "Hmm, that doesn't look quite right. Try writing something meaningful."
        }

        if (hasKeyboardPattern(trimmed)) {
            return "Let's try something more heartfelt — what are you truly grateful for?"
        }

        if (hasSequentialCharacters(trimmed)) {
            return "Take a moment and think — what made you smile today?"
        }

        if (hasExcessiveWordRepetition(trimmed)) {
            return "Try using different words to express your gratitude."
        }

        val words = trimmed.split("\\s+".toRegex()).filter { it.length > 1 }
        if (words.toSet().size < MIN_DISTINCT_WORDS) {
            return "Try to share a bit more detail about what you're grateful for."
        }

        return null
    }

    /**
     * Checks whether two entries are too similar (potential duplicates).
     *
     * @param text1 First entry text.
     * @param text2 Second entry text.
     * @return `true` if the texts exceed the similarity threshold.
     */
    fun areDuplicates(text1: String, text2: String): Boolean {
        val a = text1.trim().lowercase()
        val b = text2.trim().lowercase()
        if (a.isEmpty() || b.isEmpty()) return false
        if (a == b) return true
        val distance = levenshteinDistance(a, b)
        val maxLen = maxOf(a.length, b.length)
        val similarity = 1.0 - (distance.toDouble() / maxLen)
        return similarity >= DUPLICATE_SIMILARITY_THRESHOLD
    }

    /**
     * Detects runs of the same character (e.g., "aaaa", "!!!!").
     *
     * @param text The text to check.
     * @return `true` if any character repeats consecutively [REPEATED_CHAR_THRESHOLD] or more times.
     */
    internal fun hasRepeatedCharacters(text: String): Boolean {
        var count = 1
        for (i in 1 until text.length) {
            if (text[i] == text[i - 1] && !text[i].isWhitespace()) {
                count++
                if (count >= REPEATED_CHAR_THRESHOLD) return true
            } else {
                count = 1
            }
        }
        return false
    }

    /**
     * Detects keyboard mashing patterns (e.g., "qwerty", "asdfg").
     *
     * @param text The text to check.
     * @return `true` if any keyboard pattern is found.
     */
    internal fun hasKeyboardPattern(text: String): Boolean {
        val lower = text.lowercase().replace("\\s+".toRegex(), "")
        return KEYBOARD_PATTERNS.any { pattern -> lower.contains(pattern) }
    }

    /**
     * Detects long sequential character runs (e.g., "abcdefg").
     *
     * @param text The text to check.
     * @return `true` if 5+ sequential ascending or descending characters are found.
     */
    internal fun hasSequentialCharacters(text: String): Boolean {
        val lower = text.lowercase().replace("\\s+".toRegex(), "")
        if (lower.length < 5) return false
        var ascending = 1
        var descending = 1
        for (i in 1 until lower.length) {
            if (lower[i].code == lower[i - 1].code + 1) {
                ascending++
                if (ascending >= 5) return true
            } else {
                ascending = 1
            }
            if (lower[i].code == lower[i - 1].code - 1) {
                descending++
                if (descending >= 5) return true
            } else {
                descending = 1
            }
        }
        return false
    }

    /**
     * Detects excessive word repetition (e.g., "good good good good").
     *
     * @param text The text to check.
     * @return `true` if the most frequent word appears more than [WORD_REPETITION_RATIO] of the time.
     */
    internal fun hasExcessiveWordRepetition(text: String): Boolean {
        val words = text.lowercase().split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (words.size < 3) return false
        val frequencies = words.groupingBy { it }.eachCount()
        val maxFreq = frequencies.values.maxOrNull() ?: 0
        return maxFreq.toDouble() / words.size > WORD_REPETITION_RATIO
    }

    /**
     * Computes the Levenshtein edit distance between two strings.
     *
     * @param a First string.
     * @param b Second string.
     * @return The minimum number of single-character edits to transform [a] into [b].
     */
    internal fun levenshteinDistance(a: String, b: String): Int {
        val m = a.length
        val n = b.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j

        for (i in 1..m) {
            for (j in 1..n) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost,
                )
            }
        }
        return dp[m][n]
    }
}
