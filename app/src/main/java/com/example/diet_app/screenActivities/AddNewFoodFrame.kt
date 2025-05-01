package com.example.diet_app.screenActivities

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.diet_app.model.Screen
import com.example.diet_app.screenActivities.components.BackButton
import com.example.diet_app.screenActivities.components.Header
import com.example.diet_app.screenActivities.components.NextButton
import com.example.diet_app.screenActivities.components.SkipButton
import com.example.diet_app.screenActivities.components.TitleSection
import com.example.diet_app.ui.theme.Typography
import com.example.diet_app.ui.theme.DarkGreen
import com.example.diet_app.ui.theme.DarkOverlay
import com.example.diet_app.ui.theme.GrayGreen
import com.example.diet_app.ui.theme.LightGray
import com.example.diet_app.ui.theme.PrimaryGreen

@Composable
fun AddNewFoodScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
) {

    // Estados para los valores nutricionales
    var protein by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    var sugar by remember { mutableStateOf("") }
    var salt by remember { mutableStateOf("") }
    var carbohydrates by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    // Estados para los switches
    var isVegetarian by remember { mutableStateOf(false) }
    var isVegan by remember { mutableStateOf(false) }
    var isCeliac by remember { mutableStateOf(false) }
    var isHalal by remember { mutableStateOf(false) }

    // Función para manejar cambios en el switch Vegano
    val onVeganChange = { newVeganState: Boolean ->
        isVegan = newVeganState
        // Si se activa Vegano, también activar Vegetariano
        if (newVeganState) {
            isVegetarian = true
        } else {
            isVegetarian = false
        }
    }

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

            TitleSection("Qualities of the", "new food", "Please specify the qualities of the new food to add")

            Spacer(modifier = Modifier.height(24.dp))

            // Campos de entrada para valores nutricionales
            NutritionTextField(label = "Protein", value = protein, onValueChange = { protein = it })
            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            NutritionTextField(label = "Fats", value = fats, onValueChange = { fats = it })
            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            NutritionTextField(label = "Sugar", value = sugar, onValueChange = { sugar = it })
            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            NutritionTextField(label = "Salt", value = salt, onValueChange = { salt = it })
            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            NutritionTextField(label = "Carbohydrates", value = carbohydrates, onValueChange = { carbohydrates = it })
            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            NutritionTextField(label = "Calories", value = calories, onValueChange = { calories = it })
            Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            NutritionTextField(label = "Price", value = price, onValueChange = { price = it })

            Spacer(modifier = Modifier.height(32.dp))

            // Sección de opciones dietéticas
            DietOptionItem(
                title = "Vegetarian",
                description = "This food is vegetarian-friendly!",
                checked = isVegetarian,
                onCheckedChange = { isVegetarian = it }
            )

            DietOptionItem(
                title = "Vegan",
                description = "This food is vegan-friendly!",
                checked = isVegan,
                onCheckedChange = onVeganChange
            )

            DietOptionItem(
                title = "Celiac",
                description = "This food is celiac-friendly!",
                checked = isCeliac,
                onCheckedChange = { isCeliac = it }
            )

            DietOptionItem(
                title = "Halal",
                description = "This food is muslim-friendly!",
                checked = isHalal,
                onCheckedChange = { isHalal = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            NextButton(
                enabled = protein.isNotEmpty() && fats.isNotEmpty() && sugar.isNotEmpty() && salt.isNotEmpty() && carbohydrates.isNotEmpty() && calories.isNotEmpty() && price.isNotEmpty(),
                onClick = {navController.navigate(Screen.NewFoodType.route)}
            )
        }
    }
}

@Composable
fun NutritionTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Composable
fun DietOptionItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryGreen,
                uncheckedThumbColor = GrayGreen,
                uncheckedTrackColor = LightGray
            )
        )
    }
    // Divider(color = Color.LightGray, thickness = 1.dp)
}