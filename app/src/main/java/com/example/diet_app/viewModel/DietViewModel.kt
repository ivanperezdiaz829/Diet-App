package com.example.diet_app.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.diet_app.DietPlanFromPlatesSelectedComplete
import com.example.diet_app.model.DietModel
import com.example.diet_app.model.FoodVariant
import com.example.diet_app.model.Goal
import com.example.diet_app.model.UserModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class DietViewModel: ViewModel() {

    private var currentDiet: DietModel = DietModel()

    fun updateDiet(
        name: String = currentDiet.name,
        userModel: UserModel = currentDiet.userModel,
        duration: Int = currentDiet.duration,
        creationDate: String = currentDiet.creationDate,
        diets: List<DietDayViewModel> = currentDiet.diets,
        dietsId: List<Int> = currentDiet.dietsId,
        foodVariant: FoodVariant = currentDiet.foodVariant,
        goal: Goal = currentDiet.goal,
        dietId: String = currentDiet.dietId
    ) {
        currentDiet = DietModel(
            name = name,
            userModel = userModel,
            duration = duration,
            creationDate = creationDate,
            diets = diets,
            foodVariant = foodVariant,
            goal = goal,
            dietId = dietId,
            dietsId = dietsId
        )
    }

    fun getDiet(): DietModel {
        return currentDiet
    }

    fun getDayByIndex(index: Int): DietDayViewModel? {
        return currentDiet.diets.getOrNull(index)
    }

    // En DietViewModel
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("name", currentDiet.name)
            put("duration", currentDiet.duration)
            put("dietId", currentDiet.dietId)
            put("creationDate", currentDiet.creationDate)
            put("diets", JSONArray().apply {
                currentDiet.diets.forEach { dietDay ->
                    put(dietDay.toJson())
                }
            })
            // Añade más campos según necesites
        }
    }

    fun saveDietToPreferences(context: Context, diet: DietViewModel) {
        val prefs = context.getSharedPreferences("DietData", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        try {
            // Guardar la dieta principal
            editor.putString("current_diet", diet.toJson().toString())

            // Guardar días individualmente (opcional, para fácil acceso)
            diet.getDiet().diets.forEachIndexed { index, day ->
                editor.putString("diet_day_$index", day.toJson().toString())
            }

            editor.apply()
        } catch (e: JSONException) {
            Log.e("SaveDiet", "Error al serializar dieta", e)
        }
    }
    fun toDietPlanFromPlatesSelectedComplete() : DietPlanFromPlatesSelectedComplete {
        for (i in 0 until 7) {
            val foodsIds = currentDiet.diets[i].getDiet().foodsId
            Log.d("DietPlanCheck", "Day ${i + 1} foodsId count: ${foodsIds.size}")
        }
        return DietPlanFromPlatesSelectedComplete(
            currentDiet.name,
            currentDiet.userModel.id,
            currentDiet.diets[0].getDiet().foodsId,
            currentDiet.diets[1].getDiet().foodsId,
            currentDiet.diets[2].getDiet().foodsId,
            currentDiet.diets[3].getDiet().foodsId,
            currentDiet.diets[4].getDiet().foodsId,
            currentDiet.diets[5].getDiet().foodsId,
            currentDiet.diets[6].getDiet().foodsId,
            currentDiet.foodVariant.ordinal,
            currentDiet.duration
        )
    }

    fun validateDietDaysHave7Foods(): Boolean {
        return currentDiet.diets.all { dietDay ->
            dietDay.getDiet().foods.size == 7
        }
    }

}