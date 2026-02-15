package com.gratelock.app.ui.screen.gratitudeform

/**
 * UI state for the Gratitude Form screen.
 */
sealed interface GratitudeFormUiState {

    /** Initial loading state. */
    data object Loading : GratitudeFormUiState

    /**
     * Form is ready for input.
     *
     * @param field1 Text of the first gratitude item.
     * @param field2 Text of the second gratitude item.
     * @param field3 Text of the third gratitude item.
     * @param fieldErrors Map of field index (0, 1, 2) to error message.
     * @param isSubmitting Whether the form is currently being submitted.
     */
    data class Ready(
        val field1: String = "",
        val field2: String = "",
        val field3: String = "",
        val fieldErrors: Map<Int, String> = emptyMap(),
        val isSubmitting: Boolean = false,
    ) : GratitudeFormUiState

    /**
     * Entry saved successfully — apps unlocked.
     *
     * @param newStreak The user's updated streak count.
     */
    data class Success(
        val newStreak: Int = 0,
    ) : GratitudeFormUiState
}
