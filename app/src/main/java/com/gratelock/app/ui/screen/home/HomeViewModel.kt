package com.gratelock.app.ui.screen.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gratelock.app.data.local.GrateLockPreferences
import com.gratelock.app.data.repository.StatsRepository
import com.gratelock.app.util.PermissionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
 * Checks the accessibility service status on resume and shows a
 * re-enable banner if it has been disabled.
 *
 * @param context Application context for checking permissions.
 * @param statsRepository Repository for reading/writing user stats.
 * @param preferences SharedPreferences cache for syncing with the blocking service.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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
            is HomeEvent.OpenAccessibilitySettings -> {
                PermissionUtils.openAccessibilitySettings(context)
            }
            is HomeEvent.RefreshServiceStatus -> refreshServiceStatus()
            is HomeEvent.DebugRelock -> debugRelock()
        }
    }

    private fun observeStats() {
        viewModelScope.launch {
            statsRepository.initializeStats()
            statsRepository.getStats().collect { stats ->
                val isServiceEnabled =
                    PermissionUtils.isAccessibilityServiceEnabled(context)
                _uiState.value = HomeUiState.Success(
                    currentStreak = stats?.currentStreak ?: 0,
                    longestStreak = stats?.longestStreak ?: 0,
                    totalEntries = stats?.totalEntries ?: 0,
                    jarFillPercent = (stats?.totalEntries ?: 0).coerceAtMost(100) / 100f,
                    isUnlockedToday = stats?.isUnlockedToday ?: false,
                    isAccessibilityServiceEnabled = isServiceEnabled,
                )
            }
        }
    }

    private fun refreshServiceStatus() {
        val current = _uiState.value
        if (current is HomeUiState.Success) {
            _uiState.value = current.copy(
                isAccessibilityServiceEnabled =
                    PermissionUtils.isAccessibilityServiceEnabled(context),
            )
        }
    }

    private fun debugRelock() {
        viewModelScope.launch {
            statsRepository.setUnlockedToday(false)
            preferences.isUnlockedToday = false
        }
    }
}
