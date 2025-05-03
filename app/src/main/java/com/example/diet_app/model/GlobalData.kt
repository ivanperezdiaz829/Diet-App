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

    val dietJson = """
{
  "dieta": [
    [
      {
        "name": "Manzana",
        "calories": 52,
        "carbohydrates": 14,
        "protein": 0.3,
        "fat": 0.2,
        "sugar": 10,
        "salt": 0,
        "price": 0.5
      },
      {
        "name": "Yogur",
        "calories": 59,
        "carbohydrates": 3.6,
        "protein": 10,
        "fat": 1.5,
        "sugar": 4.7,
        "salt": 0.1,
        "price": 0.8
      }
    ],
    [
      {
        "name": "Pasta",
        "calories": 131,
        "carbohydrates": 25,
        "protein": 5,
        "fat": 1.1,
        "sugar": 1,
        "salt": 0.3,
        "price": 1.2
      },
      {
        "name": "Ensalada",
        "calories": 33,
        "carbohydrates": 6,
        "protein": 1.5,
        "fat": 0.5,
        "sugar": 2,
        "salt": 0.2,
        "price": 1.0
      },
      {
        "name": "Agua",
        "calories": 0,
        "carbohydrates": 0,
        "protein": 0,
        "fat": 0,
        "sugar": 0,
        "salt": 0,
        "price": 0
      }
    ],
    [
      {
        "name": "Sopa",
        "calories": 80,
        "carbohydrates": 10,
        "protein": 3,
        "fat": 2,
        "sugar": 1,
        "salt": 0.4,
        "price": 1.0
      },
      {
        "name": "Pan integral",
        "calories": 69,
        "carbohydrates": 12,
        "protein": 2.5,
        "fat": 1.2,
        "sugar": 1,
        "salt": 0.3,
        "price": 0.6
      }
    ]
  ]
}
""".trimIndent()

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