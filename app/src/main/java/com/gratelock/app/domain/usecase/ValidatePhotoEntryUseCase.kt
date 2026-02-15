package com.gratelock.app.domain.usecase

import android.graphics.Bitmap
import com.gratelock.app.data.repository.EntryRepository
import com.gratelock.app.util.Constants
import com.gratelock.app.util.PerceptualHash
import com.gratelock.app.util.PhotoValidationResult
import com.gratelock.app.util.PhotoValidator
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
 * Combines ML Kit text recognition (via [PhotoValidator]) with perceptual hash
 * duplicate detection (via [PerceptualHash]).
 *
 * @param entryRepository Repository for querying recent photo hashes.
 */
class ValidatePhotoEntryUseCase @Inject constructor(
    private val entryRepository: EntryRepository,
) {

    /**
     * Validates the captured photo.
     *
     * @param bitmap The photo bitmap to validate.
     * @return [PhotoEntryValidationResult.Valid] with extracted text and hash,
     *         or [PhotoEntryValidationResult.Invalid] with a warm error message.
     */
    suspend operator fun invoke(bitmap: Bitmap): PhotoEntryValidationResult {
        // Step 1: ML Kit text recognition
        val textResult = PhotoValidator.validate(bitmap)
        if (textResult is PhotoValidationResult.Invalid) {
            return PhotoEntryValidationResult.Invalid(textResult.reason)
        }

        val validResult = textResult as PhotoValidationResult.Valid

        // Step 2: Compute perceptual hash
        val photoHash = PerceptualHash.compute(bitmap)

        // Step 3: Duplicate detection against recent entries
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

        return PhotoEntryValidationResult.Valid(
            extractedText = validResult.extractedText,
            photoHash = photoHash,
        )
    }
}
