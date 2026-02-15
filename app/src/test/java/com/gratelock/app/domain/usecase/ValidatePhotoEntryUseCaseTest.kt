package com.gratelock.app.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [PhotoEntryValidationResult] sealed interface.
 *
 * Full integration tests for [ValidatePhotoEntryUseCase] require
 * ML Kit and Bitmap, which need instrumented tests. These tests
 * verify the result types and their data.
 */
class ValidatePhotoEntryUseCaseTest {

    @Test
    fun validResult_holdsExtractedTextAndHash() {
        val result = PhotoEntryValidationResult.Valid(
            extractedText = "I am grateful for sunshine",
            photoHash = "abc123",
        )
        assertEquals("I am grateful for sunshine", result.extractedText)
        assertEquals("abc123", result.photoHash)
    }

    @Test
    fun invalidResult_holdsReason() {
        val result = PhotoEntryValidationResult.Invalid(
            reason = "Not enough text detected",
        )
        assertEquals("Not enough text detected", result.reason)
    }

    @Test
    fun validResult_isCorrectType() {
        val result: PhotoEntryValidationResult = PhotoEntryValidationResult.Valid(
            extractedText = "text",
            photoHash = "hash",
        )
        assertTrue(result is PhotoEntryValidationResult.Valid)
    }

    @Test
    fun invalidResult_isCorrectType() {
        val result: PhotoEntryValidationResult = PhotoEntryValidationResult.Invalid(
            reason = "error",
        )
        assertTrue(result is PhotoEntryValidationResult.Invalid)
    }
}
