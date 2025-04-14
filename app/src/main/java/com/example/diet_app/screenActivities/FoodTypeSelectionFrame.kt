package com.example.diet_app.screenActivities

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.diet_app.screenActivities.components.BackButton
import com.example.diet_app.screenActivities.components.Header
import com.example.diet_app.screenActivities.components.NextButton
import com.example.diet_app.screenActivities.components.TitleSection
import com.example.diet_app.ui.theme.Typography
import com.example.diet_app.ui.theme.DarkGreen
import com.example.diet_app.ui.theme.DarkOverlay
import com.example.diet_app.ui.theme.GrayGreen
import com.example.diet_app.ui.theme.LightGray
import com.example.diet_app.R

enum class FoodType {
    Breakfast,
    Lunch,
    Dinner
}

@Composable
fun FoodTypeSelectionScreen(
    onNavigateBack: () -> Unit,
    onNext: (Set<FoodType>) -> Unit // Cambiado para aceptar múltiples selecciones
) {
    var selectedFoodTypes by remember { mutableStateOf<Set<FoodType>>(setOf()) } // Usamos un Set para almacenar múltiples selecciones

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp, bottom = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onNavigateBack)
            }

            Spacer(modifier = Modifier.height(30.dp))

            TitleSection("Which one is your", "new food?", "Specify the type of the new food")

            Spacer(modifier = Modifier.height(40.dp))

            FoodOptions(
                selectedFoodTypes = selectedFoodTypes,
                onFoodTypeSelected = { foodType ->
                    selectedFoodTypes = if (selectedFoodTypes.contains(foodType)) {
                        selectedFoodTypes - foodType // Elimina si ya está seleccionado
                    } else {
                        selectedFoodTypes + foodType // Añade si no está seleccionado
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                enabled = selectedFoodTypes.isNotEmpty(), // Habilitar si hay al menos una selección
                onClick = { onNext(selectedFoodTypes) }
            )
        }
    }
}

@Composable
private fun FoodOptions(
    selectedFoodTypes: Set<FoodType>,
    onFoodTypeSelected: (FoodType) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        FoodOption(
            foodType = FoodType.Breakfast,
            text = "Breakfast",
            imageId = R.drawable.sun_day_morning,
            isSelected = selectedFoodTypes.contains(FoodType.Breakfast),
            onClick = { onFoodTypeSelected(FoodType.Breakfast) }
        )

        FoodOption(
            foodType = FoodType.Lunch,
            text = "Lunch",
            imageId = R.drawable.sun_day_midday,
            isSelected = selectedFoodTypes.contains(FoodType.Lunch),
            onClick = { onFoodTypeSelected(FoodType.Lunch) }
        )

        FoodOption(
            foodType = FoodType.Dinner,
            text = "Dinner",
            imageId = R.drawable.sun_day_afternoon,
            isSelected = selectedFoodTypes.contains(FoodType.Dinner),
            onClick = { onFoodTypeSelected(FoodType.Dinner) }
        )
    }
}

@Composable
private fun FoodOption(
    foodType: FoodType,
    text: String,
    imageId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) DarkOverlay else LightGray,
        /*
        border = if (isSelected) {
            BorderStroke(width = 2.dp, color = DarkGreen)
        } else {
            null
        }*/
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = Typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) DarkGreen else GrayGreen
                )
            )

            Image(
                painter = painterResource(id = imageId),
                contentDescription = null,
                modifier = Modifier.size(70.dp),
                // contentScale = ContentScale.Crop
            )
        }
    }
}