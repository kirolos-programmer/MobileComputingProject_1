package com.example.bmitrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import com.example.bmitrackerapp.data.AppDatabase
import com.example.bmitrackerapp.data.BMIRecord
import com.example.bmitrackerapp.ui.BMIViewModel
import com.example.bmitrackerapp.ui.classificationColor
import com.example.bmitrackerapp.ui.theme.BMITrackerTheme


class BMIViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BMIViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BMIViewModel(db.bmiDao()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : ComponentActivity() {

    private val appDB by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "BmiTrackerDB").build()
    }

    private val viewModel: BMIViewModel by viewModels {
        BMIViewModelFactory(appDB)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BMITrackerTheme {
                BmiTrackerScreen(vm = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiTrackerScreen(vm: BMIViewModel) {
    val history by vm.historyRecords.collectAsStateWithLifecycle()

    var weightInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }

    val isInputValid = weightInput.toDoubleOrNull() != null && weightInput.toDouble() > 0 &&
            heightInput.toDoubleOrNull() != null && heightInput.toDouble() > 0

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("BMI Tracker & Calculator") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it.filter { it.isDigit() || it == '.' } },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = heightInput,
                    onValueChange = { heightInput = it.filter { it.isDigit() || it == '.' } },
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = { vm.calculateAndSave(weightInput, heightInput) },
                enabled = isInputValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Calculate BMI & Save")
            }

            Divider(Modifier.padding(vertical = 16.dp))

            Text(
                text = "Calculation History",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(history, key = { it.recordId }) { record ->
                    BMIRecordRow(record = record, onDelete = { vm.deleteRecord(it) })
                    Divider(Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
fun BMIRecordRow(record: BMIRecord, onDelete: (BMIRecord) -> Unit) {
    val resultColor = classificationColor(record.classification)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "BMI: ${record.bmiValue}",
                style = MaterialTheme.typography.headlineSmall,
                color = resultColor
            )
            Text(
                text = record.classification,
                style = MaterialTheme.typography.titleSmall,
                color = resultColor
            )
            Text(
                text = "on ${record.formattedDate()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = { onDelete(record) }) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Delete Record",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}