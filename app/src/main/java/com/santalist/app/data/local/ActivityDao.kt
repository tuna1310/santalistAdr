package com.santalist.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.santalist.app.data.model.ActivityRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY timestamp DESC")
    fun getAllActivities(): Flow<List<ActivityRecord>>

    @Query("SELECT * FROM activities WHERE year = :year ORDER BY timestamp DESC")
    fun getActivitiesForYear(year: Int): Flow<List<ActivityRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityRecord)

    @Query("DELETE FROM activities")
    suspend fun clearAll()
}
