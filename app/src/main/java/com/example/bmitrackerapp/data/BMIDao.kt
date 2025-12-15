package com.example.bmitrackerapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BMIDao {
    @Query("SELECT * FROM BmiHistory ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<BMIRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: BMIRecord)

    @Delete
    suspend fun deleteRecord(record: BMIRecord)
}