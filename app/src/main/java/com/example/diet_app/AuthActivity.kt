package com.example.diet_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diet_app.ui.theme.MyAppTheme

class AuthActivity : ComponentActivity() {
    private lateinit var databaseManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databaseManager = DatabaseManager(this) // Inicializar DatabaseManager

        setContent {
            MyAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "auth") {
                    composable("auth") {
                        AuthScreen(
                            onAuthenticate = { email, password ->
                                val isAuthenticated = databaseManager.authenticateUser(email, password)
                                if (isAuthenticated) {
                                    Toast.makeText(applicationContext, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                                    navController.navigate("welcome")
                                } else {
                                    Toast.makeText(applicationContext, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onRegister = { name, email, password ->
                                val isRegistered = databaseManager.registerUser(name, email, password)
                                if (isRegistered) {
                                    Toast.makeText(applicationContext, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                    navController.navigate("welcome")
                                } else {
                                    Toast.makeText(applicationContext, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    composable("welcome") {
                        WelcomeScreen(navController)
                    }
                    composable("diet_form") {
                        DietForm()
                    }
                    composable("basal_metabolism") {
                        BasalMetabolismScreen()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthenticate: (email: String, password: String) -> Unit,
    onRegister: (name: String, email: String, password: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(true) } // Predeterminado en modo registro

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = if (isRegisterMode) "Registro" else "Inicio de Sesión") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isRegisterMode) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (isRegisterMode) {
                        onRegister(name, email, password)
                    } else {
                        onAuthenticate(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isRegisterMode) "Registrar" else "Iniciar Sesión")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { isRegisterMode = !isRegisterMode }
            ) {
                Text(
                    text = if (isRegisterMode) "¿Ya tienes cuenta? Inicia Sesión"
                    else "¿No tienes cuenta? Regístrate"
                )
            }
        }
    }
}