package com.gratelock.app.data.repository

import com.gratelock.app.data.local.entity.UserStats
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user stats operations.
 */
interface StatsRepository {

    /** @return A [Flow] emitting the current user stats, or null if uninitialized. */
    fun getStats(): Flow<UserStats?>

    /** @return The current user stats, or null. Non-Flow variant. */
    suspend fun getStatsOnce(): UserStats?

    /** Initializes the singleton stats row with default values if it doesn't exist. */
    suspend fun initializeStats()

    /**
     * @param currentStreak The new current streak.
     * @param longestStreak The new longest streak.
     * @param lastEntryDate Date of the latest entry ("YYYY-MM-DD").
     */
    suspend fun updateStreak(currentStreak: Int, longestStreak: Int, lastEntryDate: String)

    /** @param isUnlocked Whether apps should be unlocked for today. */
    suspend fun setUnlockedToday(isUnlocked: Boolean)

    /** @param totalEntries The new total entries count. */
    suspend fun updateTotalEntries(totalEntries: Int)

    /** Uses one emergency unlock (decrements remaining count). */
    suspend fun useEmergencyUnlock()

    /** @param count Number to reset emergency unlocks to. */
    suspend fun resetEmergencyUnlocks(count: Int)

    /** Marks onboarding as complete. */
    suspend fun markOnboardingComplete()

    /** Increments the weekly blocked attempts counter by 1. */
    suspend fun incrementBlockedAttempts()

    /** @param count Value to reset weekly blocked attempts to. */
    suspend fun resetWeeklyBlockedAttempts(count: Int)

    /** Deletes all stats (for data reset). */
    suspend fun deleteAll()
}
