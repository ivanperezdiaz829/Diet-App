package com.example.diet_app.screenActivities

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    onNext: () -> Unit
) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") } // Edad inicial

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

            TitleSection("What is your", "age?","We will use this data to give you a better diet type for you")

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier
                    .width(335.dp)
                    .padding(vertical = 7.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Number Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(105.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkOverlay),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = age, // Edad dinámica
                        style = TextStyle(
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.44).sp,
                            color = DarkGreen
                        )
                    )
                }

                // Barra de selección de fecha
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(LightGray)
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Fecha seleccionada
                    Text(
                        text = selectedDate, // Fecha dinámica con mes en inglés
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = DarkGreen
                        )
                    )
                    // Icono de calendario
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_calendar),
                        contentDescription = "Calendar",
                        tint = DarkGreen,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                showDatePicker(context) { date, calculatedAge ->
                                    selectedDate = date // Actualiza la fecha
                                    age = calculatedAge.toString() // Actualiza la edad
                                }
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                enabled = selectedDate.isNotEmpty() && age.isNotEmpty(),
                onClick = { }
            )

        }
    }
}

// Función para mostrar el DatePickerDialog con cálculo de edad y mes en inglés
private fun showDatePicker(
    context: android.content.Context,
    onDateSelected: (String, Int) -> Unit
) {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    val months = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val age = currentYear - selectedYear - if (selectedMonth > currentMonth || (selectedMonth == currentMonth && selectedDay > currentDay)) 1 else 0
            val formattedDate = "${months[selectedMonth]} / $selectedDay / $selectedYear"
            onDateSelected(formattedDate, age) // Devuelve la fecha con mes en inglés y la edad calculada
        },
        currentYear, currentMonth, currentDay
    ).show()
}
