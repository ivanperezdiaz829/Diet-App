package com.example.diet_app.screenActivities.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diet_app.R
import com.example.diet_app.model.FoodType
import com.example.diet_app.ui.theme.DarkGreen
import com.example.diet_app.ui.theme.DarkOverlay
import com.example.diet_app.ui.theme.Typography
import com.example.diet_app.viewModel.FoodViewModel

@Composable
fun FoodViewScreen(
    onClick: () -> Unit,
    food: FoodViewModel
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = DarkOverlay
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween, // Distribuye el espacio equitativamente
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = food.getFood().name,
                style = Typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                ),
                modifier = Modifier.weight(1f) // Ocupa espacio restante
            )

            for (foodType in food.getFood().foodTypes) {
                val id = when (foodType) {
                    FoodType.BREAKFAST -> R.drawable.sun_day_morning
                    FoodType.LUNCH -> R.drawable.sun_day_midday
                    FoodType.DINNER -> R.drawable.sun_day_afternoon
                    FoodType.SNACK -> R.drawable.healthy_icon
                }

                Image(
                    painter = painterResource(id = id),
                    contentDescription = foodType.name,
                    modifier = Modifier
                        .size(70.dp) // Tamaño fijo para todas las imágenes
                        .padding(horizontal = 4.dp) // Espaciado entre imágenes
                )
            }
        }
    }
}