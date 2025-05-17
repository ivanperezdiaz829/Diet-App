package com.example.diet_app

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import com.example.diet_app.model.FoodType
import com.example.diet_app.model.FoodVariant
import com.example.diet_app.model.Goal
import com.example.diet_app.model.Sex
import com.example.diet_app.viewModel.DietDayViewModel
import com.example.diet_app.viewModel.DietViewModel
import com.example.diet_app.viewModel.FoodViewModel
import com.example.diet_app.viewModel.UserViewModel
import com.example.diet_app.viewModel.toPlate
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


import com.google.gson.Gson


fun DietInformationResponse.toDietViewModels(): MutableList<DietViewModel> {
    val dietViewModels = mutableListOf<DietViewModel>()
    
    Log.d("DIET_CONVERSION", "Iniciando conversión de DietInformationResponse a DietViewModel")

    // Convertimos todos los días de dieta (esto se comparte entre todas las dietas)
    val dietDayViewModels = this.days_values.flatMap { dayDetails ->
        //Log.d("DIET_CONVERSION", "Procesando DietPlanDayDetails: ${dayDetails.diet_plan_name}, días: ${dayDetails.days_details.size}")

        dayDetails.days_details.filterNotNull().map { dietDay ->
            val dayViewModel = dietDay.toDietDayViewModel()
            //Log.d("DIET_CONVERSION", "Convertido DietPlanDay con ID: ${dietDay.day_id} a DietDayViewModel con ${dayViewModel.getDiet().foods.size} alimentos")
            dayViewModel
        }
    }

    // Procesamos cada plan de dieta completo
    this.diet_plans_complete.forEach { dietPlan ->
        //Log.d("DIET_CONVERSION", "Procesando DietPlanComplete: ${dietPlan.name} (ID: ${dietPlan.id})")

        val dietViewModel = DietViewModel().apply {
            updateDiet(
                name = dietPlan.name,
                duration = dietPlan.duration,
                creationDate = dietPlan.created_at,
                diets = dietDayViewModels,
                dietsId = listOfNotNull(
                    dietPlan.day1, dietPlan.day2, dietPlan.day3,
                    dietPlan.day4, dietPlan.day5, dietPlan.day6, dietPlan.day7
                ),
                foodVariant = when (dietPlan.diet_type_id) {
                    1 -> FoodVariant.REGULAR
                    2 -> FoodVariant.VEGAN
                    3 -> FoodVariant.VEGETARIAN
                    4 -> FoodVariant.CELIAC
                    5 -> FoodVariant.HALAL
                    else -> FoodVariant.REGULAR // Valor por defecto
                },
                dietId = dietPlan.id.toString()
            )
        }

        //Log.d("DIET_CONVERSION", "Agregado DietViewModel para el plan: ${dietPlan.name} con ${dietDayViewModels.size} días")

        dietViewModels.add(dietViewModel)
    }

    //Log.d("DIET_CONVERSION", "Conversión completa: ${dietViewModels.size} DietViewModels creados")
    Log.d("DIET_CONVERSION", "Conversión completa: DietViewModels creados")
    return dietViewModels
}

fun DietPlanDay.toDietDayViewModel(): DietDayViewModel {
    val dietDayViewModel = DietDayViewModel()

    val foodViewModels = this.plates.filterNotNull().map { plate ->
        val foodViewModel = FoodViewModel()

        // Determinar la variante de comida
        val variant = when {
            plate.vegan == 1 -> FoodVariant.VEGAN
            plate.vegetarian == 1 -> FoodVariant.VEGETARIAN
            plate.celiac == 1 -> FoodVariant.CELIAC
            plate.halal == 1 -> FoodVariant.HALAL
            else -> FoodVariant.REGULAR
        }

        foodViewModel.updateFood(
            name = plate.name,
            foodId = plate.id,
            protein = plate.proteins.toDouble(),
            fats = plate.fats.toDouble(),
            sugar = plate.sugar.toDouble(),
            salt = plate.sodium.toDouble(),
            carbohydrates = plate.carbohydrates.toDouble(),
            calories = plate.calories.toDouble(),
            price = plate.price,
            foodVariants = setOf(variant),
            foodTypes = setOf(FoodType.fromTypeId(plate.type))
        )
        foodViewModel
    }.toMutableList()

    dietDayViewModel.updateDietDay(
        foods = foodViewModels,
        foodVariant = FoodVariant.REGULAR,
        goal = Goal.MANTENERSE,
        dietId = this.day_id,
        foodsId = this.plates.filterNotNull().map { it.id }
    )

    return dietDayViewModel
}

// Representa la información del usuario
data class User(
    val id: Int,
    val email: String,
    val birthday: String,  // Podrías considerar usar un tipo de dato `Date`
    val goal: Int,
    val height: Int,
    val password: String,
    val physical_activity: Int,
    val sex: Int,
    val weight: Int
)

data class UserPlatesResponse(
    val plates: List<Plate>,
    val user: User
)

// Representa un plato, con los campos esenciales de la tabla `plates`
data class Plate(
    val id: Int,
    val name: String,
    val user_id: String, // Usé String porque el campo en la tabla es un texto
    val calories: Int,
    val carbohydrates: Double,
    val proteins: Double,
    val fats: Double,
    val sugar: Double,
    val sodium: Double,
    val price: Double,
    val type: Int,  // Puede ser un valor que represente el tipo del plato (por ejemplo, desayuno, comida, cena, etc.)
    val vegan: Int,  // Usualmente 0 o 1, indica si es vegano
    val vegetarian: Int, // Usualmente 0 o 1, indica si es vegetariano
    val celiac: Int, // Usualmente 0 o 1, indica si es libre de gluten
    val halal: Int // Usualmente 0 o 1, indica si es halal
)

