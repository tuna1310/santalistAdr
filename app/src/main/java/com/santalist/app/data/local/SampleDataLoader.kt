package com.santalist.app.data.local

import android.content.Context
import com.santalist.app.data.model.Gift
import com.santalist.app.data.model.GiftStatus
import com.santalist.app.data.model.Occasion
import com.santalist.app.data.model.Person
import com.santalist.app.notifications.ReminderScheduler
import java.util.Calendar

object SampleDataLoader {
    private const val PREFS_NAME = "santalist_prefs"
    private const val KEY_SEEDED = "has_seeded_initial_data"

    suspend fun seedInitialDataIfNeeded(context: Context, database: AppDatabase) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_SEEDED, false)) {
            return
        }

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val calDec15 = Calendar.getInstance().apply {
            set(currentYear, Calendar.DECEMBER, 15, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calDec20 = Calendar.getInstance().apply {
            set(currentYear, Calendar.DECEMBER, 20, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calDec10 = Calendar.getInstance().apply {
            set(currentYear, Calendar.DECEMBER, 10, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val mom = Person(
            name = "Mom",
            budget = 2_000_000,
            note = "The most wonderful woman in the family"
        )

        val minh = Person(
            name = "Minh (best friend)",
            budget = 800_000,
            note = "Best friend from college"
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
            personId = mom.id
        )

        val gift2 = Gift(
            title = "Herbal tea set",
            price = 300_000,
            status = GiftStatus.IDEA,
            dueDate = calDec20.timeInMillis,
            occasion = Occasion.CHRISTMAS,
            note = "Chamomile and relaxing mint",
            url = "",
            personId = mom.id
        )

        val gift3 = Gift(
            title = "Atomic Habits book",
            price = 220_000,
            status = GiftStatus.WRAPPED,
            dueDate = calDec10.timeInMillis,
            occasion = Occasion.CHRISTMAS,
            note = "Hardcover edition",
            url = "https://example.com/atomic-habits",
            personId = minh.id
        )

        database.giftDao().insertGift(gift1)
        database.giftDao().insertGift(gift2)
        database.giftDao().insertGift(gift3)

        ReminderScheduler.scheduleReminder(context, gift1, mom.name)
        ReminderScheduler.scheduleReminder(context, gift2, mom.name)
        ReminderScheduler.scheduleReminder(context, gift3, minh.name)

        prefs.edit().putBoolean(KEY_SEEDED, true).apply()
    }
}
