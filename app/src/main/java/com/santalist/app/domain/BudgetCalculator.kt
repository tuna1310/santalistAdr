package com.santalist.app.domain

import com.santalist.app.data.model.Gift
import com.santalist.app.data.model.Person
import kotlin.math.max

object BudgetCalculator {
    fun totalBudget(people: List<Person>): Int {
        return people.sumOf { max(0, it.budget) }
    }

    fun totalPlanned(gifts: List<Gift>): Int {
        return gifts.sumOf { max(0, it.price) }
    }

    fun totalSpent(gifts: List<Gift>): Int {
        return gifts.filter { it.status.isSpent }.sumOf { max(0, it.price) }
    }

    fun totalRemaining(people: List<Person>, gifts: List<Gift>): Int {
        return totalBudget(people) - totalPlanned(gifts)
    }

    fun isOverBudget(people: List<Person>, gifts: List<Gift>): Boolean {
        return totalRemaining(people, gifts) < 0
    }

    fun personPlanned(gifts: List<Gift>): Int {
        return gifts.sumOf { max(0, it.price) }
    }

    fun personSpent(gifts: List<Gift>): Int {
        return gifts.filter { it.status.isSpent }.sumOf { max(0, it.price) }
    }

    fun personRemaining(budget: Int, gifts: List<Gift>): Int {
        return budget - personPlanned(gifts)
    }

    fun isPersonOverBudget(budget: Int, gifts: List<Gift>): Boolean {
        return personPlanned(gifts) > budget
    }
}
