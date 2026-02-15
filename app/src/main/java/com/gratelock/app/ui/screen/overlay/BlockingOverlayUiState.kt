package com.gratelock.app.ui.screen.overlay

import android.graphics.drawable.Drawable

/**
 * UI state for the blocking overlay screen.
 */
sealed interface BlockingOverlayUiState {

    /** Loading blocked app info. */
    data object Loading : BlockingOverlayUiState

    /**
     * Overlay is ready to display.
     *
     * @param blockedAppName Display name of the blocked app.
     * @param blockedAppIcon Icon drawable of the blocked app.
     * @param currentStreak The user's current consecutive-day streak.
     * @param emergencyUnlocksRemaining Emergency unlocks left this week.
     */
    data class Ready(
        val blockedAppName: String = "",
        val blockedAppIcon: Drawable? = null,
        val currentStreak: Int = 0,
        val emergencyUnlocksRemaining: Int = 0,
    ) : BlockingOverlayUiState

    /** Apps have been unlocked — overlay should dismiss. */
    data object Unlocked : BlockingOverlayUiState
}
