package com.santalist.app.ui.people

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.santalist.app.data.local.AppDatabase
import com.santalist.app.data.model.Gift
import com.santalist.app.data.model.GiftStatus
import com.santalist.app.data.model.Occasion
import com.santalist.app.data.model.Person
import com.santalist.app.data.model.PersonWithGifts
import com.santalist.app.notifications.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PeopleViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val personDao = database.personDao()
    private val giftDao = database.giftDao()

    val peopleWithGifts: StateFlow<List<PersonWithGifts>> = personDao
        .getAllPeopleWithGifts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addPerson(name: String, budget: Int, note: String) {
        viewModelScope.launch {
            val person = Person(name = name, budget = budget, note = note)
            personDao.insertPerson(person)
        }
    }

    fun updatePerson(person: Person, name: String, budget: Int, note: String) {
        viewModelScope.launch {
            val updated = person.copy(name = name, budget = budget, note = note)
            personDao.updatePerson(updated)
        }
    }

    fun deletePerson(personWithGifts: PersonWithGifts) {
        viewModelScope.launch {
            personWithGifts.gifts.forEach { gift ->
                ReminderScheduler.cancelReminder(getApplication(), gift.id)
            }
            personDao.deletePerson(personWithGifts.person)
        }
    }

    fun addGift(
        title: String,
        price: Int,
        status: GiftStatus,
        dueDate: Long?,
        occasion: Occasion,
        note: String,
        url: String,
        personId: String
    ) {
        viewModelScope.launch {
            val gift = Gift(
                title = title,
                price = price,
                status = status,
                dueDate = dueDate,
                occasion = occasion,
                note = note,
                url = url,
                personId = personId
            )
            giftDao.insertGift(gift)
            val recipient = personDao.getPersonById(personId)?.name ?: ""
            ReminderScheduler.scheduleReminder(getApplication(), gift, recipient)
        }
    }
}