fun convertPlatesToFoodViewModels(platesResult: Result<List<Plate>>): MutableList<FoodViewModel> {
    return platesResult.fold(
        onSuccess = { plates ->
            plates.mapTo(mutableListOf()) { plate ->
                FoodViewModel().apply {
                    // Convertir flags numéricos a Set<FoodVariant>
                    val variants = mutableSetOf<FoodVariant>().apply {
                        if (plate.vegan == 1) add(FoodVariant.VEGAN)
                        if (plate.vegetarian == 1) add(FoodVariant.VEGETARIAN)
                        if (plate.celiac == 1) add(FoodVariant.CELIAC)
                        if (plate.halal == 1) add(FoodVariant.HALAL)
                    }

                    // Convertir type a FoodType (asumiendo que tienes un método fromTypeId)
                    val foodType = FoodType.fromTypeId(plate.type)

                    updateFood(
                        name = plate.name,
                        foodId = plate.id,
                        protein = plate.proteins,
                        fats = plate.fats,
                        sugar = plate.sugar,
                        salt = plate.sodium,
                        carbohydrates = plate.carbohydrates,
                        calories = plate.calories.toDouble(), // Conversión Int a Double
                        price = plate.price,
                        foodVariants = variants,
                        foodTypes = setOf(foodType) // Set con un solo tipo
                    )
                }
            }
        },
        onFailure = { exception ->
            // Log del error (opcional)
            println("Error al convertir plates: ${exception.message}")
            mutableListOf() // Retorna lista vacía en caso de error
        }
    )
}

// Representa un plan de dieta completo, según la tabla `diet_plans_complete`
data class DietPlanComplete(
    val id: Int,
    val name: String,
    val user_id: Int,
    val created_at: String,  // Podrías usar un tipo `Date` si prefieres manejar fechas de manera más eficiente
    val day1: Int?,
    val day2: Int?,
    val day3: Int?,
    val day4: Int?,
    val day5: Int?,
    val day6: Int?,
    val day7: Int?,
    val diet_type_id: Int,
    val duration: Int
)

// Representa los detalles de un día específico en un plan de dieta
data class DietPlanDay(
    val day_id: Int,
    val plates: List<Plate?> // Lista de platos relacionados a este día
)

// Detalles completos de un plan de dieta, con información de los días de dieta
data class DietPlanDayDetails(
    val diet_plan_id: Int,
    val diet_plan_name: String,
    val days_details: List<DietPlanDay?> // Días de dieta asociados con el plan
)

// Respuesta completa del servidor que incluye la información del usuario, planes de dieta y detalles de los días
data class DietInformationResponse(
    val user: User,
    val diet_plans_complete: List<DietPlanComplete>,
    val days_values: List<DietPlanDayDetails>
)

// Función para deserializar la respuesta
fun deserializeDietInformation(jsonResponse: String): DietInformationResponse {
    val gson = Gson() // Creamos una instancia de Gson
    return gson.fromJson(jsonResponse, DietInformationResponse::class.java) // Deserializamos el JSON
}

// Función para hacer la solicitud y obtener la respuesta
fun getUserDietPlansCompletePro(userId: Int, context: Context, onResult: (String) -> Unit) {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Increase timeout
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    val url = "http://10.0.2.2:8000/get_all_diets_of_user_complete_information/$userId" // Asegúrate de usar la URL correcta

    val request = Request.Builder()
        .url(url)
        .build()

    // Hacer la solicitud HTTP
    client.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
            Log.e("API", "Error en la solicitud: ${e.message}")
            (context as? Activity)?.runOnUiThread {
                onResult("Error: ${e.message}")
            }
        }

        override fun onResponse(call: okhttp3.Call, response: Response) {
            if (response.isSuccessful) {
                val jsonResponse = response.body?.string() // Obtenemos la respuesta como String

                if (jsonResponse != null) {
                    // Deserializar el JSON a nuestro objeto de Kotlin
                    val dietInfoResponse = deserializeDietInformation(jsonResponse)

                    // Imprimir los datos en el log para verlos
                    //Log.d("API", "Usuario: ${dietInfoResponse.user}")
                    //Log.d("API", "Planes de dieta completos: ${dietInfoResponse.diet_plans_complete}")
                    //Log.d("API", "Detalles de los días: ${dietInfoResponse.days_values}")
                    Log.d("API", "Se han obtenido las dietas correctamente")


                    // Llamamos a la función onResult para manejar la respuesta en la interfaz de usuario
                    (context as? Activity)?.runOnUiThread {
                        onResult(jsonResponse)
                    }
                } else {
                    Log.e("API", "La respuesta es vacía o nula")
                    (context as? Activity)?.runOnUiThread {
                        onResult("Error: Respuesta vacía del servidor")
                    }
                }
            } else {
                Log.e("API", "Error en la respuesta: ${response.message}")
                (context as? Activity)?.runOnUiThread {
                    onResult("Error: ${response.message}")
                }
            }
        }
    })
}

