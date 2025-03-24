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

    private val authHandler = AuthHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DietForm()
        }

        // Verificar usuario actual y manejar autenticación
        if (!authHandler.usuarioActual()) {
            Log.d("AuthTest", "Usuario autenticado. Probando conexión a la base de datos...")
            authHandler.checkDatabaseConnection()
            authHandler.fetchAllUsers()
        } else {
            Log.d("AuthTest", "No hay usuario autenticado. Intentando iniciar sesión...")

            val email = "gloton3@gloton3.com"
            val password = "gloton3"

            authHandler.iniciarSesion(email, password) { success, message ->
                if (success) {
                    Log.d("AuthTest", "Inicio de sesión exitoso.")
                    authHandler.checkDatabaseConnection()
                    authHandler.fetchAllUsers()
                } else {
                    Log.e("AuthTest", "Error de inicio de sesión: $message. Registrando usuario...")

                    authHandler.registrarUsuario(email, password) { regSuccess, regMessage ->
                        if (regSuccess) {
                            Log.d("AuthTest", "Registro exitoso. Iniciando sesión de nuevo...")
                            authHandler.iniciarSesion(email, password) { _, _ -> }
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
