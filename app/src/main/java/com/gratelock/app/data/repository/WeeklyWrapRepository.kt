package com.gratelock.app.data.repository

import com.gratelock.app.data.local.entity.WeeklyWrap
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for weekly wrap operations.
 */
interface WeeklyWrapRepository {

    /** @param wrap The wrap to save. @return The inserted row ID. */
    suspend fun saveWrap(wrap: WeeklyWrap): Long

    /** @return A [Flow] emitting all wraps, most recent first. */
    fun getAllWraps(): Flow<List<WeeklyWrap>>

    /** @return The most recently generated wrap, or null. */
    suspend fun getLatestWrap(): WeeklyWrap?

    /**
     * @param weekStartDate The Monday date of the week ("YYYY-MM-DD").
     * @return The wrap for the given week, or null.
     */
    suspend fun getWrapByWeek(weekStartDate: String): WeeklyWrap?

    /** Deletes all wraps. */
    suspend fun deleteAll()
}
