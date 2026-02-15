package com.gratelock.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an app that the user has selected for blocking.
 *
 * @param packageName Android package name (e.g., "com.instagram.android"). Primary key.
 * @param appName Human-readable display name of the app.
 * @param isBlocked Whether the app is currently being blocked.
 * @param addedAt Timestamp when the user added this app, in epoch milliseconds.
 */
@Entity(tableName = "blocked_apps")
data class BlockedApp(
    @PrimaryKey val packageName: String,
    val appName: String,
    val isBlocked: Boolean = true,
    val addedAt: Long,
)
