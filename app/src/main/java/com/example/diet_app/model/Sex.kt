package com.example.diet_app.model

enum class Sex {
    Hombre,
    Mujer,
}

fun getSexInt(sex: Sex): Int {
    return when (sex) {
        Sex.Hombre -> 1
        Sex.Mujer -> 0
    }
}