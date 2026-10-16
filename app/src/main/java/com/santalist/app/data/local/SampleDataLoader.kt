package com.santalist.app.data.local

import android.content.Context
import com.santalist.app.data.model.ActivityRecord
import com.santalist.app.data.model.Gift
import com.santalist.app.data.model.GiftStatus
import com.santalist.app.data.model.Occasion
import com.santalist.app.data.model.Person
import com.santalist.app.data.model.Season
import com.santalist.app.notifications.ReminderScheduler
import java.util.Calendar

object SampleDataLoader {
    private const val PREFS_NAME = "santalist_prefs"
    private const val KEY_SEEDED = "has_seeded_initial_data"

    suspend fun initializeDefaultSeasonIfNeeded(database: AppDatabase) {
        val defaultSeason = database.seasonDao().getSeasonByYear(2026)
        if (defaultSeason == null) {
            database.seasonDao().insertSeason(Season(year = 2026, occasion = "Christmas"))
        }
    }

    suspend fun seedInitialDataIfNeeded(context: Context, database: AppDatabase, year: Int = 2026) {
        initializeDefaultSeasonIfNeeded(database)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_SEEDED, false)) {
            return
        }
        // By default, start with empty state to match App Store reference screenshots!
        // We set KEY_SEEDED = true so we don't automatically overwrite user data.
        prefs.edit().putBoolean(KEY_SEEDED, true).apply()
    }

    suspend fun loadSampleData(context: Context, database: AppDatabase, year: Int = 2026) {
        database.seasonDao().insertSeason(Season(year = year, occasion = "Christmas"))

        val calDec15 = Calendar.getInstance().apply {
            set(year, Calendar.DECEMBER, 15, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calDec20 = Calendar.getInstance().apply {
            set(year, Calendar.DECEMBER, 20, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calDec10 = Calendar.getInstance().apply {
            set(year, Calendar.DECEMBER, 10, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val mom = Person(
            name = "Mom",
            budget = 2_000_000,
            note = "The most wonderful woman in the family",
            year = year
        )

        val minh = Person(
            name = "Minh (best friend)",
            budget = 800_000,
            note = "Best friend from college",
            year = year
        )

        database.personDao().insertPerson(mom)
        database.personDao().insertPerson(minh)

        val gift1 = Gift(
            title = "Cashmere scarf",
            price = 650_000,
            status = GiftStatus.ORDERED,
            dueDate = calDec15.timeInMillis,
            occasion = Occasion.CHRISTMAS,
            note = "Soft cream color, warm wool",
            url = "https://example.com/scarf",
            personId = mom.id,
            year = year
        )

        val gift2 = Gift(
            title = "Herbal tea set",
            price = 300_000,
            status = GiftStatus.IDEA,
            dueDate = calDec20.timeInMillis,
            occasion = Occasion.CHRISTMAS,
            note = "Chamomile and relaxing mint",
            url = "",
            personId = mom.id,
            year = year
        )

        val gift3 = Gift(
            title = "Atomic Habits book",
            price = 220_000,
            status = GiftStatus.WRAPPED,
            dueDate = calDec10.timeInMillis,
            occasion = Occasion.CHRISTMAS,
            note = "Hardcover edition",
            url = "https://example.com/atomic-habits",
            personId = minh.id,
            year = year
        )

        database.giftDao().insertGift(gift1)
        database.giftDao().insertGift(gift2)
        database.giftDao().insertGift(gift3)

        // Add sample activity records
        database.activityDao().insertActivity(
            ActivityRecord(
                actionType = "ADD_PERSON",
                title = "Added Mom to list",
                details = "Budget: 2,000,000 ₫",
                timestamp = System.currentTimeMillis() - 7200000,
                year = year
            )
        )
        database.activityDao().insertActivity(
            ActivityRecord(
                actionType = "ADD_GIFT",
                title = "Added Cashmere scarf for Mom",
                details = "Price: 650,000 ₫",
                timestamp = System.currentTimeMillis() - 3600000,
                year = year
            )
        )
        database.activityDao().insertActivity(
            ActivityRecord(
                actionType = "UPDATE_GIFT_STATUS",
                title = "Marked Cashmere scarf as Ordered",
                details = "Status: Ordered",
                timestamp = System.currentTimeMillis() - 1800000,
                year = year
            )
        )

        ReminderScheduler.scheduleReminder(context, gift1, mom.name)
        ReminderScheduler.scheduleReminder(context, gift2, mom.name)
        ReminderScheduler.scheduleReminder(context, gift3, minh.name)
    }

    suspend fun clearAllData(database: AppDatabase) {
        database.giftDao().clearAll()
        database.personDao().clearAll()
        database.activityDao().clearAll()
        // Re-insert default season 2026
        initializeDefaultSeasonIfNeeded(database)
    }
}
