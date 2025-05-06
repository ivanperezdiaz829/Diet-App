package com.example.diet_app.screenActivities

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.diet_app.viewModel.DietDayViewModel
import com.example.diet_app.viewModel.FoodViewModel
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import com.example.diet_app.screenActivities.components.FoodDetailDialog

@Composable
fun CalendarScreen(
    onNavigateBack: () -> Unit,
    onSkip: () -> Unit,
    onNext: (String) -> Unit
) {
    val context = LocalContext.current

    var selectedDate by remember { mutableStateOf("") }
    var infoText by remember { mutableStateOf("Selecciona una fecha para ver información.") }

    var selectedFoods by remember { mutableStateOf<List<FoodViewModel>>(emptyList()) }
    var dayViewModel by remember { mutableStateOf<DietDayViewModel?>(null) }
    var selectedFood by remember { mutableStateOf<FoodViewModel?>(null) }

    // Mostrar diálogo si hay comida seleccionada
    selectedFood?.let { food ->
        FoodDetailDialog(
            foodViewModel = food,
            onDismiss = { selectedFood = null }
        )
    }

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

            TitleSection("Tu", "calendario", "Selecciona una fecha para ver tu programación")

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
                        text = if (selectedDate.isEmpty()) "Ninguna fecha seleccionada" else selectedDate,
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
                        text = "Selecciona una fecha",
                        style = TextStyle(fontSize = 16.sp, color = DarkGreen)
                    )

                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_calendar),
                        contentDescription = "Calendario",
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
                                                    val foodViewModels = plates

                                                    selectedFoods = foodViewModels

                                                    val dietDayVM = DietDayViewModel()
                                                    dietDayVM.updateDietDay(foods = foodViewModels)
                                                    dayViewModel = dietDayVM

                                                    infoText = "" // limpiamos texto plano
                                                } else {
                                                    dayViewModel = null
                                                    selectedFoods = emptyList()
                                                    infoText = "⚠️ No se pudo cargar la información nutricional para $date."
                                                }
                                            }
                                        } else {
                                            dayViewModel = null
                                            infoText = "⚠️ Error al convertir la fecha: $date"
                                        }
                                    } else {
                                        dayViewModel = null
                                        selectedFoods = emptyList()
                                        infoText = "❌ No hay dieta guardada para $date."
                                    }
                                }
                            }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Mostrar dieta o mensaje informativo
                if (dayViewModel != null && selectedFoods.isNotEmpty()) {
                    DayDiet(
                        dayDietDayViewModel = dayViewModel!!,
                        foods = selectedFoods,
                        onFoodSelected = { food -> selectedFood = food }
                    )
                } else {
                    Text(
                        text = infoText,
                        style = TextStyle(fontSize = 16.sp, color = Color.DarkGray)
                    )
                }
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

@Composable
fun MealCardCalendar(
    food: FoodViewModel,
    onDetailsClick: (FoodViewModel) -> Unit
) {
    val foodData = food.getFood()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = foodData.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                Button(
                    onClick = { onDetailsClick(food) },
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Detalles", fontWeight = FontWeight.Medium)
                }
            }

            // Mejoramos el display de los tipos de comida
            Text(
                text = foodData.foodTypes.joinToString(", ") { it.name.lowercase().replaceFirstChar(Char::uppercase) },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
