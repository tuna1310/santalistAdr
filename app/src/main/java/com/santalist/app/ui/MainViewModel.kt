package com.santalist.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.santalist.app.data.local.AppDatabase
import com.santalist.app.data.local.SampleDataLoader
import com.santalist.app.data.model.ActivityRecord
import com.santalist.app.data.model.Gift
import com.santalist.app.data.model.GiftStatus
import com.santalist.app.data.model.Occasion
import com.santalist.app.data.model.Person
import com.santalist.app.data.model.PersonWithGifts
import com.santalist.app.data.model.Season
import com.santalist.app.domain.AppSettings
import com.santalist.app.domain.Formatters
import com.santalist.app.domain.PipActionType
import com.santalist.app.domain.PipReminder
import com.santalist.app.domain.PipReminderGenerator
import com.santalist.app.domain.SettingsState
import com.santalist.app.notifications.ReminderScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val personDao = database.personDao()
    private val giftDao = database.giftDao()
    private val seasonDao = database.seasonDao()
    private val activityDao = database.activityDao()

    val appSettings = AppSettings(application)
    val settingsState: StateFlow<SettingsState> = appSettings.settingsFlow

    // Current Year Selection
    private val _currentYear = MutableStateFlow(settingsState.value.selectedYear)
    val currentYear: StateFlow<Int> = _currentYear.asStateFlow()

    // Seasons List
    val seasons: StateFlow<List<Season>> = seasonDao.getAllSeasons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf(Season(2026, "Christmas")))

    // People with gifts for currently selected year
    val peopleWithGifts: StateFlow<List<PersonWithGifts>> = _currentYear
        .flatMapLatest { year -> personDao.getPeopleWithGiftsForYear(year) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All gifts for currently selected year
    val giftsForYear: StateFlow<List<Gift>> = _currentYear
        .flatMapLatest { year -> giftDao.getGiftsForYear(year) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Activities for currently selected year
    val activities: StateFlow<List<ActivityRecord>> = _currentYear
        .flatMapLatest { year -> activityDao.getActivitiesForYear(year) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic Smart Reminders for Pip
    val pipReminders: StateFlow<List<PipReminder>> = combine(
        peopleWithGifts,
        settingsState
    ) { pwg, settings ->
        PipReminderGenerator.generateReminders(pwg, currency = settings.currency)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search Query State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Sort order for People tab: "NAME", "BUDGET", "DUE_DATE"
    private val _peopleSortOrder = MutableStateFlow("NAME")
    val peopleSortOrder: StateFlow<String> = _peopleSortOrder.asStateFlow()

    init {
        viewModelScope.launch {
            SampleDataLoader.initializeDefaultSeasonIfNeeded(database)
        }
    }

    fun setYear(year: Int) {
        _currentYear.value = year
        appSettings.setSelectedYear(year)
    }

    fun addSeason(year: Int, occasion: String) {
        viewModelScope.launch {
            seasonDao.insertSeason(Season(year = year, occasion = occasion))
            setYear(year)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setPeopleSortOrder(order: String) {
        _peopleSortOrder.value = order
    }

    // --- Action Methods ---

    fun insertPerson(name: String, budget: Int, note: String, year: Int = _currentYear.value) {
        viewModelScope.launch {
            val person = Person(name = name, budget = budget, note = note, year = year)
            personDao.insertPerson(person)
            logActivity(
                actionType = "ADD_PERSON",
                title = "Added $name to list",
                details = "Budget: ${Formatters.formatCurrency(budget, settingsState.value.currency)}",
                year = year
            )
        }
    }

    fun updatePerson(person: Person) {
        viewModelScope.launch {
            personDao.updatePerson(person)
            logActivity(
                actionType = "UPDATE_PERSON",
                title = "Updated ${person.name}",
                details = "Budget: ${Formatters.formatCurrency(person.budget, settingsState.value.currency)}",
                year = person.year
            )
        }
    }

    fun deletePerson(person: Person) {
        viewModelScope.launch {
            personDao.deletePerson(person)
            logActivity(
                actionType = "DELETE_PERSON",
                title = "Removed ${person.name}",
                details = "",
                year = person.year
            )
        }
    }

    fun insertGift(
        title: String,
        price: Int,
        status: GiftStatus,
        dueDate: Long?,
        occasion: Occasion,
        note: String,
        url: String,
        personId: String,
        year: Int = _currentYear.value
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
                year = year
            )
            giftDao.insertGift(gift)

            val person = personDao.getPersonById(personId)
            val recipientName = person?.name ?: ""
            if (settingsState.value.notificationsEnabled) {
                ReminderScheduler.scheduleReminder(getApplication(), gift, recipientName)
            }

            logActivity(
                actionType = "ADD_GIFT",
                title = "Added \"$title\" for $recipientName",
                details = "Price: ${Formatters.formatCurrency(price, settingsState.value.currency)} • Status: ${status.title}",
                year = year
            )
        }
    }

    fun updateGift(gift: Gift) {
        viewModelScope.launch {
            giftDao.updateGift(gift)
            val person = personDao.getPersonById(gift.personId)
            val recipientName = person?.name ?: ""
            if (settingsState.value.notificationsEnabled) {
                ReminderScheduler.scheduleReminder(getApplication(), gift, recipientName)
            }
            logActivity(
                actionType = "UPDATE_GIFT",
                title = "Updated gift \"${gift.title}\"",
                details = "Status: ${gift.status.title}",
                year = gift.year
            )
        }
    }

    fun cycleGiftStatus(gift: Gift) {
        viewModelScope.launch {
            val nextStatus = gift.status.next()
            val updated = gift.copy(status = nextStatus)
            giftDao.updateGift(updated)

            val person = personDao.getPersonById(gift.personId)
            val recipientName = person?.name ?: ""

            if (nextStatus.isCompleted) {
                ReminderScheduler.cancelReminder(getApplication(), gift.id)
            } else if (settingsState.value.notificationsEnabled) {
                ReminderScheduler.scheduleReminder(getApplication(), updated, recipientName)
            }

            logActivity(
                actionType = "UPDATE_GIFT_STATUS",
                title = "Marked \"${gift.title}\" as ${nextStatus.title}",
                details = "For $recipientName",
                year = gift.year
            )
        }
    }

    fun deleteGift(gift: Gift) {
        viewModelScope.launch {
            ReminderScheduler.cancelReminder(getApplication(), gift.id)
            giftDao.deleteGift(gift)
            logActivity(
                actionType = "DELETE_GIFT",
                title = "Removed gift \"${gift.title}\"",
                details = "",
                year = gift.year
            )
        }
    }

    fun handlePipReminderAction(reminder: PipReminder) {
        viewModelScope.launch {
            when (reminder.actionType) {
                PipActionType.MARK_ORDERED -> {
                    reminder.targetGiftId?.let { giftId ->
                        val gift = giftDao.getGiftById(giftId)
                        if (gift != null) {
                            val updated = gift.copy(status = GiftStatus.ORDERED)
                            giftDao.updateGift(updated)
                            logActivity("UPDATE_GIFT_STATUS", "Marked \"${gift.title}\" as Ordered", "", gift.year)
                        }
                    }
                }
                PipActionType.MARK_RECEIVED -> {
                    reminder.targetGiftId?.let { giftId ->
                        val gift = giftDao.getGiftById(giftId)
                        if (gift != null) {
                            val updated = gift.copy(status = GiftStatus.RECEIVED)
                            giftDao.updateGift(updated)
                            ReminderScheduler.cancelReminder(getApplication(), gift.id)
                            logActivity("UPDATE_GIFT_STATUS", "Marked \"${gift.title}\" as Received", "", gift.year)
                        }
                    }
                }
                else -> { /* Navigation handled in UI */ }
            }
        }
    }

    private suspend fun logActivity(
        actionType: String,
        title: String,
        details: String,
        year: Int = _currentYear.value
    ) {
        activityDao.insertActivity(
            ActivityRecord(
                actionType = actionType,
                title = title,
                details = details,
                timestamp = System.currentTimeMillis(),
                year = year
            )
        )
    }

    // --- Settings / Data Management ---

    fun setCurrency(currency: String) = appSettings.setCurrency(currency)
    fun setLanguage(language: String) = appSettings.setLanguage(language)
    fun setNotificationsEnabled(enabled: Boolean) = appSettings.setNotificationsEnabled(enabled)
    fun setThemeMode(mode: String) = appSettings.setThemeMode(mode)

    fun loadSampleData() {
        viewModelScope.launch {
            SampleDataLoader.loadSampleData(getApplication(), database, _currentYear.value)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            SampleDataLoader.clearAllData(database)
        }
    }

    fun exportDataAsJson(): String {
        val root = JSONObject()
        val pwgList = peopleWithGifts.value
        val peopleArray = JSONArray()

        for (pwg in pwgList) {
            val pObj = JSONObject().apply {
                put("id", pwg.person.id)
                put("name", pwg.person.name)
                put("budget", pwg.person.budget)
                put("note", pwg.person.note)
                put("year", pwg.person.year)
                val giftsArray = JSONArray()
                for (g in pwg.gifts) {
                    giftsArray.put(JSONObject().apply {
                        put("id", g.id)
                        put("title", g.title)
                        put("price", g.price)
                        put("status", g.status.name)
                        put("dueDate", g.dueDate)
                        put("occasion", g.occasion.name)
                        put("note", g.note)
                        put("url", g.url)
                    })
                }
                put("gifts", giftsArray)
            }
            peopleArray.put(pObj)
        }
        root.put("year", _currentYear.value)
        root.put("people", peopleArray)
        root.put("exportedAt", System.currentTimeMillis())
        return root.toString(2)
    }
}
