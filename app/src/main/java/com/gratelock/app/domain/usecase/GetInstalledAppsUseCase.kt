package com.gratelock.app.domain.usecase

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.gratelock.app.domain.model.AppCategory
import com.gratelock.app.domain.model.InstalledApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Queries the device for installed launchable apps, categorizes them,
 * and merges with the user's current blocked-app selections.
 *
 * @param context Application context for accessing PackageManager.
 */
class GetInstalledAppsUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    /**
     * Returns all installed launchable apps, excluding this app itself.
     *
     * Apps are categorized based on known package prefixes and sorted
     * by category priority (social media first, then video, games, etc.).
     *
     * @param blockedPackages Set of package names the user has already blocked.
     * @return List of [InstalledApp] sorted by category then name.
     */
    operator fun invoke(blockedPackages: Set<String> = emptySet()): List<InstalledApp> {
        val pm = context.packageManager
        val launchIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = pm.queryIntentActivities(launchIntent, PackageManager.MATCH_ALL)
        val ownPackage = context.packageName

        return resolveInfos
            .asSequence()
            .map { it.activityInfo.packageName }
            .distinct()
            .filter { it != ownPackage }
            .map { packageName ->
                val appInfo = pm.getApplicationInfo(packageName, 0)
                InstalledApp(
                    packageName = packageName,
                    appName = pm.getApplicationLabel(appInfo).toString(),
                    icon = pm.getApplicationIcon(appInfo),
                    isBlocked = packageName in blockedPackages,
                    category = categorize(packageName),
                )
            }
            .sortedWith(compareBy<InstalledApp> { it.category.sortOrder }.thenBy { it.appName.lowercase() })
            .toList()
    }

    companion object {

        private val SOCIAL_MEDIA_PACKAGES = setOf(
            "com.instagram.android",
            "com.twitter.android",
            "com.zhiliaoapp.musically",   // TikTok
            "com.reddit.frontpage",
            "com.snapchat.android",
            "com.facebook.katana",
            "com.facebook.lite",
            "com.tumblr",
            "com.pinterest",
            "com.linkedin.android",
            "com.bereal.ft",
            "app.bluesky.android",
            "org.telegram.messenger",
            "com.discord",
            "com.threads.android",         // Threads (Meta)
        )

        private val VIDEO_PACKAGES = setOf(
            "com.google.android.youtube",
            "com.netflix.mediaclient",
            "com.amazon.avod.thirdpartyclient",
            "com.hbo.hbonow",
            "com.disney.disneyplus",
            "com.crunchyroll.crunchyroid",
            "tv.twitch.android.app",
            "com.hulu.livingroomplus",
            "com.peacocktv.peacockandroid",
            "com.plexapp.android",
        )

        private val MESSAGING_PACKAGES = setOf(
            "com.whatsapp",
            "com.facebook.orca",           // Messenger
            "com.google.android.apps.messaging",
            "org.thoughtcrime.securesms",  // Signal
            "com.viber.voip",
        )

        private val NEWS_PACKAGES = setOf(
            "com.google.android.apps.magazines",
            "com.nytimes.android",
            "com.cnn.mobile.android.phone",
            "com.foxnews.android",
            "flipboard.app",
        )

        /**
         * Categorizes a package based on known package name prefixes.
         *
         * @param packageName The Android package name to categorize.
         * @return The matching [AppCategory].
         */
        fun categorize(packageName: String): AppCategory = when {
            packageName in SOCIAL_MEDIA_PACKAGES -> AppCategory.SOCIAL_MEDIA
            packageName in VIDEO_PACKAGES -> AppCategory.VIDEO
            packageName in MESSAGING_PACKAGES -> AppCategory.MESSAGING
            packageName in NEWS_PACKAGES -> AppCategory.NEWS
            packageName.contains(".game") || packageName.contains(".games") -> AppCategory.GAMES
            else -> AppCategory.OTHER
        }
    }
}
