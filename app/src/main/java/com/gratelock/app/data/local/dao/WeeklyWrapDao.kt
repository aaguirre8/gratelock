package com.gratelock.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gratelock.app.data.local.entity.WeeklyWrap
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for [WeeklyWrap] entities.
 */
@Dao
interface WeeklyWrapDao {

    /** @param wrap The weekly wrap to insert. */
    @Insert
    suspend fun insert(wrap: WeeklyWrap): Long

    /**
     * @return A [Flow] emitting all wraps ordered by most recent first.
     */
    @Query("SELECT * FROM weekly_wraps ORDER BY generatedAt DESC")
    fun getAll(): Flow<List<WeeklyWrap>>

    /**
     * @param weekStartDate The Monday date of the week ("YYYY-MM-DD").
     * @return The wrap for the given week, or null.
     */
    @Query("SELECT * FROM weekly_wraps WHERE weekStartDate = :weekStartDate LIMIT 1")
    suspend fun getByWeek(weekStartDate: String): WeeklyWrap?

    /**
     * @return The most recently generated wrap, or null.
     */
    @Query("SELECT * FROM weekly_wraps ORDER BY generatedAt DESC LIMIT 1")
    suspend fun getLatest(): WeeklyWrap?

    /** Deletes all wraps. */
    @Query("DELETE FROM weekly_wraps")
    suspend fun deleteAll()
}
