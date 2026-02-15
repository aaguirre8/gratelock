package com.gratelock.app.ui.screen.gratitudeform

/**
 * Events that can be triggered from the Gratitude Form screen UI.
 */
sealed interface GratitudeFormEvent {

    /**
     * User updated a gratitude field.
     *
     * @param fieldIndex Which field was changed (0, 1, or 2).
     * @param text The new text content.
     */
    data class FieldChanged(val fieldIndex: Int, val text: String) : GratitudeFormEvent

    /** User tapped the submit button. */
    data object Submit : GratitudeFormEvent
}
