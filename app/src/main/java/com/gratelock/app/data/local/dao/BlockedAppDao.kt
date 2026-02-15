package com.gratelock.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gratelock.app.data.local.entity.BlockedApp
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for [BlockedApp] entities.
 */
@Dao
interface BlockedAppDao {

    /** @param app The blocked app to insert or update. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(app: BlockedApp)

    /** @param apps List of blocked apps to insert or update. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(apps: List<BlockedApp>)

    /**
     * @return A [Flow] emitting all blocked app entries.
     */
    @Query("SELECT * FROM blocked_apps ORDER BY appName ASC")
    fun getAll(): Flow<List<BlockedApp>>

    /**
     * @return A [Flow] emitting only apps where isBlocked is true.
     */
    @Query("SELECT * FROM blocked_apps WHERE isBlocked = 1 ORDER BY appName ASC")
    fun getBlocked(): Flow<List<BlockedApp>>

    /**
     * @return List of package names for all currently blocked apps.
     */
    @Query("SELECT packageName FROM blocked_apps WHERE isBlocked = 1")
    suspend fun getBlockedPackageNames(): List<String>

    /**
     * @param packageName The package to update.
     * @param isBlocked The new blocked status.
     */
    @Query("UPDATE blocked_apps SET isBlocked = :isBlocked WHERE packageName = :packageName")
    suspend fun updateBlockedStatus(packageName: String, isBlocked: Boolean)

    /**
     * @param packageName The package to remove from the list.
     */
    @Query("DELETE FROM blocked_apps WHERE packageName = :packageName")
    suspend fun delete(packageName: String)

    /** Deletes all blocked app entries. */
    @Query("DELETE FROM blocked_apps")
    suspend fun deleteAll()
}
