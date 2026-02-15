package com.gratelock.app.ui.screen.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [HomeUiState] and [HomeEvent].
 *
 * Full ViewModel integration tests with Room require instrumented tests.
 * These tests verify the state types and their data.
 */
class HomeViewModelTest {

    @Test
    fun homeUiState_loading_isCorrectType() {
        val state: HomeUiState = HomeUiState.Loading
        assertTrue(state is HomeUiState.Loading)
    }

    @Test
    fun homeUiState_success_defaultValues() {
        val state = HomeUiState.Success()
        assertEquals(0, state.currentStreak)
        assertEquals(0, state.longestStreak)
        assertEquals(0, state.totalEntries)
        assertEquals(0f, state.jarFillPercent, 0.001f)
        assertFalse(state.isUnlockedToday)
    }

    @Test
    fun homeUiState_success_holdsCustomValues() {
        val state = HomeUiState.Success(
            currentStreak = 12,
            longestStreak = 30,
            totalEntries = 47,
            jarFillPercent = 0.47f,
            isUnlockedToday = true,
        )
        assertEquals(12, state.currentStreak)
        assertEquals(30, state.longestStreak)
        assertEquals(47, state.totalEntries)
        assertEquals(0.47f, state.jarFillPercent, 0.001f)
        assertTrue(state.isUnlockedToday)
    }

    @Test
    fun homeUiState_success_jarFillPercentCanBeZero() {
        val state = HomeUiState.Success(jarFillPercent = 0f)
        assertEquals(0f, state.jarFillPercent, 0.001f)
    }

    @Test
    fun homeUiState_success_jarFillPercentCanBeOne() {
        val state = HomeUiState.Success(jarFillPercent = 1.0f)
        assertEquals(1.0f, state.jarFillPercent, 0.001f)
    }

    @Test
    fun homeEvent_journalToday_isCorrectType() {
        val event: HomeEvent = HomeEvent.JournalToday
        assertTrue(event is HomeEvent.JournalToday)
    }

    @Test
    fun homeEvent_debugRelock_isCorrectType() {
        val event: HomeEvent = HomeEvent.DebugRelock
        assertTrue(event is HomeEvent.DebugRelock)
    }
}
