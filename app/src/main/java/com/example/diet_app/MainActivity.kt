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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    private val authManager = AuthManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DietForm()
        }
        if (authManager.usuarioActual()) {
            Log.d("AuthTest", "Usuario autenticado. Probando conexión a la base de datos...")
            checkDatabaseConnection()
            fetchAllUsers()
        } else {
            Log.d("AuthTest", "No hay usuario autenticado. Intentando iniciar sesión...")

            val email = "gloton3@gloton3.com"
            val password = "gloton3"

            authManager.iniciarSesion(email, password) { success, message ->
                if (success) {
                    Log.d("AuthTest", "Inicio de sesión exitoso.")
                    checkDatabaseConnection()
                    fetchAllUsers()
                } else {
                    Log.e("AuthTest", "Error de inicio de sesión: $message. Registrando usuario...")

                    authManager.registrarUsuario(email, password) { regSuccess, regMessage ->
                        if (regSuccess) {
                            Log.d("AuthTest", "Registro exitoso. Iniciando sesión de nuevo...")
                            authManager.iniciarSesion(email, password) { _, _ -> }
                        } else {
                            Log.e("AuthTest", "Error en el registro: $regMessage")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DietForm() {
    var minCalories by remember { mutableStateOf("") }
    var maxCalories by remember { mutableStateOf("") }
    var minFat by remember { mutableStateOf("") }
    var maxFat by remember { mutableStateOf("") }
    var minSalt by remember { mutableStateOf("") }
    var maxSalt by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Configurar valores nutricionales", style = MaterialTheme.typography.titleLarge)

        InputField(label = "Kcal mínimas", value = minCalories) { minCalories = it }
        InputField(label = "Kcal máximas", value = maxCalories) { maxCalories = it }
        InputField(label = "Grasa mínima (g)", value = minFat) { minFat = it }
        InputField(label = "Grasa máxima (g)", value = maxFat) { maxFat = it }
        InputField(label = "Sal mínima (g)", value = minSalt) { minSalt = it }
        InputField(label = "Sal máxima (g)", value = maxSalt) { maxSalt = it }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Aquí puedes procesar los valores ingresados
            println("Min Kcal: $minCalories, Max Kcal: $maxCalories")
        }) {
            Text("Generar dieta")
        }
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

class AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun usuarioActual(): Boolean {
        return auth.currentUser != null
    }

    fun iniciarSesion(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("AuthManager", "Inicio de sesión exitoso para: $email")
                callback(true, null)
            }
            .addOnFailureListener { exception ->
                Log.e("AuthManager", "Error de inicio de sesión: ${exception.message}")
                callback(false, exception.message)
            }
    }

    fun registrarUsuario(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("AuthManager", "Registro exitoso para: $email")
                callback(true, null)
            }
            .addOnFailureListener { exception ->
                Log.e("AuthManager", "Error de registro: ${exception.message}")
                callback(false, exception.message)
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
