package com.gratelock.app.di

import com.gratelock.app.data.repository.BlockedAppRepository
import com.gratelock.app.data.repository.BlockedAppRepositoryImpl
import com.gratelock.app.data.repository.EntryRepository
import com.gratelock.app.data.repository.EntryRepositoryImpl
import com.gratelock.app.data.repository.StatsRepository
import com.gratelock.app.data.repository.StatsRepositoryImpl
import com.gratelock.app.data.repository.WeeklyWrapRepository
import com.gratelock.app.data.repository.WeeklyWrapRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module binding repository interfaces to their implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /** @param impl The implementation. @return Bound [EntryRepository]. */
    @Binds
    @Singleton
    abstract fun bindEntryRepository(impl: EntryRepositoryImpl): EntryRepository

    /** @param impl The implementation. @return Bound [BlockedAppRepository]. */
    @Binds
    @Singleton
    abstract fun bindBlockedAppRepository(impl: BlockedAppRepositoryImpl): BlockedAppRepository

    /** @param impl The implementation. @return Bound [StatsRepository]. */
    @Binds
    @Singleton
    abstract fun bindStatsRepository(impl: StatsRepositoryImpl): StatsRepository

    /** @param impl The implementation. @return Bound [WeeklyWrapRepository]. */
    @Binds
    @Singleton
    abstract fun bindWeeklyWrapRepository(impl: WeeklyWrapRepositoryImpl): WeeklyWrapRepository
}
