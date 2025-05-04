package com.example.diet_app.model

enum class FoodType {
    LIGHT_MEAL,
    MAIN_DISH,
    SIDE_DISH,
    DRINK,
    DESSERT;

    companion object {
        fun fromTypeId(typeId: Int): FoodType {
            return when (typeId) {
                1  ->  LIGHT_MEAL
                2  ->  MAIN_DISH
                3  ->  SIDE_DISH
                4  ->  DRINK
                5  ->  DESSERT
                else -> throw IllegalArgumentException("Unknown food type ID: $typeId")
            }
        }
    }
}