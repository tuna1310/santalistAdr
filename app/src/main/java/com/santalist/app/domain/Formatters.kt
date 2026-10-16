package com.santalist.app.domain

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatters {
    private val currencyFormat: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN")).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        DecimalFormat("#,##0 ₫", symbols).apply {
            negativePrefix = "-#"
            negativeSuffix = " ₫"
            // For custom pattern like -1.250.000 ₫
            applyPattern("#,##0 ₫;-#,##0 ₫")
        }
    }

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
    private val dayMonthFormat = SimpleDateFormat("dd/MM", Locale("vi", "VN"))

    fun formatVND(amount: Int): String {
        return currencyFormat.format(amount)
    }

    fun formatDate(timeMillis: Long): String {
        return dateFormat.format(Date(timeMillis))
    }

    fun formatDayMonth(timeMillis: Long): String {
        return dayMonthFormat.format(Date(timeMillis))
    }

    fun formatDueDate(timeMillis: Long?): String {
        if (timeMillis == null) return "No due date"

        val targetCal = Calendar.getInstance().apply { timeInMillis = timeMillis }
        val nowCal = Calendar.getInstance()

        return when {
            isSameDay(targetCal, nowCal) -> "Today"
            isTomorrow(targetCal, nowCal) -> "Tomorrow"
            isYesterday(targetCal, nowCal) -> "Yesterday"
            else -> formatDate(timeMillis)
        }
    }

    fun formatCompactVND(value: Int): String {
        return when {
            value >= 1_000_000 -> {
                val m = value.toDouble() / 1_000_000.0
                String.format(Locale.US, "%.1fM", m)
            }
            value >= 1_000 -> "${value / 1_000}k"
            else -> value.toString()
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isTomorrow(target: Calendar, now: Calendar): Boolean {
        val tomorrow = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
        return isSameDay(target, tomorrow)
    }

    private fun isYesterday(target: Calendar, now: Calendar): Boolean {
        val yesterday = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        return isSameDay(target, yesterday)
    }
}
