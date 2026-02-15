package com.gratelock.app.data.repository

import com.gratelock.app.data.local.dao.BlockedAppDao
import com.gratelock.app.data.local.entity.BlockedApp
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [BlockedAppRepository] backed by Room via [BlockedAppDao].
 *
 * @param blockedAppDao The DAO for blocked app database operations.
 */
class BlockedAppRepositoryImpl @Inject constructor(
    private val blockedAppDao: BlockedAppDao,
) : BlockedAppRepository {

    override suspend fun upsertApp(app: BlockedApp) =
        blockedAppDao.upsert(app)

    override suspend fun upsertAll(apps: List<BlockedApp>) =
        blockedAppDao.upsertAll(apps)

    override suspend fun removeApp(packageName: String) =
        blockedAppDao.delete(packageName)

    override fun getAllApps(): Flow<List<BlockedApp>> =
        blockedAppDao.getAll()

    override fun getBlockedApps(): Flow<List<BlockedApp>> =
        blockedAppDao.getBlocked()

    override suspend fun getBlockedPackageNames(): List<String> =
        blockedAppDao.getBlockedPackageNames()

    override suspend fun toggleApp(packageName: String, isBlocked: Boolean) =
        blockedAppDao.updateBlockedStatus(packageName, isBlocked)

    override suspend fun deleteAll() =
        blockedAppDao.deleteAll()
}
