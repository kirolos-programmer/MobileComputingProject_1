package com.example.bmitrackerapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BMIRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bmiDao(): BMIDao
}