fun getUserPlatesPro(userId: Int, context: Context, onResult: (String) -> Unit) {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Increase timeout
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    val url = "http://10.0.2.2:8000/get_all_user_plates/$userId" // Asegúrate de que coincida con el endpoint real

    val request = Request.Builder()
        .url(url)
        .get()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("API", "Error en la solicitud: ${e.message}")
            (context as? Activity)?.runOnUiThread {
                onResult("Error: ${e.message}")
            }
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val jsonResponse = response.body?.string()
                if (jsonResponse != null) {
                    //Log.d("API", "Respuesta recibida: $jsonResponse")
                    Log.d("API", "Se han obtenido los platos del usuario correctamente")
                    (context as? Activity)?.runOnUiThread {
                        onResult(jsonResponse)
                    }
                } else {
                    Log.e("API", "Respuesta vacía o nula")
                    (context as? Activity)?.runOnUiThread {
                        onResult("Error: Respuesta vacía del servidor")
                    }
                }
            } else {
                Log.e("API", "Error HTTP: ${response.code} ${response.message}")
                (context as? Activity)?.runOnUiThread {
                    onResult("Error HTTP: ${response.code}")
                }
            }
        }
    })
}

fun getAllPlatesWhereUserIdIsNull(context: Context, onResult: (Result<List<Plate>>) -> Unit) {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Increase timeout
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    val url = "http://10.0.2.2:8000/get_all_plates_where_user_id_is_null"

    val request = Request.Builder()
        .url(url)
        .get()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("PlateAPI", "Error in request: ${e.message}")
            (context as? Activity)?.runOnUiThread {
                onResult(Result.failure(e))
            }
        }

        override fun onResponse(call: Call, response: Response) {
            try {
                if (!response.isSuccessful) {
                    throw IOException("HTTP error: ${response.code}")
                }

                val responseData = response.body?.string()
                    ?: throw IOException("Empty server response")

                //(Log.d("PlateAPI", "Response for null user_id plates: $responseData")

                val json = JSONObject(responseData)
                val platesArray = json.getJSONArray("plates")
                val platesList = mutableListOf<Plate>()

                for (i in 0 until platesArray.length()) {
                    val plateJson = platesArray.getJSONObject(i)
                    val plate = Plate(
                        id = plateJson.getInt("id"),
                        name = plateJson.getString("name"),
                        user_id = plateJson.optString("user_id", ""),
                        calories = plateJson.getInt("calories"),
                        carbohydrates = plateJson.getDouble("carbohydrates"),
                        proteins = plateJson.getDouble("proteins"),
                        fats = plateJson.getDouble("fats"),
                        sugar = plateJson.getDouble("sugar"),
                        sodium = plateJson.getDouble("sodium"),
                        price = plateJson.getDouble("price"),
                        type = plateJson.getInt("type"),
                        vegan = plateJson.getInt("vegan"),
                        vegetarian = plateJson.getInt("vegetarian"),
                        celiac = plateJson.getInt("celiac"),
                        halal = plateJson.getInt("halal")
                    )
                    platesList.add(plate)
                }

                (context as? Activity)?.runOnUiThread {
                    onResult(Result.success(platesList))
                }

            } catch (e: Exception) {
                Log.e("PlateAPI", "Error processing response: ${e.message}")
                (context as? Activity)?.runOnUiThread {
                    onResult(Result.failure(e))
                }
            }
        }
    })
}

fun getAllPlatesWhereUserIdIsEitherUsersOrNull(userId: Int, context: Context, onResult: (Result<List<Plate>>) -> Unit) {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Increase timeout
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val url = "http://10.0.2.2:8000/get_all_plates_where_user_id_is_either_users_or_null/$userId"

    val request = Request.Builder()
        .url(url)
        .get()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("PlateAPI", "Error in request: ${e.message}")
            (context as? Activity)?.runOnUiThread {
                onResult(Result.failure(e))
            }
        }

        override fun onResponse(call: Call, response: Response) {
            try {
                if (!response.isSuccessful) {
                    throw IOException("HTTP error: ${response.code}")
                }

                val responseData = response.body?.string()
                    ?: throw IOException("Empty server response")

                // Log.d("PlateAPI", "Response for user_id $userId or null plates: $responseData")
                Log.d("PlateAPI", "Response for user_id $userId or null plates correct")

                val json = JSONObject(responseData)
                if (json.has("error")) {
                    throw IOException("Server error: ${json.getString("error")}")
                }

                val platesArray = json.optJSONArray("plates") ?: JSONArray() // Use empty array if null
                val platesList = mutableListOf<Plate>()

                for (i in 0 until platesArray.length()) {
                    val plateJson = platesArray.getJSONObject(i)
                    val plate = Plate(
                        id = plateJson.getInt("id"),
                        name = plateJson.getString("name"),
                        user_id = plateJson.optString("user_id", ""),
                        calories = plateJson.getInt("calories"),
                        carbohydrates = plateJson.getDouble("carbohydrates"),
                        proteins = plateJson.getDouble("proteins"),
                        fats = plateJson.getDouble("fats"),
                        sugar = plateJson.getDouble("sugar"),
                        sodium = plateJson.getDouble("sodium"),
                        price = plateJson.getDouble("price"),
                        type = plateJson.getInt("type"),
                        vegan = plateJson.getInt("vegan"),
                        vegetarian = plateJson.getInt("vegetarian"),
                        celiac = plateJson.getInt("celiac"),
                        halal = plateJson.getInt("halal")
                    )
                    platesList.add(plate)
                }

                (context as? Activity)?.runOnUiThread {
                    onResult(Result.success(platesList))
                }

            } catch (e: Exception) {
                Log.e("PlateAPI", "Error processing response: ${e.message}")
                (context as? Activity)?.runOnUiThread {
                    onResult(Result.failure(e))
                }
            }
        }
    })
}

