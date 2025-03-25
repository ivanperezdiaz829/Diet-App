package com.example.diet_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.diet_app.ui.theme.MyAppTheme

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                AuthScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }

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
                    // Aquí se llama a la función correspondiente según el modo (registro o login)
                    if (isRegisterMode) {
                        // Registrar usuario
                    } else {
                        // Iniciar sesión
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