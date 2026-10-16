package com.santalist.app

import com.santalist.app.data.model.Gift
import com.santalist.app.data.model.GiftStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class OverdueCheckerTest {

    @Test
    fun testOverdueCases() {
        val today = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 15, 12, 0, 0)
        }.timeInMillis

        val yesterday = Calendar.getInstance().apply {
            timeInMillis = today
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis

        val tomorrow = Calendar.getInstance().apply {
            timeInMillis = today
            add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        // 1. Past due and not completed (IDEA / ORDERED) -> Overdue
        val overdueIdeaGift = Gift(title = "Gift A", price = 100, status = GiftStatus.IDEA, dueDate = yesterday, personId = "p1")
        assertTrue(overdueIdeaGift.isOverdue(today))

        val overdueOrderedGift = Gift(title = "Gift B", price = 100, status = GiftStatus.ORDERED, dueDate = yesterday, personId = "p1")
        assertTrue(overdueOrderedGift.isOverdue(today))

        // 2. Past due but completed (RECEIVED / WRAPPED) -> NOT Overdue
        val completedReceivedGift = Gift(title = "Gift C", price = 100, status = GiftStatus.RECEIVED, dueDate = yesterday, personId = "p1")
        assertFalse(completedReceivedGift.isOverdue(today))

        val completedWrappedGift = Gift(title = "Gift D", price = 100, status = GiftStatus.WRAPPED, dueDate = yesterday, personId = "p1")
        assertFalse(completedWrappedGift.isOverdue(today))

        // 3. Due tomorrow -> NOT Overdue
        val futureGift = Gift(title = "Gift E", price = 100, status = GiftStatus.IDEA, dueDate = tomorrow, personId = "p1")
        assertFalse(futureGift.isOverdue(today))

        // 4. No due date -> NOT Overdue
        val noDueDateGift = Gift(title = "Gift F", price = 100, status = GiftStatus.IDEA, dueDate = null, personId = "p1")
        assertFalse(noDueDateGift.isOverdue(today))
    }
}
