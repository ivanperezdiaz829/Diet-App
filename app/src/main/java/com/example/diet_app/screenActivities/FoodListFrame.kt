package com.example.diet_app.screenActivities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diet_app.model.Screen
import com.example.diet_app.screenActivities.components.FoodDetailDialog
import com.example.diet_app.screenActivities.components.FoodListDialog
import com.example.diet_app.screenActivities.components.FoodViewScreen
import com.example.diet_app.screenActivities.components.TitleSection
import com.example.diet_app.screenActivities.components.ToolBox
import com.example.diet_app.ui.theme.PrimaryGreen
import com.example.diet_app.viewModel.FoodViewModel

@Composable
fun FoodListViewScreen(
    navController: NavController,
    foods: MutableList<FoodViewModel>,
) {

    var selectedFood by remember { mutableStateOf<FoodViewModel?>(null) }

    // Mostrar diálogo si hay comida seleccionada
    selectedFood?.let { food ->
        FoodDetailDialog(
            foodViewModel = food,
            onDismiss = { selectedFood = null }
        )
    }

    Scaffold( modifier = Modifier
        .padding(bottom = 70.dp),
            floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddFood.route) }, // Ruta para creación
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add food")
            }
        }
    ) { padding ->


        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Hace la columna scrollable
                .padding(horizontal = 16.dp),

            verticalArrangement = Arrangement.spacedBy(12.dp), // Espacio entre comidas
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //BackButtonLeft(onNavigateBack = { navController.popBackStack() })

            TitleSection("Tus ", "comidas", "Puedes añadir tus comidas preferidas ")

            Spacer(modifier = Modifier.padding(20.dp))

            if (foods.isEmpty()) {
                Text("No hay comidas añadidas")
            } else {
                for (food in foods) {
                    FoodViewScreen(
                        onClick = { selectedFood = food }, // Pasa el ViewModel al hacer clic
                        food = food
                    )
                }
            }

        }
    }
    ToolBox(navController)
}