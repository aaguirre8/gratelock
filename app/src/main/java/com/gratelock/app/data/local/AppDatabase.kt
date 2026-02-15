package com.gratelock.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gratelock.app.data.local.dao.BlockedAppDao
import com.gratelock.app.data.local.dao.EntryDao
import com.gratelock.app.data.local.dao.StatsDao
import com.gratelock.app.data.local.dao.WeeklyWrapDao
import com.gratelock.app.data.local.entity.BlockedApp
import com.gratelock.app.data.local.entity.JournalEntry
import com.gratelock.app.data.local.entity.UserStats
import com.gratelock.app.data.local.entity.WeeklyWrap

/**
 * Room database for GrateLock.
 *
 * Contains all four entities and provides access to their DAOs.
 * Version 1 is the initial schema for the MVP.
 */
@Database(
    entities = [
        JournalEntry::class,
        BlockedApp::class,
        UserStats::class,
        WeeklyWrap::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    /** @return DAO for journal entry operations. */
    abstract fun entryDao(): EntryDao

    /** @return DAO for blocked app operations. */
    abstract fun blockedAppDao(): BlockedAppDao

    /** @return DAO for user stats operations. */
    abstract fun statsDao(): StatsDao

    /** @return DAO for weekly wrap operations. */
    abstract fun weeklyWrapDao(): WeeklyWrapDao
}
