package com.example.diet_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diet_app.ui.theme.DietappTheme
import androidx.compose.material3.Button
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diet_app.ui.theme.DietappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DietApp()
        }
        checkDatabaseConnection()
        fetchAllUsers()
    }
}

@Composable
fun DietApp() {
    // Usamos NavController para manejar la navegación
    val navController = rememberNavController()

    // Configuración de la navegación entre pantallas
    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(navController)  // Pantalla de bienvenida
        }
        composable("diet_form") {
            DietForm()  // Pantalla del formulario de la dieta
        }
    }
}

@Composable
fun WelcomeScreen(navController: NavController) {
    // Pantalla de bienvenida con fondo verde y mensaje
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8BC34A)), // Fondo verde
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡Bienvenido a la aplicación de dieta!",
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    // Navegar a la pantalla del formulario de la dieta
                    navController.navigate("diet_form")
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Ir al formulario")
            }
        }
    }
}

@Composable
fun DietForm() {
    var minCarbohydrates by remember { mutableStateOf("") }
    var maxCarbohydrates by remember { mutableStateOf("") }
    var minSugar by remember { mutableStateOf("") }
    var maxSugar by remember { mutableStateOf("") }
    var minEnergy by remember { mutableStateOf("") }
    var maxEnergy by remember { mutableStateOf("") }
    var minProtein by remember { mutableStateOf("") }
    var maxProtein by remember { mutableStateOf("") }
    var minSalt by remember { mutableStateOf("") }
    var maxSalt by remember { mutableStateOf("") }
    var minFat by remember { mutableStateOf("") }
    var maxFat by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Configurar valores nutricionales", style = MaterialTheme.typography.titleLarge)

        InputField(label = "Carbs mínimas", value = minCarbohydrates) { minCarbohydrates= it }
        InputField(label = "Carbs máximas", value = maxCarbohydrates) { maxCarbohydrates = it }
        InputField(label = "Azúcar mínimas", value = minSugar) { minSugar = it }
        InputField(label = "Azúcar máximas", value = maxSugar) { maxSugar = it }
        InputField(label = "Energía mínimas", value = minEnergy) { minEnergy = it }
        InputField(label = "Energía máximas", value = maxEnergy) { maxEnergy = it }
        InputField(label = "Proteina mínimas", value = minProtein) { minProtein= it }
        InputField(label = "Proteina máximas", value = maxProtein) { maxProtein= it }
        InputField(label = "Sal mínima (g)", value = minSalt) { minSalt = it }
        InputField(label = "Sal máxima (g)", value = maxSalt) { maxSalt = it }
        InputField(label = "Grasa mínima (g)", value = minFat) { minFat = it }
        InputField(label = "Grasa máxima (g)", value = maxFat) { maxFat = it }
        InputField(label = "Presupuesto", value = budget) { budget = it }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Convertir todos los valores a Double, ignorando los que no sean válidos
            val numericValues = listOf(minCarbohydrates, maxCarbohydrates, minSugar, maxSugar,
                minEnergy, maxEnergy, minProtein, maxProtein, minSalt, maxSalt, minFat, maxFat, budget)
                .map { it.replace(",", ".") }  // Asegura el formato correcto de decimales
                .mapNotNull { it.toDoubleOrNull() }  // Convierte String a Double si es válido

            Log.d("DietForm", "Valores convertidos a Double: $numericValues")

            if (numericValues.size == 13) { // Asegurar que todos los valores sean numéricos
                sendDataToServer(numericValues) { response ->
                    result = response
                }
            } else {
                result = "Error: Ingresa valores numéricos válidos"
            }
        }) {
            Text("Generar dieta")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Resultado: $result", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}

fun sendDataToServer(values: List<Double>, onResult: (String) -> Unit) {
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
            onResult("Error: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseBody ->
                Log.d("DietForm", "Respuesta del servidor: $responseBody")
                try {
                    val jsonResponse = JSONObject(responseBody)
                    if (jsonResponse.has("error")) {
                        onResult("Error: ${jsonResponse.getString("error")}")
                    } else {
                        val breakfast = jsonResponse.getString("breakfast")
                        val lunch = jsonResponse.getJSONArray("lunch")
                        val dinner = jsonResponse.getJSONArray("dinner")

                        // Convierte JSONArray a String
                        val lunchList = (0 until lunch.length()).map { lunch.getString(it) }
                        val dinnerList = (0 until dinner.length()).map { dinner.getString(it) }

                        val resultString = """
                            🍳 **Desayuno:** $breakfast
                            
                            🥗 **Almuerzo:** 
                            - ${lunchList.joinToString("\n- ")}
                            
                            🍽 **Cena:** 
                            - ${dinnerList.joinToString("\n- ")}
                        """.trimIndent()

                        onResult(resultString)
                    }
                } catch (e: Exception) {
                    onResult("Error al procesar la respuesta")
                }
            }
        }
    })
}

fun checkDatabaseConnection() {
    val db = FirebaseFirestore.getInstance()

    // Intenta realizar una consulta simple a la base de datos
    db.collection("testConnection")
        .get()
        .addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                Log.d("FirestoreConnection", "Conexión exitosa: la colección está vacía o no existe.")
            } else {
                Log.d("FirestoreConnection", "Conexión exitosa: datos encontrados en la colección.")
            }
        }
        .addOnFailureListener { exception ->
            Log.e("FirestoreConnection", "Error al conectar con la base de datos: ${exception.message}")
        }
}

fun fetchAllUsers() {
    try {
        val db = FirebaseFirestore.getInstance()

        // Accede a la colección "users"
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("FirestoreUsers", "No se encontraron usuarios en la base de datos.")
                } else {
                    for (document in documents) {
                        // Muestra los datos de cada documento (usuario)
                        Log.d(
                            "FirestoreUsers",
                            "Usuario ID: ${document.id}, Datos: ${document.data}"
                        )
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreUsers", "Error al obtener usuarios: ${exception.message}")
            }
    } catch (e: SecurityException) {
        Log.e("MyAppTag", "SecurityException: ${e.message}", e) // Log with the exception details
    } catch (e: Exception){
        Log.e("MyAppTag", "General exception", e) //Log general exception
    }
}
