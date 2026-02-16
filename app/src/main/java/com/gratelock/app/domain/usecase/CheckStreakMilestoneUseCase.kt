package com.gratelock.app.domain.usecase

import javax.inject.Inject

/**
 * Checks if a streak count is a milestone worth celebrating.
 *
 * Milestones: 7, 14, 30, 60, 90, 100, 365 days.
 */
class CheckStreakMilestoneUseCase @Inject constructor() {

    /**
     * @param streak The current streak count.
     * @return The milestone message if the streak is a milestone, or null.
     */
    operator fun invoke(streak: Int): String? {
        return when (streak) {
            7 -> "1 week of gratitude!"
            14 -> "2 weeks of gratitude!"
            30 -> "1 month of gratitude!"
            60 -> "2 months of gratitude!"
            90 -> "3 months of gratitude!"
            100 -> "100 days of gratitude!"
            365 -> "1 year of gratitude!"
            else -> null
        }
    }

    /**
     * @param streak The current streak count.
     * @return True if the streak is a recognized milestone.
     */
    fun isMilestone(streak: Int): Boolean = invoke(streak) != null

    companion object {
        val MILESTONES = listOf(7, 14, 30, 60, 90, 100, 365)
    }
}
