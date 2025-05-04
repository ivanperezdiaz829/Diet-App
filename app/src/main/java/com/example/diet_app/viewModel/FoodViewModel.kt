package com.example.diet_app.viewModel
import androidx.lifecycle.ViewModel
import com.example.diet_app.model.FoodModel
import com.example.diet_app.model.FoodType
import com.example.diet_app.model.FoodVariant
import com.example.diet_app.model.DietDayModel
import com.example.diet_app.model.DietModel
import com.example.diet_app.model.Goal
import com.example.diet_app.model.UserModel
import org.json.JSONArray
import org.json.JSONObject

class FoodViewModel : ViewModel() {
    private var food: FoodModel = FoodModel() // Inicialización por defecto

    fun updateFood(
        name: String = food.name,
        foodId: Int = food.foodId,
        protein: Double = food.protein,
        fats: Double = food.fats,
        sugar: Double = food.sugar,
        salt: Double = food.salt,
        carbohydrates: Double = food.carbohydrates,
        calories: Double = food.calories,
        price: Double = food.price,
        foodVariants: Set<FoodVariant> = food.foodVariants,
        foodTypes: Set<FoodType> = food.foodTypes
    ) {
        food = FoodModel(
            name = name,
            foodId = foodId,
            protein = protein,
            fats = fats,
            sugar = sugar,
            salt = salt,
            carbohydrates = carbohydrates,
            calories = calories,
            price = price,
            foodVariants = foodVariants,
            foodTypes = foodTypes
        )
    }

    // Métodos para acceder y modificar propiedades individuales
    fun getFood(): FoodModel = food

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("name", food.name)
            put("proteins", food.protein)
            put("fats", food.fats)
            put("sugar", food.sugar)
            put("sodium", food.salt)
            put("carbohydrates", food.carbohydrates)
            put("calories", food.calories)
            put("price", food.price)

            // Serializar conjunto de FoodVariant
            val variantsArray = JSONArray()
            food.foodVariants.forEach { variant ->
                variantsArray.put(variant.name) // Guardamos el nombre del enum
            }
            put("foodVariants", variantsArray)

            // Serializar conjunto de FoodType
            val typesArray = JSONArray()
            food.foodTypes.forEach { type ->
                typesArray.put(type.name) // Guardamos el nombre del enum
            }
            put("foodTypes", typesArray)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): FoodViewModel {
            val viewModel = FoodViewModel()
            viewModel.updateFood(
                name = json.getString("name"),
                protein = json.getDouble("proteins"),
                fats = json.getDouble("fats"),
                sugar = json.getDouble("sugar"),
                salt = json.getDouble("sodium"),
                carbohydrates = json.getDouble("carbohydrates"),
                calories = json.getDouble("calories"),
                price = json.getDouble("price"),
                foodVariants = mutableSetOf<FoodVariant>().apply {
                    if (json.optInt("vegan", 0) == 1) add(FoodVariant.VEGAN)
                    if (json.optInt("vegetarian", 0) == 1) add(FoodVariant.VEGETARIAN)
                    if (json.optInt("celiac", 0) == 1) add(FoodVariant.CELIAC)
                    if (json.optInt("halal", 0) == 1) add(FoodVariant.HALAL)
                },
                foodTypes = setOf(FoodType.fromTypeId(json.getInt("type"))) // Ahora funciona
            )
            return viewModel
        }
    }
}
