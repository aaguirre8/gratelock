package com.gratelock.app.ui.screen.onboarding

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Onboarding screen.
 *
 * Manages page navigation, permission state tracking, and onboarding
 * completion. Permission states are refreshed when the user returns
 * from system settings.
 *
 * @param context Application context for checking permissions.
 * @param statsRepository Repository for marking onboarding complete.
 * @param preferences SharedPreferences cache for syncing onboarding state.
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val statsRepository: StatsRepository,
    private val preferences: GrateLockPreferences,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        refreshPermissions()
    }

    /** @param event The [OnboardingEvent] to process. */
    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.NextPage -> nextPage()
            is OnboardingEvent.PreviousPage -> previousPage()
            is OnboardingEvent.OpenAccessibilitySettings -> {
                PermissionUtils.openAccessibilitySettings(context)
            }
            is OnboardingEvent.OpenOverlaySettings -> {
                PermissionUtils.openOverlaySettings(context)
            }
            is OnboardingEvent.RefreshPermissions -> refreshPermissions()
            is OnboardingEvent.CompleteOnboarding -> completeOnboarding()
        }
    }

    private fun nextPage() {
        _uiState.update { current ->
            val nextPage = (current.currentPage + 1).coerceAtMost(
                OnboardingUiState.PAGE_TOTAL - 1,
            )
            current.copy(currentPage = nextPage)
        }
    }

    private fun previousPage() {
        _uiState.update { current ->
            current.copy(currentPage = (current.currentPage - 1).coerceAtLeast(0))
        }
    }

    private fun refreshPermissions() {
        _uiState.update { current ->
            current.copy(
                isAccessibilityEnabled = PermissionUtils.isAccessibilityServiceEnabled(context),
                isOverlayEnabled = PermissionUtils.canDrawOverlays(context),
            )
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            statsRepository.initializeStats()
            statsRepository.markOnboardingComplete()
            preferences.onboardingComplete = true
            _uiState.update { it.copy(isComplete = true) }
        }
    }
}
