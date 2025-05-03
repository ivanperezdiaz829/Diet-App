package com.example.diet_app.screenActivities.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.diet_app.R
import com.example.diet_app.model.Screen
import com.example.diet_app.ui.theme.Typography

// Composable que genera una cuadrícula de opciones en la pantalla principal
@Composable
fun OptionGrid(navController: NavController) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            HomePageOptionCard(
                label = "Calories Calculator",
                imageId = R.drawable.calculator,
                backgroundColor = Color(0xFFFF6C87), // color personalizado
                onClick = { /* Acción temporal, no se hace nada por ahora */ }
            )

            HomePageOptionCard(
                label = "Diet Calendar",
                imageId = R.drawable.calendar,
                backgroundColor = Color(0xFF5C6DFF), // color personalizado
                onClick = {
                    navController.navigate(Screen.Calendar.route)
                }
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            HomePageOptionCard(
                label = "Generate Diet",
                imageId = R.drawable.add,
                backgroundColor = Color(0xFF05D0A0), // color personalizado
                onClick = { /* Acción temporal, no se hace nada por ahora */ }
            )

            HomePageOptionCard(
                label = "Modify diet",
                imageId = R.drawable.modify,
                backgroundColor = Color(0xFFFFB36D), // color personalizado
                onClick = {
                    navController.navigate(Screen.FoodList.route)
                }
            )
        }
    }
}

// Composable reutilizable para cada tarjeta de opción en la pantalla principal
@Composable
private fun HomePageOptionCard(
    label: String,
    imageId: Int,
    backgroundColor: Color, // Se añade parámetro para recibir el color de fondo
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(157.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .background(backgroundColor), // Se aplica el color recibido como fondo
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
                style = Typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

