package com.example.diet_app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private lateinit var dbManager: DatabaseManager

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbManager = DatabaseManager(this)

        // Prueba abriendo la base de datos
        try {
            val database = dbManager.openDatabase()
            Log.d("MainActivity", "Base de datos abierta correctamente: $database")
            database.close()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al abrir la base de datos: ${e.message}")
        }
        setContent {
            DietApp(dbManager = dbManager, applicationContext = applicationContext, viewModel = viewModel)
        }
    }
}

class MainViewModel : ViewModel() {
    internal lateinit var currentUser: String
    internal lateinit var currentEmail: String
    internal lateinit var basalMetabolism: String
    internal lateinit var maintenanceCalories: String

    fun updateUser(
        currentUser: String = "",
        currentEmail: String = "",
        basalMetabolism: String = "",
        maintenanceCalories: String = ""
    ) {
        if (currentUser.isNotEmpty()) {
            this.currentUser = currentUser
        }
        if (currentEmail.isNotEmpty()) {
            this.currentEmail = currentEmail
        }
        if (basalMetabolism.isNotEmpty()) {
            this.basalMetabolism = basalMetabolism
        }
        if (maintenanceCalories.isNotEmpty()) {
            this.maintenanceCalories = maintenanceCalories
        }
    }
}


@Composable
fun DietApp(dbManager: DatabaseManager, applicationContext: Context, viewModel: MainViewModel) {
    // Usamos NavController para manejar la navegación
    val navController = rememberNavController()

    // Configuración de la navegación entre pantallas
    NavHost(navController = navController, startDestination = "auth") {

        composable("auth") {
            AuthScreen(
                onAuthenticate = { email, password ->
                    val isAuthenticated = dbManager.authenticateUser(email, password)
                    if (isAuthenticated) {
                        Toast.makeText(applicationContext, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        viewModel.updateUser(currentEmail = email)
                        viewModel.updateUser(currentUser = dbManager.getName(email).toString())
                        navController.navigate("welcome")
                    } else {
                        Toast.makeText(applicationContext, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                },
                onRegister = { name, email, password ->
                    val isRegistered = dbManager.registerUser(name, email, password)
                    if (isRegistered) {
                        Toast.makeText(applicationContext, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        viewModel.updateUser(currentEmail = email)
                        viewModel.updateUser(currentUser = dbManager.getName(email).toString())
                        navController.navigate("welcome")
                    } else {
                        Toast.makeText(applicationContext, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        composable("welcome") {
            WelcomeScreen(navController, viewModel)  // Pantalla de bienvenida
        }
        composable("diet_form") {
            DietForm(viewModel)  // Pantalla del formulario de la dieta
        }
        composable("basal_metabolism") {
            BasalMetabolismScreen(viewModel)
        }
        composable("maintenance_calories") {
            MaintenanceCaloriesScreen(viewModel)
        }
        composable("calendar") {
            CalendarScreen(navController, viewModel)  // Pantalla de calendario
        }
        composable("main_screen") {
            ButtonGridScreen(navController) // Pasar el navController aquí
        }

        composable("edit_meal/{dayIndex}") { backStackEntry ->
            val dayIndex = backStackEntry.arguments?.getString("dayIndex")?.toInt() ?: 0
            EditMealScreen(navController, dayIndex, viewModel)  // Pantalla de edición de comidas
        }
    }
}

@Composable
fun WelcomeScreen(navController: NavController, viewModel: MainViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8BC34A)), // Fondo verde
        contentAlignment = Alignment.Center
    ) {
        // Contenido principal de la pantalla
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Si el usuario está autenticado (currentUser no está vacío)
            var currentUser = viewModel.currentUser
            if (currentUser.isNotEmpty()) {
                // Símbolo en la esquina superior izquierda y mensaje de bienvenida
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), // Margen superior
                ) {
                    // Mensaje "Bienvenido gloton" en el centro superior
                    Text(
                        text = "¡Bienvenido $currentUser!",
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                        modifier = Modifier.align(Alignment.TopCenter)
                    )

                    // Símbolo en la esquina superior izquierda
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, shape = CircleShape)
                            .align(Alignment.TopStart),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓", // Símbolo
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Green)
                        )
                    }
                }
            } else {
                Text(
                    text = "¡Bienvenido a la aplicación de dieta!",
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                    modifier = Modifier.padding(16.dp)
                )
            }
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
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("basal_metabolism") }) {
                Text("Calcular Gasto Energético Basal")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("maintenance_calories") }) {
                Text("Calcular Calorías de Mantenimiento")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("calendar") }) {
                Text("Ver Calendario de Comidas")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("main_screen") }) {
                Text("Ver Pantalla Principal")
            }
        }
    }
}

