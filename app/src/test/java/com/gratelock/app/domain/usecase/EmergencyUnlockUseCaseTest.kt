package com.gratelock.app.domain.usecase

import com.gratelock.app.data.local.GrateLockPreferences
import com.gratelock.app.data.local.entity.UserStats
import com.gratelock.app.data.repository.StatsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class EmergencyUnlockUseCaseTest {

    private lateinit var statsRepository: StatsRepository
    private lateinit var preferences: GrateLockPreferences
    private lateinit var useCase: EmergencyUnlockUseCase

    @Before
    fun setUp() {
        statsRepository = mock()
        preferences = mock()
        useCase = EmergencyUnlockUseCase(statsRepository, preferences)
    }

    @Test
    fun invoke_withUnlocksRemaining_returnsSuccess() = runTest {
        whenever(statsRepository.getStatsOnce()).thenReturn(
            UserStats(emergencyUnlocksRemaining = 1),
        )

        val result = useCase()

        assertTrue(result is EmergencyUnlockResult.Success)
    }

    @Test
    fun invoke_withUnlocksRemaining_decrementsAndUnlocks() = runTest {
        whenever(statsRepository.getStatsOnce()).thenReturn(
            UserStats(emergencyUnlocksRemaining = 1),
        )

        useCase()

        verify(statsRepository).useEmergencyUnlock()
        verify(statsRepository).setUnlockedToday(true)
        verify(preferences).isUnlockedToday = true
        verify(preferences).emergencyUnlocksRemaining = 0
    }

    @Test
    fun invoke_withNoUnlocksRemaining_returnsNoUnlocksRemaining() = runTest {
        whenever(statsRepository.getStatsOnce()).thenReturn(
            UserStats(emergencyUnlocksRemaining = 0),
        )

        val result = useCase()

        assertTrue(result is EmergencyUnlockResult.NoUnlocksRemaining)
    }

    @Test
    fun invoke_withNoUnlocksRemaining_doesNotModifyState() = runTest {
        whenever(statsRepository.getStatsOnce()).thenReturn(
            UserStats(emergencyUnlocksRemaining = 0),
        )

        useCase()

        verify(statsRepository, never()).useEmergencyUnlock()
        verify(statsRepository, never()).setUnlockedToday(true)
    }

    @Test
    fun invoke_withNullStats_returnsNoUnlocksRemaining() = runTest {
        whenever(statsRepository.getStatsOnce()).thenReturn(null)

        val result = useCase()

        assertTrue(result is EmergencyUnlockResult.NoUnlocksRemaining)
    }

    @Test
    fun invoke_withMultipleUnlocks_decrementsByOne() = runTest {
        whenever(statsRepository.getStatsOnce()).thenReturn(
            UserStats(emergencyUnlocksRemaining = 3),
        )

        useCase()

        verify(preferences).emergencyUnlocksRemaining = 2
    }

    @Test
    fun invoke_doesNotAffectStreak() = runTest {
        whenever(statsRepository.getStatsOnce()).thenReturn(
            UserStats(emergencyUnlocksRemaining = 1, currentStreak = 5),
        )

        useCase()

        // Verify streak-related methods were not called
        verify(statsRepository, never()).updateStreak(
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any(),
            org.mockito.kotlin.any(),
        )
    }
}
