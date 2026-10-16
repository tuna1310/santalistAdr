package com.santalist.app.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class PersonWithGifts(
    @Embedded
    val person: Person,

    @Relation(
        parentColumn = "id",
        entityColumn = "personId"
    )
    val gifts: List<Gift> = emptyList()
) {
    val plannedAmount: Int
        get() = gifts.sumOf { it.price }

    val spentAmount: Int
        get() = gifts.filter { it.status.isSpent }.sumOf { it.price }

    val remainingAmount: Int
        get() = person.budget - plannedAmount

    val isOverBudget: Boolean
        get() = plannedAmount > person.budget

    val budgetUsageRatio: Float
        get() = if (person.budget > 0) plannedAmount.toFloat() / person.budget.toFloat() else 0f
}
