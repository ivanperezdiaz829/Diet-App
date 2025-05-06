package com.example.diet_app.viewModel

import androidx.lifecycle.ViewModel
import com.example.diet_app.model.Goal
import com.example.diet_app.model.UserModel
import com.example.diet_app.model.Sex

class UserViewModel : ViewModel() {
    private var currentUser: UserModel = UserModel() // Inicialización por defecto

    fun updateUser(
        id: Int = currentUser.id,
        name: String = currentUser.name,
        email: String = currentUser.email,
        password: String = currentUser.password,
        age: String = currentUser.age,
        sex: Sex = currentUser.sex,
        height: Int = currentUser.height,
        currentWeight: Double = currentUser.currentWeight,
        goal: Goal = currentUser.goal,
        foodList: MutableList<FoodViewModel> = currentUser.foodList
    ) {
        currentUser = UserModel(
            id = id,
            name = name,
            email = email,
            password = password,
            age = age,
            sex = sex,
            height = height,
            currentWeight = currentWeight,
            goal = goal,
            foodList = foodList
        )
    }

    // Métodos para acceder y modificar propiedades individuales
    fun getUser(): UserModel = currentUser
}