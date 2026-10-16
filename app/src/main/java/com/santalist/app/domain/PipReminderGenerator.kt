package com.santalist.app.domain

import com.santalist.app.data.model.Gift
import com.santalist.app.data.model.Person
import com.santalist.app.data.model.PersonWithGifts
import java.util.Calendar

enum class PipReminderType {
    OVERDUE_GIFT,
    DUE_SOON,
    PERSON_OVER_BUDGET,
    TOTAL_OVER_BUDGET,
    PERSON_NO_GIFTS,
    NO_BUDGET_SET
}

enum class PipActionType {
    MARK_ORDERED,
    MARK_RECEIVED,
    OPEN_PERSON,
    SET_BUDGET,
    ADD_GIFT
}

data class PipReminder(
    val id: String,
    val type: PipReminderType,
    val title: String,
    val subtitle: String,
    val targetPersonId: String? = null,
    val targetGiftId: String? = null,
    val actionText: String,
    val actionType: PipActionType
)

object PipReminderGenerator {
    fun generateReminders(
        peopleWithGifts: List<PersonWithGifts>,
        referenceMillis: Long = System.currentTimeMillis(),
        currency: String = "VND"
    ): List<PipReminder> {
        val reminders = mutableListOf<PipReminder>()
        if (peopleWithGifts.isEmpty()) return emptyList()

        val allPeople = peopleWithGifts.map { it.person }
        val allGifts = peopleWithGifts.flatMap { it.gifts }

        val refCal = Calendar.getInstance().apply {
            timeInMillis = referenceMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // 1. Check Overdue Gifts
        for (pwg in peopleWithGifts) {
            val person = pwg.person
            for (gift in pwg.gifts) {
                if (gift.dueDate != null && !gift.status.isCompleted) {
                    val dueCal = Calendar.getInstance().apply {
                        timeInMillis = gift.dueDate
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    if (dueCal.timeInMillis < refCal.timeInMillis) {
                        reminders.add(
                            PipReminder(
                                id = "overdue_${gift.id}",
                                type = PipReminderType.OVERDUE_GIFT,
                                title = "Gift overdue: ${gift.title}",
                                subtitle = "Was due for ${person.name} on ${Formatters.formatDueDate(gift.dueDate)}",
                                targetPersonId = person.id,
                                targetGiftId = gift.id,
                                actionText = if (gift.status.name == "IDEA") "Mark Ordered" else "Mark Received",
                                actionType = if (gift.status.name == "IDEA") PipActionType.MARK_ORDERED else PipActionType.MARK_RECEIVED
                            )
                        )
                    } else {
                        // 2. Check Due Soon (within 3 days)
                        val diffDays = ((dueCal.timeInMillis - refCal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                        if (diffDays in 0..3) {
                            val daysLabel = when (diffDays) {
                                0 -> "today"
                                1 -> "tomorrow"
                                else -> "in $diffDays days"
                            }
                            reminders.add(
                                PipReminder(
                                    id = "due_soon_${gift.id}",
                                    type = PipReminderType.DUE_SOON,
                                    title = "Due $daysLabel: ${gift.title}",
                                    subtitle = "For ${person.name} (${Formatters.formatCurrency(gift.price, currency)})",
                                    targetPersonId = person.id,
                                    targetGiftId = gift.id,
                                    actionText = if (gift.status.name == "IDEA") "Mark Ordered" else "View Details",
                                    actionType = if (gift.status.name == "IDEA") PipActionType.MARK_ORDERED else PipActionType.OPEN_PERSON
                                )
                            )
                        }
                    }
                }
            }
        }

        // 3. Check Person Over Budget
        for (pwg in peopleWithGifts) {
            val person = pwg.person
            if (person.budget > 0 && BudgetCalculator.isPersonOverBudget(person.budget, pwg.gifts)) {
                val overAmount = BudgetCalculator.personPlanned(pwg.gifts) - person.budget
                reminders.add(
                    PipReminder(
                        id = "over_budget_${person.id}",
                        type = PipReminderType.PERSON_OVER_BUDGET,
                        title = "${person.name} is over budget",
                        subtitle = "Exceeds budget by ${Formatters.formatCurrency(overAmount, currency)}",
                        targetPersonId = person.id,
                        actionText = "Open Person",
                        actionType = PipActionType.OPEN_PERSON
                    )
                )
            }
        }

        // 4. Check Total Budget Exceeded
        val totalBudget = BudgetCalculator.totalBudget(allPeople)
        if (totalBudget > 0 && BudgetCalculator.isOverBudget(allPeople, allGifts)) {
            val overAmount = BudgetCalculator.totalPlanned(allGifts) - totalBudget
            reminders.add(
                PipReminder(
                    id = "total_over_budget",
                    type = PipReminderType.TOTAL_OVER_BUDGET,
                    title = "Total budget exceeded",
                    subtitle = "Overall spending is ${Formatters.formatCurrency(overAmount, currency)} over budget",
                    actionText = "Adjust Budget",
                    actionType = PipActionType.SET_BUDGET
                )
            )
        }

        // 5. Check People with 0 gifts
        for (pwg in peopleWithGifts) {
            if (pwg.gifts.isEmpty()) {
                reminders.add(
                    PipReminder(
                        id = "no_gifts_${pwg.person.id}",
                        type = PipReminderType.PERSON_NO_GIFTS,
                        title = "No gifts for ${pwg.person.name}",
                        subtitle = "Add gifts to start checking off your list",
                        targetPersonId = pwg.person.id,
                        actionText = "Add Gift",
                        actionType = PipActionType.ADD_GIFT
                    )
                )
            }
        }

        // 6. Check People without budget set
        for (pwg in peopleWithGifts) {
            if (pwg.person.budget <= 0) {
                reminders.add(
                    PipReminder(
                        id = "no_budget_${pwg.person.id}",
                        type = PipReminderType.NO_BUDGET_SET,
                        title = "No budget for ${pwg.person.name}",
                        subtitle = "Set a budget limit to track gift expenses",
                        targetPersonId = pwg.person.id,
                        actionText = "Set Budget",
                        actionType = PipActionType.SET_BUDGET
                    )
                )
            }
        }

        return reminders
    }
}
