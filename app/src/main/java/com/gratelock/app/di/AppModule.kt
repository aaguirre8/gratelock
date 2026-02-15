package com.gratelock.app.di

import android.content.Context
import androidx.room.Room
import com.gratelock.app.data.local.AppDatabase
import com.gratelock.app.data.local.dao.BlockedAppDao
import com.gratelock.app.data.local.dao.EntryDao
import com.gratelock.app.data.local.dao.StatsDao
import com.gratelock.app.data.local.dao.WeeklyWrapDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing application-level dependencies.
 *
 * Provides the Room database singleton and all DAO instances.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * @param context Application context.
     * @return Singleton [AppDatabase] instance.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "gratelock_db",
        ).build()

    /** @param db The database instance. @return [EntryDao]. */
    @Provides
    fun provideEntryDao(db: AppDatabase): EntryDao = db.entryDao()

    /** @param db The database instance. @return [BlockedAppDao]. */
    @Provides
    fun provideBlockedAppDao(db: AppDatabase): BlockedAppDao = db.blockedAppDao()

    /** @param db The database instance. @return [StatsDao]. */
    @Provides
    fun provideStatsDao(db: AppDatabase): StatsDao = db.statsDao()

    /** @param db The database instance. @return [WeeklyWrapDao]. */
    @Provides
    fun provideWeeklyWrapDao(db: AppDatabase): WeeklyWrapDao = db.weeklyWrapDao()
}
