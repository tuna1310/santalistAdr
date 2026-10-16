package com.santalist.app

import com.santalist.app.data.model.Gift
import com.santalist.app.data.model.GiftStatus
import com.santalist.app.data.model.Person
import com.santalist.app.domain.BudgetCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BudgetCalculatorTest {

    @Test
    fun testBudgetCalculations() {
        val person1 = Person(id = "p1", name = "A", budget = 1_000_000)
        val person2 = Person(id = "p2", name = "B", budget = 2_000_000)

        val gift1 = Gift(id = "g1", title = "G1", price = 300_000, status = GiftStatus.IDEA, personId = "p1")
        val gift2 = Gift(id = "g2", title = "G2", price = 400_000, status = GiftStatus.ORDERED, personId = "p1")
        val gift3 = Gift(id = "g3", title = "G3", price = 500_000, status = GiftStatus.RECEIVED, personId = "p2")
        val gift4 = Gift(id = "g4", title = "G4", price = 600_000, status = GiftStatus.WRAPPED, personId = "p2")

        val people = listOf(person1, person2)
        val gifts = listOf(gift1, gift2, gift3, gift4)

        // Total Budget: 1M + 2M = 3.000.000
        assertEquals(3_000_000, BudgetCalculator.totalBudget(people))

        // Total Planned: 300k + 400k + 500k + 600k = 1.800.000
        assertEquals(1_800_000, BudgetCalculator.totalPlanned(gifts))

        // Total Spent (status >= ORDERED): gift2 (400k) + gift3 (500k) + gift4 (600k) = 1.500.000
        assertEquals(1_500_000, BudgetCalculator.totalSpent(gifts))

        // Total Remaining: 3M - 1.8M = 1.200.000
        assertEquals(1_200_000, BudgetCalculator.totalRemaining(people, gifts))
        assertFalse(BudgetCalculator.isOverBudget(people, gifts))
    }

    @Test
    fun testOverBudgetDetection() {
        val person = Person(id = "p3", name = "C", budget = 500_000)
        val expensiveGift = Gift(id = "g5", title = "Phone", price = 800_000, status = GiftStatus.IDEA, personId = "p3")

        assertEquals(-300_000, BudgetCalculator.totalRemaining(listOf(person), listOf(expensiveGift)))
        assertTrue(BudgetCalculator.isOverBudget(listOf(person), listOf(expensiveGift)))
        assertTrue(BudgetCalculator.isPersonOverBudget(person.budget, listOf(expensiveGift)))
    }
}
