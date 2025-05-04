package com.example.diet_app.model

import com.example.diet_app.viewModel.FoodViewModel

class DietDayModel(
    var foods: List<FoodViewModel> = emptyList(),
    var dietId: Int = 0,
    var foodsId: List<Int> = emptyList(),
    var foodVariant: FoodVariant = FoodVariant.REGULAR,
    var goal: Goal = Goal.STAY_HEALTHY
) {
}