package com.example.diet_app.screenActivities

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.diet_app.convertPlatesToFoodViewModels
import com.example.diet_app.getAllPlatesWhereUserIdIsEitherUsersOrNull
import com.example.diet_app.model.FoodType
import com.example.diet_app.model.FoodVariant
import com.example.diet_app.screenActivities.components.BackButton
import com.example.diet_app.screenActivities.components.FoodListDialog
import com.example.diet_app.viewModel.DietViewModel
import com.example.diet_app.viewModel.FoodViewModel
import com.example.diet_app.viewModel.UserViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChosenDietInterface(
    context: Context,
    navController: NavController,
    dietViewModel: DietViewModel,
    userViewModel: UserViewModel
) {
    var dietViewModel1 = dietViewModel
    var selectedDay by remember { mutableIntStateOf(1) }
    var selectedFood by remember { mutableStateOf<FoodViewModel?>(null) }
    var addFood by remember { mutableStateOf<Boolean>(false) }

    var foodsForSelectedDay by remember { mutableStateOf<MutableList<FoodViewModel>>(mutableListOf()) }
    var foodsDatabase by remember { mutableStateOf<MutableList<FoodViewModel>>(mutableListOf()) }

    LaunchedEffect(Unit) {
        getAllPlatesWhereUserIdIsEitherUsersOrNull(userViewModel.getUser().id, context) { result ->
            foodsDatabase = convertPlatesToFoodViewModels(result)
        }
    }


    // Mostrar diálogo si hay comida seleccionada
    if (addFood) {
        /*
        var foodViewModel1 = FoodViewModel()
        foodViewModel1.updateFood(
            name = "Avena con frutas",
            calories = 300.0,
            protein = 10.0,
            fats = 5.0,
            sugar = 10.0,
            salt = 1.0,
            carbohydrates = 50.0,
            price = 10.0,
            foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
            foodTypes = setOf(FoodType.PLATO_LIGERO)
        )*/

        FoodListDialog(
            foodViewModels = foodsDatabase,
            onDismiss = {
                selectedFood = null
                addFood = false
            }
        )
    }

    LaunchedEffect(selectedDay) {
        if (dietViewModel1.getDiet().diets.isNotEmpty()) {
            foodsForSelectedDay = dietViewModel1.getDiet().diets[selectedDay - 1].getDiet().foods
        }
        val date = Date() // Cambia la fecha si es necesario
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            BackButton(onNavigateBack = { navController.popBackStack() })

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            GreenInfoCard(
                title = "Objetivo",
                content = dietViewModel1.getDiet().goal.toString(),
            )
        }

        if (dietViewModel1.getDiet().diets.isNotEmpty()) {
            DaysList(
                dietViewModel = dietViewModel1,
                selectedDay = selectedDay, // Pasamos el día seleccionado
                onDaySelected = { day ->
                    selectedDay = day
                }
            )

            Divider(
                color = Color.Black,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 24.dp)
            )

            DayDiet(
                dayDietDayViewModel = dietViewModel1.getDiet().diets[selectedDay - 1],
                foods = foodsForSelectedDay, // Usamos la lista de alimentos
                onFoodSelected = { food ->
                    selectedFood = food // Actualizamos la comida seleccionada
                }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    addFood = true
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50)), // Verde
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("+", color = Color.White, fontSize = 32.sp)
            }
        }

        /*
        Button(
            onClick = {
                var foodViewModel1 = FoodViewModel()
                foodViewModel1.updateFood(
                    name = "Avena con frutas",
                    calories = 300.0,
                    protein = 10.0,
                    fats = 5.0,
                    sugar = 10.0,
                    salt = 1.0,
                    carbohydrates = 50.0,
                    price = 10.0,
                    foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                    foodTypes = setOf(FoodType.PLATO_LIGERO)
                )
                var foodViewModel3 = FoodViewModel()
                foodViewModel3.updateFood(
                    name = "Jugo de naranja",
                    calories = 70.0,
                    protein = 0.0,
                    fats = 0.0,
                    sugar = 15.0,
                    salt = 0.2,
                    carbohydrates = 20.0,
                    price = 3.0,
                    foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                    foodTypes = setOf(FoodType.BEBIDA)
                )
                var foodViewModel2 = FoodViewModel()
                foodViewModel2.updateFood(
                    name = "Tortilla de vegetales",
                    calories = 250.0,
                    protein = 8.0,
                    fats = 3.0,
                    sugar = 8.0,
                    salt = 0.5,
                    carbohydrates = 40.0,
                    price = 8.0,
                    foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                    foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
                )
                var foodViewModel4 = FoodViewModel()
                foodViewModel4.updateFood(
                    name = "Ensalada mixta",
                    calories = 150.0,
                    protein = 5.0,
                    fats = 2.0,
                    sugar = 5.0,
                    salt = 0.3,
                    carbohydrates = 30.0,
                    price = 6.0,
                    foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                    foodTypes = setOf(FoodType.PLATO_SECUNDARIO)
                )
                var foodViewModel5 = FoodViewModel()
                foodViewModel5.updateFood(
                    name = "Agua con gas",
                    calories = 0.0,
                    protein = 0.0,
                    fats = 0.0,
                    sugar = 0.0,
                    salt = 0.0,
                    carbohydrates = 0.0,
                    price = 1.0,
                    foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                    foodTypes = setOf(FoodType.BEBIDA)
                )
                var foodViewModel6 = FoodViewModel()
                foodViewModel6.updateFood(
                    name = "Pechuga de pavo al horno",
                    calories = 300.0,
                    protein = 20.0,
                    fats = 10.0,
                    sugar = 5.0,
                    salt = 2.0,
                    carbohydrates = 0.0,
                    price = 15.0,
                    foodVariants = setOf(),
                    foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
                )
                var foodViewModel7 = FoodViewModel()
                foodViewModel7.updateFood(
                    name = "Smoothie de mango",
                    calories = 100.0,
                    protein = 1.0,
                    fats = 0.0,
                    sugar = 20.0,
                    salt = 0.5,
                    carbohydrates = 20.0,
                    price = 5.0,
                    foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                    foodTypes = setOf(FoodType.BEBIDA)
                )
                var foodListDiet1 = listOf(foodViewModel1,foodViewModel3, foodViewModel2, foodViewModel4, foodViewModel5, foodViewModel6, foodViewModel7)

                // Desayuno ligero
                val breakfastViewModel = FoodViewModel().apply {
                    updateFood(
                        name = "Yogur griego con granola",
                        calories = 280.0,
                        protein = 15.0,  // Alto en proteína por el yogur griego
                        fats = 8.0,
                        sugar = 12.0,
                        salt = 0.3,
                        carbohydrates = 35.0,
                        price = 12.0,
                        foodVariants = setOf(FoodVariant.VEGETARIAN),
                        foodTypes = setOf(FoodType.PLATO_LIGERO)
                    )
                }

// Bebida matutina
                val greenTeaViewModel = FoodViewModel().apply {
                    updateFood(
                        name = "Té verde matcha",
                        calories = 5.0,
                        protein = 0.5,
                        fats = 0.0,
                        sugar = 0.0,
                        salt = 0.0,
                        carbohydrates = 1.0,
                        price = 4.0,
                        foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                        foodTypes = setOf(FoodType.BEBIDA)
                    )
                }

// Plato principal almuerzo
                val salmonViewModel = FoodViewModel().apply {
                    updateFood(
                        name = "Salmón a la parrilla",
                        calories = 350.0,
                        protein = 25.0,  // Rico en proteínas
                        fats = 18.0,    // Grasas saludables omega-3
                        sugar = 2.0,
                        salt = 1.2,
                        carbohydrates = 5.0,
                        price = 18.0,
                        foodVariants = setOf(),
                        foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
                    )
                }

// Acompañamiento
                val quinoaViewModel = FoodViewModel().apply {
                    updateFood(
                        name = "Ensalada de quinoa",
                        calories = 220.0,
                        protein = 8.0,
                        fats = 6.0,
                        sugar = 3.0,
                        salt = 0.4,
                        carbohydrates = 35.0,
                        price = 9.0,
                        foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN, FoodVariant.CELIAC),
                        foodTypes = setOf(FoodType.PLATO_SECUNDARIO)
                    )
                }

// Bebida refrescante
                val lemonadeViewModel = FoodViewModel().apply {
                    updateFood(
                        name = "Limonada natural",
                        calories = 90.0,
                        protein = 0.3,
                        fats = 0.1,
                        sugar = 22.0,
                        salt = 0.1,
                        carbohydrates = 24.0,
                        price = 5.0,
                        foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                        foodTypes = setOf(FoodType.BEBIDA)
                    )
                }

// Cena principal
                val chickenViewModel = FoodViewModel().apply {
                    updateFood(
                        name = "Pollo al curry",
                        calories = 320.0,
                        protein = 28.0,
                        fats = 12.0,
                        sugar = 8.0,    // Por el curry y coco
                        salt = 1.5,
                        carbohydrates = 20.0,
                        price = 16.0,
                        foodVariants = setOf(),
                        foodTypes = setOf(FoodType.PLATO_PRINCIPAL)
                    )
                }

// Bebida nocturna
                val chamomileTeaViewModel = FoodViewModel().apply {
                    updateFood(
                        name = "Infusión de manzanilla",
                        calories = 2.0,
                        protein = 0.0,
                        fats = 0.0,
                        sugar = 0.0,
                        salt = 0.0,
                        carbohydrates = 0.5,
                        price = 3.0,
                        foodVariants = setOf(FoodVariant.VEGETARIAN, FoodVariant.VEGAN),
                        foodTypes = setOf(FoodType.BEBIDA)
                    )
                }

                var foodListDiet2 = listOf(breakfastViewModel, greenTeaViewModel, salmonViewModel, quinoaViewModel, lemonadeViewModel, chickenViewModel, chamomileTeaViewModel)

                var daydietViewModel1 = DietDayViewModel()
                var daydietViewModel12 = DietDayViewModel()

                daydietViewModel1.updateDietDay(foods = foodListDiet1)
                daydietViewModel12.updateDietDay(foods = foodListDiet2)

                var dayDietLists = listOf(daydietViewModel1, daydietViewModel12)

                dietViewModel1.updateDiet(diets = dayDietLists)
            },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Ver Gráfico")
        }
         */
    }
}
