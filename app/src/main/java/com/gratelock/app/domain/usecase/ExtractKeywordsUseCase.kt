package com.gratelock.app.domain.usecase

import javax.inject.Inject

/**
 * Extracts top keywords from gratitude entry text.
 *
 * Tokenizes the combined text, removes common stop words, and returns
 * the most frequent meaningful words. Used for keyword tagging on entries.
 */
class ExtractKeywordsUseCase @Inject constructor() {

    private companion object {
        const val MAX_KEYWORDS = 5
        val STOP_WORDS = setOf(
            "i", "me", "my", "myself", "we", "our", "ours", "ourselves",
            "you", "your", "yours", "yourself", "yourselves",
            "he", "him", "his", "himself", "she", "her", "hers", "herself",
            "it", "its", "itself", "they", "them", "their", "theirs", "themselves",
            "what", "which", "who", "whom", "this", "that", "these", "those",
            "am", "is", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "having", "do", "does", "did", "doing",
            "a", "an", "the", "and", "but", "if", "or", "because", "as",
            "until", "while", "of", "at", "by", "for", "with", "about",
            "against", "between", "through", "during", "before", "after",
            "above", "below", "to", "from", "up", "down", "in", "out",
            "on", "off", "over", "under", "again", "further", "then", "once",
            "here", "there", "when", "where", "why", "how", "all", "both",
            "each", "few", "more", "most", "other", "some", "such", "no",
            "nor", "not", "only", "own", "same", "so", "than", "too",
            "very", "can", "will", "just", "should", "now",
            "im", "ive", "id", "ill", "its", "thats", "dont", "doesnt",
            "grateful", "thankful", "today", "really", "much", "also",
            "got", "get", "getting", "make", "made", "making",
        )
    }

    /**
     * Extracts top keywords from the three gratitude fields.
     *
     * @param field1 First gratitude item.
     * @param field2 Second gratitude item.
     * @param field3 Third gratitude item.
     * @return Comma-separated string of top keywords, or empty string if none found.
     */
    operator fun invoke(field1: String, field2: String, field3: String): String {
        val combined = "$field1 $field2 $field3"
        val words = combined
            .lowercase()
            .replace("[^a-z\\s']".toRegex(), "")
            .split("\\s+".toRegex())
            .filter { it.length > 2 && it !in STOP_WORDS }

        val frequencies = words.groupingBy { it }.eachCount()

        return frequencies.entries
            .sortedByDescending { it.value }
            .take(MAX_KEYWORDS)
            .joinToString(",") { it.key }
    }
}
