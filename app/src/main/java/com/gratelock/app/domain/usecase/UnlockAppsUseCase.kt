package com.gratelock.app.domain.usecase

import com.gratelock.app.data.local.GrateLockPreferences
import com.gratelock.app.data.repository.StatsRepository
import javax.inject.Inject

/**
 * Unlocks blocked apps for the day after a successful journal entry.
 *
 * Updates both the Room database (source of truth) and SharedPreferences
 * cache (for the blocking service's fast lookup).
 *
 * @param statsRepository Repository for writing unlock status.
 * @param preferences SharedPreferences cache for the blocking service.
 */
class UnlockAppsUseCase @Inject constructor(
    private val statsRepository: StatsRepository,
    private val preferences: GrateLockPreferences,
) {

    /**
     * Sets isUnlockedToday to true in both Room and SharedPreferences.
     */
    suspend operator fun invoke() {
        statsRepository.setUnlockedToday(true)
        preferences.isUnlockedToday = true
    }
}
