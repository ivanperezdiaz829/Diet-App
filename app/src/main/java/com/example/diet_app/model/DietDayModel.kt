package com.example.diet_app.model

import com.example.diet_app.viewModel.FoodViewModel

class DietDayModel(
    var foods: List<FoodViewModel> = emptyList(),
    var foodVariant: FoodVariant = FoodVariant.REGULAR,
    var goal: Goal = Goal.STAY_HEALTHY
) {
}