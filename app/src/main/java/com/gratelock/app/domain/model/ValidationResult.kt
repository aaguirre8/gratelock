package com.gratelock.app.domain.model

/**
 * Result of validating a typed gratitude entry.
 *
 * Each field is validated independently. The overall result is [Valid]
 * only when all three fields pass validation.
 */
sealed interface ValidationResult {

    /** All fields passed validation. */
    data object Valid : ValidationResult

    /**
     * One or more fields failed validation.
     *
     * @param fieldErrors Map of field index (0, 1, 2) to the warm error message.
     */
    data class Invalid(val fieldErrors: Map<Int, String>) : ValidationResult
}
