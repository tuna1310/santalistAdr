package com.santalist.app.ui.timeline

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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TimelineFilter(val title: String) {
    ALL("All"),
    PENDING("Pending"),
    OVERDUE("Overdue"),
    COMPLETED("Completed")
}

data class TimelineItem(
    val gift: Gift,
    val recipientName: String
)

class TimelineViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val personDao = database.personDao()
    private val giftDao = database.giftDao()

    private val _selectedFilter = MutableStateFlow(TimelineFilter.ALL)
    val selectedFilter: StateFlow<TimelineFilter> = _selectedFilter.asStateFlow()

    val peopleWithGifts: StateFlow<List<PersonWithGifts>> = personDao
        .getAllPeopleWithGifts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val timelineItems: StateFlow<List<TimelineItem>> = combine(
        peopleWithGifts,
        selectedFilter
    ) { pwgList, filter ->
        val items = mutableListOf<TimelineItem>()
        for (pwg in pwgList) {
            for (gift in pwg.gifts) {
                items.add(TimelineItem(gift = gift, recipientName = pwg.person.name))
            }
        }

        // Sort by due date ascending, null due dates at the end
        val sorted = items.sortedWith { o1, o2 ->
            val d1 = o1.gift.dueDate
            val d2 = o2.gift.dueDate
            when {
                d1 != null && d2 != null -> d1.compareTo(d2)
                d1 != null && d2 == null -> -1
                d1 == null && d2 != null -> 1
                else -> o1.gift.title.compareTo(o2.gift.title)
            }
        }

        // Filter
        when (filter) {
            TimelineFilter.ALL -> sorted
            TimelineFilter.PENDING -> sorted.filter { !it.gift.status.isCompleted }
            TimelineFilter.OVERDUE -> sorted.filter { it.gift.isOverdue() }
            TimelineFilter.COMPLETED -> sorted.filter { it.gift.status.isCompleted }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setFilter(filter: TimelineFilter) {
        _selectedFilter.value = filter
    }

    fun advanceStatus(gift: Gift, recipientName: String) {
        viewModelScope.launch {
            val nextStatus = gift.status.next()
            val updated = gift.copy(status = nextStatus)
            giftDao.updateGift(updated)
            ReminderScheduler.scheduleReminder(getApplication(), updated, recipientName)
        }
    }

    fun setStatus(gift: Gift, newStatus: GiftStatus, recipientName: String) {
        viewModelScope.launch {
            val updated = gift.copy(status = newStatus)
            giftDao.updateGift(updated)
            ReminderScheduler.scheduleReminder(getApplication(), updated, recipientName)
        }
    }

    fun deleteGift(gift: Gift) {
        viewModelScope.launch {
            ReminderScheduler.cancelReminder(getApplication(), gift.id)
            giftDao.deleteGift(gift)
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
}
