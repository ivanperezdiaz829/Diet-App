package com.example.diet_app.viewModel

import com.example.diet_app.model.DietDayModel
import com.example.diet_app.model.DietModel
import com.example.diet_app.model.FoodModel
import com.example.diet_app.model.UserModel

class DietDayViewModel {
    private var currentDietDay: DietDayModel = DietDayModel()

    fun updateDietDay(
        foods: List<FoodViewModel> = currentDietDay.foods
    ) {
        currentDietDay = DietDayModel(
            foods = foods
        )
    }

    fun getDiet(): DietDayModel {
        return currentDietDay
    }
}