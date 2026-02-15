package com.gratelock.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module providing application-level dependencies.
 *
 * Providers for Room database, DAOs, SharedPreferences, and other
 * singletons will be added here in subsequent increments.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule
