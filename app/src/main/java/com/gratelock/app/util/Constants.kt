package com.gratelock.app.util

/**
 * Application-wide constants.
 */
object Constants {

    // SharedPreferences
    const val PREFS_NAME = "gratelock_prefs"

    // Notification channels
    const val CHANNEL_REMINDERS = "reminders"
    const val CHANNEL_STREAKS = "streaks"
    const val CHANNEL_WRAPS = "wraps"
    const val CHANNEL_CELEBRATIONS = "celebrations"

    // Typed entry validation
    const val MIN_ENTRY_CHARS = 10
    const val MAX_ENTRY_CHARS = 200
    const val MIN_DISTINCT_WORDS = 2
    const val DUPLICATE_SIMILARITY_THRESHOLD = 0.8

    // Photo validation
    const val MIN_TEXT_BLOCKS = 3
    const val MIN_TOTAL_CHARS = 30
    const val MIN_ML_CONFIDENCE = 0.5f
    const val PHOTO_HASH_SIMILARITY_THRESHOLD = 0.95
    const val PHOTO_HASH_COMPARE_LIMIT = 30

    // Blocking
    const val OVERLAY_DEBOUNCE_MS = 500L
    const val EMERGENCY_UNLOCKS_PER_WEEK = 1

    // Jar
    const val JAR_MAX_ENTRIES = 100

    // Weekly wrap
    const val WRAP_IMAGE_WIDTH = 1080
    const val WRAP_IMAGE_HEIGHT = 1920
}
