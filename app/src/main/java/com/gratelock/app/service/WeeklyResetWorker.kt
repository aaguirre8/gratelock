package com.gratelock.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gratelock.app.data.local.GrateLockPreferences
import com.gratelock.app.data.repository.StatsRepository
import com.gratelock.app.util.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker that runs Monday at midnight to reset weekly counters.
 *
 * Resets emergency unlock count and weekly blocked attempts counter.
 */
@HiltWorker
class WeeklyResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val statsRepository: StatsRepository,
    private val preferences: GrateLockPreferences,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Reset emergency unlocks in Room (source of truth)
        statsRepository.resetEmergencyUnlocks(Constants.EMERGENCY_UNLOCKS_PER_WEEK)
        statsRepository.resetWeeklyBlockedAttempts(0)

        // Sync to SharedPreferences cache
        preferences.emergencyUnlocksRemaining = Constants.EMERGENCY_UNLOCKS_PER_WEEK

        return Result.success()
    }

    companion object {
        const val WORK_NAME = "weekly_reset"
    }
}
