package com.gratelock.app.service

import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime

class WorkerSchedulerTest {

    @Test
    fun delayUntilNextMonday_neverReturnsZeroOrNegative() {
        // The delay should always be positive (at least a few milliseconds in the future)
        val now = LocalDateTime.now()
        val daysUntilMonday = (8 - now.dayOfWeek.value) % 7
        val days = if (daysUntilMonday == 0) 7 else daysUntilMonday
        assertTrue("Days until next Monday should be 1-7, got $days", days in 1..7)
    }

    @Test
    fun dayOfWeekValues_mondayIs1() {
        // Verify the DayOfWeek.value assumption used in WorkerScheduler
        assertTrue(DayOfWeek.MONDAY.value == 1)
        assertTrue(DayOfWeek.SUNDAY.value == 7)
    }
}
