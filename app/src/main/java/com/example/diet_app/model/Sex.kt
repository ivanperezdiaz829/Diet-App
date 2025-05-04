package com.example.diet_app.model

enum class Sex {
    Male,
    Female,
}

fun getSexInt(sex: Sex): Int {
    return when (sex) {
        Sex.Male -> 1
        Sex.Female -> 0
    }
}