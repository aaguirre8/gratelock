package com.gratelock.app.data.repository

import com.gratelock.app.data.local.dao.EntryDao
import com.gratelock.app.data.local.entity.JournalEntry
import com.gratelock.app.util.DateUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [EntryRepository] backed by Room via [EntryDao].
 *
 * @param entryDao The DAO for journal entry database operations.
 */
class EntryRepositoryImpl @Inject constructor(
    private val entryDao: EntryDao,
) : EntryRepository {

    override suspend fun saveEntry(entry: JournalEntry): Long =
        entryDao.insert(entry)

    override fun getTodayEntry(): Flow<JournalEntry?> =
        entryDao.getByDate(DateUtils.todayDateString())

    override fun getAllEntries(): Flow<List<JournalEntry>> =
        entryDao.getAll()

    override suspend fun getYesterdayEntry(): JournalEntry? =
        entryDao.getByDateOnce(DateUtils.yesterdayDateString())

    override suspend fun getEntriesForDateRange(
        startDate: String,
        endDate: String,
    ): List<JournalEntry> = entryDao.getByDateRange(startDate, endDate)

    override suspend fun getRecentPhotoHashes(limit: Int): List<String> =
        entryDao.getRecentPhotoHashes(limit)

    override suspend fun getEntryById(id: Long): JournalEntry? =
        entryDao.getById(id)

    override suspend fun getEntryCount(): Int =
        entryDao.count()

    override suspend fun deleteAll() =
        entryDao.deleteAll()
}
