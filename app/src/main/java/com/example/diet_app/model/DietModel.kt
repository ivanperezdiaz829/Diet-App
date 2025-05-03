package com.example.diet_app.model

import com.example.diet_app.viewModel.DietDayViewModel

class DietModel(
    var name: String = "",
    var userModel: UserModel = UserModel(),
    var duration: Int = 0,
    var creationDate: String = "",
    var diets: List<DietDayViewModel> = emptyList(),
    var foodVariant: FoodVariant = FoodVariant.REGULAR,
    var goal: Goal = Goal.STAY_HEALTHY
) {

}