package com.example.diet_app.screenActivities

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsProperties.ImeAction
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.diet_app.model.FoodType

@Composable
fun NewFoodSummaryScreen(
    onNavigateBack: () -> Unit,
    onNext: (Set<FoodType>) -> Unit // Cambiado para aceptar múltiples selecciones
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

            FoodSummaryView()

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
                onClick = { }
            )
        }
    }
}

@Composable
fun FoodSummaryView(){

    Text(
        text = "Food Variants",
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        color = DarkGray,
        modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
    )

    FoodVariants("Vegetarian", "Vegan", "Celiac", "Halal")

    Spacer(modifier = Modifier.height(30.dp))

    val nutritionalData = mapOf(
        "Proteins" to 12.7f,
        "Fats" to 6.3f,
        "Sugar" to 20.3f,
        "Salt" to 15.7f,
        "Carbohydrates" to 26.7f,
        "Calories" to 229.8f
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