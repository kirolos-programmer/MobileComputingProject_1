package com.example.bmitrackerapp.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bmitrackerapp.data.BMIDao
import com.example.bmitrackerapp.data.BMIRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.pow

class BMIViewModel(private val dao: BMIDao) : ViewModel() {

    val historyRecords: StateFlow<List<BMIRecord>> =
        dao.getAllRecords()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun calculateBMI(weightKg: Double, heightMeters: Double): Pair<Double, String> {
        if (heightMeters <= 0.0 || weightKg <= 0.0) return Pair(0.0, "Invalid Input")

        val bmi = weightKg / heightMeters.pow(2)
        val roundedBmi = String.format("%.2f", bmi).toDouble()

        val classification = when {
            bmi < 18.5 -> "Underweight"
            bmi >= 18.5 && bmi <= 24.9 -> "Normal weight"
            bmi >= 25.0 && bmi <= 29.9 -> "Overweight"
            else -> "Obesity"
        }

        return Pair(roundedBmi, classification)
    }

    fun calculateAndSave(weight: String, height: String) {
        val weightKg = weight.toDoubleOrNull()
        val heightMeters = height.toDoubleOrNull()?.div(100)

        if (weightKg == null || weightKg <= 0 || heightMeters == null || heightMeters <= 0) return

        val (bmiValue, classification) = calculateBMI(weightKg, heightMeters)

        viewModelScope.launch {
            val newRecord = BMIRecord(
                weightKg = weightKg,
                heightMeters = heightMeters,
                bmiValue = bmiValue,
                classification = classification
            )
            dao.insertRecord(newRecord)
        }
    }

    fun deleteRecord(record: BMIRecord) {
        viewModelScope.launch {
            dao.deleteRecord(record)
        }
    }
}

fun classificationColor(classification: String): Color {
    return when (classification) {
        "Underweight" -> Color(0xFF4FC3F7)
        "Normal weight" -> Color(0xFF66BB6A)
        "Overweight" -> Color(0xFFFFB300)
        "Obesity" -> Color(0xFFE53935)
        else -> Color.Gray
    }
}