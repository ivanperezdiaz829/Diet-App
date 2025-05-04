package com.example.diet_app.screenActivities

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diet_app.model.Screen
import com.example.diet_app.screenActivities.components.BackButton
import com.example.diet_app.screenActivities.components.FitnessIconButton
import com.example.diet_app.screenActivities.components.FoodDetailDialog
import com.example.diet_app.ui.theme.BackButtonBackground
import com.example.diet_app.ui.theme.PrimaryGreen
import com.example.diet_app.ui.theme.Typography
import com.example.diet_app.viewModel.DietDayViewModel
import com.example.diet_app.viewModel.DietViewModel
import com.example.diet_app.viewModel.FoodViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietInterface(
    diets: MutableList<DietViewModel>, // Lista de ViewModels
    navController: NavController,
) {

    var selectedDay by remember { mutableIntStateOf(1) }
    var selectedFood by remember { mutableStateOf<FoodViewModel?>(null) }

    var foodsForSelectedDay by remember { mutableStateOf<List<FoodViewModel>>(emptyList()) }

    // Mostrar diálogo si hay comida seleccionada
    selectedFood?.let { food ->
        FoodDetailDialog(
            foodViewModel = food,
            onDismiss = { selectedFood = null }
        )
    }

    val context = LocalContext.current

    LaunchedEffect(selectedDay) {
        val date = Date() // Cambia la fecha si es necesario
        /*
        dietViewModel.getDiet().diets.getOrNull(selectedDay - 1)?.let { dietDayViewModel ->
            dietDayViewModel.loadFromStorage(
                context = context,
                date = date,
                foodVariant = dietViewModel.getDiet().foodVariant,
                goal = dietViewModel.getDiet().goal,
                dietId = dietViewModel.getDiet().dietId
            ) { loadedDay ->
                foodsForSelectedDay = loadedDay.getDiet().foods
            }
        }
        */
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            BackButton(onNavigateBack = { navController.popBackStack() })

            Button(
                onClick = {
                    //navController.navigate(Screen.GraphicFrame.createRoute(diets[0].getDiet().dietId))
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ver Gráfico")
            }

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            FitnessIconButton(
                modifier = Modifier.size(92.dp, 100.dp),
                onClick = { /* Acción al hacer clic */ }
            )

            GreenInfoCard(
                title = "Goal",
                content = "diets[0].getDiet().goal.toString()",
            )
        }
        /*
        DaysList(
            dietViewModel = diets[0],
            selectedDay = selectedDay, // Pasamos el día seleccionado
            onDaySelected = { day ->
                selectedDay = day
            }
        )
        */
        Divider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp)
        )

        /*
        DayDiet(
            dayDietDayViewModel = dietViewModel.getDiet().diets[selectedDay - 1],
            foods = foodsForSelectedDay, // Usamos la lista de alimentos
            onFoodSelected = { food ->
                selectedFood = food // Actualizamos la comida seleccionada
            }
        )
        */
        // Línea divisoria negra
    }
}

@Composable
fun DaysList(
    dietViewModel: DietViewModel,
    selectedDay: Int, // Recibe el día seleccionado
    onDaySelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (day in 0 until dietViewModel.getDiet().duration) {
            Day(
                day = day + 1,
                isSelected = (day + 1) == selectedDay, // Comparamos con el día seleccionado
                onClick = onDaySelected
            )
        }
    }
}

@Composable
fun Day(
    day: Int,
    isSelected: Boolean, // Nuevo parámetro
    onClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (isSelected) PrimaryGreen else BackButtonBackground)
            .clickable { onClick(day) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = Typography.titleLarge.copy(
                color = if (isSelected) Color.White else DarkGray,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun GreenInfoCard(
    title: String,
    content: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD5DBE0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun MealCard(
    food: FoodViewModel,
    onDetailsClick: (FoodViewModel) -> Unit // Nuevo parámetro para manejar el click
) {
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
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = food.getFood().name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )

                Button(
                    onClick = { onDetailsClick(food) }, // Usamos el callback
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
            Text(
                text = food.getFood().foodTypes.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun DayDiet(
    dayDietDayViewModel: DietDayViewModel,
    foods: List<FoodViewModel>,
    onFoodSelected: (FoodViewModel) -> Unit // Nuevo parámetro
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (food in foods) {
            MealCard(
                food = food,
                onDetailsClick = onFoodSelected // Pasamos el callback
            )
        }
    }
}