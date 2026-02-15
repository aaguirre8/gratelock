package com.gratelock.app.domain.usecase

import android.content.Context
import android.graphics.BitmapFactory
import com.gratelock.app.data.repository.EntryRepository
import com.gratelock.app.util.Constants
import com.gratelock.app.util.PerceptualHash
import com.gratelock.app.util.PhotoValidationResult
import com.gratelock.app.util.PhotoValidator
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

/**
 * Result of validating a photo journal entry.
 */
sealed interface PhotoEntryValidationResult {

    /**
     * Photo passed all validation checks.
     *
     * @param extractedText Text found in the photo via ML Kit.
     * @param photoHash Perceptual hash for future duplicate detection.
     */
    data class Valid(
        val extractedText: String,
        val photoHash: String,
    ) : PhotoEntryValidationResult

    /**
     * Photo failed validation.
     *
     * @param reason User-friendly error message.
     */
    data class Invalid(val reason: String) : PhotoEntryValidationResult
}

/**
 * Validates a photo journal entry for handwritten text content and duplicate detection.
 *
 * Uses [PhotoValidator] to read text directly from the saved JPEG file via
 * [InputImage.fromFilePath], which is more reliable than manual bitmap conversion.
 * Perceptual hash duplicate detection uses a downscaled bitmap for comparison.
 *
 * @param entryRepository Repository for querying recent photo hashes.
 * @param context Application context for ML Kit file access.
 */
class ValidatePhotoEntryUseCase @Inject constructor(
    private val entryRepository: EntryRepository,
    @ApplicationContext private val context: Context,
) {

    /**
     * Validates the captured photo file.
     *
     * @param photoFile The saved photo file to validate.
     * @return [PhotoEntryValidationResult.Valid] with extracted text and hash,
     *         or [PhotoEntryValidationResult.Invalid] with a warm error message.
     */
    suspend operator fun invoke(photoFile: File): PhotoEntryValidationResult {
        // Step 1: ML Kit text recognition directly from the file
        val textResult = PhotoValidator.validate(context, photoFile)
        if (textResult is PhotoValidationResult.Invalid) {
            return PhotoEntryValidationResult.Invalid(textResult.reason)
        }

        val validResult = textResult as PhotoValidationResult.Valid

        // Step 2: Compute perceptual hash (decode bitmap just for hashing)
        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
        val photoHash = if (bitmap != null) {
            PerceptualHash.compute(bitmap)
        } else {
            "" // Skip hash if decode fails — don't block the entry
        }

        // Step 3: Duplicate detection against recent entries
        if (photoHash.isNotEmpty()) {
            val recentHashes = entryRepository.getRecentPhotoHashes(Constants.PHOTO_HASH_COMPARE_LIMIT)
            val isDuplicate = recentHashes.any { existingHash ->
                PerceptualHash.similarity(photoHash, existingHash) >= Constants.PHOTO_HASH_SIMILARITY_THRESHOLD
            }

            if (isDuplicate) {
                return PhotoEntryValidationResult.Invalid(
                    "This looks very similar to a photo you've already submitted. " +
                        "Try writing a new journal entry for today!",
                )
            }
        }

        return PhotoEntryValidationResult.Valid(
            extractedText = validResult.extractedText,
            photoHash = photoHash,
        )
    }
}
