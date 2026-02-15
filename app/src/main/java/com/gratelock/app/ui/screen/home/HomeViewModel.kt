package com.gratelock.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gratelock.app.data.local.GrateLockPreferences
import com.gratelock.app.data.repository.StatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Home screen.
 *
 * Exposes a single [StateFlow] of [HomeUiState] and handles UI events
 * via [onEvent]. Observes stats from Room to show real unlock status.
 *
 * @param statsRepository Repository for reading/writing user stats.
 * @param preferences SharedPreferences cache for syncing with the blocking service.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val statsRepository: StatsRepository,
    private val preferences: GrateLockPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeStats()
    }

    /** @param event The [HomeEvent] to process. */
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.JournalToday -> { /* Will navigate to entry flow */ }
            is HomeEvent.DebugRelock -> debugRelock()
        }
    }

    private fun observeStats() {
        viewModelScope.launch {
            statsRepository.initializeStats()
            statsRepository.getStats().collect { stats ->
                _uiState.value = HomeUiState.Success(
                    currentStreak = stats?.currentStreak ?: 0,
                    longestStreak = stats?.longestStreak ?: 0,
                    totalEntries = stats?.totalEntries ?: 0,
                    jarFillPercent = (stats?.totalEntries ?: 0).coerceAtMost(100) / 100f,
                    isUnlockedToday = stats?.isUnlockedToday ?: false,
                )
            }
        }
    }

    private fun debugRelock() {
        viewModelScope.launch {
            statsRepository.setUnlockedToday(false)
            preferences.isUnlockedToday = false
        }
    }
}
