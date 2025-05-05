package com.example.diet_app

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.diet_app.model.FoodVariant
import com.example.diet_app.model.Goal
import com.example.diet_app.model.Sex
import com.example.diet_app.viewModel.DietDayViewModel
import com.example.diet_app.viewModel.DietViewModel
import com.example.diet_app.viewModel.FoodViewModel
import com.example.diet_app.viewModel.UserViewModel
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

fun sendDataToServer(values: List<Double>, context: Context, onResult: (String) -> Unit) {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    val url = "http://10.0.2.2:8000/calculate"

    val json = JSONObject()
    json.put("values", values)

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
                    val jsonArray = JSONArray(responseBody)
                    (context as? Activity)?.runOnUiThread {
                        val prefs = context.getSharedPreferences("WeeklyDiet", Context.MODE_PRIVATE)
                        val editor = prefs.edit()
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val calendar = Calendar.getInstance()

                        val stringBuilder = StringBuilder()

                        for (i in 0 until jsonArray.length()) {
                            val dayData = jsonArray.getJSONObject(i)
                            val breakfastDish = dayData.getString("breakfast_dish")
                            val breakfastDrink = dayData.getString("breakfast_drink")

                            val lunchMain = dayData.getString("lunch_main_dish")
                            val lunchSide = dayData.getString("lunch_side_dish")
                            val lunchDrink = dayData.getString("lunch_drink")

                            val dinnerDish = dayData.getString("dinner_dish")
                            val dinnerDrink = dayData.getString("dinner_drink")

                            val dateString = sdf.format(calendar.time) // fecha actual

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
                        onResult(stringBuilder.toString().trim())
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

fun calculateBasalRate(
    weight: Double,
    height: Double,
    age: Int,
    gender: String,
    context: Context,
    onResult: (String) -> Unit
) {
    val client = OkHttpClient()
    val url = "http://10.0.2.2:8000/basal"

    val json = JSONObject()
    json.put("weight", weight)
    json.put("height", height)
    json.put("age", age)
    json.put("gender", gender)

    Log.d("BasalRate", "Enviando datos: weight=$weight, height=$height, age=$age, gender=$gender")

    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onResult("Error al conectar con el servidor: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseBody ->
                Log.d("BasalRate", "Respuesta del servidor: $responseBody")
                try {
                    val jsonResponse = JSONObject(responseBody)
                    val basalRate = jsonResponse.getDouble("result")
                    onResult("Tu tasa metabólica basal es: ${"%.2f".format(basalRate)} kcal/día")
                } catch (e: Exception) {
                    Log.e("BasalRate", "Error procesando respuesta: ${e.message}")
                    onResult("Error al procesar la respuesta del servidor")
                }
            }
        }
    })
}

fun calculateMaintenanceCalories(
    weight: Double,
    height: Double,
    age: Int,
    gender: String,
    physicalActivityLevel: Int,
    context: Context,
    onResult: (String) -> Unit
) {
    val client = OkHttpClient()
    val url = "http://10.0.2.2:8000/maintenance"

    val json = JSONObject()
    json.put("weight", weight)
    json.put("height", height)
    json.put("age", age)
    json.put("gender", gender)
    json.put("physical_activity_level", physicalActivityLevel)

    Log.d("MaintenanceCalories", "Enviando datos: weight=$weight, height=$height, age=$age, gender=$gender, pal=$physicalActivityLevel")

    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onResult("Error al conectar con el servidor: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseBody ->
                Log.d("MaintenanceCalories", "Respuesta del servidor: $responseBody")
                try {
                    val jsonResponse = JSONObject(responseBody)
                    val maintenanceCalories = jsonResponse.getDouble("result")
                    onResult("Tu requerimiento de calorías de mantenimiento es: ${"%.2f".format(maintenanceCalories)} kcal/día")
                } catch (e: Exception) {
                    Log.e("MaintenanceCalories", "Error procesando respuesta: ${e.message}")
                    onResult("Error al procesar la respuesta del servidor")
                }
            }
        }
    })
}

