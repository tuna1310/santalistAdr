package com.santalist.app

import com.santalist.app.data.model.Gift
import com.santalist.app.data.model.GiftStatus
import com.santalist.app.data.model.Person
import com.santalist.app.domain.BudgetCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class YearFilterTest {

    @Test
    fun testPeopleFilteringByYear() {
        val person2025 = Person(id = "p1", name = "Uncle Bob", budget = 500_000, year = 2025)
        val person2026A = Person(id = "p2", name = "Mom", budget = 2_000_000, year = 2026)
        val person2026B = Person(id = "p3", name = "Dad", budget = 1_500_000, year = 2026)
        val person2027 = Person(id = "p4", name = "Future Kid", budget = 300_000, year = 2027)

        val allPeople = listOf(person2025, person2026A, person2026B, person2027)

        val people2026 = allPeople.filter { it.year == 2026 }
        assertEquals(2, people2026.size)
        assertTrue(people2026.any { it.name == "Mom" })
        assertTrue(people2026.any { it.name == "Dad" })

        val people2025 = allPeople.filter { it.year == 2025 }
        assertEquals(1, people2025.size)
        assertEquals("Uncle Bob", people2025.first().name)

        val people2028 = allPeople.filter { it.year == 2028 }
        assertTrue(people2028.isEmpty())
    }

    @Test
    fun testBudgetCalculationsIsolatedByYear() {
        val p2025 = Person(id = "p1", name = "A", budget = 1_000_000, year = 2025)
        val p2026 = Person(id = "p2", name = "B", budget = 2_000_000, year = 2026)

        val g2025 = Gift(id = "g1", title = "Old Gift", price = 900_000, status = GiftStatus.ORDERED, personId = "p1", year = 2025)
        val g2026 = Gift(id = "g2", title = "New Gift", price = 1_200_000, status = GiftStatus.ORDERED, personId = "p2", year = 2026)

        val allPeople = listOf(p2025, p2026)
        val allGifts = listOf(g2025, g2026)

        // 2026 isolated calculations
        val people2026 = allPeople.filter { it.year == 2026 }
        val gifts2026 = allGifts.filter { it.year == 2026 }

        assertEquals(2_000_000, BudgetCalculator.totalBudget(people2026))
        assertEquals(1_200_000, BudgetCalculator.totalPlanned(gifts2026))
        assertEquals(800_000, BudgetCalculator.totalRemaining(people2026, gifts2026))

        // 2025 isolated calculations
        val people2025 = allPeople.filter { it.year == 2025 }
        val gifts2025 = allGifts.filter { it.year == 2025 }

        assertEquals(1_000_000, BudgetCalculator.totalBudget(people2025))
        assertEquals(900_000, BudgetCalculator.totalPlanned(gifts2025))
        assertEquals(100_000, BudgetCalculator.totalRemaining(people2025, gifts2025))
    }
}
