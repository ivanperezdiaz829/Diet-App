package com.example.diet_app.model

import com.example.diet_app.screenActivities.Goal
import com.example.diet_app.screenActivities.Sex
import com.example.diet_app.viewModel.FoodViewModel

class UserModel(
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var age: Int = 0,
    var sex: Sex = Sex.MALE,
    var height: Int = 0,
    var currentWeight: Double = 0.0,
    var goal: Goal = Goal.STAY_HEALTHY,
    var targetWeight: Int = 0,
    var foodList: List<FoodViewModel> = emptyList()
) {
    // Todas las propiedades son mutables (var) y tienen valores por defecto
    // Kotlin genera automáticamente getters y setters
}