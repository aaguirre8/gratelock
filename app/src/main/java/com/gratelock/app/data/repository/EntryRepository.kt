package com.gratelock.app.data.repository

import com.gratelock.app.data.local.entity.JournalEntry
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for journal entry operations.
 */
interface EntryRepository {

    /** @param entry The entry to save. @return The inserted row ID. */
    suspend fun saveEntry(entry: JournalEntry): Long

    /** @return A [Flow] emitting today's entry, or null. */
    fun getTodayEntry(): Flow<JournalEntry?>

    /** @return A [Flow] emitting all entries, newest first. */
    fun getAllEntries(): Flow<List<JournalEntry>>

    /** @return Yesterday's entry, or null. */
    suspend fun getYesterdayEntry(): JournalEntry?

    /**
     * @param startDate Start of range (inclusive), "YYYY-MM-DD".
     * @param endDate End of range (inclusive), "YYYY-MM-DD".
     * @return Entries within the date range.
     */
    suspend fun getEntriesForDateRange(startDate: String, endDate: String): List<JournalEntry>

    /**
     * @param limit Maximum number of hashes to retrieve.
     * @return Recent photo hashes for duplicate detection.
     */
    suspend fun getRecentPhotoHashes(limit: Int): List<String>

    /** @return Total number of entries. */
    suspend fun getEntryCount(): Int

    /** Deletes all entries. */
    suspend fun deleteAll()
}