// 1. Primero, modifica la función para usar Result de Kotlin correctamente
fun getUserDietPlansComplete(
    user_id: Int,
    context: Context,
    dietViewModels: MutableList<DietViewModel>,
    onResult: (Result<List<DietViewModel>>) -> Unit // Usamos el Result de Kotlin
) {
    val client = OkHttpClient()
    val url = "http://10.0.2.2:8000/get_diet_plans_by_user/$user_id"

    Log.d("DietAPI", "Iniciando solicitud para user_id: $user_id")

    val request = Request.Builder()
        .url(url)
        .get()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("DietAPI", "Error en la solicitud", e)
            (context as? Activity)?.runOnUiThread {
                onResult(Result.failure(e))
            }
        }

        override fun onResponse(call: Call, response: Response) {
            try {
                Log.d("DietAPI", "Respuesta recibida. Código: ${response.code}")

                if (!response.isSuccessful) {
                    throw IOException("Error HTTP: ${response.code}")
                }

                val responseData = response.body?.string()
                    ?: throw IOException("Respuesta vacía del servidor")

                Log.d("DietAPI", "Datos brutos: $responseData")

                val jsonArray = JSONArray(responseData)
                dietViewModels.clear()

                for (i in 0 until jsonArray.length()) {
                    val dietJson = jsonArray.getJSONObject(i)
                    Log.d("DietAPI", "Procesando dieta ${i + 1}/${jsonArray.length()}")

                    // Extraer días
                    val dayIds = (1..7).mapNotNull { dayNum ->
                        dietJson.optInt("day$dayNum", -1).takeIf { it != -1 }
                            .also { if (it != null) Log.d("DietAPI", "Encontrado day$dayNum: $it") }
                    }

                    Log.d("DietAPI", "IDs de días encontrados: $dayIds")

                    // Mapear tipo de dieta
                    val foodVariant = when (val typeId = dietJson.getInt("diet_type_id")) {
                        1 -> FoodVariant.REGULAR
                        2 -> FoodVariant.VEGAN
                        3 -> FoodVariant.VEGETARIAN
                        4 -> FoodVariant.CELIAC
                        5 -> FoodVariant.HALAL
                        else -> throw IllegalArgumentException("Tipo de dieta no válido: $typeId")
                    }

                    Log.d("DietAPI", "Tipo de dieta mapeado: $foodVariant")

                    var dietViewModel = DietViewModel().apply {
                        updateDiet(
                            dietId = dietJson.getString("id"),
                            name = dietJson.getString("name"),
                            duration = dietJson.getInt("duration"),
                            foodVariant = foodVariant,
                            dietsId = dayIds
                        )
                    }

                    dietViewModels.add(dietViewModel)
                    Log.d("DietAPI", "Dieta añadida: ${dietJson.getString("name")}")
                }

                (context as? Activity)?.runOnUiThread {
                    Log.d("DietAPI", "Procesamiento completado. Dietas encontradas: ${dietViewModels.size}")
                    onResult(Result.success(dietViewModels))
                }

            } catch (e: Exception) {
                Log.e("DietAPI", "Error procesando respuesta", e)
                (context as? Activity)?.runOnUiThread {
                    onResult(Result.failure(e))
                }
            }
        }
    })
}

