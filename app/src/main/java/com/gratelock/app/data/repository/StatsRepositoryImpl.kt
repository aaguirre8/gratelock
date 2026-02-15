package com.gratelock.app.data.repository

import com.gratelock.app.data.local.dao.StatsDao
import com.gratelock.app.data.local.entity.UserStats
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [StatsRepository] backed by Room via [StatsDao].
 *
 * @param statsDao The DAO for user stats database operations.
 */
class StatsRepositoryImpl @Inject constructor(
    private val statsDao: StatsDao,
) : StatsRepository {

    override fun getStats(): Flow<UserStats?> =
        statsDao.getStats()

    override suspend fun getStatsOnce(): UserStats? =
        statsDao.getStatsOnce()

    override suspend fun initializeStats() {
        if (statsDao.getStatsOnce() == null) {
            statsDao.insertOrUpdate(UserStats())
        }
    }

    override suspend fun updateStreak(
        currentStreak: Int,
        longestStreak: Int,
        lastEntryDate: String,
    ) = statsDao.updateStreak(currentStreak, longestStreak, lastEntryDate)

    override suspend fun setUnlockedToday(isUnlocked: Boolean) =
        statsDao.updateUnlockStatus(isUnlocked)

    override suspend fun updateTotalEntries(totalEntries: Int) =
        statsDao.updateTotalEntries(totalEntries)

    override suspend fun useEmergencyUnlock() =
        statsDao.decrementEmergencyUnlocks()

    override suspend fun resetEmergencyUnlocks(count: Int) =
        statsDao.resetEmergencyUnlocks(count)

    override suspend fun markOnboardingComplete() =
        statsDao.markOnboardingComplete()

    override suspend fun incrementBlockedAttempts() =
        statsDao.incrementWeeklyBlockedAttempts()

    override suspend fun resetWeeklyBlockedAttempts(count: Int) =
        statsDao.updateWeeklyBlockedAttempts(count)

    override suspend fun deleteAll() =
        statsDao.deleteAll()
}
