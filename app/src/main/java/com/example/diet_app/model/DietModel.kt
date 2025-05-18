package com.example.diet_app.model

import com.example.diet_app.viewModel.DietDayViewModel

class DietModel(
    var name: String = "",
    var dietId: String = "",
    var userModel: UserModel = UserModel(),
    var duration: Int = 0,
    var creationDate: String = "",
    var dietsId: List<Int> = mutableListOf(),
    var diets: List<DietDayViewModel> = mutableListOf(),
    var foodVariant: FoodVariant = FoodVariant.REGULAR,
    var goal: Goal = Goal.MANTENERSE
) {

}