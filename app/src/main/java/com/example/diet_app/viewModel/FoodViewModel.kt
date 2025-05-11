package com.example.diet_app.viewModel
import androidx.lifecycle.ViewModel
import com.example.diet_app.Plate
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

    val name: String
        get() = food.name

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
            val variants = mutableSetOf<FoodVariant>()
            val types = mutableSetOf<FoodType>()

            // Si vienen como enteros
            if (json.has("vegan") && json.getInt("vegan") == 1) variants.add(FoodVariant.VEGAN)
            if (json.has("vegetarian") && json.getInt("vegetarian") == 1) variants.add(FoodVariant.VEGETARIAN)
            if (json.has("celiac") && json.getInt("celiac") == 1) variants.add(FoodVariant.CELIAC)
            if (json.has("halal") && json.getInt("halal") == 1) variants.add(FoodVariant.HALAL)

            // Si vienen como array (mejor forma)
            if (json.has("foodVariants")) {
                val array = json.getJSONArray("foodVariants")
                for (i in 0 until array.length()) {
                    variants.add(FoodVariant.valueOf(array.getString(i)))
                }
            }

            if (json.has("foodTypes")) {
                val array = json.getJSONArray("foodTypes")
                for (i in 0 until array.length()) {
                    types.add(FoodType.valueOf(array.getString(i)))
                }
            } else if (json.has("type")) {
                // si viene como entero
                types.add(FoodType.fromTypeId(json.getInt("type")))
            }

            viewModel.updateFood(
                name = json.getString("name"),
                protein = json.getDouble("proteins"),
                fats = json.getDouble("fats"),
                sugar = json.getDouble("sugar"),
                salt = json.getDouble("sodium"),
                carbohydrates = json.getDouble("carbohydrates"),
                calories = json.getDouble("calories"),
                price = json.getDouble("price"),
                foodVariants = variants,
                foodTypes = types
            )
            return viewModel
        }
    }

}

fun FoodModel.toPlate(userId: String): Plate {
    // Convierte las variantes de comida a los flags numéricos (0 o 1)
    val isVegan = if (foodVariants.contains(FoodVariant.VEGAN)) 1 else 0
    val isVegetarian = if (foodVariants.contains(FoodVariant.VEGETARIAN)) 1 else 0
    val isCeliac = if (foodVariants.contains(FoodVariant.CELIAC)) 1 else 0
    val isHalal = if (foodVariants.contains(FoodVariant.HALAL)) 1 else 0

    // Obtiene el tipo de comida (asumiendo que solo hay un tipo)
    val foodTypeId = foodTypes.firstOrNull()?.let { type ->
        when (type) {
            FoodType.PLATO_LIGERO -> 1
            FoodType.PLATO_PRINCIPAL -> 2
            FoodType.PLATO_SECUNDARIO -> 3
            FoodType.BEBIDA -> 4
            FoodType.POSTRE -> 5
        }
    } ?: 1 // Valor por defecto si no hay tipo

    return Plate(
        id = foodId,
        name = name,
        user_id = userId,
        calories = calories.toInt(),
        carbohydrates = carbohydrates.toInt(),
        proteins = protein.toInt(),
        fats = fats.toInt(),
        sugar = sugar.toInt(),
        sodium = salt.toInt(),
        price = price,
        type = foodTypeId,
        vegan = isVegan,
        vegetarian = isVegetarian,
        celiac = isCeliac,
        halal = isHalal
    )
}
