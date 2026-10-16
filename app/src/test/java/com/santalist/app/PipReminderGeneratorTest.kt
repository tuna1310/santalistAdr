package com.santalist.app

import com.santalist.app.data.model.Gift
import com.santalist.app.data.model.GiftStatus
import com.santalist.app.data.model.Person
import com.santalist.app.data.model.PersonWithGifts
import com.santalist.app.domain.PipReminderGenerator
import com.santalist.app.domain.PipReminderType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class PipReminderGeneratorTest {

    private val referenceTime = Calendar.getInstance().apply {
        set(2026, Calendar.OCTOBER, 16, 12, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private val yesterday = Calendar.getInstance().apply {
        timeInMillis = referenceTime
        add(Calendar.DAY_OF_YEAR, -1)
    }.timeInMillis

    private val inTwoDays = Calendar.getInstance().apply {
        timeInMillis = referenceTime
        add(Calendar.DAY_OF_YEAR, 2)
    }.timeInMillis

    @Test
    fun testEmptyStateWhenNoPeople() {
        val reminders = PipReminderGenerator.generateReminders(emptyList(), referenceTime)
        assertTrue(reminders.isEmpty())
    }

    @Test
    fun testOverdueGiftReminder() {
        val person = Person(id = "p1", name = "Dad", budget = 1_000_000)
        val overdueGift = Gift(
            id = "g1",
            title = "Warm Jacket",
            price = 500_000,
            status = GiftStatus.IDEA,
            dueDate = yesterday,
            personId = "p1"
        )
        val pwg = PersonWithGifts(person = person, gifts = listOf(overdueGift))

        val reminders = PipReminderGenerator.generateReminders(listOf(pwg), referenceTime)
        assertTrue(reminders.any { it.type == PipReminderType.OVERDUE_GIFT && it.targetGiftId == "g1" })
    }

    @Test
    fun testDueSoonGiftReminder() {
        val person = Person(id = "p1", name = "Sister", budget = 500_000)
        val dueSoonGift = Gift(
            id = "g2",
            title = "Necklace",
            price = 300_000,
            status = GiftStatus.IDEA,
            dueDate = inTwoDays,
            personId = "p1"
        )
        val pwg = PersonWithGifts(person = person, gifts = listOf(dueSoonGift))

        val reminders = PipReminderGenerator.generateReminders(listOf(pwg), referenceTime)
        assertTrue(reminders.any { it.type == PipReminderType.DUE_SOON && it.targetGiftId == "g2" })
    }

    @Test
    fun testCompletedGiftDoesNotTriggerOverdue() {
        val person = Person(id = "p1", name = "Brother", budget = 500_000)
        val receivedGift = Gift(
            id = "g3",
            title = "Headphones",
            price = 400_000,
            status = GiftStatus.RECEIVED,
            dueDate = yesterday,
            personId = "p1"
        )
        val pwg = PersonWithGifts(person = person, gifts = listOf(receivedGift))

        val reminders = PipReminderGenerator.generateReminders(listOf(pwg), referenceTime)
        assertTrue(reminders.none { it.type == PipReminderType.OVERDUE_GIFT })
    }

    @Test
    fun testPersonOverBudgetReminder() {
        val person = Person(id = "p2", name = "Mom", budget = 1_000_000)
        val gift1 = Gift(id = "g4", title = "Handbag", price = 1_200_000, status = GiftStatus.IDEA, personId = "p2")
        val pwg = PersonWithGifts(person = person, gifts = listOf(gift1))

        val reminders = PipReminderGenerator.generateReminders(listOf(pwg), referenceTime)
        assertTrue(reminders.any { it.type == PipReminderType.PERSON_OVER_BUDGET && it.targetPersonId == "p2" })
    }

    @Test
    fun testPersonWithNoGiftsReminder() {
        val person = Person(id = "p3", name = "Grandma", budget = 500_000)
        val pwg = PersonWithGifts(person = person, gifts = emptyList())

        val reminders = PipReminderGenerator.generateReminders(listOf(pwg), referenceTime)
        assertTrue(reminders.any { it.type == PipReminderType.PERSON_NO_GIFTS && it.targetPersonId == "p3" })
    }

    @Test
    fun testPersonWithNoBudgetReminder() {
        val person = Person(id = "p4", name = "Friend", budget = 0)
        val gift = Gift(id = "g5", title = "Mug", price = 100_000, status = GiftStatus.IDEA, personId = "p4")
        val pwg = PersonWithGifts(person = person, gifts = listOf(gift))

        val reminders = PipReminderGenerator.generateReminders(listOf(pwg), referenceTime)
        assertTrue(reminders.any { it.type == PipReminderType.NO_BUDGET_SET && it.targetPersonId == "p4" })
    }
}
