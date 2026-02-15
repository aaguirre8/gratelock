package com.gratelock.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Broadcast receiver that re-schedules workers after a device reboot.
 *
 * WorkManager persists its work across reboots, but this ensures
 * workers are re-enqueued if anything goes wrong.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            WorkerScheduler.scheduleAll(context)
        }
    }
}
