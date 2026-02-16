package com.gratelock.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gratelock.app.data.local.GrateLockPreferences
import com.gratelock.app.data.repository.EntryRepository
import com.gratelock.app.data.repository.StatsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker that runs at midnight to reset the daily unlock status
 * and check for streak breaks.
 *
 * If the user did not journal yesterday, the streak is retroactively
 * reset to 0. The unlock status is always reset so blocked apps
 * are locked again until the user journals the next day.
 */
@HiltWorker
class DailyResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val statsRepository: StatsRepository,
    private val entryRepository: EntryRepository,
    private val preferences: GrateLockPreferences,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Reset unlock status in Room (source of truth)
        statsRepository.setUnlockedToday(false)
        preferences.isUnlockedToday = false

        // Check if yesterday had no entry — if so, break the streak
        val yesterdayEntry = entryRepository.getYesterdayEntry()

        if (yesterdayEntry == null) {
            val stats = statsRepository.getStatsOnce()
            if (stats != null && stats.currentStreak > 0) {
                statsRepository.updateStreak(0, stats.longestStreak, stats.lastEntryDate ?: "")
                preferences.currentStreak = 0
            }
        }

        return Result.success()
    }

    companion object {
        const val WORK_NAME = "daily_reset"
    }
}
