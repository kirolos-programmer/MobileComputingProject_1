package com.example.bmitrackerapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "BmiHistory")
data class BMIRecord(
    @PrimaryKey(autoGenerate = true)
    val recordId: Int = 0,
    val weightKg: Double,
    val heightMeters: Double,
    val bmiValue: Double,
    val classification: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun formattedDate(): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
}