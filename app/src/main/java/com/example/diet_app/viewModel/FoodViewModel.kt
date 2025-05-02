package com.example.diet_app.viewModel
import androidx.lifecycle.ViewModel
import com.example.diet_app.model.FoodModel
import com.example.diet_app.model.FoodType
import com.example.diet_app.model.FoodVariant

class FoodViewModel : ViewModel() {
    private var food: FoodModel = FoodModel() // Inicialización por defecto

    fun updateFood(
        name: String = food.name,
        protein: Double = food.protein,
        fats: Double = food.fats,
        sugar: Double = food.sugar,
        salt: Double = food.salt,
        carbohydrates: Double = food.carbohydrates,
        calories: Double = food.calories,
        price: Double = food.price,
        foodVariants: Set<FoodVariant> = food.foodVariants,
        foodTypes: Set<FoodType> = food.foodTypes
    ) {
        food = FoodModel(
            name = name,
            protein = protein,
            fats = fats,
            sugar = sugar,
            salt = salt,
            carbohydrates = carbohydrates,
            calories = calories,
            price = price,
            foodVariants = foodVariants,
            foodTypes = foodTypes
        )
    }

    // Métodos para acceder y modificar propiedades individuales
    fun getFood(): FoodModel = food
}