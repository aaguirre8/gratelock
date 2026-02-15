package com.gratelock.app.ui.screen.jar

import com.gratelock.app.data.local.entity.JournalEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [JarUiState] and [EntryDetailUiState].
 *
 * Full ViewModel integration tests with Room require instrumented tests.
 * These tests verify the state types and their data.
 */
class JarViewModelTest {

    // --- JarUiState ---

    @Test
    fun jarUiState_loading_isCorrectType() {
        val state: JarUiState = JarUiState.Loading
        assertTrue(state is JarUiState.Loading)
    }

    @Test
    fun jarUiState_success_emptyEntries() {
        val state = JarUiState.Success(entries = emptyList())
        assertTrue(state.entries.isEmpty())
    }

    @Test
    fun jarUiState_success_withEntries() {
        val entries = listOf(
            JournalEntry(
                id = 1,
                entryType = "typed",
                gratitude1 = "Sunshine",
                capturedAt = 1000L,
                date = "2026-02-15",
            ),
            JournalEntry(
                id = 2,
                entryType = "photo",
                photoPath = "/photos/test.jpg",
                capturedAt = 2000L,
                date = "2026-02-14",
            ),
        )
        val state = JarUiState.Success(entries = entries)
        assertEquals(2, state.entries.size)
        assertEquals("typed", state.entries[0].entryType)
        assertEquals("photo", state.entries[1].entryType)
    }

    @Test
    fun jarUiState_success_defaultEntriesIsEmpty() {
        val state = JarUiState.Success()
        assertTrue(state.entries.isEmpty())
    }

    // --- EntryDetailUiState ---

    @Test
    fun entryDetailUiState_loading_isCorrectType() {
        val state: EntryDetailUiState = EntryDetailUiState.Loading
        assertTrue(state is EntryDetailUiState.Loading)
    }

    @Test
    fun entryDetailUiState_notFound_isCorrectType() {
        val state: EntryDetailUiState = EntryDetailUiState.NotFound
        assertTrue(state is EntryDetailUiState.NotFound)
    }

    @Test
    fun entryDetailUiState_success_holdsEntry() {
        val entry = JournalEntry(
            id = 1,
            entryType = "typed",
            gratitude1 = "Family",
            gratitude2 = "Health",
            gratitude3 = "Friends",
            capturedAt = 1000L,
            date = "2026-02-15",
            keywords = "family, health, friends",
        )
        val state = EntryDetailUiState.Success(entry = entry)
        assertEquals(1L, state.entry.id)
        assertEquals("typed", state.entry.entryType)
        assertEquals("family, health, friends", state.entry.keywords)
    }

    @Test
    fun entryDetailUiState_success_photoEntry() {
        val entry = JournalEntry(
            id = 2,
            entryType = "photo",
            photoPath = "/photos/journal_1.jpg",
            photoHash = "abc123",
            ocrText = "I am grateful for sunshine",
            capturedAt = 2000L,
            date = "2026-02-14",
        )
        val state = EntryDetailUiState.Success(entry = entry)
        assertEquals("photo", state.entry.entryType)
        assertEquals("/photos/journal_1.jpg", state.entry.photoPath)
        assertEquals("I am grateful for sunshine", state.entry.ocrText)
    }
}
