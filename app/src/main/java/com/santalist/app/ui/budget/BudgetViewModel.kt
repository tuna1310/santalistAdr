package com.santalist.app.ui.budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.santalist.app.data.local.AppDatabase
import com.santalist.app.data.model.PersonWithGifts
import com.santalist.app.domain.BudgetCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class BudgetUiState(
    val peopleWithGifts: List<PersonWithGifts> = emptyList(),
    val totalBudget: Int = 0,
    val totalPlanned: Int = 0,
    val totalSpent: Int = 0,
    val totalRemaining: Int = 0,
    val isOverallOverBudget: Boolean = false
)

class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val personDao = database.personDao()

    val uiState: StateFlow<BudgetUiState> = personDao
        .getAllPeopleWithGifts()
        .map { list ->
            val people = list.map { it.person }
            val gifts = list.flatMap { it.gifts }

            val totalBudget = BudgetCalculator.totalBudget(people)
            val totalPlanned = BudgetCalculator.totalPlanned(gifts)
            val totalSpent = BudgetCalculator.totalSpent(gifts)
            val totalRemaining = BudgetCalculator.totalRemaining(people, gifts)
            val isOverallOverBudget = BudgetCalculator.isOverBudget(people, gifts)

            BudgetUiState(
                peopleWithGifts = list,
                totalBudget = totalBudget,
                totalPlanned = totalPlanned,
                totalSpent = totalSpent,
                totalRemaining = totalRemaining,
                isOverallOverBudget = isOverallOverBudget
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BudgetUiState()
        )
}
