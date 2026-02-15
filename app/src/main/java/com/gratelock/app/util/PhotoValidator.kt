package com.gratelock.app.util

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
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
 * Uses [InputImage.fromFilePath] to let ML Kit read the JPEG file directly,
 * which is more reliable than manual bitmap conversion — especially for
 * handwritten text where image quality matters.
 */
object PhotoValidator {

    private const val MIN_TEXT_BLOCKS = 1
    private const val MIN_TOTAL_CHARS = 15

    /**
     * Validates a captured photo file for journal content.
     *
     * @param context Application context for URI resolution.
     * @param photoFile The saved photo file to validate.
     * @return [PhotoValidationResult.Valid] if the photo contains enough text,
     *         or [PhotoValidationResult.Invalid] with a warm error message.
     */
    suspend fun validate(context: Context, photoFile: File): PhotoValidationResult {
        return suspendCoroutine { cont ->
            val image = InputImage.fromFilePath(context, Uri.fromFile(photoFile))
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { result ->
                    val textBlocks = result.textBlocks
                    val totalText = result.text

                    if (textBlocks.size < MIN_TEXT_BLOCKS) {
                        cont.resume(
                            PhotoValidationResult.Invalid(
                                "We couldn't find any text in your photo. " +
                                    "Make sure your journal page is clearly visible.",
                            ),
                        )
                        return@addOnSuccessListener
                    }

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
