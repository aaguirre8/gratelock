package com.gratelock.app.ui.screen.home

/**
 * Events that can be triggered from the Home screen UI.
 */
sealed interface HomeEvent {
    /** Navigate to the gratitude entry flow. */
    data object JournalToday : HomeEvent

    /** Open system accessibility settings to re-enable the blocking service. */
    data object OpenAccessibilitySettings : HomeEvent

    /** Refresh the accessibility service status (e.g., when resuming). */
    data object RefreshServiceStatus : HomeEvent

    /** Debug: re-lock apps by resetting today's unlock status. */
    data object DebugRelock : HomeEvent
}