@Composable
fun DietForm(viewModel: MainViewModel) {
    var minCarbohydrates by remember { mutableStateOf("") }
    var maxCarbohydrates by remember { mutableStateOf("") }
    var maxSugar by remember { mutableStateOf("") }
    var minEnergy by remember { mutableStateOf("") }
    var maxEnergy by remember { mutableStateOf("") }
    var minProtein by remember { mutableStateOf("") }
    var maxProtein by remember { mutableStateOf("") }
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
        InputField(label = "Azúcar máximas", value = maxSugar) { maxSugar = it }
        InputField(label = "Energía mínimas", value = minEnergy) { minEnergy = it }
        InputField(label = "Energía máximas", value = maxEnergy) { maxEnergy = it }
        InputField(label = "Proteina mínimas", value = minProtein) { minProtein= it }
        InputField(label = "Proteina máximas", value = maxProtein) { maxProtein= it }
        InputField(label = "Sal máxima (g)", value = maxSalt) { maxSalt = it }
        InputField(label = "Grasa mínima (g)", value = minFat) { minFat = it }
        InputField(label = "Grasa máxima (g)", value = maxFat) { maxFat = it }
        InputField(label = "Presupuesto", value = budget) { budget = it }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Convertir todos los valores a Double, ignorando los que no sean válidos
            val numericValues = listOf(minCarbohydrates, maxCarbohydrates, maxSugar,
                minEnergy, maxEnergy, minProtein, maxProtein, maxSalt, minFat, maxFat, budget)
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
fun BasalMetabolismScreen(viewModel: MainViewModel) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputField("Peso (kg)", weight) { weight = it }
        InputField("Altura (cm)", height) { height = it }
        InputField("Edad (años)", age) { age = it }
        InputField("Género (M/F)", gender) { gender = it }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            result = calculateBasalMetabolicRate(weight, height, age, gender)
        }) {
            Text("Calcular")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = result)
    }
    viewModel.updateUser(basalMetabolism = result)
}

fun calculateBasalMetabolicRate(weight: String, height: String, age: String, gender: String): String {
    val w = weight.toDoubleOrNull() ?: return "Peso no válido"
    val h = height.toDoubleOrNull() ?: return "Altura no válida"
    val a = age.toDoubleOrNull() ?: return "Edad no válida"

    return if (gender.equals("M", ignoreCase = true)) {
        val bmr = 88.362 + (13.397 * w) + (4.799 * h) - (5.677 * a)
        "Gasto Energético Basal: ${String.format("%.2f", bmr)} kcal/día"
    } else if (gender.equals("F", ignoreCase = true)) {
        val bmr = 447.593 + (9.247 * w) + (3.098 * h) - (4.330 * a)
        "Gasto Energético Basal: ${String.format("%.2f", bmr)} kcal/día"
    } else {
        "Género no válido"
    }
}

@Composable
fun MaintenanceCaloriesScreen(viewModel: MainViewModel) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var physicalActivityLevel by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    Column(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputField("Peso (kg)", weight) { weight = it }
        InputField("Altura (cm)", height) { height = it }
        InputField("Edad (años)", age) { age = it }
        InputField("Género (M/F)", gender) { gender = it }
        InputField("Nivel de actividad física (1/2/3/4/5)", physicalActivityLevel) { physicalActivityLevel = it }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            result = calculateMaintenanceCalories(weight, height, age, gender, physicalActivityLevel)
        }) {
            Text("Calcular")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = result)
    }
    viewModel.updateUser(maintenanceCalories = result)
}

