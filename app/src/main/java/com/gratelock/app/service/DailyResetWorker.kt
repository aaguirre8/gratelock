package com.gratelock.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gratelock.app.data.local.GrateLockPreferences
import com.gratelock.app.data.repository.StatsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker that runs at midnight to reset the daily unlock status.
 *
 * After this runs, blocked apps are locked again until the user
 * journals the next day. Streak break detection (whether yesterday
 * had no entry) will be handled in Increment 8.
 */
@HiltWorker
class DailyResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val statsRepository: StatsRepository,
    private val preferences: GrateLockPreferences,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Reset unlock status in Room (source of truth)
        statsRepository.setUnlockedToday(false)

        // Sync to SharedPreferences cache
        preferences.isUnlockedToday = false

        return Result.success()
    }

    companion object {
        const val WORK_NAME = "daily_reset"
    }
}
