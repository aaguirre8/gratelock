package com.gratelock.app.domain.usecase

import com.gratelock.app.data.local.GrateLockPreferences
import com.gratelock.app.data.repository.StatsRepository
import javax.inject.Inject

/**
 * Result of an emergency unlock attempt.
 */
sealed interface EmergencyUnlockResult {
    /** Unlock succeeded. Apps are now unlocked for today. */
    data object Success : EmergencyUnlockResult

    /**
     * Unlock failed because no emergency unlocks remain this week.
     *
     * @param remaining Always 0 when this is returned.
     */
    data class NoUnlocksRemaining(val remaining: Int = 0) : EmergencyUnlockResult
}

/**
 * Uses one emergency unlock to bypass journaling for today.
 *
 * Emergency unlocks do not affect the streak — the streak stays visible
 * during the day and only resets at midnight if no entry was made.
 * The user gets a limited number of emergency unlocks per week (resets Monday).
 *
 * @param statsRepository Repository for reading/writing user stats.
 * @param preferences SharedPreferences cache for the blocking service.
 */
class EmergencyUnlockUseCase @Inject constructor(
    private val statsRepository: StatsRepository,
    private val preferences: GrateLockPreferences,
) {

    /**
     * Attempts an emergency unlock.
     *
     * @return [EmergencyUnlockResult.Success] if unlocked, or
     *         [EmergencyUnlockResult.NoUnlocksRemaining] if none left.
     */
    suspend operator fun invoke(): EmergencyUnlockResult {
        val stats = statsRepository.getStatsOnce()
            ?: return EmergencyUnlockResult.NoUnlocksRemaining()

        if (stats.emergencyUnlocksRemaining <= 0) {
            return EmergencyUnlockResult.NoUnlocksRemaining()
        }

        // Update Room (source of truth)
        statsRepository.useEmergencyUnlock()
        statsRepository.setUnlockedToday(true)

        // Sync to SharedPreferences cache
        preferences.isUnlockedToday = true
        preferences.emergencyUnlocksRemaining = stats.emergencyUnlocksRemaining - 1

        return EmergencyUnlockResult.Success
    }
}
