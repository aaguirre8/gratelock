package com.gratelock.app.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

/**
 * Date utility functions for GrateLock.
 *
 * All dates use the device's local timezone and "YYYY-MM-DD" format.
 * A "day" runs from midnight to midnight in the user's local timezone.
 */
object DateUtils {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    /** @return Today's date as "YYYY-MM-DD" in the device's local timezone. */
    fun todayDateString(): String =
        LocalDate.now(ZoneId.systemDefault()).format(formatter)

    /** @return Yesterday's date as "YYYY-MM-DD" in the device's local timezone. */
    fun yesterdayDateString(): String =
        LocalDate.now(ZoneId.systemDefault()).minusDays(1).format(formatter)

    /** @return Current time in epoch milliseconds. */
    fun currentTimeMillis(): Long =
        System.currentTimeMillis()

    /**
     * @param dateString A date in "YYYY-MM-DD" format.
     * @return Whether the given date is today.
     */
    fun isToday(dateString: String): Boolean =
        dateString == todayDateString()

    /**
     * @param dateString A date in "YYYY-MM-DD" format.
     * @return Whether the given date is yesterday.
     */
    fun isYesterday(dateString: String): Boolean =
        dateString == yesterdayDateString()

    /**
     * Returns the Monday of the week containing the given date.
     *
     * @param dateString A date in "YYYY-MM-DD" format.
     * @return The Monday of that week as "YYYY-MM-DD".
     */
    fun weekStartDate(dateString: String): String {
        val date = LocalDate.parse(dateString, formatter)
        val monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return monday.format(formatter)
    }

    /**
     * Returns the Sunday of the week containing the given date.
     *
     * @param dateString A date in "YYYY-MM-DD" format.
     * @return The Sunday of that week as "YYYY-MM-DD".
     */
    fun weekEndDate(dateString: String): String {
        val date = LocalDate.parse(dateString, formatter)
        val sunday = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        return sunday.format(formatter)
    }

    /**
     * Calculates the number of days between two dates.
     *
     * @param fromDate Start date in "YYYY-MM-DD" format.
     * @param toDate End date in "YYYY-MM-DD" format.
     * @return Number of days between the two dates (can be negative).
     */
    fun daysBetween(fromDate: String, toDate: String): Long {
        val from = LocalDate.parse(fromDate, formatter)
        val to = LocalDate.parse(toDate, formatter)
        return java.time.temporal.ChronoUnit.DAYS.between(from, to)
    }

    /**
     * @param dateString A date in "YYYY-MM-DD" format.
     * @return The [LocalDate] parsed from the string.
     */
    fun parse(dateString: String): LocalDate =
        LocalDate.parse(dateString, formatter)

    /**
     * @param date A [LocalDate] to format.
     * @return The date as "YYYY-MM-DD" string.
     */
    fun format(date: LocalDate): String =
        date.format(formatter)

    private val displayFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    /**
     * Formats a "YYYY-MM-DD" date string for user-friendly display.
     *
     * @param dateString A date in "YYYY-MM-DD" format.
     * @return The date formatted as "Feb 15, 2026".
     */
    fun toDisplayDate(dateString: String): String =
        LocalDate.parse(dateString, formatter).format(displayFormatter)

    private val shortDisplayFormatter = DateTimeFormatter.ofPattern("MMM d")

    /**
     * Formats a "YYYY-MM-DD" date string for compact display (no year).
     *
     * @param dateString A date in "YYYY-MM-DD" format.
     * @return The date formatted as "Feb 15".
     */
    fun toShortDisplayDate(dateString: String): String =
        LocalDate.parse(dateString, formatter).format(shortDisplayFormatter)
}
