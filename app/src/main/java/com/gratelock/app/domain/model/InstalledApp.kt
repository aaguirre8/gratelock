package com.gratelock.app.domain.model

import android.graphics.drawable.Drawable

/**
 * Represents an installed app on the device, enriched with blocking state.
 *
 * @param packageName Android package name (e.g., "com.instagram.android").
 * @param appName Human-readable display name.
 * @param icon The app's launcher icon drawable.
 * @param isBlocked Whether the user has chosen to block this app.
 * @param category The app's category for grouping in the selection list.
 */
data class InstalledApp(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val isBlocked: Boolean = false,
    val category: AppCategory = AppCategory.OTHER,
)

/**
 * Categories for grouping apps in the selection screen.
 *
 * Apps are pre-filtered to show likely distracting apps first
 * (social media, video, games), followed by other categories.
 *
 * @param displayName The human-readable category label.
 * @param sortOrder Lower values appear first in the list.
 */
enum class AppCategory(val displayName: String, val sortOrder: Int) {
    SOCIAL_MEDIA("Social Media", 0),
    VIDEO("Video & Streaming", 1),
    GAMES("Games", 2),
    MESSAGING("Messaging", 3),
    NEWS("News", 4),
    OTHER("Other Apps", 5),
}
