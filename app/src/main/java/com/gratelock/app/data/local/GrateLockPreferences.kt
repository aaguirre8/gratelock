package com.gratelock.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.gratelock.app.data.local.entity.UserStats
import com.gratelock.app.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SharedPreferences wrapper for fast-access data used by the blocking service.
 *
 * This is a write-through cache — never the source of truth. All writes
 * should go to Room first, then sync here. The blocking service reads
 * from these preferences for speed since it fires on every window change.
 *
 * @param context Application context for accessing SharedPreferences.
 */
@Singleton
class GrateLockPreferences @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    /** Whether blocked apps are unlocked for today. */
    var isUnlockedToday: Boolean
        get() = prefs.getBoolean(KEY_UNLOCKED_TODAY, false)
        set(value) = prefs.edit().putBoolean(KEY_UNLOCKED_TODAY, value).apply()

    /** Date of last unlock in "YYYY-MM-DD" format. */
    var lastUnlockDate: String?
        get() = prefs.getString(KEY_LAST_UNLOCK_DATE, null)
        set(value) = prefs.edit().putString(KEY_LAST_UNLOCK_DATE, value).apply()

    /** Global blocking on/off switch. */
    var blockingEnabled: Boolean
        get() = prefs.getBoolean(KEY_BLOCKING_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_BLOCKING_ENABLED, value).apply()

    /** Cached set of blocked package names for fast lookup. */
    var blockedPackages: Set<String>
        get() = prefs.getStringSet(KEY_BLOCKED_PACKAGES, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_BLOCKED_PACKAGES, value).apply()

    /** Cached current streak for display on the overlay. */
    var currentStreak: Int
        get() = prefs.getInt(KEY_CURRENT_STREAK, 0)
        set(value) = prefs.edit().putInt(KEY_CURRENT_STREAK, value).apply()

    /** Emergency unlocks remaining this week. */
    var emergencyUnlocksRemaining: Int
        get() = prefs.getInt(KEY_EMERGENCY_UNLOCKS, Constants.EMERGENCY_UNLOCKS_PER_WEEK)
        set(value) = prefs.edit().putInt(KEY_EMERGENCY_UNLOCKS, value).apply()

    /** Whether daily journal reminder is enabled. */
    var reminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_REMINDER_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_REMINDER_ENABLED, value).apply()

    /** Reminder time in "HH:mm" format (default "08:00"). */
    var reminderTime: String
        get() = prefs.getString(KEY_REMINDER_TIME, "08:00") ?: "08:00"
        set(value) = prefs.edit().putString(KEY_REMINDER_TIME, value).apply()

    /** Whether streak-at-risk notifications are enabled. */
    var streakAtRiskEnabled: Boolean
        get() = prefs.getBoolean(KEY_STREAK_AT_RISK_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_STREAK_AT_RISK_ENABLED, value).apply()

    /**
     * Syncs preferences from the Room [UserStats] source of truth.
     *
     * @param stats The current stats from Room.
     */
    fun syncFromStats(stats: UserStats) {
        prefs.edit()
            .putBoolean(KEY_UNLOCKED_TODAY, stats.isUnlockedToday)
            .putInt(KEY_CURRENT_STREAK, stats.currentStreak)
            .putInt(KEY_EMERGENCY_UNLOCKS, stats.emergencyUnlocksRemaining)
            .apply()
    }

    /**
     * Syncs the blocked packages set from Room data.
     *
     * @param packageNames The current list of blocked package names.
     */
    fun syncBlockedApps(packageNames: List<String>) {
        blockedPackages = packageNames.toSet()
    }

    /** Resets all preferences to defaults. */
    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_UNLOCKED_TODAY = "is_unlocked_today"
        const val KEY_LAST_UNLOCK_DATE = "last_unlock_date"
        const val KEY_BLOCKING_ENABLED = "blocking_enabled"
        const val KEY_BLOCKED_PACKAGES = "blocked_packages"
        const val KEY_CURRENT_STREAK = "current_streak"
        const val KEY_EMERGENCY_UNLOCKS = "emergency_unlocks_remaining"
        const val KEY_REMINDER_ENABLED = "reminder_enabled"
        const val KEY_REMINDER_TIME = "reminder_time"
        const val KEY_STREAK_AT_RISK_ENABLED = "streak_at_risk_enabled"
    }
}
