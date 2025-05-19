package com.example.diet_app.screenActivities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.diet_app.model.FoodType
import com.example.diet_app.model.FoodVariant
import com.example.diet_app.screenActivities.components.BackButton
import com.example.diet_app.screenActivities.components.NextButton
import com.example.diet_app.screenActivities.components.TitleSection
import com.example.diet_app.ui.theme.GrayGreen
import com.example.diet_app.ui.theme.LightGray
import com.example.diet_app.ui.theme.PrimaryGreen
import com.example.diet_app.viewModel.FoodViewModel

@Composable
fun AddNewFoodScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onNext: (FoodViewModel) -> Unit,
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
    var foodVariants by remember { mutableStateOf(setOf<FoodVariant>()) }

    var foodViewModel by remember { mutableStateOf(FoodViewModel())}

    // Función para manejar cambios en el switch Vegano
    val onVeganChange = { newVeganState: Boolean ->
        isVegan = newVeganState
        // Si se activa Vegano, también activar Vegetariano y Halal
        if (newVeganState) {
            isVegetarian = true
            isHalal = true
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

            TitleSection("Cualidades de la", "nueva comida", "Especifica las cualidades de la nueva comida")

            Spacer(modifier = Modifier.height(24.dp))

            // Campos de entrada para valores nutricionales
            NutritionTextField(label = "Proteinas", value = protein, onValueChange = { protein = it })
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )

            NutritionTextField(label = "Grasas", value = fats, onValueChange = { fats = it })
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )

            NutritionTextField(label = "Azúcar", value = sugar, onValueChange = { sugar = it })
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )

            NutritionTextField(label = "Sal", value = salt, onValueChange = { salt = it })
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )

            NutritionTextField(label = "Carbohidratos", value = carbohydrates, onValueChange = { carbohydrates = it })
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )

            NutritionTextField(label = "Calorias", value = calories, onValueChange = { calories = it })
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.LightGray
            )

            NutritionTextField(label = "Precio", value = price, onValueChange = { price = it })

            Spacer(modifier = Modifier.height(32.dp))

            // Sección de opciones dietéticas
            DietOptionItem(
                title = "Vegetariana",
                description = "Esta comida es apta para vegetarianos",
                checked = isVegetarian,
                onCheckedChange = { isVegetarian = it }
            )

            DietOptionItem(
                title = "Vegana",
                description = "Esta comida es apta para veganos",
                checked = isVegan,
                onCheckedChange = onVeganChange
            )

            DietOptionItem(
                title = "Celiaca",
                description = "Esta comida es apta para celiacos!",
                checked = isCeliac,
                onCheckedChange = { isCeliac = it }
            )

            DietOptionItem(
                title = "Halal",
                description = "Esta comida es apta para dieta musulmana!",
                checked = isHalal,
                onCheckedChange = { isHalal = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            NextButton(
                enabled = protein.isNotEmpty() && fats.isNotEmpty() && sugar.isNotEmpty() && salt.isNotEmpty() && carbohydrates.isNotEmpty() && calories.isNotEmpty() && price.isNotEmpty(),
                onClick = {
                    if (isVegan) {
                        foodVariants = foodVariants.plus(FoodVariant.VEGAN)
                    }
                    if (isVegetarian) {
                        foodVariants = foodVariants.plus(FoodVariant.VEGETARIAN)
                    }
                    if (isCeliac) {
                        foodVariants = foodVariants.plus(FoodVariant.CELIAC)
                    }
                    if (isHalal) {
                        foodVariants = foodVariants.plus(FoodVariant.HALAL)
                    }
                    foodViewModel.updateFood(protein = protein.toDouble(), fats = fats.toDouble(), sugar = sugar.toDouble(), salt = salt.toDouble(), carbohydrates = carbohydrates.toDouble(), calories = calories.toDouble(), price = price.toDouble())
                    foodViewModel.updateFood(foodVariants = foodVariants)
                    onNext(foodViewModel)
                }
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
            onValueChange = {  newValue ->
                if (newValue.all { it.isDigit() }) { // Verifica que solo sean números
                    onValueChange(newValue)
                }
            },
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