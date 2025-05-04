package com.example.diet_app

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.diet_app.viewModel.FoodViewModel
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
                    val prefs = context.getSharedPreferences("WeeklyDiet", Context.MODE_PRIVATE)
                    val editor = prefs.edit()
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val calendar = Calendar.getInstance() // hoy

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

                        /* Construir texto para mostrar
                        stringBuilder.append("📅 *$dateString*\n")
                        stringBuilder.append("🍳 **Desayuno:** $breakfast\n")
                        stringBuilder.append("🥗 **Almuerzo:** $lunch\n")
                        stringBuilder.append("🍽 **Cena:** $dinner\n\n")
                         */

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

                        // Avanzar al día siguiente
                        calendar.add(Calendar.DAY_OF_YEAR, 1)
                    }

                    editor.apply()
                    onResult(stringBuilder.toString().trim())

                } catch (e: Exception) {
                    Log.e("DietForm", "Error al procesar JSON: ${e.message}")
                    onResult("⚠️ Error al procesar la respuesta del servidor")
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

fun createUser(
    email: String,
    password: String,
    physicalActivity: String,
    sex: Int,
    birthday: String,
    height: Int,
    weight: Int,
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

fun updateUserByEmail(
    email: String,
    updatedFields: Map<String, Any>,
    context: Context,
    onResult: (String) -> Unit
) {
    val client = OkHttpClient()
    val url = "http://10.0.2.2:8000/update_user/$email"

    val json = JSONObject()
    for ((key, value) in updatedFields) {
        json.put(key, value)
    }

    Log.d("UpdateUser", "Datos actualizados enviados: $json")

    val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url(url)
        .patch(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("UpdateUser", "Fallo en la conexión: ${e.message}")
            onResult("Error al conectar con el servidor: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseBody ->
                Log.d("UpdateUser", "Respuesta del servidor: $responseBody")
                try {
                    val jsonResponse = JSONObject(responseBody)
                    if (response.isSuccessful) {
                        val message = jsonResponse.getString("message")
                        val user = jsonResponse.getJSONObject("user")
                        val userInfo = buildString {
                            append("Email: ${user.getString("email")}\n")
                            append("⚖Peso: ${user.getDouble("weight")} kg\n")
                            append("Peso objetivo: ${user.getDouble("target_weight")} kg\n")
                            append("Altura: ${user.getDouble("height")} cm\n")
                            append("Sexo: ${user.getString("sex")}\n")
                            append("Nacimiento: ${user.getString("birthday")}\n")
                            append("Actividad: ${user.getInt("physical_activity")}")
                        }
                        onResult("$message\n\n$userInfo")
                    } else {
                        val error = jsonResponse.optString("error", "❗ Error desconocido")
                        onResult("Error: $error")
                    }
                } catch (e: Exception) {
                    Log.e("UpdateUser", "Error procesando JSON: ${e.message}")
                    onResult("Error al procesar la respuesta del servidor")
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

fun getPlateById(context: Context, date: Date, onResult: (String) -> Unit) {
    val prefs = context.getSharedPreferences("WeeklyDiet", Context.MODE_PRIVATE)
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateKey = "${sdf.format(date)}_diet"

    val dayJsonString = prefs.getString(dateKey, null)

    if (dayJsonString == null) {
        onResult("⚠️ No hay dieta guardada para ese día.")
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
    val resultBuilder = StringBuilder()

    var remaining = plateIds.size

    for (id in plateIds) {
        val request = Request.Builder()
            .url("http://10.0.2.2:8000/get_plate/$id")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                synchronized(resultBuilder) {
                    resultBuilder.append("❌ Error cargando plato $id: ${e.message}\n")
                    checkFinish()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                synchronized(resultBuilder) {
                    val body = response.body?.string()
                    if (response.isSuccessful && body != null) {
                        val json = JSONObject(body)
                        val plate = json.getJSONObject("plate")
                        val name = plate.getString("name")
                        val calories = plate.getDouble("calories")
                        resultBuilder.append("🍽 $name - ${calories} kcal\n")
                    } else {
                        resultBuilder.append("⚠️ Plato $id no encontrado.\n")
                    }
                    checkFinish()
                }
            }

            fun checkFinish() {
                remaining--
                if (remaining == 0) {
                    onResult(resultBuilder.toString().trim())
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

                } catch (e: Exception) {
                    onResult("❌ Error al procesar JSON: ${e.message}")
                }
            }
        }
    })
}




