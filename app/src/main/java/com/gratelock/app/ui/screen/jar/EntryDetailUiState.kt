package com.gratelock.app.ui.screen.jar

import com.gratelock.app.data.local.entity.JournalEntry

/**
 * UI state for the entry detail screen.
 */
sealed interface EntryDetailUiState {

    /** Initial loading state. */
    data object Loading : EntryDetailUiState

    /**
     * Successfully loaded entry.
     *
     * @param entry The journal entry to display.
     */
    data class Success(val entry: JournalEntry) : EntryDetailUiState

    /** Entry not found. */
    data object NotFound : EntryDetailUiState
}
