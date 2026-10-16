package com.santalist.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.santalist.app.data.model.Person
import com.santalist.app.data.model.PersonWithGifts
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM people ORDER BY name ASC")
    fun getAllPeople(): Flow<List<Person>>

    @Transaction
    @Query("SELECT * FROM people ORDER BY name ASC")
    fun getAllPeopleWithGifts(): Flow<List<PersonWithGifts>>

    @Transaction
    @Query("SELECT * FROM people WHERE id = :id")
    fun getPersonWithGifts(id: String): Flow<PersonWithGifts?>

    @Query("SELECT * FROM people WHERE id = :id")
    suspend fun getPersonById(id: String): Person?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: Person)

    @Update
    suspend fun updatePerson(person: Person)

    @Delete
    suspend fun deletePerson(person: Person)
}
