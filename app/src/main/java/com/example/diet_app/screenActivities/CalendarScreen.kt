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
                                    infoText = if (storedDiet != null) {
                                        val json = JSONObject(storedDiet)
                                        """
                                            📅 *$date*
    
                                            🍳 Desayuno:
                                            - Plato: ${json.getString("breakfast_dish")}
                                            - Bebida: ${json.getString("breakfast_drink")}
    
                                            🥗 Almuerzo:
                                            - Plato principal: ${json.getString("lunch_main_dish")}
                                            - Segundo: ${json.getString("lunch_side_dish")}
                                            - Bebida: ${json.getString("lunch_drink")}
    
                                            🍽 Cena:
                                            - Plato: ${json.getString("dinner_dish")}
                                            - Bebida: ${json.getString("dinner_drink")}
                                        """.trimIndent()

                                    } else {
                                        "❌ No hay dieta guardada para $date."
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

            NextButton(
                enabled = selectedDate.isNotEmpty(),
                onClick = { onNext(selectedDate) }
            )
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