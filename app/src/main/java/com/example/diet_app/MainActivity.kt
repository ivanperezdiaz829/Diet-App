package com.example.diet_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diet_app.ui.theme.DietappTheme
import androidx.compose.material3.Button

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DietForm()
        }
    }
}

@Composable
fun DietForm() {
    var minCalories by remember { mutableStateOf("") }
    var maxCalories by remember { mutableStateOf("") }
    var minFat by remember { mutableStateOf("") }
    var maxFat by remember { mutableStateOf("") }
    var minSalt by remember { mutableStateOf("") }
    var maxSalt by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Configurar valores nutricionales", style = MaterialTheme.typography.titleLarge)

        InputField(label = "Kcal mínimas", value = minCalories) { minCalories = it }
        InputField(label = "Kcal máximas", value = maxCalories) { maxCalories = it }
        InputField(label = "Grasa mínima (g)", value = minFat) { minFat = it }
        InputField(label = "Grasa máxima (g)", value = maxFat) { maxFat = it }
        InputField(label = "Sal mínima (g)", value = minSalt) { minSalt = it }
        InputField(label = "Sal máxima (g)", value = maxSalt) { maxSalt = it }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Aquí puedes procesar los valores ingresados
            println("Min Kcal: $minCalories, Max Kcal: $maxCalories")
        }) {
            Text("Generar dieta")
        }
    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}