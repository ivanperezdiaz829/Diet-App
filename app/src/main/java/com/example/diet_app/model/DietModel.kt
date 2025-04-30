package com.example.diet_app.model

class DietModel(
    var name: String = "",
    var userModel: UserModel = UserModel(),
    var breakfast: FoodModel = FoodModel(),
    var lunch: FoodModel = FoodModel(),
    var snack: FoodModel = FoodModel(),
    var dinner: FoodModel = FoodModel(),
    var duration: Int = 0,
) {

}