fun calculateMaintenanceCalories(weight: String, height: String, age: String, gender: String, physicalActivityLevel: String): String {
    val w = weight.toDoubleOrNull() ?: return "Peso no válido"
    val h = height.toDoubleOrNull() ?: return "Altura no válida"
    val a = age.toDoubleOrNull() ?: return "Edad no válida"
    val pal = physicalActivityLevel.toDoubleOrNull() ?: return "Nivel de actividad física no válido"
    val physicalActivityCoefficients = arrayOf(1.2, 1.375,1.55, 1.725, 1.9)
    return if (gender.equals("M", ignoreCase = true)) {
        val mc = physicalActivityCoefficients[pal.toInt()] * (66 + (13.7 * w) + (5 * h) - (6.8 * a))
        "Calorías de mantenimiento: ${String.format("%.2f", mc)} kcal/día"
    } else if (gender.equals("F", ignoreCase = true)) {
        val mc = physicalActivityCoefficients[pal.toInt()] * (665 + (9.6 * w) + (1.8 * h) - (4.7 * a))
        "Calorías de mantenimiento: ${String.format("%.2f", mc)} kcal/día"
    } else {
        "Género no válido"
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

@Composable
fun CalendarScreen(navController: NavController, viewModel: MainViewModel) {
    val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val meals = remember { mutableStateOf(emptyList<String>()) }  // Aquí se guardarán las comidas del día

    // Simulamos que tenemos una comida por cada día
    val mealList = daysOfWeek.map { "$it: Desayuno, Almuerzo, Cena" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Calendario de comidas", style = MaterialTheme.typography.titleLarge)

        mealList.forEachIndexed { index, day ->
            Text(day, modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodyLarge)
            Button(
                onClick = {
                    // Accede al día específico para editar las comidas
                    navController.navigate("edit_meal/$index")
                },
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text("Editar Comidas")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("welcome") }) {
            Text("Regresar al inicio")
        }
    }
}

@Composable
fun EditMealScreen(navController: NavController, dayIndex: Int, viewModel: MainViewModel) {
    var breakfast by remember { mutableStateOf("") }
    var lunch by remember { mutableStateOf("") }
    var dinner by remember { mutableStateOf("") }

    // Aquí podrías consultar los datos del servidor para obtener las comidas del día
    // Simulamos que obtenemos las comidas para el día seleccionado
    val meal = when (dayIndex) {
        0 -> listOf("Huevos", "Ensalada", "Pollo al horno")
        1 -> listOf("Avena", "Arroz con pollo", "Pasta")
        else -> listOf("", "", "")
    }

    breakfast = meal[0]
    lunch = meal[1]
    dinner = meal[2]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Editar Comidas", style = MaterialTheme.typography.titleLarge)

        InputField("Desayuno", breakfast) { breakfast = it }
        InputField("Almuerzo", lunch) { lunch = it }
        InputField("Cena", dinner) { dinner = it }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Aquí puedes enviar las comidas al servidor para actualizarlas
            val updatedMeals = mapOf(
                "breakfast" to breakfast,
                "lunch" to lunch,
                "dinner" to dinner
            )
            // updateMealsForDay(dayIndex, updatedMeals)
            navController.navigate("calendar")
        }) {
            Text("Guardar Cambios")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("calendar") }) {
            Text("Cancelar")
        }
    }
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

@Composable
fun ButtonGridScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8BC34A)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fila de botones para las diferentes funcionalidades
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ColoredButton("Formulario Dieta", Color.Black) {
                navController.navigate("diet_form")
            }
            ColoredButton("Gasto Basal", Color.Black) {
                navController.navigate("basal_metabolism")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ColoredButton("Calorías Mantenimiento", Color.Black) {
                navController.navigate("maintenance_calories")
            }
            ColoredButton("Calendario", Color.Black) {
                navController.navigate("calendar")
            }
        }
    }
}

@Composable
fun ColoredButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick, // Llama a la función onClick pasada como argumento
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .size(120.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 16.sp)
    }
}