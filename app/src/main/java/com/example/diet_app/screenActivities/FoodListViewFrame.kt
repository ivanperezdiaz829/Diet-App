package com.example.diet_app.screenActivities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diet_app.screenActivities.components.BackButtonLeft
import com.example.diet_app.screenActivities.components.FoodViewScreen
import com.example.diet_app.screenActivities.components.TitleSection
import com.example.diet_app.viewModel.FoodViewModel

@Composable
fun FoodListViewScreen(
    foods: List<FoodViewModel>,
    onFoodClick: (FoodViewModel) -> Unit, // Callback al hacer clic en una comida
    navController: NavController
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Hace la columna scrollable
            .padding(horizontal = 16.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp), // Espacio entre comidas
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        BackButtonLeft(onNavigateBack = { navController.popBackStack() })

        TitleSection("Your ", "foods", "You can add your favorite foods")

        Spacer(modifier = Modifier.padding(20.dp))

        for (food in foods) {
            FoodViewScreen(
                onClick = { onFoodClick(food) }, // Pasa el ViewModel al hacer clic
                food = food
            )
        }
    }
}