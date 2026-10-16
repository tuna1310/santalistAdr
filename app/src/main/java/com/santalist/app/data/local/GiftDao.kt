package com.santalist.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.santalist.app.data.model.Gift
import kotlinx.coroutines.flow.Flow

@Dao
interface GiftDao {
    @Query("SELECT * FROM gifts")
    fun getAllGifts(): Flow<List<Gift>>

    @Query("SELECT * FROM gifts WHERE year = :year")
    fun getGiftsForYear(year: Int): Flow<List<Gift>>

    @Query("SELECT * FROM gifts WHERE personId = :personId")
    fun getGiftsForPerson(personId: String): Flow<List<Gift>>

    @Query("SELECT * FROM gifts WHERE id = :id")
    suspend fun getGiftById(id: String): Gift?

    @Query("SELECT * FROM gifts WHERE year = :year AND (title LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%')")
    fun searchGifts(year: Int, query: String): Flow<List<Gift>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGift(gift: Gift)

    @Update
    suspend fun updateGift(gift: Gift)

    @Delete
    suspend fun deleteGift(gift: Gift)

    @Query("DELETE FROM gifts")
    suspend fun clearAll()
}
