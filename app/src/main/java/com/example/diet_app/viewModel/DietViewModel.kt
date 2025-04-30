package com.example.diet_app.viewModel

import androidx.lifecycle.ViewModel
import com.example.diet_app.model.DietModel
import com.example.diet_app.model.FoodModel
import com.example.diet_app.model.UserModel

class DietViewModel: ViewModel() {

    private var currentDiet: DietModel = DietModel()

    fun updateDiet(
        name: String = currentDiet.name,
        userModel: UserModel = currentDiet.userModel,
        breakfast: FoodModel = currentDiet.breakfast,
        lunch: FoodModel = currentDiet.lunch,
        snack: FoodModel = currentDiet.snack,
        dinner: FoodModel = currentDiet.dinner,
        duration: Int = currentDiet.duration
    ) {
        currentDiet = DietModel(
            name = name,
            userModel = userModel,
            breakfast = breakfast,
            lunch = lunch,
            snack = snack,
            dinner = dinner,
            duration = duration
        )
    }

    fun getDiet(): DietModel {
        return currentDiet
    }
}