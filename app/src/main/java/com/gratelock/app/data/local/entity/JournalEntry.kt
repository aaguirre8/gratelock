package com.gratelock.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single gratitude journal entry — one per day.
 *
 * Entries can be either typed (3 gratitude items) or photo-based
 * (a photograph of a handwritten journal page).
 *
 * @param id Auto-generated primary key.
 * @param entryType Either "typed" or "photo".
 * @param gratitude1 First gratitude item (typed entries only).
 * @param gratitude2 Second gratitude item (typed entries only).
 * @param gratitude3 Third gratitude item (typed entries only).
 * @param photoPath Local file path to saved photo (photo entries only).
 * @param photoHash Perceptual hash for duplicate detection (photo entries only).
 * @param capturedAt Timestamp of entry submission in epoch milliseconds.
 * @param date Calendar date this entry is for, formatted as "YYYY-MM-DD".
 * @param ocrText Extracted text from photo (null until processed, post-MVP).
 * @param sentimentScore Sentiment value from -1.0 to 1.0 (post-MVP).
 * @param keywords Comma-separated keywords extracted from text.
 * @param isValid Whether the entry passed validation.
 */
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entryType: String,
    val gratitude1: String? = null,
    val gratitude2: String? = null,
    val gratitude3: String? = null,
    val photoPath: String? = null,
    val photoHash: String? = null,
    val capturedAt: Long,
    val date: String,
    val ocrText: String? = null,
    val sentimentScore: Float? = null,
    val keywords: String? = null,
    val isValid: Boolean = true,
)
