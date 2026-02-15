package com.gratelock.app.ui.screen.home

/**
 * Events that can be triggered from the Home screen UI.
 */
sealed interface HomeEvent {
    /** Navigate to the gratitude entry flow. */
    data object JournalToday : HomeEvent

    /** Debug: re-lock apps by resetting today's unlock status. */
    data object DebugRelock : HomeEvent
}
