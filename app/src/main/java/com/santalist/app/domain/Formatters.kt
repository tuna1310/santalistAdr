package com.santalist.app.domain

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatters {
    private val vndFormat: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN")).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        DecimalFormat("#,##0 ₫", symbols).apply {
            applyPattern("#,##0 ₫;-#,##0 ₫")
        }
    }

    private val usdFormat: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = ','
            decimalSeparator = '.'
        }
        DecimalFormat("$#,##0", symbols).apply {
            applyPattern("$#,##0;-$#,##0")
        }
    }

    private val eurFormat: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale.GERMANY).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        DecimalFormat("€#,##0", symbols).apply {
            applyPattern("€#,##0;-€#,##0")
        }
    }

    private val gbpFormat: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale.UK).apply {
            groupingSeparator = ','
            decimalSeparator = '.'
        }
        DecimalFormat("£#,##0", symbols).apply {
            applyPattern("£#,##0;-£#,##0")
        }
    }

    private val jpyFormat: DecimalFormat by lazy {
        val symbols = DecimalFormatSymbols(Locale.JAPAN).apply {
            groupingSeparator = ','
            decimalSeparator = '.'
        }
        DecimalFormat("¥#,##0", symbols).apply {
            applyPattern("¥#,##0;-¥#,##0")
        }
    }

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
    private val dayMonthFormat = SimpleDateFormat("dd/MM", Locale("vi", "VN"))
    private val headerDateFormat = SimpleDateFormat("EEEE, d MMMM", Locale.ENGLISH)

    fun formatVND(amount: Int): String {
        return vndFormat.format(amount)
    }

    fun formatCurrency(amount: Int, currency: String = "VND"): String {
        return when (currency.uppercase()) {
            "USD" -> usdFormat.format(amount)
            "EUR" -> eurFormat.format(amount)
            "GBP" -> gbpFormat.format(amount)
            "JPY" -> jpyFormat.format(amount)
            else -> vndFormat.format(amount)
        }
    }

    fun formatHeaderDate(timeMillis: Long = System.currentTimeMillis()): String {
        return headerDateFormat.format(Date(timeMillis))
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

    fun formatCompactCurrency(value: Int, currency: String = "VND"): String {
        val prefix = when (currency.uppercase()) {
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "JPY" -> "¥"
            else -> ""
        }
        val suffix = if (currency.uppercase() == "VND") " ₫" else ""

        return when {
            value >= 1_000_000 -> {
                val m = value.toDouble() / 1_000_000.0
                String.format(Locale.US, "%s%.1fM%s", prefix, m, suffix)
            }
            value >= 1_000 -> "${prefix}${value / 1_000}k$suffix"
            else -> "$prefix$value$suffix"
        }
    }

    fun formatCompactVND(value: Int): String {
        return formatCompactCurrency(value, "VND")
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
