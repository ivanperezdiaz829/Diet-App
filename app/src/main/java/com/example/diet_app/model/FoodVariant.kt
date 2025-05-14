package com.example.diet_app.model

enum class FoodVariant {
    REGULAR,
    VEGETARIAN,
    VEGAN,
    CELIAC,
    HALAL
}

fun getFoodVariantFromIndex(index: Int): FoodVariant {
    return FoodVariant.entries.get(index)
}

fun getFoodIndexFromVariant(foodVariant: FoodVariant): Int {
    return when (foodVariant) {
        FoodVariant.REGULAR -> 1
        FoodVariant.VEGAN -> 2
        FoodVariant.VEGETARIAN -> 3
        FoodVariant.CELIAC -> 4
        FoodVariant.HALAL -> 5
    }
}