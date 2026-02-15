package com.gratelock.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateUtilsTest {

    @Test
    fun todayDateString_returnsCorrectFormat() {
        val result = DateUtils.todayDateString()
        // Should match YYYY-MM-DD pattern
        assertTrue(result.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
        // Should parse without error
        LocalDate.parse(result, DateTimeFormatter.ISO_LOCAL_DATE)
    }

    @Test
    fun yesterdayDateString_isOneDayBeforeToday() {
        val today = DateUtils.todayDateString()
        val yesterday = DateUtils.yesterdayDateString()
        assertEquals(1, DateUtils.daysBetween(yesterday, today))
    }

    @Test
    fun isToday_returnsTrueForToday() {
        assertTrue(DateUtils.isToday(DateUtils.todayDateString()))
    }

    @Test
    fun isToday_returnsFalseForYesterday() {
        assertFalse(DateUtils.isToday(DateUtils.yesterdayDateString()))
    }

    @Test
    fun isYesterday_returnsTrueForYesterday() {
        assertTrue(DateUtils.isYesterday(DateUtils.yesterdayDateString()))
    }

    @Test
    fun isYesterday_returnsFalseForToday() {
        assertFalse(DateUtils.isYesterday(DateUtils.todayDateString()))
    }

    @Test
    fun weekStartDate_returnsMonday() {
        // 2026-02-11 is a Wednesday
        assertEquals("2026-02-09", DateUtils.weekStartDate("2026-02-11"))
    }

    @Test
    fun weekStartDate_mondayReturnsSelf() {
        // 2026-02-09 is a Monday
        assertEquals("2026-02-09", DateUtils.weekStartDate("2026-02-09"))
    }

    @Test
    fun weekStartDate_sundayReturnsPreviousMonday() {
        // 2026-02-15 is a Sunday
        assertEquals("2026-02-09", DateUtils.weekStartDate("2026-02-15"))
    }

    @Test
    fun weekEndDate_returnsSunday() {
        // 2026-02-11 is a Wednesday
        assertEquals("2026-02-15", DateUtils.weekEndDate("2026-02-11"))
    }

    @Test
    fun weekEndDate_sundayReturnsSelf() {
        // 2026-02-15 is a Sunday
        assertEquals("2026-02-15", DateUtils.weekEndDate("2026-02-15"))
    }

    @Test
    fun daysBetween_consecutiveDays() {
        assertEquals(1, DateUtils.daysBetween("2026-02-14", "2026-02-15"))
    }

    @Test
    fun daysBetween_sameDay() {
        assertEquals(0, DateUtils.daysBetween("2026-02-14", "2026-02-14"))
    }

    @Test
    fun daysBetween_multiDayGap() {
        assertEquals(5, DateUtils.daysBetween("2026-02-10", "2026-02-15"))
    }

    @Test
    fun daysBetween_negativeWhenReversed() {
        assertEquals(-3, DateUtils.daysBetween("2026-02-15", "2026-02-12"))
    }

    @Test
    fun daysBetween_acrossMonthBoundary() {
        assertEquals(3, DateUtils.daysBetween("2026-01-30", "2026-02-02"))
    }

    @Test
    fun parse_andFormat_roundTrip() {
        val dateString = "2026-02-15"
        val parsed = DateUtils.parse(dateString)
        assertEquals(dateString, DateUtils.format(parsed))
    }

    @Test
    fun currentTimeMillis_returnsReasonableValue() {
        val millis = DateUtils.currentTimeMillis()
        // Should be after 2025-01-01 in epoch millis
        assertTrue(millis > 1_735_689_600_000L)
    }

    @Test
    fun toDisplayDate_formatsCorrectly() {
        assertEquals("Feb 15, 2026", DateUtils.toDisplayDate("2026-02-15"))
    }

    @Test
    fun toDisplayDate_handlesJanuary() {
        assertEquals("Jan 1, 2026", DateUtils.toDisplayDate("2026-01-01"))
    }

    @Test
    fun toDisplayDate_handlesDecember() {
        assertEquals("Dec 31, 2025", DateUtils.toDisplayDate("2025-12-31"))
    }

    @Test
    fun toShortDisplayDate_formatsWithoutYear() {
        assertEquals("Feb 15", DateUtils.toShortDisplayDate("2026-02-15"))
    }

    @Test
    fun toShortDisplayDate_handlesSingleDigitDay() {
        assertEquals("Mar 3", DateUtils.toShortDisplayDate("2026-03-03"))
    }
}
