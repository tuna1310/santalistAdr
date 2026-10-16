package com.santalist.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.santalist.app.data.model.Season
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonDao {
    @Query("SELECT * FROM seasons ORDER BY year DESC")
    fun getAllSeasons(): Flow<List<Season>>

    @Query("SELECT * FROM seasons WHERE year = :year LIMIT 1")
    suspend fun getSeasonByYear(year: Int): Season?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeason(season: Season)

    @Delete
    suspend fun deleteSeason(season: Season)

    @Query("DELETE FROM seasons")
    suspend fun clearAll()
}
