package com.santalist.app

import com.santalist.app.domain.Formatters
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class FormattersTest {

    @Test
    fun testVNDCurrencyFormatting() {
        assertEquals("1.250.000 ₫", Formatters.formatVND(1_250_000))
        assertEquals("0 ₫", Formatters.formatVND(0))
        assertEquals("650.000 ₫", Formatters.formatVND(650_000))
        assertEquals("-250.000 ₫", Formatters.formatVND(-250_000))
    }

    @Test
    fun testDateFormatting() {
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.DECEMBER, 15, 12, 0, 0)
        }
        assertEquals("15/12/2026", Formatters.formatDate(cal.timeInMillis))
        assertEquals("15/12", Formatters.formatDayMonth(cal.timeInMillis))
    }
}
