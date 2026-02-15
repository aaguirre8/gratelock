package com.gratelock.app.ui.screen.jar

import com.gratelock.app.data.local.entity.JournalEntry

/**
 * UI state for the Jar (entry gallery) screen.
 */
sealed interface JarUiState {

    /** Initial loading state. */
    data object Loading : JarUiState

    /**
     * Successfully loaded entries.
     *
     * @param entries All journal entries, newest first.
     */
    data class Success(
        val entries: List<JournalEntry> = emptyList(),
    ) : JarUiState
}
