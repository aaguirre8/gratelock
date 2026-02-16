package com.gratelock.app.ui.screen.home

/**
 * UI state for the Home screen.
 */
sealed interface HomeUiState {

    /** Initial loading state. */
    data object Loading : HomeUiState

    /**
     * Successfully loaded home data.
     *
     * @param currentStreak The user's current consecutive-day streak.
     * @param longestStreak The user's all-time longest streak.
     * @param totalEntries Total number of journal entries in the jar.
     * @param jarFillPercent Jar fill level as a float from 0f to 1f.
     * @param isUnlockedToday Whether apps are unlocked for today.
     * @param isAccessibilityServiceEnabled Whether the blocking service is active.
     */
    data class Success(
        val currentStreak: Int = 0,
        val longestStreak: Int = 0,
        val totalEntries: Int = 0,
        val jarFillPercent: Float = 0f,
        val isUnlockedToday: Boolean = false,
        val isAccessibilityServiceEnabled: Boolean = true,
    ) : HomeUiState
}
