package com.gratelock.app.ui.screen.overlay

import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gratelock.app.data.local.GrateLockPreferences
import com.gratelock.app.data.repository.StatsRepository
import com.gratelock.app.domain.usecase.EmergencyUnlockResult
import com.gratelock.app.domain.usecase.EmergencyUnlockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the blocking overlay screen.
 *
 * Loads the blocked app's info and handles unlock actions.
 * Temporary: both "Type it" and "Photograph" just unlock for the day
 * until Increments 5 and 6 implement the real entry flows.
 *
 * @param savedStateHandle Holds the blocked package name from the intent.
 * @param context Application context for querying PackageManager.
 * @param statsRepository Repository for reading user stats.
 * @param preferences SharedPreferences cache.
 * @param emergencyUnlockUseCase Use case for emergency unlock logic.
 */
@HiltViewModel
class BlockingOverlayViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val statsRepository: StatsRepository,
    private val preferences: GrateLockPreferences,
    private val emergencyUnlockUseCase: EmergencyUnlockUseCase,
) : ViewModel() {

    private val blockedPackage: String =
        savedStateHandle.get<String>("blocked_package") ?: ""

    private val _uiState = MutableStateFlow<BlockingOverlayUiState>(BlockingOverlayUiState.Loading)
    val uiState: StateFlow<BlockingOverlayUiState> = _uiState.asStateFlow()

    init {
        loadBlockedAppInfo()
    }

    /** @param event The [BlockingOverlayEvent] to process. */
    fun onEvent(event: BlockingOverlayEvent) {
        when (event) {
            is BlockingOverlayEvent.TypeEntry -> temporaryUnlock()
            is BlockingOverlayEvent.PhotoEntry -> temporaryUnlock()
            is BlockingOverlayEvent.EmergencyUnlock -> emergencyUnlock()
        }
    }

    private fun loadBlockedAppInfo() {
        viewModelScope.launch {
            // Ensure stats row exists before any UPDATE queries
            statsRepository.initializeStats()

            try {
                val pm = context.packageManager
                val appInfo = pm.getApplicationInfo(blockedPackage, 0)
                val appName = pm.getApplicationLabel(appInfo).toString()
                val appIcon = pm.getApplicationIcon(appInfo)

                val stats = statsRepository.getStatsOnce()

                _uiState.value = BlockingOverlayUiState.Ready(
                    blockedAppName = appName,
                    blockedAppIcon = appIcon,
                    currentStreak = stats?.currentStreak ?: 0,
                    emergencyUnlocksRemaining = stats?.emergencyUnlocksRemaining ?: 0,
                )
            } catch (e: PackageManager.NameNotFoundException) {
                _uiState.value = BlockingOverlayUiState.Ready(
                    blockedAppName = blockedPackage,
                    currentStreak = preferences.currentStreak,
                    emergencyUnlocksRemaining = preferences.emergencyUnlocksRemaining,
                )
            }
        }
    }

    /**
     * Temporary unlock — will be replaced by real entry flows in Increments 5/6.
     * For now, both CTAs just unlock apps for the day.
     */
    private fun temporaryUnlock() {
        viewModelScope.launch {
            statsRepository.setUnlockedToday(true)
            preferences.isUnlockedToday = true
            _uiState.value = BlockingOverlayUiState.Unlocked
        }
    }

    private fun emergencyUnlock() {
        viewModelScope.launch {
            val result = emergencyUnlockUseCase()
            if (result is EmergencyUnlockResult.Success) {
                _uiState.value = BlockingOverlayUiState.Unlocked
            }
            // If no unlocks remaining, stay on overlay — user sees the count is 0
        }
    }
}
