package com.example.diet_app.model

enum class FoodType {
    PLATO_LIGERO,
    PLATO_PRINCIPAL,
    PLATO_SECUNDARIO,
    BEBIDA,
    POSTRE;

    companion object {
        fun fromTypeId(typeId: Int): FoodType {
            return when (typeId) {
                1  ->  PLATO_LIGERO
                2  ->  PLATO_PRINCIPAL
                3  ->  PLATO_SECUNDARIO
                4  ->  BEBIDA
                5  ->  POSTRE
                else -> throw IllegalArgumentException("Unknown food type ID: $typeId")
            }
        }
    }
}