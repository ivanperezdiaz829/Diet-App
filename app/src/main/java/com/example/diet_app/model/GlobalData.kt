package com.example.diet_app.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.diet_app.viewModel.DietDayViewModel
import com.example.diet_app.viewModel.DietViewModel
import com.example.diet_app.viewModel.FoodViewModel
import com.example.diet_app.viewModel.UserViewModel

object GlobalData {
    // Almacena los ViewModels de comida

    var userViewModel by mutableStateOf(UserViewModel())

    var foodViewModel by mutableStateOf(FoodViewModel())

    var foodList by mutableStateOf(listOf<FoodViewModel>())

    var dietsList by mutableStateOf(listOf<DietViewModel>())

    var dietsDayList by mutableStateOf(listOf<DietDayViewModel>())

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
    var dayDiet by mutableStateOf(DietDayViewModel())

    // Dieta principal
    var mainDiet by mutableStateOf(DietViewModel())

    init {
        // Configuración inicial
        initializeData()
    }

    fun initializeData() {

    }

    // Función para resetear datos
    fun reset() {
        foodViewModel = FoodViewModel()
        dayDiet = DietDayViewModel()
        mainDiet = DietViewModel()
        userViewModel = UserViewModel()
        foodList = listOf()
        dietsList = listOf()
        initializeData()
    }

    fun login(userViewModel: UserViewModel) {
        this.userViewModel = userViewModel
    }

    fun mainDietUpdate(dietViewModel: DietViewModel){
        mainDiet = dietViewModel
    }
}