package com.gratelock.app.ui.screen.jar

import androidx.lifecycle.SavedStateHandle
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
 * ViewModel for the entry detail screen.
 *
 * Loads a single journal entry by its ID from the navigation argument.
 *
 * @param entryRepository Repository for reading journal entries.
 * @param savedStateHandle Contains the navigation argument "entryId".
 */
@HiltViewModel
class EntryDetailViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val entryId: Long = savedStateHandle.get<Long>("entryId") ?: -1L

    private val _uiState = MutableStateFlow<EntryDetailUiState>(EntryDetailUiState.Loading)
    val uiState: StateFlow<EntryDetailUiState> = _uiState.asStateFlow()

    init {
        loadEntry()
    }

    private fun loadEntry() {
        viewModelScope.launch {
            val entry = entryRepository.getEntryById(entryId)
            _uiState.value = if (entry != null) {
                EntryDetailUiState.Success(entry)
            } else {
                EntryDetailUiState.NotFound
            }
        }
    }
}
