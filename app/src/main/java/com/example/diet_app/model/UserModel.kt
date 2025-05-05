package com.example.diet_app.model

import com.example.diet_app.viewModel.FoodViewModel

class UserModel(
    var id: Int = 0,
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var age: String = "",
    var sex: Sex = Sex.Hombre,
    var height: Int = 0,
    var currentWeight: Double = 0.0,
    var goal: Goal = Goal.MANTENERSE,
    var foodList: MutableList<FoodViewModel> = mutableListOf()
) {
    // Todas las propiedades son mutables (var) y tienen valores por defecto
    // Kotlin genera automáticamente getters y setters
}