fun getPlanCompleteDays(
    planCompleteId: Int,
    context: Context,
    dietDayViewModels: MutableList<DietDayViewModel>,
    onResult: (Result<MutableList<DietDayViewModel>>) -> Unit
) {
    val client = OkHttpClient()
    val url = "http://10.0.2.2:8000/get_diet_plan_days_by_complete/$planCompleteId"

    Log.d("DietDayAPI", "Fetching days for plan ID: $planCompleteId")

    val request = Request.Builder()
        .url(url)
        .get()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("DietDayAPI", "Network error", e)
            (context as? Activity)?.runOnUiThread {
                onResult(Result.failure(e))
            }
        }

        override fun onResponse(call: Call, response: Response) {
            try {
                Log.d("DietDayAPI", "Response received. Code: ${response.code}")

                if (!response.isSuccessful) {
                    throw IOException("HTTP error: ${response.code}")
                }

                val responseData = response.body?.string()
                    ?: throw IOException("Empty server response")

                Log.d("DietDayAPI", "Raw data: $responseData")

                val jsonArray = JSONArray(responseData)
                dietDayViewModels.clear()

                for (i in 0 until jsonArray.length()) {
                    val dayJson = jsonArray.getJSONObject(i)
                    Log.d("DietDayAPI", "Processing day ${i + 1}/${jsonArray.length()}")

                    // Extract day ID
                    val dayId = dayJson.getInt("id")
                    Log.d("DietDayAPI", "Day ID: $dayId")

                    // Extract food IDs from all fields except 'id'
                    val foodIds = mutableListOf<Int>()
                    val keys = dayJson.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        if (key != "id" && dayJson.optInt(key, -1) != -1) {
                            foodIds.add(dayJson.getInt(key))
                            Log.d("DietDayAPI", "Found food ID: ${dayJson.getInt(key)} in field $key")
                        }
                    }

                    Log.d("DietDayAPI", "Food IDs found: $foodIds")

                    // Create and configure DietDayViewModel
                    val dietDayViewModel = DietDayViewModel().apply {
                        updateDietDay(
                            dietId = dayId,
                            foodsId = foodIds
                            // Other parameters keep default values
                        )
                    }

                    dietDayViewModels.add(dietDayViewModel)
                    Log.d("DietDayAPI", "Added day with ID: $dayId")
                }

                (context as? Activity)?.runOnUiThread {
                    Log.d("DietDayAPI", "Processing complete. Days found: ${dietDayViewModels.size}")
                    onResult(Result.success(dietDayViewModels))
                }

            } catch (e: Exception) {
                Log.e("DietDayAPI", "Processing error", e)
                (context as? Activity)?.runOnUiThread {
                    onResult(Result.failure(e))
                }
            }
        }
    })
}

