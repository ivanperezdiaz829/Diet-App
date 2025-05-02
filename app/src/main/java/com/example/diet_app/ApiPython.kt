package com.example.diet_app

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
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

fun sendDataToServer(values: List<Double>, context: Context, onResult: (String) -> Unit) {
    val client = OkHttpClient()
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
                    val dias = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
                    val stringBuilder = StringBuilder()

                    for (i in 0 until jsonArray.length()) {
                        val dayData = jsonArray.getJSONObject(i)
                        val breakfast = dayData.getString("breakfast")
                        val lunch = dayData.getString("lunch")
                        val dinner = dayData.getString("dinner")

                        val dayName = dias.getOrElse(i) { "Día ${i + 1}" }

                        stringBuilder.append("📅 *$dayName*\n")
                        stringBuilder.append("🍳 **Desayuno:** $breakfast\n")
                        stringBuilder.append("🥗 **Almuerzo:** $lunch\n")
                        stringBuilder.append("🍽 **Cena:** $dinner\n\n")

                        // Guardar en SharedPreferences por día
                        val dietData = JSONObject().apply {
                            put("breakfast", breakfast)
                            put("lunch", lunch)
                            put("dinner", dinner)
                        }
                        editor.putString("${dayName.lowercase()}_diet", dietData.toString())
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
    sex: String,
    birthday: String,
    height: Double,
    weight: Double,
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

fun ImageView.loadBarplotImage(context: Context, dietJson: String) {
    val client = OkHttpClient()
    val url = "http://10.0.2.2:8000/generate_barplot"

    val requestBody = dietJson.toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("Graph", "Error al obtener gráfica: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val inputStream = response.body?.byteStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)
                (context as Activity).runOnUiThread {
                    setImageBitmap(bitmap)
                }
            } else {
                Log.e("Graph", "Error en la respuesta del servidor")
            }
        }
    })
}