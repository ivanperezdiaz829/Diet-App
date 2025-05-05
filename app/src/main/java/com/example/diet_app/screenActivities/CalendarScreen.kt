package com.example.diet_app.screenActivities

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.example.diet_app.R
import com.example.diet_app.getPlateById
import com.example.diet_app.ui.theme.DarkGreen
import com.example.diet_app.ui.theme.LightGray
import com.example.diet_app.ui.theme.DarkOverlay
import com.example.diet_app.screenActivities.components.Header
import com.example.diet_app.screenActivities.components.NextButton
import com.example.diet_app.screenActivities.components.TitleSection
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    onNavigateBack: () -> Unit,
    onSkip: () -> Unit,
    onNext: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf("") }
    var infoText by remember { mutableStateOf("Selecciona una fecha para ver información.") }

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

            Spacer(modifier = Modifier.height(80.dp))

            TitleSection("Your", "Calendar", "Select a date to view the diet")

            Spacer(modifier = Modifier.height(30.dp))

            Column(
                modifier = Modifier
                    .width(335.dp)
                    .padding(vertical = 7.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Fecha seleccionada
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(105.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkOverlay),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedDate.isEmpty()) "No date selected" else selectedDate,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                    )
                }

                // Selector de fecha
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
                    Text(
                        text = "Pick a date",
                        style = TextStyle(fontSize = 16.sp, color = DarkGreen)
                    )

                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_calendar),
                        contentDescription = "Calendar",
                        tint = DarkGreen,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                showDatePicker(context) { date ->
                                    selectedDate = date

                                    val prefs = context.getSharedPreferences("WeeklyDiet", Context.MODE_PRIVATE)
                                    val storedDiet = prefs.getString("${date}_diet", null)

                                    if (storedDiet != null) {
                                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        val dateObj = sdf.parse(date)

                                        if (dateObj != null) {
                                            getPlateById(context, dateObj) { plates ->
                                                if (plates.isNotEmpty()) {
                                                    infoText = """
                        📅 *$date*

                        🍳 Desayuno:
                        - Plato: ${plates[0].name}
                        - Bebida: ${plates[1].name}

                        🥗 Almuerzo:
                        - Plato principal: ${plates[2].name}
                        - Segundo: ${plates[3].name}
                        - Bebida: ${plates[4].name}

                        🍽 Cena:
                        - Plato: ${plates[5].name}
                        - Bebida: ${plates[6].name}
                    """.trimIndent()
                                                } else {
                                                    infoText = "⚠️ No se pudo cargar la información nutricional para $date."
                                                }
                                            }
                                        } else {
                                            infoText = "⚠️ Error al convertir la fecha: $date"
                                        }
                                    } else {
                                        infoText = "❌ No hay dieta guardada para $date."
                                    }
                                }

                            }
                    )
                }

                // Texto informativo
                Text(
                    text = infoText,
                    modifier = Modifier.padding(top = 20.dp),
                    style = TextStyle(fontSize = 16.sp, color = Color.DarkGray)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, day ->
            val date = Calendar.getInstance()
            date.set(year, month, day)
            val formatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date.time)
            onDateSelected(formatted)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}