fun getPrefs(context: Context, name: String, userId: Int, dietTypeId: Int, duration: Int) : JSONObject {
    var planJson = JSONObject()
    for (i in 0 until duration) {
        val prefs = context.getSharedPreferences("WeeklyDiet", Context.MODE_PRIVATE)
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val dateKey = sdf.format(calendar.time) + "_diet"
        val dayString = prefs.getString(dateKey, null)
       planJson = JSONObject().apply {
            put("name", name)
            put("user_id", userId)
            put("duration", duration)
            put("diet_type_id", dietTypeId)
        }
        if (dayString != null) {
            val dayJsonRaw = JSONObject(dayString)

            val dayJson = JSONObject().apply {
                put("breakfast_dish", dayJsonRaw.getString("breakfast_dish"))
                put("breakfast_drink", dayJsonRaw.getString("breakfast_drink"))
                put("lunch_main_dish", dayJsonRaw.getString("lunch_main_dish"))
                put("lunch_side_dish", dayJsonRaw.getString("lunch_side_dish"))
                put("lunch_drink", dayJsonRaw.getString("lunch_drink"))
                put("dinner_dish", dayJsonRaw.getString("dinner_dish"))
                put("dinner_drink", dayJsonRaw.getString("dinner_drink"))
            }



            planJson.put("day${i + 1}", dayJson)
        }
    }
    return planJson
}

fun parseFoodsFromJson(jsonObject: JSONObject): List<FoodViewModel> {
    val foodList = mutableListOf<FoodViewModel>()
    val dietaArray = jsonObject.getJSONArray("dieta")

    for (i in 0 until dietaArray.length()) {
        val dayArray = dietaArray.getJSONArray(i)
        for (j in 0 until dayArray.length()) {
            val foodJson = dayArray.getJSONObject(j)
            val foodViewModel = FoodViewModel().apply {
                updateFood(
                    name = foodJson.getString("name"),
                    protein = foodJson.getDouble("protein"),
                    fats = foodJson.getDouble("fat"), // "fat" en JSON → "fats" en el modelo
                    sugar = foodJson.getDouble("sugar"),
                    salt = foodJson.getDouble("salt"),
                    carbohydrates = foodJson.getDouble("carbohydrates"),
                    calories = foodJson.getDouble("calories"),
                    price = foodJson.getDouble("price")
                    // foodVariants y foodTypes se omiten
                )
            }
            foodList.add(foodViewModel)
        }
    }

    return foodList
}

fun create_diet_with_inputs(values: List<Any>, context: Context, onResult: (String) -> Unit) {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    val url = "http://10.0.2.2:8000/calculate_diet_with_inputs"

    // Serializar 'values' correctamente como JSON string
    val json = JSONObject()
    json.put("values", JSONArray(values).toString())  // <- Esta línea es crucial

    Log.d("DietForm", "Enviando valores al servidor: $values")

    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onResult("❌ Error: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseBody ->
                Log.d("DietForm", "Respuesta del servidor: $responseBody")
                try {
                    // Usa JSONArray solo si el servidor responde con una lista
                    val jsonArray = JSONArray(responseBody)
                    (context as? Activity)?.runOnUiThread {
                        val prefs = context.getSharedPreferences("WeeklyDiet", Context.MODE_PRIVATE)
                        val editor = prefs.edit()
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val calendar = Calendar.getInstance()

                        for (i in 0 until jsonArray.length()) {
                            val dayData = jsonArray.getJSONObject(i)
                            val breakfastDish = dayData.getString("breakfast_dish")
                            val breakfastDrink = dayData.getString("breakfast_drink")
                            val lunchMain = dayData.getString("lunch_main_dish")
                            val lunchSide = dayData.getString("lunch_side_dish")
                            val lunchDrink = dayData.getString("lunch_drink")
                            val dinnerDish = dayData.getString("dinner_dish")
                            val dinnerDrink = dayData.getString("dinner_drink")

                            val dateString = sdf.format(calendar.time)

                            // Guardar en SharedPreferences
                            val dietData = JSONObject().apply {
                                put("breakfast_dish", breakfastDish)
                                put("breakfast_drink", breakfastDrink)
                                put("lunch_main_dish", lunchMain)
                                put("lunch_side_dish", lunchSide)
                                put("lunch_drink", lunchDrink)
                                put("dinner_dish", dinnerDish)
                                put("dinner_drink", dinnerDrink)
                            }
                            editor.putString("${dateString}_diet", dietData.toString())
                            calendar.add(Calendar.DAY_OF_YEAR, 1)
                        }

                        editor.apply()
                        onResult("✅ Dieta guardada exitosamente")
                    }
                } catch (e: Exception) {
                    Log.e("DietForm", "Error al procesar JSON: ${e.message}")
                    (context as? Activity)?.runOnUiThread {
                        onResult("⚠️ Error al procesar la respuesta del servidor")
                    }
                }
            }
        }
    })
}

