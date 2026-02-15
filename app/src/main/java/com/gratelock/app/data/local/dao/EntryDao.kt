package com.gratelock.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gratelock.app.data.local.entity.JournalEntry
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for [JournalEntry] entities.
 */
@Dao
interface EntryDao {

    /** @param entry The journal entry to insert. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntry): Long

    /**
     * @param date The calendar date in "YYYY-MM-DD" format.
     * @return A [Flow] emitting the entry for the given date, or null.
     */
    @Query("SELECT * FROM journal_entries WHERE date = :date LIMIT 1")
    fun getByDate(date: String): Flow<JournalEntry?>

    /**
     * @param date The calendar date in "YYYY-MM-DD" format.
     * @return The entry for the given date, or null. Suspending (non-Flow) variant.
     */
    @Query("SELECT * FROM journal_entries WHERE date = :date LIMIT 1")
    suspend fun getByDateOnce(date: String): JournalEntry?

    /**
     * @return A [Flow] emitting all entries ordered by most recent first.
     */
    @Query("SELECT * FROM journal_entries ORDER BY capturedAt DESC")
    fun getAll(): Flow<List<JournalEntry>>

    /**
     * @param startDate Start of range (inclusive), "YYYY-MM-DD".
     * @param endDate End of range (inclusive), "YYYY-MM-DD".
     * @return Entries within the date range.
     */
    @Query("SELECT * FROM journal_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getByDateRange(startDate: String, endDate: String): List<JournalEntry>

    /**
     * @param limit Maximum number of hashes to return.
     * @return Recent photo hashes for duplicate detection.
     */
    @Query("SELECT photoHash FROM journal_entries WHERE photoHash IS NOT NULL ORDER BY capturedAt DESC LIMIT :limit")
    suspend fun getRecentPhotoHashes(limit: Int): List<String>

    /** @return Total count of journal entries. */
    @Query("SELECT COUNT(*) FROM journal_entries")
    suspend fun count(): Int

    /** Deletes all journal entries. */
    @Query("DELETE FROM journal_entries")
    suspend fun deleteAll()
}
