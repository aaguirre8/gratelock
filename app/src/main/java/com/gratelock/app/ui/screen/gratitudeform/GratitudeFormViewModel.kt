package com.gratelock.app.ui.screen.gratitudeform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gratelock.app.data.local.entity.JournalEntry
import com.gratelock.app.data.repository.EntryRepository
import com.gratelock.app.data.repository.StatsRepository
import com.gratelock.app.domain.model.ValidationResult
import com.gratelock.app.domain.usecase.CalculateStreakUseCase
import com.gratelock.app.domain.usecase.ExtractKeywordsUseCase
import com.gratelock.app.domain.usecase.UnlockAppsUseCase
import com.gratelock.app.domain.usecase.ValidateTypedEntryUseCase
import com.gratelock.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Gratitude Form screen.
 *
 * Manages the 3 gratitude text fields, validates input, saves entries
 * to Room, calculates streaks, and unlocks apps upon success.
 *
 * @param validateTypedEntryUseCase Validates all three fields.
 * @param extractKeywordsUseCase Extracts keywords from entry text.
 * @param calculateStreakUseCase Calculates and updates the streak.
 * @param unlockAppsUseCase Unlocks blocked apps for the day.
 * @param entryRepository Repository for saving journal entries.
 * @param statsRepository Repository for updating total entries count.
 */
@HiltViewModel
class GratitudeFormViewModel @Inject constructor(
    private val validateTypedEntryUseCase: ValidateTypedEntryUseCase,
    private val extractKeywordsUseCase: ExtractKeywordsUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val unlockAppsUseCase: UnlockAppsUseCase,
    private val entryRepository: EntryRepository,
    private val statsRepository: StatsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<GratitudeFormUiState>(GratitudeFormUiState.Ready())
    val uiState: StateFlow<GratitudeFormUiState> = _uiState.asStateFlow()

    /** @param event The [GratitudeFormEvent] to process. */
    fun onEvent(event: GratitudeFormEvent) {
        when (event) {
            is GratitudeFormEvent.FieldChanged -> updateField(event.fieldIndex, event.text)
            is GratitudeFormEvent.Submit -> submit()
        }
    }

    private fun updateField(fieldIndex: Int, text: String) {
        val current = _uiState.value as? GratitudeFormUiState.Ready ?: return
        val updatedState = when (fieldIndex) {
            0 -> current.copy(field1 = text)
            1 -> current.copy(field2 = text)
            2 -> current.copy(field3 = text)
            else -> current
        }
        // Clear the error for the field being edited
        val clearedErrors = updatedState.fieldErrors.toMutableMap()
        clearedErrors.remove(fieldIndex)
        _uiState.value = updatedState.copy(fieldErrors = clearedErrors)
    }

    private fun submit() {
        val current = _uiState.value as? GratitudeFormUiState.Ready ?: return
        if (current.isSubmitting) return

        val result = validateTypedEntryUseCase(current.field1, current.field2, current.field3)

        when (result) {
            is ValidationResult.Invalid -> {
                _uiState.value = current.copy(fieldErrors = result.fieldErrors)
            }
            is ValidationResult.Valid -> {
                _uiState.update { current.copy(isSubmitting = true, fieldErrors = emptyMap()) }
                saveEntry(current.field1, current.field2, current.field3)
            }
        }
    }

    private fun saveEntry(field1: String, field2: String, field3: String) {
        viewModelScope.launch {
            val keywords = extractKeywordsUseCase(field1, field2, field3)

            val entry = JournalEntry(
                entryType = "typed",
                gratitude1 = field1.trim(),
                gratitude2 = field2.trim(),
                gratitude3 = field3.trim(),
                capturedAt = DateUtils.currentTimeMillis(),
                date = DateUtils.todayDateString(),
                keywords = keywords.ifEmpty { null },
                isValid = true,
            )

            entryRepository.saveEntry(entry)

            // Update total entries count
            val newCount = entryRepository.getEntryCount()
            statsRepository.updateTotalEntries(newCount)

            // Calculate and update streak
            val newStreak = calculateStreakUseCase()

            // Unlock apps for the day
            unlockAppsUseCase()

            _uiState.value = GratitudeFormUiState.Success(newStreak = newStreak)
        }
    }
}
