package com.example.diet_app.screenActivities

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import com.example.diet_app.screenActivities.components.BackButton

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
            age <= 17 -> {
                errorMessage = "Por favor, una fecha de nacimiento válida"
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
            BackButton(onNavigateBack = onNavigateBack)

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
            var isDateValid by remember { mutableStateOf(true) }

            Text("Fecha de nacimiento", fontSize = 16.sp)
            TextField(
                value = selectedDate,
                onValueChange = { newValue ->
                    if (isValidDateInput(newValue)) {
                        selectedDate = newValue
                        isDateValid = true
                        if (newValue.length == 10 && isValidDateFormat(newValue)) {
                            age = calculateAge(newValue)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDateValid) Color.Transparent else Color.Red.copy(alpha = 0.1f)),
                textStyle = LocalTextStyle.current.copy(
                    color = if (isDateValid) Color.Black else Color.Red
                ),
                placeholder = { Text("dd/mm/aaaa", color = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = !isDateValid,
                trailingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_calendar),
                        contentDescription = "Calendar",
                        modifier = Modifier.clickable {
                            showDatePicker(context) { date, calculatedAge ->
                                selectedDate = date
                                age = calculatedAge
                                isDateValid = true
                            }
                        }
                    )
                },
                singleLine = true
            )


// Asegúrate de agregar estas funciones auxiliares al mismo archivo:

            // Función para validar el input mientras se escribe
            fun isValidDateInput(input: String): Boolean {
                if (input.isEmpty()) return true
                if (input.length > 10) return false
                if (!input.matches(Regex("^[0-9/]*$"))) return false

                // Validar posiciones de las barras
                if (input.length >= 3 && input[2] != '/') return false
                if (input.length >= 6 && input[5] != '/') return false

                // Validar día (01-31)
                if (input.length >= 2) {
                    val day = input.substring(0, 2).toIntOrNull() ?: return false
                    if (day < 1 || day > 31) return false
                }
                // Validar mes (01-12)
                if (input.length >= 5) {
                    val month = input.substring(3, 5).toIntOrNull() ?: return false
                    if (month < 1 || month > 12) return false
                }

                return true
            }

            // Validación del formato completo "dd/mm/yyyy"
            fun isValidDateFormat(date: String): Boolean {
                val regex = Regex("^\\d{2}/\\d{2}/\\d{4}$")
                if (!regex.matches(date)) return false

                val parts = date.split("/")
                val day = parts[0].toInt()
                val month = parts[1].toInt()
                val year = parts[2].toInt()

                if (month < 1 || month > 12) return false
                if (day < 1 || day > 31) return false

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month - 1)

                val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                if (day > maxDay) return false

                // Validar que no sea fecha futura
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
                val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

                if (year > currentYear) return false
                if (year == currentYear && month > currentMonth) return false
                if (year == currentYear && month == currentMonth && day > currentDay) return false

                return true
            }

            // Función para calcular la edad
            fun calculateAge(date: String): Int {
                val parts = date.split("/")
                if (parts.size != 3) return 0

                val birthDay = parts[0].toInt()
                val birthMonth = parts[1].toInt() - 1
                val birthYear = parts[2].toInt()

                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)
                val currentMonth = calendar.get(Calendar.MONTH)
                val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

                return currentYear - birthYear - if (birthMonth > currentMonth ||
                    (birthMonth == currentMonth && birthDay > currentDay)) 1 else 0
            }

            // Función para mostrar el DatePickerDialog
            fun showDatePicker(
                context: android.content.Context,
                onDateSelected: (String, Int) -> Unit
            ) {
                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)
                val currentMonth = calendar.get(Calendar.MONTH)
                val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

                android.app.DatePickerDialog(
                    context,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        val formattedDate = String.format(
                            "%02d/%02d/%d",
                            selectedDay,
                            selectedMonth + 1,
                            selectedYear
                        )
                        val age = calculateAge(formattedDate)
                        onDateSelected(formattedDate, age)
                    },
                    currentYear, currentMonth, currentDay
                ).apply {
                    datePicker.maxDate = calendar.timeInMillis
                }.show()
            }

            // Función para formatear la fecha al formato yyyy-mm-dd
            fun formatDate(inputDate: String): String {
                val parts = inputDate.split("/")
                return "${parts[2]}-${parts[1]}-${parts[0]}"
            }
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
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
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
