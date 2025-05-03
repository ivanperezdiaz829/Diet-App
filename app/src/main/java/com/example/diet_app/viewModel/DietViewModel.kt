package com.example.diet_app.viewModel

import androidx.lifecycle.ViewModel
import com.example.diet_app.model.DietDayModel
import com.example.diet_app.model.DietModel
import com.example.diet_app.model.FoodModel
import com.example.diet_app.model.UserModel

class DietViewModel: ViewModel() {

    private var currentDiet: DietModel = DietModel()

    fun updateDiet(
        name: String = currentDiet.name,
        userModel: UserModel = currentDiet.userModel,
        duration: Int = currentDiet.duration,
        creationDate: String = currentDiet.creationDate,
        diets: List<DietDayViewModel> = currentDiet.diets
    ) {
        currentDiet = DietModel(
            name = name,
            userModel = userModel,
            duration = duration,
            creationDate = creationDate,
            diets = diets
        )
    }

    fun getDiet(): DietModel {
        return currentDiet
    }
}