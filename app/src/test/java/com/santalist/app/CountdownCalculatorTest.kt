package com.santalist.app

import com.santalist.app.domain.CountdownCalculator
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class CountdownCalculatorTest {

    @Test
    fun testChristmasCountdownDays() {
        // Case 1: Dec 24, 2026 -> 1 day remaining
        val cal24 = Calendar.getInstance().apply {
            set(2026, Calendar.DECEMBER, 24, 12, 0, 0)
        }
        assertEquals(1, CountdownCalculator.daysRemaining(cal24.timeInMillis))
        assertEquals("1 day until Christmas 🎄", CountdownCalculator.countdownText(cal24.timeInMillis))

        // Case 2: Dec 25, 2026 -> 0 days remaining
        val cal25 = Calendar.getInstance().apply {
            set(2026, Calendar.DECEMBER, 25, 12, 0, 0)
        }
        assertEquals(0, CountdownCalculator.daysRemaining(cal25.timeInMillis))
        assertEquals("Today is Christmas! 🎅", CountdownCalculator.countdownText(cal25.timeInMillis))

        // Case 3: Dec 26, 2026 -> Already passed -> Rollover to Dec 25, 2027
        val cal26 = Calendar.getInstance().apply {
            set(2026, Calendar.DECEMBER, 26, 12, 0, 0)
        }
        val remainingFrom26 = CountdownCalculator.daysRemaining(cal26.timeInMillis)
        // 2027 is non-leap year -> 364 days from Dec 26, 2026 to Dec 25, 2027
        assertEquals(364, remainingFrom26)

        // Case 4: Dec 20, 2026 -> 5 days remaining
        val cal20 = Calendar.getInstance().apply {
            set(2026, Calendar.DECEMBER, 20, 12, 0, 0)
        }
        assertEquals(5, CountdownCalculator.daysRemaining(cal20.timeInMillis))
        assertEquals("5 days until Christmas 🎄", CountdownCalculator.countdownText(cal20.timeInMillis))
    }
}
