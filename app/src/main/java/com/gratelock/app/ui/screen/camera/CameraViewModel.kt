package com.gratelock.app.ui.screen.camera

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gratelock.app.data.local.entity.JournalEntry
import com.gratelock.app.data.repository.EntryRepository
import com.gratelock.app.data.repository.StatsRepository
import com.gratelock.app.domain.usecase.CalculateStreakUseCase
import com.gratelock.app.domain.usecase.PhotoEntryValidationResult
import com.gratelock.app.domain.usecase.UnlockAppsUseCase
import com.gratelock.app.domain.usecase.ValidatePhotoEntryUseCase
import com.gratelock.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * ViewModel for the Camera screen.
 *
 * Manages camera state, photo capture, ML Kit validation, perceptual
 * hash duplicate detection, entry saving, streak calculation, and unlock.
 *
 * @param validatePhotoEntryUseCase Validates the photo via ML Kit + hash.
 * @param calculateStreakUseCase Calculates and updates the streak.
 * @param unlockAppsUseCase Unlocks blocked apps for the day.
 * @param entryRepository Repository for saving journal entries.
 * @param statsRepository Repository for updating total entries.
 * @param context Application context for saving photos to internal storage.
 */
@HiltViewModel
class CameraViewModel @Inject constructor(
    private val validatePhotoEntryUseCase: ValidatePhotoEntryUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val unlockAppsUseCase: UnlockAppsUseCase,
    private val entryRepository: EntryRepository,
    private val statsRepository: StatsRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Previewing())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    /** @param event The [CameraEvent] to process. */
    fun onEvent(event: CameraEvent) {
        when (event) {
            is CameraEvent.Capture -> {
                _uiState.value = CameraUiState.Capturing
            }
            is CameraEvent.ToggleFlash -> toggleFlash()
            is CameraEvent.PhotoCaptured -> processPhoto(event.bitmap)
            is CameraEvent.Retry -> {
                _uiState.value = CameraUiState.Previewing(
                    isFlashOn = false,
                )
            }
        }
    }

    private fun toggleFlash() {
        val current = _uiState.value as? CameraUiState.Previewing ?: return
        _uiState.value = current.copy(isFlashOn = !current.isFlashOn)
    }

    private fun processPhoto(bitmap: Bitmap) {
        _uiState.value = CameraUiState.Processing(bitmap)

        viewModelScope.launch {
            val result = validatePhotoEntryUseCase(bitmap)

            when (result) {
                is PhotoEntryValidationResult.Invalid -> {
                    _uiState.value = CameraUiState.Error(result.reason)
                }
                is PhotoEntryValidationResult.Valid -> {
                    saveEntry(bitmap, result.extractedText, result.photoHash)
                }
            }
        }
    }

    private suspend fun saveEntry(bitmap: Bitmap, extractedText: String, photoHash: String) {
        // Save photo to internal storage
        val photoPath = savePhotoToInternal(bitmap)

        val entry = JournalEntry(
            entryType = "photo",
            photoPath = photoPath,
            photoHash = photoHash,
            ocrText = extractedText,
            capturedAt = DateUtils.currentTimeMillis(),
            date = DateUtils.todayDateString(),
            isValid = true,
        )

        entryRepository.saveEntry(entry)

        // Update total entries
        val newCount = entryRepository.getEntryCount()
        statsRepository.updateTotalEntries(newCount)

        // Calculate streak
        val newStreak = calculateStreakUseCase()

        // Unlock apps
        unlockAppsUseCase()

        _uiState.value = CameraUiState.Success(newStreak = newStreak)
    }

    private fun savePhotoToInternal(bitmap: Bitmap): String {
        val photosDir = File(context.filesDir, "journal_photos")
        if (!photosDir.exists()) photosDir.mkdirs()

        val fileName = "journal_${DateUtils.todayDateString()}_${System.currentTimeMillis()}.jpg"
        val file = File(photosDir, fileName)

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }

        return file.absolutePath
    }
}
