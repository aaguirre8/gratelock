package com.gratelock.app.ui.screen.appselection

/**
 * Events that can be triggered from the App Selection screen UI.
 */
sealed interface AppSelectionEvent {

    /**
     * User toggled an app's blocked status.
     *
     * @param packageName The package to toggle.
     * @param isBlocked The new blocked state.
     */
    data class ToggleApp(val packageName: String, val isBlocked: Boolean) : AppSelectionEvent

    /**
     * User changed the search query.
     *
     * @param query The new search text.
     */
    data class SearchChanged(val query: String) : AppSelectionEvent

    /** User tapped "Select All Social Media". */
    data object SelectAllSocialMedia : AppSelectionEvent

    /** User tapped "Save" to persist selections. */
    data object Save : AppSelectionEvent
}
