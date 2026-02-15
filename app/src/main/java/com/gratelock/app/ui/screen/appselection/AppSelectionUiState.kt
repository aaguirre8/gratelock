package com.gratelock.app.ui.screen.appselection

import com.gratelock.app.domain.model.AppCategory
import com.gratelock.app.domain.model.InstalledApp

/**
 * UI state for the App Selection screen.
 */
sealed interface AppSelectionUiState {

    /** Initial loading state while querying installed apps. */
    data object Loading : AppSelectionUiState

    /**
     * Successfully loaded the list of installed apps.
     *
     * @param apps All installed apps, filtered by the current search query.
     * @param searchQuery The current search text.
     * @param blockedCount Number of apps currently toggled for blocking.
     * @param categories Categories present in the filtered list, sorted by priority.
     */
    data class Success(
        val apps: List<InstalledApp> = emptyList(),
        val searchQuery: String = "",
        val blockedCount: Int = 0,
        val categories: List<AppCategory> = emptyList(),
    ) : AppSelectionUiState

    /**
     * Error state if apps could not be loaded.
     *
     * @param message Describes what went wrong.
     */
    data class Error(val message: String) : AppSelectionUiState
}
