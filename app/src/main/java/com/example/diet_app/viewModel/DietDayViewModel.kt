package com.example.diet_app.viewModel

import com.example.diet_app.model.DietDayModel
import com.example.diet_app.model.DietModel
import com.example.diet_app.model.FoodModel
import com.example.diet_app.model.FoodVariant
import com.example.diet_app.model.Goal
import com.example.diet_app.model.UserModel
import org.json.JSONArray
import org.json.JSONObject

class DietDayViewModel {
    private var currentDietDay: DietDayModel = DietDayModel()

    fun updateDietDay(
        foods: List<FoodViewModel> = currentDietDay.foods,
        foodVariant: FoodVariant = currentDietDay.foodVariant,
        goal: Goal = currentDietDay.goal,
        dietId: String = currentDietDay.dietId
    ) {
        currentDietDay = DietDayModel(
            foods = foods,
            foodVariant = foodVariant,
            goal = goal,
            dietId = dietId
        )
    }

    fun getDiet(): DietDayModel {
        return currentDietDay
    }

    // En DietDayViewModel
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("foods", JSONArray().apply {
                currentDietDay.foods.forEach { food ->
                    put(food.toJson()) // Necesitarás implementar toJson() en FoodViewModel
                }
            })
            put("dietId", currentDietDay.dietId)
            // Añade más campos según necesites
        }
    }
}