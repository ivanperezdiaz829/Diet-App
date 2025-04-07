package com.example.diet_app.screenActivities

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ButtonGridScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8BC34A)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fila de botones para las diferentes funcionalidades
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ColoredButton("Formulario Dieta", Color.Black) {
                navController.navigate("diet_form")
            }
            ColoredButton("Gasto Basal", Color.Black) {
                navController.navigate("basal_metabolism")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ColoredButton("Calorías Mantenimiento", Color.Black) {
                navController.navigate("maintenance_calories")
            }
            ColoredButton("Calendario", Color.Black) {
                navController.navigate("calendar")
            }
        }
    }
}
