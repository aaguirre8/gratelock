package com.gratelock.app.ui.screen.camera

import android.content.Context
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
import javax.inject.Inject

/**
 * ViewModel for the Camera screen.
 *
 * Manages camera state, photo capture to file, ML Kit validation from the
 * saved file, perceptual hash duplicate detection, entry saving, streak
 * calculation, and unlock.
 *
 * CameraX saves the photo directly to a JPEG file, then ML Kit reads
 * from that file via [InputImage.fromFilePath]. This avoids lossy manual
 * bitmap conversion and gives ML Kit the highest-quality image data.
 *
 * @param validatePhotoEntryUseCase Validates the photo file via ML Kit + hash.
 * @param calculateStreakUseCase Calculates and updates the streak.
 * @param unlockAppsUseCase Unlocks blocked apps for the day.
 * @param entryRepository Repository for saving journal entries.
 * @param statsRepository Repository for updating total entries.
 * @param context Application context for file path access.
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

    /**
     * Returns the directory where journal photos are saved.
     * CameraScreen uses this to tell CameraX where to write.
     */
    fun getPhotosDir(): File {
        val photosDir = File(context.filesDir, "journal_photos")
        if (!photosDir.exists()) photosDir.mkdirs()
        return photosDir
    }

    /** @param event The [CameraEvent] to process. */
    fun onEvent(event: CameraEvent) {
        when (event) {
            is CameraEvent.Capture -> {
                _uiState.value = CameraUiState.Capturing
            }
            is CameraEvent.ToggleFlash -> toggleFlash()
            is CameraEvent.PhotoSaved -> processPhoto(event.file)
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

    private fun processPhoto(photoFile: File) {
        _uiState.value = CameraUiState.Processing

        viewModelScope.launch {
            val result = validatePhotoEntryUseCase(photoFile)

            when (result) {
                is PhotoEntryValidationResult.Invalid -> {
                    _uiState.value = CameraUiState.Error(result.reason)
                }
                is PhotoEntryValidationResult.Valid -> {
                    saveEntry(photoFile, result.extractedText, result.photoHash)
                }
            }
        }
    }

    private suspend fun saveEntry(photoFile: File, extractedText: String, photoHash: String) {
        val entry = JournalEntry(
            entryType = "photo",
            photoPath = photoFile.absolutePath,
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
}
