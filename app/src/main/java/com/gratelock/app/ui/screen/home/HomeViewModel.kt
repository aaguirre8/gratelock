package com.gratelock.app.ui.screen.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for the Home screen.
 *
 * Exposes a single [StateFlow] of [HomeUiState] and handles UI events
 * via [onEvent]. Currently provides placeholder data until the data
 * layer is wired in Increment 2.
 */
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Success())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /** @param event The [HomeEvent] to process. */
    fun onEvent(event: HomeEvent) {
        // Events will be handled as features are added
    }
}
