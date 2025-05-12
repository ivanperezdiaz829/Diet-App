package com.example.diet_app.screenActivities

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.diet_app.screenActivities.components.BackButton
import com.example.diet_app.screenActivities.components.NextButton
import com.example.diet_app.screenActivities.components.TitleSection
import com.example.diet_app.ui.theme.Typography
import com.example.diet_app.ui.theme.DarkGreen
import com.example.diet_app.ui.theme.DarkOverlay
import com.example.diet_app.ui.theme.GrayGreen
import com.example.diet_app.ui.theme.LightGray
import com.example.diet_app.R
import com.example.diet_app.model.FoodType

@Composable
fun FoodTypeSelectionScreen(
    navController: NavHostController,
    onNavigateBack: () -> Unit,
    onNext: (Set<FoodType>) -> Unit
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

            TitleSection("¿Cuál es tu", "nueva comida?", "Especifica el tipo de la neva comida")

            Spacer(modifier = Modifier.height(40.dp))

            FoodOptions(
                selectedFoodTypes = selectedFoodTypes,
                onFoodTypeSelected = { foodType ->
                    selectedFoodTypes = if (selectedFoodTypes.contains(foodType)) {
                        setOf() // Deselecciona si ya está seleccionado
                    } else {
                        setOf(foodType) // Siempre crea un nuevo Set con solo este elemento
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
            foodType = FoodType.PLATO_LIGERO,
            text = "Plato Ligero",
            imageId = R.drawable.sun_day_morning,
            isSelected = selectedFoodTypes.contains(FoodType.PLATO_LIGERO),
            onClick = { onFoodTypeSelected(FoodType.PLATO_LIGERO) }
        )

        FoodOption(
            foodType = FoodType.PLATO_PRINCIPAL,
            text = "Plato Principal",
            imageId = R.drawable.sun_day_midday,
            isSelected = selectedFoodTypes.contains(FoodType.PLATO_PRINCIPAL),
            onClick = { onFoodTypeSelected(FoodType.PLATO_PRINCIPAL) }
        )

        FoodOption(
            foodType = FoodType.PLATO_SECUNDARIO,
            text = "Plato Secundario",
            imageId = R.drawable.sun_day_afternoon,
            isSelected = selectedFoodTypes.contains(FoodType.PLATO_SECUNDARIO),
            onClick = { onFoodTypeSelected(FoodType.PLATO_SECUNDARIO) }
        )

        FoodOption(
            foodType = FoodType.BEBIDA,
            text = "Bebida",
            imageId = R.drawable.bebida,
            isSelected = selectedFoodTypes.contains(FoodType.BEBIDA),
            onClick = { onFoodTypeSelected(FoodType.BEBIDA) }
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
            )
        }
    }
}