package com.gratelock.app.data.repository

import com.gratelock.app.data.local.entity.BlockedApp
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for blocked app operations.
 */
interface BlockedAppRepository {

    /** @param app The app to add or update. */
    suspend fun upsertApp(app: BlockedApp)

    /** @param apps List of apps to add or update. */
    suspend fun upsertAll(apps: List<BlockedApp>)

    /** @param packageName The package to remove from the blocked list. */
    suspend fun removeApp(packageName: String)

    /** @return A [Flow] emitting all blocked app entries. */
    fun getAllApps(): Flow<List<BlockedApp>>

    /** @return A [Flow] emitting only currently blocked apps. */
    fun getBlockedApps(): Flow<List<BlockedApp>>

    /** @return List of package names for all currently blocked apps. */
    suspend fun getBlockedPackageNames(): List<String>

    /**
     * @param packageName The package to update.
     * @param isBlocked The new blocked status.
     */
    suspend fun toggleApp(packageName: String, isBlocked: Boolean)

    /** Deletes all blocked app entries. */
    suspend fun deleteAll()
}