fun create_diet_with_user_data(requirements: List<Any>, context: Context, onResult: (String) -> Unit) {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    val url = "http://10.0.2.2:8000/calculate_diet_with_user_data"
    // Serializar 'values' correctamente como JSON string
    val json = JSONObject()
    json.put("requirements", JSONArray(requirements).toString())  // <- Esta línea es crucial

    Log.d("DietForm", "Enviando requerimientos al servidor: $requirements")

    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onResult("❌ Error: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseBody ->
                Log.d("DietForm", "Respuesta del servidor: $responseBody")
                try {
                    // Usa JSONArray solo si el servidor responde con una lista
                    val jsonArray = JSONArray(responseBody)
                    (context as? Activity)?.runOnUiThread {
                        val prefs = context.getSharedPreferences("WeeklyDiet", Context.MODE_PRIVATE)
                        val editor = prefs.edit()
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val calendar = Calendar.getInstance()

                        for (i in 0 until jsonArray.length()) {
                            val dayData = jsonArray.getJSONObject(i)
                            val breakfastDish = dayData.getString("breakfast_dish")
                            val breakfastDrink = dayData.getString("breakfast_drink")
                            val lunchMain = dayData.getString("lunch_main_dish")
                            val lunchSide = dayData.getString("lunch_side_dish")
                            val lunchDrink = dayData.getString("lunch_drink")
                            val dinnerDish = dayData.getString("dinner_dish")
                            val dinnerDrink = dayData.getString("dinner_drink")

                            val dateString = sdf.format(calendar.time)

                            // Guardar en SharedPreferences
                            val dietData = JSONObject().apply {
                                put("breakfast_dish", breakfastDish)
                                put("breakfast_drink", breakfastDrink)
                                put("lunch_main_dish", lunchMain)
                                put("lunch_side_dish", lunchSide)
                                put("lunch_drink", lunchDrink)
                                put("dinner_dish", dinnerDish)
                                put("dinner_drink", dinnerDrink)
                            }
                            editor.putString("${dateString}_diet", dietData.toString())
                            calendar.add(Calendar.DAY_OF_YEAR, 1)
                        }

                        editor.apply()
                        onResult("✅ Dieta guardada exitosamente")
                    }
                } catch (e: Exception) {
                    Log.e("DietForm", "Error al procesar JSON: ${e.message}")
                    (context as? Activity)?.runOnUiThread {
                        onResult("⚠️ Error al procesar la respuesta del servidor")
                    }
                }
            }
        }
    })
}

fun createUser(
    email: String,
    password: String,
    physicalActivity: Int,
    sex: Int,
    birthday: String,
    height: Int,
    weight: Int,
    goal: Int,
    context: Context,
    onResult: (String) -> Unit
): String {
    val client = OkHttpClient()
    val url = "http://10.0.2.2:8000/create_user"

    val json = JSONObject().apply {
        put("email", email)
        put("password", password)
        put("physical_activity", physicalActivity)
        put("sex", sex)
        put("birthday", birthday) // formato: "YYYY-MM-DD"
        put("height", height)
        put("weight", weight)
        put("goal", goal)
    }

    Log.d("CreateUser", "Enviando datos: $json")

    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("CreateUser", "Fallo en la conexión: ${e.message}")
            onResult("Error al conectar con el servidor: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseBody ->
                Log.d("CreateUser", "Respuesta del servidor: $responseBody")
                try {
                    val jsonResponse = JSONObject(responseBody)
                    if (response.isSuccessful) {
                        val message = jsonResponse.optString("message", "✅ Usuario creado con éxito")
                        return onResult(message)
                    } else {
                        val error = jsonResponse.optString("error", "❗ Error desconocido")
                        onResult("Error: $error")
                        return onResult("Error: $error")
                    }
                } catch (e: Exception) {
                    Log.e("CreateUser", "Error procesando JSON: ${e.message}")
                    onResult("Error al procesar la respuesta del servidor")
                }
            }
        }
    })
    return ""
}

fun authenticateUser(
    email: String,
    password: String,
    context: Context,
    userViewModel: UserViewModel, // Recibimos el ViewModel como parámetro
    onResult: (Result<Boolean>) -> Unit // Ahora devuelve Boolean indicando éxito/fallo
) {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val url = "http://10.0.2.2:8000/get_user_by_credentials"

    val json = JSONObject().apply {
        put("email", email)
        put("password", password)
    }

    Log.d("UserAuth", "Intentando autenticar usuario: $email")

    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("UserAuth", "Error de conexión: ${e.message}")
            (context as? Activity)?.runOnUiThread {
                onResult(Result.failure(Exception("Error de conexión: ${e.message}")))
            }
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseBody ->
                try {
                    Log.d("UserAuth", "Respuesta del servidor: $responseBody")

                    if (response.isSuccessful) {
                        val userJson = JSONObject(responseBody)

                        // Mapeo de valores numéricos a enums
                        val receivedSex = when (userJson.optInt("sex", 1)) {
                            0 -> Sex.Mujer
                            else -> Sex.Hombre // Valor por defecto si no es 0
                        }

                        val receivedGoal = when (userJson.optInt("goal", 1)) {
                            0 -> Goal.PERDER_PESO
                            1 -> Goal.MANTENERSE
                            2 -> Goal.GANAR_PESO
                            else -> Goal.MANTENERSE // Valor por defecto
                        }

                        // Actualizamos el UserViewModel recibido
                        userViewModel.updateUser(
                            id = userJson.optInt("id"),
                            name = userJson.optString("name", ""),
                            email = userJson.optString("email", ""),
                            password = "", // No guardamos la contraseña en el ViewModel
                            age = userJson.optString("birthday", ""),
                            sex = receivedSex,
                            height = userJson.optInt("height"),
                            currentWeight = userJson.optDouble("weight"),
                            goal = receivedGoal
                            // Mantenemos foodList como estaba
                        )

                        Log.d("UserAuth", """
                            Usuario autenticado: 
                            Nombre: ${userViewModel.getUser().name}
                            Sexo: ${userViewModel.getUser().sex}
                            Objetivo: ${userViewModel.getUser().goal}
                        """.trimIndent())

                        // Guardar datos básicos en SharedPreferences
                        val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        prefs.edit().apply {
                            putInt("user_id", userViewModel.getUser().id)
                            putString("user_name", userViewModel.getUser().name)
                            putString("user_email", userViewModel.getUser().email)
                            putInt("user_sex", userJson.optInt("sex", 1))
                            putInt("user_goal", userJson.optInt("goal", 1))
                            apply()
                        }

                        (context as? Activity)?.runOnUiThread {
                            onResult(Result.success(true))
                        }
                    } else {
                        val errorResponse = JSONObject(responseBody)
                        val errorMsg = errorResponse.optString("error", "Error desconocido")
                        Log.e("UserAuth", "Error en autenticación: $errorMsg")

                        (context as? Activity)?.runOnUiThread {
                            onResult(Result.failure(Exception(errorMsg)))
                        }
                    }
                } catch (e: Exception) {
                    Log.e("UserAuth", "Error al procesar respuesta: ${e.message}")
                    (context as? Activity)?.runOnUiThread {
                        onResult(Result.failure(Exception("Error al procesar la respuesta del servidor")))
                    }
                }
            } ?: run {
                (context as? Activity)?.runOnUiThread {
                    onResult(Result.failure(Exception("Respuesta vacía del servidor")))
                }
            }
        }
    })
}

