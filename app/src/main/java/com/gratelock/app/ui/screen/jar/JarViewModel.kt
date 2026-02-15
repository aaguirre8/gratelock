package com.gratelock.app.ui.screen.jar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gratelock.app.data.repository.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Jar (entry gallery) screen.
 *
 * Observes all journal entries from Room and exposes them sorted
 * newest-first via [JarUiState].
 *
 * @param entryRepository Repository for reading journal entries.
 */
@HiltViewModel
class JarViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<JarUiState>(JarUiState.Loading)
    val uiState: StateFlow<JarUiState> = _uiState.asStateFlow()

    init {
        observeEntries()
    }

    private fun observeEntries() {
        viewModelScope.launch {
            entryRepository.getAllEntries().collect { entries ->
                _uiState.value = JarUiState.Success(entries = entries)
            }
        }
    }
}
