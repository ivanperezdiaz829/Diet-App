package com.example.diet_app.screenActivities

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavHostController
import com.example.diet_app.R
import com.example.diet_app.getPlateById
import com.example.diet_app.ui.theme.DarkGreen
import com.example.diet_app.ui.theme.LightGray
import com.example.diet_app.ui.theme.DarkOverlay
import com.example.diet_app.screenActivities.components.Header
import com.example.diet_app.screenActivities.components.TitleSection
import com.example.diet_app.viewModel.DietDayViewModel
import com.example.diet_app.viewModel.FoodViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.diet_app.screenActivities.components.FoodDetailDialog
import com.example.diet_app.screenActivities.components.ToolBox
import com.example.diet_app.viewModel.DietViewModel

@Composable
fun CalendarScreen(
    onNavigateBack: () -> Unit,
    onSkip: () -> Unit,
    onNext: (String) -> Unit,
    diets: List<DietViewModel>,
    navController: NavHostController
) {
    val context = LocalContext.current

    var selectedDate by remember { mutableStateOf("") }
    var infoText by remember { mutableStateOf("Selecciona una fecha para ver información.") }

    var selectedFoods by remember { mutableStateOf<List<FoodViewModel>>(emptyList()) }
    var dayViewModel by remember { mutableStateOf<DietDayViewModel?>(null) }
    var selectedFood by remember { mutableStateOf<FoodViewModel?>(null) }

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
            .padding(bottom = 40.dp)
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
            //Header(onNavigateBack, onSkip)

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

                                    val selectedDiet = diets.firstOrNull()

                                    if (selectedDiet != null) {
                                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        val selectedDateObj = sdf.parse(date)

                                        if (selectedDateObj != null) {
                                            // Normalizar ambas fechas a las 00:00 para evitar errores de redondeo
                                            val today = Calendar.getInstance().apply {
                                                set(Calendar.HOUR_OF_DAY, 0)
                                                set(Calendar.MINUTE, 0)
                                                set(Calendar.SECOND, 0)
                                                set(Calendar.MILLISECOND, 0)
                                            }

                                            val selected = Calendar.getInstance().apply {
                                                time = selectedDateObj
                                                set(Calendar.HOUR_OF_DAY, 0)
                                                set(Calendar.MINUTE, 0)
                                                set(Calendar.SECOND, 0)
                                                set(Calendar.MILLISECOND, 0)
                                            }

                                            val daysDiff = ((selected.timeInMillis - today.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

                                            if (daysDiff in 0 until selectedDiet.getDiet().diets.size) {
                                                val dietDay = selectedDiet.getDiet().diets[daysDiff]
                                                val foods = dietDay.getDiet().foods

                                                if (foods.isNotEmpty()) {
                                                    selectedFoods = foods

                                                    val dietDayVM = DietDayViewModel().apply {
                                                        updateDietDay(foods)
                                                    }
                                                    dayViewModel = dietDayVM
                                                    infoText = ""
                                                } else {
                                                    dayViewModel = null
                                                    selectedFoods = emptyList()
                                                    infoText = "⚠️ No hay alimentos para mostrar en la dieta seleccionada."
                                                }
                                            } else {
                                                dayViewModel = null
                                                selectedFoods = emptyList()
                                                infoText = "❌ La dieta no tiene asignado un día para ese día."
                                            }
                                        } else {
                                            dayViewModel = null
                                            selectedFoods = emptyList()
                                            infoText = "❌ Error al interpretar la fecha seleccionada."
                                        }
                                    } else {
                                        dayViewModel = null
                                        selectedFoods = emptyList()
                                        infoText = "❌ No hay dietas disponibles."
                                    }
                                }
                            }
                    )

                }

                Spacer(modifier = Modifier.height(20.dp))

                // Mostrar dieta o mensaje informativo
                if (dayViewModel != null && selectedFoods.isNotEmpty()) {
                    // Aquí está el cambio principal - envolver en un Column scrollable
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp) // Altura máxima para evitar que ocupe toda la pantalla
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        selectedFoods.forEach { food ->
                            MealCardCalendar(
                                food = food,
                                onDetailsClick = { selectedFood = it }
                            )
                        }
                    }
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
    ToolBox(navController)
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
