package com.example.diet_app.screenActivities

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diet_app.ui.theme.DarkGreen
import com.example.diet_app.ui.theme.LightGray
import com.example.diet_app.R
import com.example.diet_app.ui.theme.DarkOverlay
import java.util.Calendar
import com.example.diet_app.screenActivities.components.Header
import com.example.diet_app.screenActivities.components.NextButton
import com.example.diet_app.screenActivities.components.TitleSection

@Composable
fun AgeSelectionScreen(
    onNavigateBack: () -> Unit,
    onSkip: () -> Unit,
    onNext: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Obtener la fecha actual en formato "dd/mm/yyyy"
    val defaultDate = String.format("%02d/%02d/%d",
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.YEAR)
    )

    var selectedDate by remember { mutableStateOf(defaultDate) }
    var age by remember { mutableIntStateOf(calculateAge(defaultDate)) }
    var isDateValid by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(onNavigateBack, onSkip)

            Spacer(modifier = Modifier.height(120.dp))

            TitleSection(
                "¿Cuál es tu",
                "fecha de nacimiento?",
                "Usaremos estos datos para darte una mejor dieta"
            )

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier
                    .width(335.dp)
                    .padding(vertical = 7.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Edad calculada
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(105.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkOverlay),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = age.toString(),
                        style = TextStyle(
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                    )
                }
// Campo de entrada de fecha editable
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
                        .height(60.dp)
                        .background(if (isDateValid) LightGray else Color.Red.copy(alpha = 0.1f)),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = if (isDateValid) DarkGreen else Color.Red
                    ),
                    placeholder = { Text("dd/mm/aaaa", color = Color.Gray) }, // Fixed placeholder syntax
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Added keyboardOptions
                    isError = !isDateValid, // Added isError parameter
                    trailingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_calendar),
                            contentDescription = "Calendar",
                            tint = DarkGreen,
                            modifier = Modifier.clickable {
                                showDatePicker(context) { date, calculatedAge ->
                                    selectedDate = date
                                    age = calculatedAge
                                    isDateValid = true
                                }
                            }
                        )
                    },
                    singleLine = true // Added singleLine parameter
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                enabled = age >= 18 && isDateValid && isValidDateFormat(selectedDate),
                onClick = { onNext(formatDate(selectedDate)) }
            )
        }
    }
}

// Función para validar el input mientras se escribe
fun isValidDateInput(input: String): Boolean {
    // Permitir borrado
    if (input.isEmpty()) return true

    // Validar longitud máxima
    if (input.length > 10) return false

    // Validar caracteres permitidos (números y /)
    if (!input.matches(Regex("^[0-9/]*$"))) return false

    // Validar posiciones de las barras
    if (input.length >= 3 && input[2] != '/') return false
    if (input.length >= 6 && input[5] != '/') return false

    // Validar día (01-31)
    if (input.length >= 2) {
        val day = input.substring(0, 2).toIntOrNull() ?: return false
        if (day < 1 || day > 31) return false
    }

    // Validar mes (01-12) cuando esté completo
    if (input.length >= 5) {
        val month = input.substring(3, 5).toIntOrNull() ?: return false
        if (month < 1 || month > 12) return false
    }

    // Validar año cuando esté completo
    if (input.length == 10) {
        val year = input.substring(6, 10).toIntOrNull() ?: return false
        if (year < 1900 || year > Calendar.getInstance().get(Calendar.YEAR)) return false
    }

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

// Validación del formato completo "dd/mm/yyyy"
fun isValidDateFormat(date: String): Boolean {
    val regex = Regex("^\\d{2}/\\d{2}/\\d{4}$")
    if (!regex.matches(date)) return false

    val parts = date.split("/")
    val day = parts[0].toInt()
    val month = parts[1].toInt()
    val year = parts[2].toInt()

    // Validación de fecha real (días por mes, años bisiestos, etc.)
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

// Función para mostrar el DatePickerDialog
fun showDatePicker(
    context: android.content.Context,
    onDateSelected: (String, Int) -> Unit
) {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%02d/%02d/%d",
                selectedDay,
                selectedMonth + 1,
                selectedYear
            )
            val age = calculateAge(formattedDate)
            onDateSelected(formattedDate, age)
        },
        currentYear, currentMonth, currentDay
    ).apply {
        // Establecer fecha máxima como hoy
        datePicker.maxDate = calendar.timeInMillis
    }.show()
}

// Función para formatear la fecha al formato yyyy-mm-dd
fun formatDate(inputDate: String): String {
    val parts = inputDate.split("/")
    return "${parts[2]}-${parts[1]}-${parts[0]}"
}