fun getPlatesWithIds(
    platesIds: List<Int>,
    context: Context,
    onResult: (String) -> Unit,
) {
    if (platesIds.size != 7) {
        val errorMsg = "La lista debe contener exactamente 7 IDs de platos."
        Log.e("getPlatesWithId", errorMsg)
        return
    }

    val client = OkHttpClient()
    val baseUrl = "http://10.0.2.2:8000/get_plate/"
    val platesResults = mutableListOf<String>()

    for (id in platesIds) {
        val request = Request.Builder()
            .url("$baseUrl$id")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val msg = "Error de red al obtener el plato con ID $id: ${e.message}"
                Log.e("getPlatesWithId", msg)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        val msg = "Error HTTP ${response.code} para el plato ID $id"
                        Log.e("getPlatesWithId", msg)
                    } else {
                        val body = response.body?.string()
                        if (body != null) {
                            Log.d("getPlatesWithId", "Plato ID $id recibido: $body")
                            platesResults.add(body)
                            onResult(body)
                        } else {
                            val msg = "Respuesta vacía para el plato ID $id"
                            Log.e("getPlatesWithId", msg)
                        }
                    }
                }
            }
        })
    }
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
    val json = JSONObject()
    for ((key, value) in updatedFields) {
        json.put(key, value)
    }

    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBody = json.toString().toRequestBody(mediaType)

    val request = Request.Builder()
        .url("http://10.0.2.2:8000/update_user_physical/$id")
        .patch(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            val msg = "Error de red: ${e.message}"
            Log.e("updateUserPhysicalData", msg)
            onError(msg)
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                val responseBody = response.body?.string()
                if (!response.isSuccessful) {
                    val msg = "Error HTTP ${response.code}: $responseBody"
                    Log.e("updateUserPhysicalData", msg)
                    onError(msg)
                } else {
                    Log.d("updateUserPhysicalData", "Respuesta: $responseBody")
                    onResult(responseBody ?: "Actualización exitosa sin cuerpo")
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

    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBody = json.toString().toRequestBody(mediaType)

    val request = Request.Builder()
        .url("http://10.0.2.2:8000/update_user_password/$id")
        .patch(requestBody)
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

fun fetchNutritionalData(context: Context, dietJson: String, onDataReceived: (Map<String, Float>) -> Unit) {
    val client = OkHttpClient()
    val url = "http://10.0.2.2:8000/barplot"
    val requestBody = dietJson.toRequestBody("application/json; charset=utf-8".toMediaType())

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
                        "Calorías" to json.getDouble("calorias").toFloat(), // Cambiado a "Calorías"
                        "Carbohidratos" to json.getDouble("carbohidratos").toFloat(),
                        "Proteinas" to json.getDouble("proteinas").toFloat(),
                        "Grasas" to json.getDouble("grasas").toFloat(),
                        "Azucares" to json.getDouble("azucares").toFloat(),
                        "Sales" to json.getDouble("sales").toFloat()
                        // Eliminar "Precio" del mapa
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

fun getPlateById(context: Context, date: Date, onResult: (List<FoodViewModel>) -> Unit) {
    val prefs = context.getSharedPreferences("WeeklyDiet", Context.MODE_PRIVATE)
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateKey = "${sdf.format(date)}_diet"

    val dayJsonString = prefs.getString(dateKey, null)

    if (dayJsonString == null) {
        onResult(emptyList())
        return
    }

    val dayJson = JSONObject(dayJsonString)
    val plateIds = listOf(
        dayJson.getString("breakfast_dish"),
        dayJson.getString("breakfast_drink"),
        dayJson.getString("lunch_main_dish"),
        dayJson.getString("lunch_side_dish"),
        dayJson.getString("lunch_drink"),
        dayJson.getString("dinner_dish"),
        dayJson.getString("dinner_drink")
    )

    val client = OkHttpClient()
    val foodViewModels = mutableListOf<FoodViewModel>()

    var remaining = plateIds.size

    for (id in plateIds) {
        val request = Request.Builder()
            .url("http://10.0.2.2:8000/get_plate/$id")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                synchronized(foodViewModels) {
                    remaining--
                    if (remaining == 0) {
                        onResult(foodViewModels)
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                synchronized(foodViewModels) {
                    val body = response.body?.string()
                    if (response.isSuccessful && body != null) {
                        val plateJson = JSONObject(body).getJSONObject("plate")
                        try {
                            val viewModel = FoodViewModel.fromJson(plateJson)
                            foodViewModels.add(viewModel)
                        } catch (e: Exception) {
                            Log.e("PlateParse", "Error parseando plate: ${e.message}")
                        }
                    }
                    remaining--
                    if (remaining == 0) onResult(foodViewModels)
                }
            }
        })
    }
}

fun createDiet(context: Context, name: String, userId: Int, dietTypeId: Int, onResult: (String) -> Unit) {
    val prefs = context.getSharedPreferences("WeeklyDiet", Context.MODE_PRIVATE)
    val client = OkHttpClient()

    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()

    val duration = 7
    val planJson = JSONObject().apply {
        put("name", name)
        put("user_id", userId)
        put("duration", duration)
        put("diet_type_id", dietTypeId)
    }

    for (i in 0 until duration) {
        val dateKey = sdf.format(calendar.time) + "_diet"
        val dayString = prefs.getString(dateKey, null)
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
        } else {
            onResult("❌ No se encontró dieta guardada para el día ${i + 1}")
            return
        }

        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    val body = planJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url("http://10.0.2.2:8000/create_diet_plan")
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onResult("❌ Error de red: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseBody ->
                try {
                    val jsonResponse = JSONObject(responseBody)

                    if (response.isSuccessful) {
                        val planId = jsonResponse.optInt("plan_id", -1)
                        if (planId != -1) {
                            Log.d("DietSubmit", "✅ Plan creado con ID: $planId")
                            onResult("✅ Plan creado exitosamente con ID: $planId")
                        } else {
                            onResult("⚠️ Plan creado pero no se recibió plan_id")
                        }
                    } else {
                        val error = jsonResponse.optString("error", "Error desconocido")
                        onResult("⚠️ Error al crear plan: $error")
                    }

                } catch (e: Exception) {
                    Log.e("DietSubmit", "❌ Error al procesar la respuesta: ${e.message}")
                    onResult("❌ Error al procesar la respuesta del servidor")
                }
            }
        }
    })
}

