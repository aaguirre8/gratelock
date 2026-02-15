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
 * Checks that the photo contains enough handwritten text to be a genuine
 * journal entry: minimum text blocks, character count, and ML confidence.
 */
object PhotoValidator {

    private const val MIN_TEXT_BLOCKS = 3
    private const val MIN_TOTAL_CHARS = 30
    private const val MIN_CONFIDENCE = 0.5f

    /**
     * Validates a captured photo for handwritten journal content.
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

                    // Check minimum text blocks
                    if (textBlocks.size < MIN_TEXT_BLOCKS) {
                        cont.resume(
                            PhotoValidationResult.Invalid(
                                "We couldn't find enough text in your photo. " +
                                    "Try writing a bit more or adjusting the angle.",
                            ),
                        )
                        return@addOnSuccessListener
                    }

                    // Check minimum total characters
                    if (totalText.length < MIN_TOTAL_CHARS) {
                        cont.resume(
                            PhotoValidationResult.Invalid(
                                "Your journal entry looks a bit short. " +
                                    "Try including at least a few sentences of gratitude.",
                            ),
                        )
                        return@addOnSuccessListener
                    }

                    // Check average confidence across text blocks
                    val confidences = textBlocks.mapNotNull { block ->
                        block.lines.mapNotNull { line ->
                            line.elements.mapNotNull { it.confidence }.average()
                                .takeIf { !it.isNaN() }
                        }.average().takeIf { !it.isNaN() }
                    }

                    val avgConfidence = if (confidences.isNotEmpty()) {
                        confidences.average().toFloat()
                    } else {
                        0f
                    }

                    if (avgConfidence < MIN_CONFIDENCE) {
                        cont.resume(
                            PhotoValidationResult.Invalid(
                                "The text is a bit hard to read. " +
                                    "Try taking the photo in better lighting or holding the camera steady.",
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
