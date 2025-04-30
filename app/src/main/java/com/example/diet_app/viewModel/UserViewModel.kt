package com.example.diet_app.viewModel
import androidx.lifecycle.ViewModel
import com.example.diet_app.model.UserModel

class UserViewModel : ViewModel() {
    private var currentUser: UserModel = UserModel() // Inicialización por defecto

    fun updateUser(
        name: String = currentUser.name,
        email: String = currentUser.email,
        password: String = currentUser.password,
        age: Int = currentUser.age,
        sex: String = currentUser.sex,
        height: Int = currentUser.height,
        currentWeight: Double = currentUser.currentWeight,
        goal: String = currentUser.goal,
        targetWeight: Int = currentUser.targetWeight
    ) {
        currentUser = UserModel(
            name = name,
            email = email,
            password = password,
            age = age,
            sex = sex,
            height = height,
            currentWeight = currentWeight,
            goal = goal,
            targetWeight = targetWeight
        )
    }

    // Métodos para acceder y modificar propiedades individuales
    fun getUser(): UserModel = currentUser
}