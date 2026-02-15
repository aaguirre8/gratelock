package com.gratelock.app.ui.screen.home

/**
 * Events that can be triggered from the Home screen UI.
 */
sealed interface HomeEvent {
    /** Navigate to the gratitude entry flow. */
    data object JournalToday : HomeEvent
}
