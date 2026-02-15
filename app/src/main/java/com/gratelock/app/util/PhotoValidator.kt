package com.gratelock.app.util

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Result of photo validation via ML Kit text recognition.
 */
sealed interface PhotoValidationResult {

    /**
     * Photo passed validation — contains handwritten text.
     *
     * @param extractedText The full text extracted from the photo.
     * @param textBlockCount Number of distinct text blocks found.
     */
    data class Valid(
        val extractedText: String,
        val textBlockCount: Int,
    ) : PhotoValidationResult

    /**
     * Photo failed validation.
     *
     * @param reason A warm, user-friendly message explaining why.
     */
    data class Invalid(val reason: String) : PhotoValidationResult
}

/**
 * Validates journal photos using ML Kit text recognition.
 *
 * Relies on ML Kit's built-in text detection capabilities rather than
 * re-implementing confidence scoring. If ML Kit finds text blocks with
 * enough total characters, the photo is considered a valid journal entry.
 */
object PhotoValidator {

    private const val MIN_TEXT_BLOCKS = 1
    private const val MIN_TOTAL_CHARS = 15

    /**
     * Validates a captured photo for journal content.
     *
     * @param bitmap The captured photo to validate.
     * @return [PhotoValidationResult.Valid] if the photo contains enough text,
     *         or [PhotoValidationResult.Invalid] with a warm error message.
     */
    suspend fun validate(bitmap: Bitmap): PhotoValidationResult {
        return suspendCoroutine { cont ->
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { result ->
                    val textBlocks = result.textBlocks
                    val totalText = result.text

                    // Check that ML Kit found any text at all
                    if (textBlocks.size < MIN_TEXT_BLOCKS) {
                        cont.resume(
                            PhotoValidationResult.Invalid(
                                "We couldn't find any text in your photo. " +
                                    "Make sure your journal page is clearly visible.",
                            ),
                        )
                        return@addOnSuccessListener
                    }

                    // Check minimum total characters
                    if (totalText.length < MIN_TOTAL_CHARS) {
                        cont.resume(
                            PhotoValidationResult.Invalid(
                                "Your journal entry looks a bit short. " +
                                    "Try including at least a few words of gratitude.",
                            ),
                        )
                        return@addOnSuccessListener
                    }

                    cont.resume(
                        PhotoValidationResult.Valid(
                            extractedText = totalText,
                            textBlockCount = textBlocks.size,
                        ),
                    )
                }
                .addOnFailureListener {
                    cont.resume(
                        PhotoValidationResult.Invalid(
                            "Something went wrong reading your photo. Please try again.",
                        ),
                    )
                }
        }
    }
}
