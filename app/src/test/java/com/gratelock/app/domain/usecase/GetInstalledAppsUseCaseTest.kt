package com.gratelock.app.domain.usecase

import com.gratelock.app.domain.model.AppCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class GetInstalledAppsUseCaseTest {

    @Test
    fun categorize_instagramIsSocialMedia() {
        assertEquals(
            AppCategory.SOCIAL_MEDIA,
            GetInstalledAppsUseCase.categorize("com.instagram.android"),
        )
    }

    @Test
    fun categorize_tiktokIsSocialMedia() {
        assertEquals(
            AppCategory.SOCIAL_MEDIA,
            GetInstalledAppsUseCase.categorize("com.zhiliaoapp.musically"),
        )
    }

    @Test
    fun categorize_twitterIsSocialMedia() {
        assertEquals(
            AppCategory.SOCIAL_MEDIA,
            GetInstalledAppsUseCase.categorize("com.twitter.android"),
        )
    }

    @Test
    fun categorize_redditIsSocialMedia() {
        assertEquals(
            AppCategory.SOCIAL_MEDIA,
            GetInstalledAppsUseCase.categorize("com.reddit.frontpage"),
        )
    }

    @Test
    fun categorize_snapchatIsSocialMedia() {
        assertEquals(
            AppCategory.SOCIAL_MEDIA,
            GetInstalledAppsUseCase.categorize("com.snapchat.android"),
        )
    }

    @Test
    fun categorize_youtubeIsVideo() {
        assertEquals(
            AppCategory.VIDEO,
            GetInstalledAppsUseCase.categorize("com.google.android.youtube"),
        )
    }

    @Test
    fun categorize_netflixIsVideo() {
        assertEquals(
            AppCategory.VIDEO,
            GetInstalledAppsUseCase.categorize("com.netflix.mediaclient"),
        )
    }

    @Test
    fun categorize_twitchIsVideo() {
        assertEquals(
            AppCategory.VIDEO,
            GetInstalledAppsUseCase.categorize("tv.twitch.android.app"),
        )
    }

    @Test
    fun categorize_whatsappIsMessaging() {
        assertEquals(
            AppCategory.MESSAGING,
            GetInstalledAppsUseCase.categorize("com.whatsapp"),
        )
    }

    @Test
    fun categorize_signalIsMessaging() {
        assertEquals(
            AppCategory.MESSAGING,
            GetInstalledAppsUseCase.categorize("org.thoughtcrime.securesms"),
        )
    }

    @Test
    fun categorize_gamePackageIsGames() {
        assertEquals(
            AppCategory.GAMES,
            GetInstalledAppsUseCase.categorize("com.supercell.clashofclans.game"),
        )
    }

    @Test
    fun categorize_gamesPackageIsGames() {
        assertEquals(
            AppCategory.GAMES,
            GetInstalledAppsUseCase.categorize("com.king.games.candycrush"),
        )
    }

    @Test
    fun categorize_unknownPackageIsOther() {
        assertEquals(
            AppCategory.OTHER,
            GetInstalledAppsUseCase.categorize("com.example.randomapp"),
        )
    }

    @Test
    fun categorize_calculatorIsOther() {
        assertEquals(
            AppCategory.OTHER,
            GetInstalledAppsUseCase.categorize("com.google.android.calculator"),
        )
    }

    @Test
    fun categorize_nytimesIsNews() {
        assertEquals(
            AppCategory.NEWS,
            GetInstalledAppsUseCase.categorize("com.nytimes.android"),
        )
    }

    @Test
    fun appCategory_sortOrder_socialMediaFirst() {
        val categories = AppCategory.entries.sortedBy { it.sortOrder }
        assertEquals(AppCategory.SOCIAL_MEDIA, categories.first())
    }

    @Test
    fun appCategory_sortOrder_otherLast() {
        val categories = AppCategory.entries.sortedBy { it.sortOrder }
        assertEquals(AppCategory.OTHER, categories.last())
    }
}