fun getDietPlanById(planId: Int, context: Context, onResult: (String) -> Unit) {
    val client = OkHttpClient()
    val url = "http://10.0.2.2:8000/get_diet_plan/$planId"

    val request = Request.Builder()
        .url(url)
        .get()
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onResult("❌ Error de conexión: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseBody ->
                try {
                    val jsonResponse = JSONObject(responseBody)

                    if (!response.isSuccessful) {
                        val errorMsg = jsonResponse.optString("error", "Error desconocido")
                        onResult("⚠️ Error al obtener plan: $errorMsg")
                        return
                    }

                    val plan = jsonResponse.getJSONObject("plan")
                    val days = jsonResponse.getJSONObject("days")

                    val prefs = context.getSharedPreferences("WeeklyDiet", Context.MODE_PRIVATE)
                    val editor = prefs.edit()

                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val calendar = Calendar.getInstance()

                    val output = StringBuilder()
                    output.append("📋 *Plan:* ${plan.getString("name")} (ID: $planId)\n\n")

                    for (i in 1..7) {
                        val dayKey = "day$i"
                        if (days.has(dayKey)) {
                            val dateStr = sdf.format(calendar.time)
                            val dayObj = days.getJSONObject(dayKey)

                            val breakfast = "${dayObj.getString("breakfast_dish")} + ${dayObj.getString("breakfast_drink")}"
                            val lunch = "${dayObj.getString("lunch_main_dish")}, ${dayObj.getString("lunch_side_dish")} + ${dayObj.getString("lunch_drink")}"
                            val dinner = "${dayObj.getString("dinner_dish")} + ${dayObj.getString("dinner_drink")}"

                            // Mostrar y guardar
                            output.append("📅 *$dateStr*\n")
                            output.append("🍳 Desayuno: $breakfast\n")
                            output.append("🥗 Almuerzo: $lunch\n")
                            output.append("🍽 Cena: $dinner\n\n")

                            val dayJson = JSONObject().apply {
                                put("breakfast", breakfast)
                                put("lunch", lunch)
                                put("dinner", dinner)
                            }

                            editor.putString("${dateStr}_diet", dayJson.toString())
                            calendar.add(Calendar.DAY_OF_YEAR, 1)
                        }
                    }

                    editor.apply()
                    onResult(output.toString().trim())
                    Log.d("DietGet", output.toString().trim())

                } catch (e: Exception) {
                    onResult("❌ Error al procesar JSON: ${e.message}")
                }
            }
        }
    })
}

fun deleteDiet(planId: Int, context: Context, onResult: (String) -> Unit) {
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val url = "http://10.0.2.2:8000/delete_diet_plan/$planId"

    Log.d("DietDelete", "Intentando eliminar plan con ID: $planId")

    val request = Request.Builder()
        .url(url)
        .delete() // Método HTTP DELETE
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            (context as? Activity)?.runOnUiThread {
                onResult("❌ Error de conexión: ${e.message}")
            }
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseBody ->
                Log.d("DietDelete", "Respuesta del servidor: $responseBody")
                try {
                    val jsonResponse = JSONObject(responseBody)

                    if (response.isSuccessful) {
                        // Limpiar SharedPreferences si la eliminación fue exitosa
                        val prefs = context.getSharedPreferences("WeeklyDiet", Context.MODE_PRIVATE)
                        val editor = prefs.edit()
                        editor.clear()
                        editor.apply()

                        val message = jsonResponse.getString("message")
                        val deletedDays = jsonResponse.getJSONArray("deleted_days")

                        Log.d("DietDelete", "Plan eliminado: $message, Días afectados: $deletedDays")

                        (context as? Activity)?.runOnUiThread {
                            onResult("✅ $message\nDías eliminados: ${deletedDays.length()}")
                        }
                    } else {
                        val error = jsonResponse.getString("error")
                        (context as? Activity)?.runOnUiThread {
                            onResult("⚠️ $error")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("DietDelete", "Error al procesar JSON: ${e.message}")
                    (context as? Activity)?.runOnUiThread {
                        onResult("⚠️ Error al procesar la respuesta del servidor")
                    }
                }
            } ?: run {
                (context as? Activity)?.runOnUiThread {
                    onResult("⚠️ Respuesta vacía del servidor")
                }
            }
        }
    })
}




