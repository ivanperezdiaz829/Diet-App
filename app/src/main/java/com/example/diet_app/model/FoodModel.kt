package com.example.diet_app.model

class FoodModel(
    var name: String = "",
    var protein: Double = 0.0,
    var fats: Double = 0.0,
    var sugar: Double = 0.0,
    var salt: Double = 0.0,
    var carbohydrates: Double = 0.0,
    var calories: Double = 0.0,
    var price: Double = 0.0,
    var foodVariants: Set<FoodVariant> = emptySet(),
    var foodTypes: Set<FoodType> = emptySet(),
) {

}