package com.gratelock.app.util

import android.graphics.Bitmap
import android.graphics.Color

/**
 * Perceptual hashing for duplicate photo detection.
 *
 * Uses an average hash (aHash) algorithm: downscale to 32x32 grayscale,
 * compute average brightness, then generate a 1024-bit hash where each
 * bit indicates whether a pixel is above or below the average.
 *
 * Duplicate detection uses Hamming distance — the number of differing
 * bits between two hashes. A similarity ratio above the threshold
 * (e.g., 95%) indicates a near-duplicate image.
 */
object PerceptualHash {

    private const val HASH_SIZE = 32

    /**
     * Computes the perceptual hash of a bitmap.
     *
     * @param bitmap The image to hash.
     * @return A hex string representing the perceptual hash.
     */
    fun compute(bitmap: Bitmap): String {
        // Downscale to HASH_SIZE x HASH_SIZE
        val scaled = Bitmap.createScaledBitmap(bitmap, HASH_SIZE, HASH_SIZE, true)

        // Convert to grayscale values
        val pixels = IntArray(HASH_SIZE * HASH_SIZE)
        scaled.getPixels(pixels, 0, HASH_SIZE, 0, 0, HASH_SIZE, HASH_SIZE)

        val grayscale = pixels.map { pixel ->
            (0.299 * Color.red(pixel) + 0.587 * Color.green(pixel) + 0.114 * Color.blue(pixel)).toInt()
        }

        if (scaled != bitmap) {
            scaled.recycle()
        }

        // Compute average brightness
        val average = grayscale.average()

        // Build hash: 1 if pixel >= average, 0 otherwise
        val bits = grayscale.map { if (it >= average) 1 else 0 }

        // Convert bits to hex string (process 4 bits at a time)
        val sb = StringBuilder()
        for (i in bits.indices step 4) {
            val nibble = (bits.getOrElse(i) { 0 } shl 3) or
                (bits.getOrElse(i + 1) { 0 } shl 2) or
                (bits.getOrElse(i + 2) { 0 } shl 1) or
                bits.getOrElse(i + 3) { 0 }
            sb.append(Integer.toHexString(nibble))
        }
        return sb.toString()
    }

    /**
     * Computes the Hamming distance between two hex hash strings.
     *
     * @param hash1 First hash (hex string).
     * @param hash2 Second hash (hex string).
     * @return Number of differing bits.
     */
    fun hammingDistance(hash1: String, hash2: String): Int {
        val maxLen = maxOf(hash1.length, hash2.length)
        var distance = 0
        for (i in 0 until maxLen) {
            val a = if (i < hash1.length) hash1[i].digitToIntOrNull(16) ?: 0 else 0
            val b = if (i < hash2.length) hash2[i].digitToIntOrNull(16) ?: 0 else 0
            distance += Integer.bitCount(a xor b)
        }
        return distance
    }

    /**
     * Computes the similarity ratio between two hashes.
     *
     * @param hash1 First hash (hex string).
     * @param hash2 Second hash (hex string).
     * @return Similarity as a double from 0.0 (completely different) to 1.0 (identical).
     */
    fun similarity(hash1: String, hash2: String): Double {
        val totalBits = maxOf(hash1.length, hash2.length) * 4
        if (totalBits == 0) return 1.0
        val distance = hammingDistance(hash1, hash2)
        return 1.0 - (distance.toDouble() / totalBits)
    }
}
