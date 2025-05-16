package com.example.diet_app.viewModel

import com.example.diet_app.model.DietDayModel
import com.example.diet_app.model.FoodVariant
import com.example.diet_app.model.Goal
import org.json.JSONArray
import org.json.JSONObject

class DietDayViewModel {
    private var currentDietDay: DietDayModel = DietDayModel()

    fun updateDietDay(
        foods: MutableList<FoodViewModel> = currentDietDay.foods,
        foodVariant: FoodVariant = currentDietDay.foodVariant,
        goal: Goal = currentDietDay.goal,
        dietId: Int = currentDietDay.dietId,
        foodsId: List<Int>? = null
    ) {
        val finalFoodsId = foodsId ?: foods.map { it.foodId }
        currentDietDay = DietDayModel(
            foods = foods,
            foodVariant = foodVariant,
            goal = goal,
            dietId = dietId,
            foodsId = finalFoodsId
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
    /*
    fun loadFromStorage(
        context: Context,
        date: Date,
        foodVariant: FoodVariant = FoodVariant.REGULAR,
        goal: Goal = Goal.STAY_HEALTHY,
        dietId: String = "generated-${System.currentTimeMillis()}",
        onLoaded: (DietDayViewModel) -> Unit
    ) {
        getPlateById(context, date) { foodList ->
            updateDietDay(
                foods = foodList,
                foodVariant = foodVariant,
                goal = goal,
                dietId = dietId
            )
            onLoaded(this)
        }
    }*/