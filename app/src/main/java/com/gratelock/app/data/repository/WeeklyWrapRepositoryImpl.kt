package com.gratelock.app.data.repository

import com.gratelock.app.data.local.dao.WeeklyWrapDao
import com.gratelock.app.data.local.entity.WeeklyWrap
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [WeeklyWrapRepository] backed by Room via [WeeklyWrapDao].
 *
 * @param weeklyWrapDao The DAO for weekly wrap database operations.
 */
class WeeklyWrapRepositoryImpl @Inject constructor(
    private val weeklyWrapDao: WeeklyWrapDao,
) : WeeklyWrapRepository {

    override suspend fun saveWrap(wrap: WeeklyWrap): Long =
        weeklyWrapDao.insert(wrap)

    override fun getAllWraps(): Flow<List<WeeklyWrap>> =
        weeklyWrapDao.getAll()

    override suspend fun getLatestWrap(): WeeklyWrap? =
        weeklyWrapDao.getLatest()

    override suspend fun getWrapByWeek(weekStartDate: String): WeeklyWrap? =
        weeklyWrapDao.getByWeek(weekStartDate)

    override suspend fun deleteAll() =
        weeklyWrapDao.deleteAll()
}