fun getUserByEmail(
    email: String,
    context: Context,
    onResult: (String) -> Unit
) {
    val client = OkHttpClient()
    val url = "http://10.0.2.2:8000/get_user/${email}"

    val request = Request.Builder()
        .url(url)
        .get()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("GetUser", "Fallo en la conexión: ${e.message}")
            onResult("Error al conectar con el servidor: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseBody ->
                Log.d("GetUser", "Respuesta del servidor: $responseBody")
                try {
                    val jsonResponse = JSONObject(responseBody)
                    if (response.isSuccessful) {
                        val user = jsonResponse.getJSONObject("user")
                        val message = jsonResponse.getString("message")
                        val userInfo = buildString {
                            append("Email: ${user.getString("email")}\n")
                            append("Peso: ${user.getDouble("weight")} kg\n")
                            append("Altura: ${user.getDouble("height")} cm\n")
                            append("Peso objetivo: ${user.getDouble("target_weight")} kg\n")
                            append("Sexo: ${user.getString("sex")}\n")
                            append("Fecha de nacimiento: ${user.getString("birthday")}\n")
                            append("Nivel de actividad: ${user.getInt("physical_activity")}")
                        }
                        onResult("$message\n\n$userInfo")
                    } else {
                        val error = jsonResponse.optString("error", "❗ Error desconocido")
                        onResult("Error: $error")
                    }
                } catch (e: Exception) {
                    Log.e("GetUser", "Error procesando JSON: ${e.message}")
                    onResult("Error al procesar la respuesta del servidor")
                }
            }
        }
    })
}

fun deleteUserByEmail(
    email: String,
    context: Context,
    onResult: (String) -> Unit
) {
    val client = OkHttpClient()
    val url = "http://10.0.2.2:8000/delete_user/${email}"

    val request = Request.Builder()
        .url(url)
        .delete()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("DeleteUser", "Fallo en la conexión: ${e.message}")
            onResult("Error al conectar con el servidor: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseBody ->
                Log.d("DeleteUser", "Respuesta del servidor: $responseBody")
                try {
                    val jsonResponse = JSONObject(responseBody)
                    if (response.isSuccessful) {
                        val message = jsonResponse.getString("message")
                        val deletedUser = jsonResponse.getJSONObject("user")
                        val userInfo = buildString {
                            append("Email: ${deletedUser.getString("email")}\n")
                            append("Sexo: ${deletedUser.getString("sex")}\n")
                            append("Fecha de nacimiento: ${deletedUser.getString("birthday")}")
                        }
                        onResult("$message\n\n$userInfo")
                    } else {
                        val error = jsonResponse.optString("error", "❗ Error desconocido")
                        onResult("Error: $error")
                    }
                } catch (e: Exception) {
                    Log.e("DeleteUser", "Error procesando JSON: ${e.message}")
                    onResult("Error al procesar la respuesta del servidor")
                }
            }
        }
    })
}

fun updateUserPhysicalData(
    id: Int,
    updatedFields: Map<String, Any>,
    context: Context,
    onResult: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    val client = OkHttpClient()

    // Convertimos el Map a un JSONObject
    val json = JSONObject().apply {
        for ((key, value) in updatedFields) {
            put(key, value)
        }
    }

    // Imprimir los datos enviados en el Logcat para depuración
    Log.d("updateUserPhysicalData", "Datos enviados: ${json.toString(4)}")

    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBody = json.toString().toRequestBody(mediaType)

    val request = Request.Builder()
        .url("http://10.0.2.2:8000/update_user_physical/$id")
        .patch(requestBody)
        .header("Content-Type", "application/json")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            val msg = "Error de red: ${e.message}"
            Log.e("updateUserPhysicalData", msg)

            // Ejecutar en el hilo principal para actualizar la UI
            (context as? Activity)?.runOnUiThread {
                onError(msg)
            }
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                val responseBody = response.body?.string()
                if (!response.isSuccessful) {
                    val msg = "Error HTTP ${response.code}: $responseBody"
                    Log.e("updateUserPhysicalData", msg)

                    // Ejecutar en el hilo principal para actualizar la UI
                    (context as? Activity)?.runOnUiThread {
                        onError(msg)
                    }
                } else {
                    Log.d("updateUserPhysicalData", "Respuesta: $responseBody")

                    // Ejecutar en el hilo principal para actualizar la UI
                    (context as? Activity)?.runOnUiThread {
                        onResult(responseBody ?: "Actualización exitosa sin cuerpo")
                    }
                }
            }
        }
    })
}


