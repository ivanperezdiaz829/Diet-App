package com.example.diet_app.screenActivities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import okhttp3.*
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*

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
