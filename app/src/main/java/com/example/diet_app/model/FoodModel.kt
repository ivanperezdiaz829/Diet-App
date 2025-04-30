package com.example.diet_app.model

enum class FoodType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
}

class FoodModel(
    var name: String = "",
    var protein: Double = 0.0,
    var fats: Double = 0.0,
    var sugar: Double = 0.0,
    var salt: Double = 0.0,
    var carbohydrates: Double = 0.0,
    var calories: Double = 0.0,
    var price: Double = 0.0,
    var vegetarian: Boolean = false,
    var vegan: Boolean = false,
    var celiac: Boolean = false,
    var halal: Boolean = false,
    var type: FoodType = FoodType.BREAKFAST
) {

}