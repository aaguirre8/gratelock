package com.gratelock.app.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [CheckStreakMilestoneUseCase].
 */
class CheckStreakMilestoneUseCaseTest {

    private lateinit var useCase: CheckStreakMilestoneUseCase

    @Before
    fun setUp() {
        useCase = CheckStreakMilestoneUseCase()
    }

    @Test
    fun invoke_streak7_returnsOneWeekMessage() {
        val result = useCase(7)
        assertEquals("1 week of gratitude!", result)
    }

    @Test
    fun invoke_streak14_returnsTwoWeeksMessage() {
        val result = useCase(14)
        assertEquals("2 weeks of gratitude!", result)
    }

    @Test
    fun invoke_streak30_returnsOneMonthMessage() {
        val result = useCase(30)
        assertEquals("1 month of gratitude!", result)
    }

    @Test
    fun invoke_streak60_returnsTwoMonthsMessage() {
        val result = useCase(60)
        assertEquals("2 months of gratitude!", result)
    }

    @Test
    fun invoke_streak90_returnsThreeMonthsMessage() {
        val result = useCase(90)
        assertEquals("3 months of gratitude!", result)
    }

    @Test
    fun invoke_streak100_returns100DaysMessage() {
        val result = useCase(100)
        assertEquals("100 days of gratitude!", result)
    }

    @Test
    fun invoke_streak365_returnsOneYearMessage() {
        val result = useCase(365)
        assertEquals("1 year of gratitude!", result)
    }

    @Test
    fun invoke_nonMilestoneStreak_returnsNull() {
        assertNull(useCase(1))
        assertNull(useCase(5))
        assertNull(useCase(10))
        assertNull(useCase(15))
        assertNull(useCase(50))
        assertNull(useCase(99))
        assertNull(useCase(200))
    }

    @Test
    fun invoke_zeroStreak_returnsNull() {
        assertNull(useCase(0))
    }

    @Test
    fun isMilestone_milestoneValues_returnsTrue() {
        CheckStreakMilestoneUseCase.MILESTONES.forEach { milestone ->
            assertTrue("$milestone should be a milestone", useCase.isMilestone(milestone))
        }
    }

    @Test
    fun isMilestone_nonMilestoneValues_returnsFalse() {
        assertFalse(useCase.isMilestone(0))
        assertFalse(useCase.isMilestone(1))
        assertFalse(useCase.isMilestone(8))
        assertFalse(useCase.isMilestone(29))
    }

    @Test
    fun milestones_containsAllExpectedValues() {
        val expected = listOf(7, 14, 30, 60, 90, 100, 365)
        assertEquals(expected, CheckStreakMilestoneUseCase.MILESTONES)
    }

    @Test
    fun invoke_allMilestones_returnNonNullMessages() {
        CheckStreakMilestoneUseCase.MILESTONES.forEach { milestone ->
            assertNotNull(
                "Milestone $milestone should have a message",
                useCase(milestone),
            )
        }
    }
}
