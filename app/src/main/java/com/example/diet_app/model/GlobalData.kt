package com.example.diet_app.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.diet_app.viewModel.DietDayViewModel
import com.example.diet_app.viewModel.DietViewModel
import com.example.diet_app.viewModel.FoodViewModel

object GlobalData {
    // Almacena los ViewModels de comida
    var food1 by mutableStateOf(FoodViewModel().apply {
        updateFood(name = "Croissant", foodTypes = setOf(FoodType.LIGHT_MEAL))
    })

    var food2 by mutableStateOf(FoodViewModel().apply {
        updateFood(name = "Rice", foodTypes = setOf(FoodType.MAIN_DISH))
    })

    var food3 by mutableStateOf(FoodViewModel().apply {
        updateFood(name = "Sandwich", foodTypes = setOf(FoodType.LIGHT_MEAL, FoodType.SIDE_DISH))
    })

    // Almacena los días de dieta
    private var day1 by mutableStateOf(DietDayViewModel())
    private var day2 by mutableStateOf(DietDayViewModel())

    // Dieta principal
    var mainDiet by mutableStateOf(DietViewModel())

    init {
        // Configuración inicial
        initializeData()
    }

    fun initializeData() {
        day1.updateDietDay(foods = listOf(food1, food2, food3))
        day2.updateDietDay(foods = listOf(food3, food2, food1))
        mainDiet.updateDiet(
            name = "Dieta 1",
            duration = 2,
            diets = listOf(day1, day2),
            dietId = "1"
        )
    }

    // Función para resetear datos
    fun reset() {
        food1 = FoodViewModel()
        food2 = FoodViewModel()
        food3 = FoodViewModel()
        day1 = DietDayViewModel()
        day2 = DietDayViewModel()
        mainDiet = DietViewModel()
        initializeData()
    }
}