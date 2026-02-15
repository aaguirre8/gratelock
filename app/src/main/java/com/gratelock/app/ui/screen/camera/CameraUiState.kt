package com.gratelock.app.ui.screen.camera

/**
 * UI state for the Camera screen.
 */
sealed interface CameraUiState {

    /** Camera preview is active, ready for capture. */
    data class Previewing(
        val isFlashOn: Boolean = false,
    ) : CameraUiState

    /** Photo is being captured. */
    data object Capturing : CameraUiState

    /** Photo captured, now processing (ML Kit + hash). */
    data object Processing : CameraUiState

    /**
     * Validation failed — show error and allow retry.
     *
     * @param errorMessage Warm user-facing error message.
     */
    data class Error(
        val errorMessage: String,
    ) : CameraUiState

    /**
     * Photo validated and saved — entry complete.
     *
     * @param newStreak Updated streak count.
     */
    data class Success(
        val newStreak: Int,
    ) : CameraUiState
}
