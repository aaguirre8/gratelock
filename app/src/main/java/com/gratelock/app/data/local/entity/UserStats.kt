package com.gratelock.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Singleton entity holding the user's current stats and app state.
 *
 * Only one row exists (id = 1). This is the source of truth for
 * streak data, unlock status, and onboarding state. SharedPreferences
 * mirrors a subset of these fields for fast access by the blocking service.
 *
 * @param id Always 1 (singleton pattern).
 * @param currentStreak Consecutive days of journaling.
 * @param longestStreak All-time longest streak.
 * @param totalEntries Total number of journal entries submitted.
 * @param lastEntryDate Date of the most recent entry ("YYYY-MM-DD"), or null if none.
 * @param isUnlockedToday Whether blocked apps are unlocked for today.
 * @param emergencyUnlocksRemaining Emergency unlocks left this week (resets Monday).
 * @param onboardingComplete Whether the user has completed onboarding.
 * @param weeklyBlockedAttempts Number of times the overlay was shown this week.
 */
@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalEntries: Int = 0,
    val lastEntryDate: String? = null,
    val isUnlockedToday: Boolean = false,
    val emergencyUnlocksRemaining: Int = 1,
    val onboardingComplete: Boolean = false,
    val weeklyBlockedAttempts: Int = 0,
)
