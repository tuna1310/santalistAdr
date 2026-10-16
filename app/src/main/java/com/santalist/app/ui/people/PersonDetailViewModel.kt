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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PersonDetailViewModel(
    application: Application,
    private val personId: String
) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val personDao = database.personDao()
    private val giftDao = database.giftDao()

    val personWithGifts: StateFlow<PersonWithGifts?> = personDao
        .getPersonWithGifts(personId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun updatePerson(name: String, budget: Int, note: String) {
        viewModelScope.launch {
            val current = personWithGifts.value?.person ?: return@launch
            val updated = current.copy(name = name, budget = budget, note = note)
            personDao.updatePerson(updated)
        }
    }

    fun deletePerson() {
        viewModelScope.launch {
            val pwg = personWithGifts.value ?: return@launch
            pwg.gifts.forEach { gift ->
                ReminderScheduler.cancelReminder(getApplication(), gift.id)
            }
            personDao.deletePerson(pwg.person)
        }
    }

    fun addGift(
        title: String,
        price: Int,
        status: GiftStatus,
        dueDate: Long?,
        occasion: Occasion,
        note: String,
        url: String
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
                personId = personId,
                year = personWithGifts.value?.person?.year ?: 2026
            )
            giftDao.insertGift(gift)
            val recipient = personWithGifts.value?.person?.name ?: ""
            ReminderScheduler.scheduleReminder(getApplication(), gift, recipient)
        }
    }

    fun updateGift(
        gift: Gift,
        title: String,
        price: Int,
        status: GiftStatus,
        dueDate: Long?,
        occasion: Occasion,
        note: String,
        url: String,
        targetPersonId: String
    ) {
        viewModelScope.launch {
            val updated = gift.copy(
                title = title,
                price = price,
                status = status,
                dueDate = dueDate,
                occasion = occasion,
                note = note,
                url = url,
                personId = targetPersonId
            )
            giftDao.updateGift(updated)
            val recipient = personDao.getPersonById(targetPersonId)?.name ?: ""
            ReminderScheduler.scheduleReminder(getApplication(), updated, recipient)
        }
    }

    fun advanceStatus(gift: Gift) {
        viewModelScope.launch {
            val nextStatus = gift.status.next()
            val updated = gift.copy(status = nextStatus)
            giftDao.updateGift(updated)
            val recipient = personWithGifts.value?.person?.name ?: ""
            ReminderScheduler.scheduleReminder(getApplication(), updated, recipient)
        }
    }

    fun setStatus(gift: Gift, newStatus: GiftStatus) {
        viewModelScope.launch {
            val updated = gift.copy(status = newStatus)
            giftDao.updateGift(updated)
            val recipient = personWithGifts.value?.person?.name ?: ""
            ReminderScheduler.scheduleReminder(getApplication(), updated, recipient)
        }
    }

    fun deleteGift(gift: Gift) {
        viewModelScope.launch {
            ReminderScheduler.cancelReminder(getApplication(), gift.id)
            giftDao.deleteGift(gift)
        }
    }
}
