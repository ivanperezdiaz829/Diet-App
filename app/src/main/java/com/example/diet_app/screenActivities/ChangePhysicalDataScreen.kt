package com.example.diet_app.screenActivities

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.diet_app.R
import com.example.diet_app.model.Goal
import com.example.diet_app.model.Sex
import com.example.diet_app.model.getGoalInt
import com.example.diet_app.model.getSexInt
import com.example.diet_app.screenActivities.components.BackButtonLeft
import com.example.diet_app.ui.theme.DarkGreen
import com.example.diet_app.updateUserPhysicalData
import com.example.diet_app.viewModel.UserViewModel
import com.example.ui.components.*
import java.util.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun UpdatePhysicalDataScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit,
    onNext: (Map<String, Any>) -> Unit
) {
    val context = LocalContext.current

    var selectedSex by remember { mutableStateOf<Sex?>(null) }
    var selectedGoal by remember { mutableStateOf<Goal?>(null) }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var age by remember { mutableStateOf(0) }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val sexOptions = Sex.values().toList()
    val goalOptions = Goal.values().toList()
    val scrollState = rememberScrollState()

    fun validateInputs(): Boolean {
        return when {
            selectedSex == null -> {
                errorMessage = "Por favor, selecciona tu género"
                false
            }
            height.isBlank() || weight.isBlank() -> {
                errorMessage = "Por favor, completa todos los campos"
                false
            }
            height.toIntOrNull() == null || height.toInt() <= 0 -> {
                errorMessage = "Altura inválida"
                false
            }
            weight.toDoubleOrNull() == null || weight.toDouble() <= 0 -> {
                errorMessage = "Peso inválido"
                false
            }
            age <= 0 -> {
                errorMessage = "Por favor, selecciona tu fecha de nacimiento"
                false
            }
            selectedGoal == null -> {
                errorMessage = "Por favor, selecciona tu objetivo"
                false
            }
            else -> true
        }
    }

    fun handleConfirm() {
        // Validar los datos antes de proceder
        if (validateInputs()) {
            // Crear los datos actualizados para enviar al servidor
            val updatedFields = mutableMapOf<String, Any>().apply {
                put("height", height.toInt())
                put("weight", weight.toDouble())
                put("birthday", formatDate(selectedDate))
                put("sex", getSexInt(selectedSex!!))
                put("goal", getGoalInt(selectedGoal!!))
            }

            // Mover la actualización de los datos dentro de onNext
            onNext(updatedFields)
        } else {
            showErrorDialog = true
        }
    }

    BackButtonLeft(onNavigateBack = onNavigateBack)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 16.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            TitleSection()
            UsernameText(userViewModel.getUser().email)

            Text("Actualiza tus datos físicos", fontSize = 20.sp)
            Text("Ingresa tus datos personales y objetivos.", fontSize = 14.sp)

            // Selector de sexo
            Text("Género", fontSize = 16.sp)
            sexOptions.forEach { sex ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedSex = sex }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedSex == sex,
                        onClick = { selectedSex = sex }
                    )
                    Text(sex.name)
                }
            }

            // Altura
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Altura (cm)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Peso
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Peso (kg)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Fecha de nacimiento
            Text("Fecha de nacimiento", fontSize = 16.sp)
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showDatePicker(context) { date, calculatedAge ->
                            selectedDate = date
                            age = calculatedAge
                        }
                    },
                label = { Text("Selecciona tu fecha de nacimiento") }
            )

            // Objetivo
            Text("Objetivo", fontSize = 16.sp)
            goalOptions.forEach { goal ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedGoal = goal }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedGoal == goal,
                        onClick = { selectedGoal = goal }
                    )
                    Text(goal.name.replace("_", " ").replaceFirstChar { it.uppercase() })
                }
            }

            // Confirmar
            Button(
                onClick = { handleConfirm() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Confirmar")
            }
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}
