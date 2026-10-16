package com.santalist.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.santalist.app.data.local.AppDatabase
import com.santalist.app.data.local.GiftDao
import com.santalist.app.data.local.PersonDao
import com.santalist.app.data.model.Gift
import com.santalist.app.data.model.GiftStatus
import com.santalist.app.data.model.Occasion
import com.santalist.app.data.model.Person
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomCascadeDeleteTest {

    private lateinit var db: AppDatabase
    private lateinit var personDao: PersonDao
    private lateinit var giftDao: GiftDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        personDao = db.personDao()
        giftDao = db.giftDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun deletePerson_cascadesGiftsDeletion() = runBlocking {
        val person = Person(id = 1L, name = "Mom", budget = 2_000_000, note = "Mother")
        personDao.insertPerson(person)

        val gift1 = Gift(
            title = "Warm Wool Scarf",
            price = 450_000,
            status = GiftStatus.WRAPPED,
            occasion = Occasion.CHRISTMAS,
            personId = 1L
        )
        val gift2 = Gift(
            title = "Jasmine Green Tea Set",
            price = 280_000,
            status = GiftStatus.ORDERED,
            occasion = Occasion.CHRISTMAS,
            personId = 1L
        )

        giftDao.insertGift(gift1)
        giftDao.insertGift(gift2)

        val giftsBefore = giftDao.getGiftsForPerson(1L).first()
        assertEquals(2, giftsBefore.size)

        // Delete person
        personDao.deletePerson(person)

        // Verify person is deleted
        val personAfter = personDao.getPersonById(1L)
        assertTrue(personAfter == null)

        // Verify cascade deletion of gifts
        val giftsAfter = giftDao.getGiftsForPerson(1L).first()
        assertTrue(giftsAfter.isEmpty())
    }
}
