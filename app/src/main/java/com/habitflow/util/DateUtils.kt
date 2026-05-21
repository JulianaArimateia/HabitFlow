package com.habitflow.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun todayRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        val end = start + 24 * 60 * 60 * 1000L
        return start to end
    }

    fun dayRange(daysAgo: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        val end = start + 24 * 60 * 60 * 1000L
        return start to end
    }

    fun startOfDay(epochMs: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = epochMs
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun formatDate(epochMs: Long, pattern: String = "dd/MM/yyyy"): String {
        val sdf = SimpleDateFormat(pattern, Locale("pt", "BR"))
        return sdf.format(Date(epochMs))
    }

    fun formatTime(epochMs: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(epochMs))
    }

    fun todayDayOfWeek(): Int = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    fun greetingForHour(): String {
        return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Bom dia"
            in 12..17 -> "Boa tarde"
            else -> "Boa noite"
        }
    }

    fun currentMonthDays(): List<Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val days = mutableListOf<Long>()
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        repeat(maxDay) { i ->
            days.add(cal.timeInMillis + i * 24 * 60 * 60 * 1000L)
        }
        return days
    }

    fun firstDayOfMonthDayOfWeek(): Int {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return cal.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun
    }

    fun parseHHmm(time: String): Pair<Int, Int> {
        val parts = time.split(":")
        return if (parts.size == 2) {
            (parts[0].toIntOrNull() ?: 7) to (parts[1].toIntOrNull() ?: 0)
        } else 7 to 0
    }

    fun averageMinutesOfDay(timestamps: List<Long>): Int {
        if (timestamps.isEmpty()) return 7 * 60
        val minutesList = timestamps.map {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it
            cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        }
        return minutesList.average().toInt()
    }
}
