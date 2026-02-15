package com.gratelock.app.domain.usecase

import com.gratelock.app.data.local.GrateLockPreferences
import com.gratelock.app.data.local.entity.UserStats
import com.gratelock.app.data.repository.StatsRepository
import com.gratelock.app.util.DateUtils
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CalculateStreakUseCaseTest {

    private lateinit var statsRepository: StatsRepository
    private lateinit var preferences: GrateLockPreferences
    private lateinit var useCase: CalculateStreakUseCase

    @Before
    fun setUp() {
        statsRepository = mock()
        preferences = mock()
        useCase = CalculateStreakUseCase(statsRepository, preferences)
    }

    @Test
    fun invoke_firstEverEntry_returnsOne() = runTest {
        whenever(statsRepository.getStatsOnce()).thenReturn(
            UserStats(currentStreak = 0, longestStreak = 0, lastEntryDate = null),
        )

        val result = useCase()

        assertEquals(1, result)
        verify(statsRepository).updateStreak(1, 1, DateUtils.todayDateString())
        verify(preferences).currentStreak = 1
    }

    @Test
    fun invoke_entryYesterday_incrementsStreak() = runTest {
        val yesterday = DateUtils.yesterdayDateString()
        whenever(statsRepository.getStatsOnce()).thenReturn(
            UserStats(currentStreak = 5, longestStreak = 10, lastEntryDate = yesterday),
        )

        val result = useCase()

        assertEquals(6, result)
        verify(statsRepository).updateStreak(6, 10, DateUtils.todayDateString())
        verify(preferences).currentStreak = 6
    }

    @Test
    fun invoke_entryYesterday_updatesLongestIfNew() = runTest {
        val yesterday = DateUtils.yesterdayDateString()
        whenever(statsRepository.getStatsOnce()).thenReturn(
            UserStats(currentStreak = 10, longestStreak = 10, lastEntryDate = yesterday),
        )

        val result = useCase()

        assertEquals(11, result)
        verify(statsRepository).updateStreak(11, 11, DateUtils.todayDateString())
    }

    @Test
    fun invoke_gapMoreThanOneDay_resetsToOne() = runTest {
        whenever(statsRepository.getStatsOnce()).thenReturn(
            UserStats(currentStreak = 5, longestStreak = 10, lastEntryDate = "2024-01-01"),
        )

        val result = useCase()

        assertEquals(1, result)
        verify(statsRepository).updateStreak(1, 10, DateUtils.todayDateString())
    }

    @Test
    fun invoke_alreadyJournaledToday_returnsCurrentStreak() = runTest {
        val today = DateUtils.todayDateString()
        whenever(statsRepository.getStatsOnce()).thenReturn(
            UserStats(currentStreak = 7, longestStreak = 15, lastEntryDate = today),
        )

        val result = useCase()

        assertEquals(7, result)
        verify(statsRepository, never()).updateStreak(any(), any(), any())
    }

    @Test
    fun invoke_nullStats_returnsOne() = runTest {
        whenever(statsRepository.getStatsOnce()).thenReturn(null)

        val result = useCase()

        assertEquals(1, result)
    }

    @Test
    fun invoke_gapDoesNotShrinkLongestStreak() = runTest {
        whenever(statsRepository.getStatsOnce()).thenReturn(
            UserStats(currentStreak = 20, longestStreak = 30, lastEntryDate = "2024-01-01"),
        )

        val result = useCase()

        assertEquals(1, result)
        // longestStreak should remain 30
        verify(statsRepository).updateStreak(1, 30, DateUtils.todayDateString())
    }
}
