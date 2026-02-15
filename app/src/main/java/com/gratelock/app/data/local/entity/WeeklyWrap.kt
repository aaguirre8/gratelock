package com.gratelock.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Weekly wrap summary generated every Sunday at 8:00 PM.
 *
 * Contains aggregated stats for one week (Monday–Sunday) and a
 * reference to the shareable wrap image.
 *
 * @param id Auto-generated primary key.
 * @param weekStartDate Monday of the wrap week ("YYYY-MM-DD").
 * @param weekEndDate Sunday of the wrap week ("YYYY-MM-DD").
 * @param daysJournaled Number of days the user submitted an entry (0–7).
 * @param typedEntries Number of days the user used the quick-type path.
 * @param photoEntries Number of days the user used the journal photo path.
 * @param streakAtEnd The user's streak count at the end of the week.
 * @param totalEntriesAtEnd Cumulative entry count at end of week.
 * @param blockedAttempts Times the blocking overlay was shown during the week.
 * @param topKeywords Comma-separated top keywords from typed entries.
 * @param wrapImagePath File path to the generated shareable PNG.
 * @param generatedAt Timestamp when the wrap was generated, in epoch milliseconds.
 */
@Entity(tableName = "weekly_wraps")
data class WeeklyWrap(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weekStartDate: String,
    val weekEndDate: String,
    val daysJournaled: Int,
    val typedEntries: Int,
    val photoEntries: Int,
    val streakAtEnd: Int,
    val totalEntriesAtEnd: Int,
    val blockedAttempts: Int,
    val topKeywords: String? = null,
    val wrapImagePath: String? = null,
    val generatedAt: Long,
)