fun updateUserPassword(
    id: Int,
    currentPassword: String,
    newPassword: String,
    context: Context,
    onResult: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    val client = OkHttpClient()

    val json = JSONObject().apply {
        put("current_password", currentPassword)
        put("new_password", newPassword)
    }

    // Imprimir datos en el Logcat para depuración
    Log.d("updateUserPassword", "Datos enviados: ${json.toString(4)}")

    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBody = json.toString().toRequestBody(mediaType)

    val request = Request.Builder()
        .url("http://10.0.2.2:8000/update_user_password/$id")
        .patch(requestBody)
        .header("Content-Type", "application/json")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            val msg = "Error de red: ${e.message}"
            Log.e("updateUserPassword", msg)
            (context as? Activity)?.runOnUiThread {
                onError(msg)
            }
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                val responseBody = response.body?.string()
                if (!response.isSuccessful) {
                    val msg = "Error HTTP ${response.code}: $responseBody"
                    Log.e("updateUserPassword", msg)
                    (context as? Activity)?.runOnUiThread {
                        onError(msg)
                    }
                } else {
                    Log.d("updateUserPassword", "Respuesta: $responseBody")
                    (context as? Activity)?.runOnUiThread {
                        onResult(responseBody ?: "Contraseña actualizada exitosamente")
                    }
                }
            }
        }
    })
}

fun fetchNutritionalData(
    context: Context,
    dietJsonArray: JSONArray,
    onDataReceived: (Map<String, Float>) -> Unit
) {
    val client = OkHttpClient()
    val url = "http://10.0.2.2:8000/barplot"

    val wrappedJson = JSONObject().apply {
        put("dieta", dietJsonArray)
    }

    val requestBody = wrappedJson
        .toString()
        .toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("Graph", "Error al obtener datos: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                response.body?.string()?.let { body ->
                    val json = JSONObject(body)
                    val data = mapOf(
                        "Calorías" to json.getDouble("calorias").toFloat(),
                        "Carbohidratos" to json.getDouble("carbohidratos").toFloat(),
                        "Proteinas" to json.getDouble("proteinas").toFloat(),
                        "Grasas" to json.getDouble("grasas").toFloat(),
                        "Azucares" to json.getDouble("azucares").toFloat(),
                        "Sales" to json.getDouble("sales").toFloat()
                    )
                    (context as Activity).runOnUiThread {
                        onDataReceived(data)
                    }
                }
            } else {
                Log.e("Graph", "Error en la respuesta del servidor")
            }
        }
    })
}

fun getDietJsonArrayFromPreferences(context: Context): JSONArray {
    val prefs = context.getSharedPreferences("DietData", Context.MODE_PRIVATE)
    val jsonArray = JSONArray()

    val calendar = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Asume 7 días, por ejemplo
    for (i in 0 until 7) {
        val dateKey = "${sdf.format(calendar.time)}_diet"
        prefs.getString(dateKey, null)?.let { jsonString ->
            val dayObject = JSONObject(jsonString)
            jsonArray.put(dayObject)
        }
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    return jsonArray
}

fun getPlateById(context: Context, date: Date, onResult: (List<FoodViewModel>) -> Unit) {
    val prefs = context.getSharedPreferences("WeeklyDiet", Context.MODE_PRIVATE)
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateKey = "${sdf.format(date)}_diet"

    val dayJsonString = prefs.getString(dateKey, null) ?: return onResult(emptyList())
    val dayJson = JSONObject(dayJsonString)

    val plateIds = listOf(
        dayJson.getString("breakfast_dish"),
        dayJson.getString("breakfast_drink"),
        dayJson.getString("lunch_main_dish"),
        dayJson.getString("lunch_side_dish"),
        dayJson.getString("lunch_drink"),
        dayJson.getString("dinner_dish"),
        dayJson.getString("dinner_drink")
    ).filter { it.isNotEmpty() }

    val client = OkHttpClient()
    val results = MutableList<FoodViewModel?>(plateIds.size) { null }
    var remaining = plateIds.size

    for ((index, id) in plateIds.withIndex()) {
        val request = Request.Builder()
            .url("http://10.0.2.2:8000/get_plate/$id")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                synchronized(results) {
                    remaining--
                    if (remaining == 0) {
                        onResult(results.filterNotNull())
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                synchronized(results) {
                    val body = response.body?.string()
                    if (response.isSuccessful && body != null) {
                        val plateJson = JSONObject(body).getJSONObject("plate")
                        try {
                            val viewModel = FoodViewModel.fromJson(plateJson)
                            results[index] = viewModel
                        } catch (e: Exception) {
                            Log.e("PlateParse", "Error parseando plate: ${e.message}")
                        }
                    }
                    remaining--
                    if (remaining == 0) {
                        onResult(results.filterNotNull())
                    }
                }
            }
        })
    }
}

