package com.gratelock.app.service

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

/**
 * Schedules periodic WorkManager tasks for daily and weekly resets.
 */
object WorkerScheduler {

    /**
     * Schedules the daily midnight reset worker.
     *
     * Uses [ExistingPeriodicWorkPolicy.KEEP] so it doesn't restart
     * if already scheduled (e.g., after boot).
     *
     * @param context Application context.
     */
    fun scheduleDailyReset(context: Context) {
        val initialDelay = delayUntilMidnight()

        val request = PeriodicWorkRequestBuilder<DailyResetWorker>(
            1, TimeUnit.DAYS,
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DailyResetWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    /**
     * Schedules the weekly Monday midnight reset worker.
     *
     * @param context Application context.
     */
    fun scheduleWeeklyReset(context: Context) {
        val initialDelay = delayUntilNextMonday()

        val request = PeriodicWorkRequestBuilder<WeeklyResetWorker>(
            7, TimeUnit.DAYS,
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WeeklyResetWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    /**
     * Schedules both daily and weekly reset workers.
     *
     * @param context Application context.
     */
    fun scheduleAll(context: Context) {
        scheduleDailyReset(context)
        scheduleWeeklyReset(context)
    }

    /**
     * @return Milliseconds until the next midnight.
     */
    private fun delayUntilMidnight(): Long {
        val now = LocalDateTime.now()
        val nextMidnight = now.toLocalDate().plusDays(1).atTime(LocalTime.MIDNIGHT)
        return Duration.between(now, nextMidnight).toMillis()
    }

    /**
     * @return Milliseconds until next Monday at midnight.
     */
    private fun delayUntilNextMonday(): Long {
        val now = LocalDateTime.now()
        val daysUntilMonday = (8 - now.dayOfWeek.value) % 7
        val days = if (daysUntilMonday == 0) 7 else daysUntilMonday
        val nextMonday = now.toLocalDate().plusDays(days.toLong()).atTime(LocalTime.MIDNIGHT)
        return Duration.between(now, nextMonday).toMillis()
    }
}
