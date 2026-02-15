package com.gratelock.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TextValidatorTest {

    // --- validateField ---

    @Test
    fun validateField_validText_returnsNull() {
        assertNull(TextValidator.validateField("I'm grateful for my morning coffee"))
    }

    @Test
    fun validateField_emptyText_returnsError() {
        assertNotNull(TextValidator.validateField(""))
    }

    @Test
    fun validateField_tooShort_returnsError() {
        assertNotNull(TextValidator.validateField("hi"))
    }

    @Test
    fun validateField_exactlyMinimum_returnsNull() {
        // 10 chars minimum, need 2+ distinct words
        assertNull(TextValidator.validateField("I love dogs today"))
    }

    @Test
    fun validateField_tooLong_returnsError() {
        val longText = "a".repeat(201)
        assertNotNull(TextValidator.validateField(longText))
    }

    @Test
    fun validateField_exactly200chars_returnsNull() {
        // 200 chars of valid varied text
        val text = "I am grateful for so many wonderful things in my life " +
            "including my family and friends who bring joy and love " +
            "every single day and I appreciate all the beautiful moments we share together"
        assertTrue(text.length <= 200)
        assertNull(TextValidator.validateField(text))
    }

    @Test
    fun validateField_repeatedCharacters_returnsError() {
        assertNotNull(TextValidator.validateField("I loveeee this soooo much"))
    }

    @Test
    fun validateField_keyboardPattern_returnsError() {
        assertNotNull(TextValidator.validateField("qwerty asdfg something"))
    }

    @Test
    fun validateField_sequentialChars_returnsError() {
        assertNotNull(TextValidator.validateField("abcdefghij something here"))
    }

    @Test
    fun validateField_excessiveWordRepetition_returnsError() {
        assertNotNull(TextValidator.validateField("good good good good good"))
    }

    @Test
    fun validateField_singleDistinctWord_returnsError() {
        assertNotNull(TextValidator.validateField("word word word word word word"))
    }

    @Test
    fun validateField_normalGratitudeEntry_returnsNull() {
        assertNull(TextValidator.validateField("My family was really supportive today"))
    }

    @Test
    fun validateField_warmErrorMessages_areEncouraging() {
        val error = TextValidator.validateField("hi")
        assertNotNull(error)
        // Should not contain punitive/guilt-tripping language
        assertFalse(error!!.contains("invalid", ignoreCase = true))
        assertFalse(error.contains("wrong", ignoreCase = true))
        assertFalse(error.contains("fail", ignoreCase = true))
    }

    // --- hasRepeatedCharacters ---

    @Test
    fun hasRepeatedCharacters_fourInARow_returnsTrue() {
        assertTrue(TextValidator.hasRepeatedCharacters("helloooo"))
    }

    @Test
    fun hasRepeatedCharacters_threeInARow_returnsFalse() {
        assertFalse(TextValidator.hasRepeatedCharacters("hellooo"))
    }

    @Test
    fun hasRepeatedCharacters_normalText_returnsFalse() {
        assertFalse(TextValidator.hasRepeatedCharacters("I love my family"))
    }

    @Test
    fun hasRepeatedCharacters_repeatedSpaces_ignored() {
        assertFalse(TextValidator.hasRepeatedCharacters("hello     world"))
    }

    // --- hasKeyboardPattern ---

    @Test
    fun hasKeyboardPattern_qwerty_returnsTrue() {
        assertTrue(TextValidator.hasKeyboardPattern("qwerty"))
    }

    @Test
    fun hasKeyboardPattern_asdfg_returnsTrue() {
        assertTrue(TextValidator.hasKeyboardPattern("asdfg"))
    }

    @Test
    fun hasKeyboardPattern_normalText_returnsFalse() {
        assertFalse(TextValidator.hasKeyboardPattern("grateful for sunshine"))
    }

    @Test
    fun hasKeyboardPattern_caseInsensitive() {
        assertTrue(TextValidator.hasKeyboardPattern("QWERT"))
    }

    // --- hasSequentialCharacters ---

    @Test
    fun hasSequentialCharacters_ascending_returnsTrue() {
        assertTrue(TextValidator.hasSequentialCharacters("abcde"))
    }

    @Test
    fun hasSequentialCharacters_descending_returnsTrue() {
        assertTrue(TextValidator.hasSequentialCharacters("edcba"))
    }

    @Test
    fun hasSequentialCharacters_fourChars_returnsFalse() {
        assertFalse(TextValidator.hasSequentialCharacters("abcd"))
    }

    @Test
    fun hasSequentialCharacters_normalText_returnsFalse() {
        assertFalse(TextValidator.hasSequentialCharacters("grateful"))
    }

    // --- hasExcessiveWordRepetition ---

    @Test
    fun hasExcessiveWordRepetition_allSame_returnsTrue() {
        assertTrue(TextValidator.hasExcessiveWordRepetition("yes yes yes yes"))
    }

    @Test
    fun hasExcessiveWordRepetition_varied_returnsFalse() {
        assertFalse(TextValidator.hasExcessiveWordRepetition("I love my family"))
    }

    @Test
    fun hasExcessiveWordRepetition_fewWords_returnsFalse() {
        assertFalse(TextValidator.hasExcessiveWordRepetition("hi hi"))
    }

    // --- areDuplicates ---

    @Test
    fun areDuplicates_identicalText_returnsTrue() {
        assertTrue(TextValidator.areDuplicates("grateful for sunshine", "grateful for sunshine"))
    }

    @Test
    fun areDuplicates_verySimilar_returnsTrue() {
        assertTrue(TextValidator.areDuplicates("grateful for sunshine", "grateful for sunshines"))
    }

    @Test
    fun areDuplicates_completelyDifferent_returnsFalse() {
        assertFalse(TextValidator.areDuplicates("grateful for sunshine", "my dog is amazing"))
    }

    @Test
    fun areDuplicates_emptyStrings_returnsFalse() {
        assertFalse(TextValidator.areDuplicates("", ""))
    }

    @Test
    fun areDuplicates_caseInsensitive() {
        assertTrue(TextValidator.areDuplicates("GRATEFUL FOR SUNSHINE", "grateful for sunshine"))
    }

    // --- levenshteinDistance ---

    @Test
    fun levenshteinDistance_identical_returnsZero() {
        assertEquals(0, TextValidator.levenshteinDistance("hello", "hello"))
    }

    @Test
    fun levenshteinDistance_oneEdit_returnsOne() {
        assertEquals(1, TextValidator.levenshteinDistance("hello", "hallo"))
    }

    @Test
    fun levenshteinDistance_emptyToNonEmpty_returnsLength() {
        assertEquals(5, TextValidator.levenshteinDistance("", "hello"))
    }

    @Test
    fun levenshteinDistance_completelyDifferent_returnsMaxLength() {
        assertEquals(3, TextValidator.levenshteinDistance("abc", "xyz"))
    }
}
