package com.gratelock.app.domain.usecase

import com.gratelock.app.data.local.GrateLockPreferences
import com.gratelock.app.data.repository.StatsRepository
import com.gratelock.app.util.DateUtils
import javax.inject.Inject

/**
 * Calculates and updates the user's gratitude streak.
 *
 * Streak logic:
 * - If there was an entry yesterday, increment the current streak.
 * - If last entry was today (duplicate), do nothing (return current streak).
 * - If last entry was more than 1 day ago (or null), start a new streak at 1.
 * - If the new streak exceeds the longest streak, update longest.
 *
 * @param statsRepository Repository for reading/writing streak data.
 * @param preferences SharedPreferences cache for syncing streak to the blocking service.
 */
class CalculateStreakUseCase @Inject constructor(
    private val statsRepository: StatsRepository,
    private val preferences: GrateLockPreferences,
) {

    /**
     * Recalculates the streak based on the current entry and updates Room + prefs.
     *
     * @return The new current streak value.
     */
    suspend operator fun invoke(): Int {
        val stats = statsRepository.getStatsOnce() ?: return 1
        val today = DateUtils.todayDateString()
        val lastEntryDate = stats.lastEntryDate

        // Already journaled today — no streak change
        if (lastEntryDate == today) {
            return stats.currentStreak
        }

        val newStreak = when {
            // First ever entry or gap > 1 day — start fresh
            lastEntryDate == null -> 1
            // Entry was yesterday — continue streak
            DateUtils.isYesterday(lastEntryDate) -> stats.currentStreak + 1
            // Gap of more than 1 day — reset
            else -> 1
        }

        val newLongest = maxOf(newStreak, stats.longestStreak)

        // Update Room (source of truth)
        statsRepository.updateStreak(newStreak, newLongest, today)

        // Sync to SharedPreferences cache
        preferences.currentStreak = newStreak

        return newStreak
    }
}
