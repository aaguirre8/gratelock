package com.gratelock.app.domain.usecase

import com.gratelock.app.domain.model.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateTypedEntryUseCaseTest {

    private lateinit var useCase: ValidateTypedEntryUseCase

    @Before
    fun setUp() {
        useCase = ValidateTypedEntryUseCase()
    }

    @Test
    fun invoke_allValidFields_returnsValid() {
        val result = useCase(
            "I'm grateful for my morning coffee",
            "The sunshine through the window was beautiful",
            "My friend called to check on me today",
        )
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun invoke_firstFieldTooShort_returnsInvalidWithError() {
        val result = useCase(
            "hi",
            "The sunshine through the window was beautiful",
            "My friend called to check on me today",
        )
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).fieldErrors
        assertTrue(0 in errors)
        assertEquals(1, errors.size)
    }

    @Test
    fun invoke_allFieldsTooShort_returnsThreeErrors() {
        val result = useCase("hi", "yo", "ok")
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).fieldErrors
        assertEquals(3, errors.size)
        assertTrue(0 in errors)
        assertTrue(1 in errors)
        assertTrue(2 in errors)
    }

    @Test
    fun invoke_duplicateFields1And2_returnsInvalid() {
        val result = useCase(
            "I'm grateful for my morning coffee today",
            "I'm grateful for my morning coffee today",
            "My friend called to check on me today",
        )
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).fieldErrors
        assertTrue(1 in errors)
    }

    @Test
    fun invoke_duplicateFields1And3_returnsInvalid() {
        val result = useCase(
            "I'm grateful for my morning coffee today",
            "The sunshine through the window was beautiful",
            "I'm grateful for my morning coffee today",
        )
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).fieldErrors
        assertTrue(2 in errors)
    }

    @Test
    fun invoke_duplicateFields2And3_returnsInvalid() {
        val result = useCase(
            "I'm grateful for my morning coffee today",
            "The sunshine through the window was beautiful",
            "The sunshine through the window was beautiful",
        )
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).fieldErrors
        assertTrue(2 in errors)
    }

    @Test
    fun invoke_gibberishField_returnsInvalid() {
        val result = useCase(
            "qwerty asdfg something here test",
            "The sunshine through the window was beautiful",
            "My friend called to check on me today",
        )
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).fieldErrors
        assertTrue(0 in errors)
    }

    @Test
    fun invoke_duplicateNotFlaggedWhenFieldAlreadyInvalid() {
        // If field 0 is invalid (too short), don't also flag field 1 as duplicate
        val result = useCase(
            "hi",
            "hi",
            "My friend called to check on me today",
        )
        assertTrue(result is ValidationResult.Invalid)
        val errors = (result as ValidationResult.Invalid).fieldErrors
        // Both fields are individually invalid for being too short
        // No duplicate error should be added since both failed individual validation
        assertTrue(0 in errors)
        assertTrue(1 in errors)
    }
}
