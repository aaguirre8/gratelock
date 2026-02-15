package com.gratelock.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.gratelock.app.service.WorkerScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class for GrateLock.
 *
 * Annotated with [HiltAndroidApp] to trigger Hilt's code generation
 * and serve as the application-level dependency container.
 * Implements [Configuration.Provider] to configure WorkManager with Hilt.
 */
@HiltAndroidApp
class GrateLockApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        WorkerScheduler.scheduleAll(this)
    }
}