fun createPlate(plate: Plate, context: Context, onResult: (String) -> Unit, onError: (String) -> Unit = {}) {
    val client = OkHttpClient()
    val url = "http://10.0.2.2:8000/create_plate"

    try {
        val json = JSONObject().apply {
            put("name", plate.name)
            put("user_id", plate.user_id)
            put("calories", plate.calories)
            put("carbohydrates", plate.carbohydrates)
            put("proteins", plate.proteins)
            put("fats", plate.fats)
            put("sugar", plate.sugar)
            put("sodium", plate.sodium)
            put("price", plate.price)
            put("type", plate.type)
            put("vegan", plate.vegan)
            put("vegetarian", plate.vegetarian)
            put("celiac", plate.celiac)
            put("halal", plate.halal)
        }

        Log.d("SendPlate", "Sending data: $json")

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val msg = "Connection failed: ${e.message}"
                Log.e("SendPlate", msg)
                (context as? Activity)?.runOnUiThread {
                    onError(msg)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBody = response.body?.string()
                    if (!response.isSuccessful) {
                        val msg = "Error HTTP ${response.code}: $responseBody"
                        Log.e("SendPlate", msg)
                        (context as? Activity)?.runOnUiThread {
                            onError(msg)
                        }
                    } else {
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val message = jsonResponse.optString("message", "✅ Plate created successfully")
                            Log.d("SendPlate", "Server response: $message")
                            (context as? Activity)?.runOnUiThread {
                                onResult(message)
                            }
                        } catch (e: Exception) {
                            val msg = "JSON processing error: ${e.message}"
                            Log.e("SendPlate", msg)
                            (context as? Activity)?.runOnUiThread {
                                onError(msg)
                            }
                        }
                    }
                }
            }
        })
    } catch (e: Exception) {
        val msg = "Error creating request: ${e.message}"
        Log.e("SendPlate", msg)
        (context as? Activity)?.runOnUiThread {
            onError(msg)
        }
    }
}

fun createPlateFromViewModel(
    foodViewModel: FoodViewModel,
    userId: String,
    context: Context,
    onResult: (String) -> Unit
) {
    // Obtiene el FoodModel del ViewModel
    val foodModel = foodViewModel.getFood()

    // Convierte a Plate
    val plate = foodModel.toPlate(userId)

    // Llama a la función original
    createPlate(plate, context, onResult)
}

data class DietPlanFromPlatesSelectedComplete(
    val name: String,
    val user_id: Int,
    val day1: List<Int>?,
    val day2: List<Int>?,
    val day3: List<Int>?,
    val day4: List<Int>?,
    val day5: List<Int>?,
    val day6: List<Int>?,
    val day7: List<Int>?,
    val diet_type: Int,
    val duration: Int
)

fun createDietPlanFromPlates(
    dietPlan: DietPlanFromPlatesSelectedComplete,
    context: Context,
    onResult: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()
    val url = "http://10.0.2.2:8000/create_diet_from_plates"

    try {
        // ✅ Verificar duración de la dieta
        Log.d("createDietPlan", "Validating diet duration...")
        if (dietPlan.duration !in 1..7) {
            throw IllegalArgumentException("Duration must be between 1 and 7, but found ${dietPlan.duration}")
        }
        Log.d("createDietPlan", "Duration is valid: ${dietPlan.duration} days")

        // ✅ Crear JSON con la estructura de los platos
        Log.d("createDietPlan", "Building JSON object for diet plan...")
        val jsonObject = JSONObject().apply {
            put("name", dietPlan.name)
            put("user_id", dietPlan.user_id)
            put("duration", dietPlan.duration)
            put("diet_type", dietPlan.diet_type)

            val days = listOf(
                dietPlan.day1, dietPlan.day2, dietPlan.day3, dietPlan.day4,
                dietPlan.day5, dietPlan.day6, dietPlan.day7
            )

            // ✅ Verificar y agregar platos para cada día
            for (i in 0 until dietPlan.duration) {
                val dayPlates = days[i]
                if (dayPlates == null || dayPlates.size != 7) {
                    Log.e("createDietPlan", "Day ${i + 1} has ${dayPlates?.size ?: 0} plates, expected 7")
                    throw IllegalArgumentException("Day ${i + 1} must have exactly 7 plates")
                }
                Log.d("createDietPlan", "Day ${i + 1} has exactly 7 plates")
                put((i + 1).toString(), JSONArray(dayPlates))
            }
        }

        val json = jsonObject.toString()
        Log.d("createDietPlan", "JSON object created successfully: $json")

        // ✅ Preparar y enviar la solicitud HTTP
        Log.d("createDietPlan", "Preparing HTTP request...")
        val mediaType = "application/json".toMediaType()
        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        Log.d("createDietPlan", "Sending request to $url")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val msg = "Connection failed: ${e.message}"
                Log.e("createDietPlan", msg)
                (context as? Activity)?.runOnUiThread {
                    onError(msg)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBody = response.body?.string()
                    if (!response.isSuccessful) {
                        val msg = "Error HTTP ${response.code}: $responseBody"
                        Log.e("createDietPlan", msg)
                        (context as? Activity)?.runOnUiThread {
                            onError(msg)
                        }
                    } else {
                        Log.d("createDietPlan", "Success: $responseBody")
                        (context as? Activity)?.runOnUiThread {
                            onResult(responseBody ?: "Diet plan created successfully")
                        }
                    }
                }
            }
        })

        Log.d("createDietPlan", "Request sent successfully")

    } catch (e: Exception) {
        val msg = "Error creating diet plan: ${e.message}"
        Log.e("createDietPlan", msg)
        (context as? Activity)?.runOnUiThread {
            onError(msg)
        }
    }
}