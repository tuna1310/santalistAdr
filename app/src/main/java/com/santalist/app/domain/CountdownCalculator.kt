package com.santalist.app.domain

import java.util.Calendar

object CountdownCalculator {
    fun nextChristmas(referenceMillis: Long = System.currentTimeMillis()): Calendar {
        val refCal = Calendar.getInstance().apply {
            timeInMillis = referenceMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val currentYear = refCal.get(Calendar.YEAR)

        val targetCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 25)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (refCal.after(targetCal)) {
            // Already passed Dec 25 this year -> count to Dec 25 next year
            targetCal.set(Calendar.YEAR, currentYear + 1)
        }

        return targetCal
    }

    fun daysRemaining(referenceMillis: Long = System.currentTimeMillis()): Int {
        val refCal = Calendar.getInstance().apply {
            timeInMillis = referenceMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val targetCal = nextChristmas(referenceMillis)

        val diffMillis = targetCal.timeInMillis - refCal.timeInMillis
        val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

        return diffDays.coerceAtLeast(0)
    }

    fun countdownText(referenceMillis: Long = System.currentTimeMillis()): String {
        val days = daysRemaining(referenceMillis)
        return when (days) {
            0 -> "Today is Christmas! 🎅"
            1 -> "1 day until Christmas 🎄"
            else -> "$days days until Christmas 🎄"
        }
    }
}
