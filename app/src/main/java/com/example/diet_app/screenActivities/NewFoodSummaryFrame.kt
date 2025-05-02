package com.example.diet_app.screenActivities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diet_app.screenActivities.components.BackButton
import com.example.diet_app.screenActivities.components.NextButton
import com.example.diet_app.screenActivities.components.TitleSection
import com.example.diet_app.ui.theme.GrayGreen
import com.example.diet_app.model.FoodType
import com.example.diet_app.viewModel.FoodViewModel

@Composable
fun NewFoodSummaryScreen(
    onNavigateBack: () -> Unit,
    onNext: (foodViewModel: FoodViewModel) -> Unit, // Cambiado para aceptar múltiples selecciones
    foodViewModel: FoodViewModel
) {
    var selectedName by remember { mutableStateOf("") } // Usamos un Set para almacenar múltiples selecciones

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
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

            TitleSection("What is the name of the", "new food?", "Specify the name of the added food")

            Spacer(modifier = Modifier.height(40.dp))

            FoodSummaryView(foodViewModel)

            Spacer(modifier = Modifier.height(40.dp))

            LabeledOutlinedTextField(
                label = "Name of the food",
                placeholder = "Croissant with poached egg",
                value = selectedName,
                onValueChange = { selectedName = it }
            )

            Spacer(modifier = Modifier.height(40.dp))

            NextButton(
                enabled = selectedName.isNotEmpty(), // Habilitar si hay al menos una selección
                onClick = {
                    foodViewModel.updateFood(name = selectedName)
                    onNext(foodViewModel)
                }
            )
        }
    }
}

@Composable
fun FoodSummaryView(foodViewModel: FoodViewModel){

    FoodVariants(foodViewModel)

    Spacer(modifier = Modifier.height(30.dp))

    val nutritionalData = mapOf(
        "Proteins" to foodViewModel.getFood().protein,
        "Fats" to foodViewModel.getFood().fats,
        "Sugar" to foodViewModel.getFood().sugar,
        "Salt" to foodViewModel.getFood().salt,
        "Carbohydrates" to foodViewModel.getFood().carbohydrates,
        "Calories" to foodViewModel.getFood().calories,
    )

    NutritionalInfoGrid(data = nutritionalData)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabeledOutlinedTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.Transparent,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedBorderColor = GrayGreen
            ),
            singleLine = true
        )
    }
}