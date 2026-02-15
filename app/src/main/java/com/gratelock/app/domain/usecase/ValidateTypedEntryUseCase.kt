package com.gratelock.app.domain.usecase

import com.gratelock.app.domain.model.ValidationResult
import com.gratelock.app.util.TextValidator
import javax.inject.Inject

/**
 * Validates a typed gratitude entry (3 fields).
 *
 * Checks each field independently for gibberish, minimum content, and
 * character limits. Also performs cross-field duplicate detection to
 * ensure the user isn't repeating the same gratitude item.
 */
class ValidateTypedEntryUseCase @Inject constructor() {

    /**
     * Validates the three gratitude fields.
     *
     * @param field1 First gratitude item text.
     * @param field2 Second gratitude item text.
     * @param field3 Third gratitude item text.
     * @return [ValidationResult.Valid] if all pass, or [ValidationResult.Invalid] with per-field errors.
     */
    operator fun invoke(field1: String, field2: String, field3: String): ValidationResult {
        val errors = mutableMapOf<Int, String>()
        val fields = listOf(field1, field2, field3)

        // Validate each field individually
        fields.forEachIndexed { index, text ->
            val error = TextValidator.validateField(text)
            if (error != null) {
                errors[index] = error
            }
        }

        // Cross-field duplicate detection (only check fields that passed individual validation)
        if (0 !in errors && 1 !in errors && TextValidator.areDuplicates(field1, field2)) {
            errors[1] = "This sounds a lot like your first item — try something different!"
        }
        if (0 !in errors && 2 !in errors && TextValidator.areDuplicates(field1, field3)) {
            errors[2] = "This sounds a lot like your first item — try something different!"
        }
        if (1 !in errors && 2 !in errors && TextValidator.areDuplicates(field2, field3)) {
            errors[2] = "This sounds a lot like your second item — try something different!"
        }

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}
