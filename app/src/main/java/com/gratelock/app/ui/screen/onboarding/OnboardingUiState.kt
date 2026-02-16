package com.gratelock.app.ui.screen.onboarding

/**
 * UI state for the Onboarding screen.
 *
 * @param currentPage The current page index (0-3).
 * @param isAccessibilityEnabled Whether the accessibility service is enabled.
 * @param isOverlayEnabled Whether the overlay permission is granted.
 * @param isComplete Whether onboarding is finished and ready to proceed.
 */
data class OnboardingUiState(
    val currentPage: Int = 0,
    val isAccessibilityEnabled: Boolean = false,
    val isOverlayEnabled: Boolean = false,
    val isComplete: Boolean = false,
) {
    /** Whether all required permissions are granted. */
    val allPermissionsGranted: Boolean
        get() = isAccessibilityEnabled && isOverlayEnabled

    companion object {
        const val PAGE_WELCOME = 0
        const val PAGE_HOW_IT_WORKS = 1
        const val PAGE_PERMISSIONS = 2
        const val PAGE_TOTAL = 3
    }
}
