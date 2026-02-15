package com.gratelock.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gratelock.app.data.local.entity.UserStats
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for the singleton [UserStats] entity.
 */
@Dao
interface StatsDao {

    /**
     * @return A [Flow] emitting the current user stats (singleton row, id = 1).
     */
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getStats(): Flow<UserStats?>

    /**
     * @return The current user stats, or null if not initialized. Non-Flow variant.
     */
    @Query("SELECT * FROM user_stats WHERE id = 1")
    suspend fun getStatsOnce(): UserStats?

    /** @param stats The stats to insert or replace (always id = 1). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: UserStats)

    /**
     * @param currentStreak The new current streak value.
     * @param longestStreak The new longest streak value.
     * @param lastEntryDate Date of the latest entry ("YYYY-MM-DD").
     */
    @Query("UPDATE user_stats SET currentStreak = :currentStreak, longestStreak = :longestStreak, lastEntryDate = :lastEntryDate WHERE id = 1")
    suspend fun updateStreak(currentStreak: Int, longestStreak: Int, lastEntryDate: String)

    /** @param isUnlocked Whether apps are unlocked for today. */
    @Query("UPDATE user_stats SET isUnlockedToday = :isUnlocked WHERE id = 1")
    suspend fun updateUnlockStatus(isUnlocked: Boolean)

    /** @param totalEntries The new total entries count. */
    @Query("UPDATE user_stats SET totalEntries = :totalEntries WHERE id = 1")
    suspend fun updateTotalEntries(totalEntries: Int)

    /** Decrements emergency unlocks remaining by 1 (minimum 0). */
    @Query("UPDATE user_stats SET emergencyUnlocksRemaining = MAX(0, emergencyUnlocksRemaining - 1) WHERE id = 1")
    suspend fun decrementEmergencyUnlocks()

    /** @param count The number of emergency unlocks to reset to. */
    @Query("UPDATE user_stats SET emergencyUnlocksRemaining = :count WHERE id = 1")
    suspend fun resetEmergencyUnlocks(count: Int)

    /** Marks onboarding as complete. */
    @Query("UPDATE user_stats SET onboardingComplete = 1 WHERE id = 1")
    suspend fun markOnboardingComplete()

    /** @param count The new weekly blocked attempts count. */
    @Query("UPDATE user_stats SET weeklyBlockedAttempts = :count WHERE id = 1")
    suspend fun updateWeeklyBlockedAttempts(count: Int)

    /** Increments weekly blocked attempts by 1. */
    @Query("UPDATE user_stats SET weeklyBlockedAttempts = weeklyBlockedAttempts + 1 WHERE id = 1")
    suspend fun incrementWeeklyBlockedAttempts()

    /** Deletes all stats (for data reset). */
    @Query("DELETE FROM user_stats")
    suspend fun deleteAll()
}
