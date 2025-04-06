package com.example.diet_app

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun LoginScreen(
    navController: NavController,
    context: Context = LocalContext.current // por si no lo pasas explícitamente
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF797979))
            .padding(top = 82.dp, start = 8.dp, end = 8.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        LoginCard(context = context) { success ->
            if (success) {
                navController.navigate("welcome") {
                    popUpTo("login") { inclusive = true } // evita volver con back
                }
            }
        }
    }
}

@Composable
private fun LoginCard(
    context: Context,
    onLoginResult: (Boolean) -> Unit
) {
    var isLogin by remember { mutableStateOf(true) } // Alterna entre login y signup
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") } // Mensaje de error
    var showError by remember { mutableStateOf(false) }

    val dbManager = remember { DatabaseManager(context) }

    Card(
        modifier = Modifier
            .width(342.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Header image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(233.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.healthfoods),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(30.dp))

            // Botones Log In / Sign Up
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val selectedColor = Color(0xFF40B93C)
                val unselectedColor = Color.White
                val selectedTextColor = Color.White
                val unselectedTextColor = Color(0xFF767676)

                // Botón Log In
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(39.dp)
                        .border(
                            width = 0.8.dp,
                            color = Color(0x40000000),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Button(
                        onClick = { isLogin = true },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLogin) selectedColor else unselectedColor
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Log In",
                            fontSize = 15.sp,
                            color = if (isLogin) selectedTextColor else unselectedTextColor
                        )
                    }
                }

                // Botón Sign Up
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(39.dp)
                        .border(
                            width = 0.8.dp,
                            color = Color(0x40000000),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Button(
                        onClick = { isLogin = false },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isLogin) selectedColor else unselectedColor
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Sign Up",
                            fontSize = 15.sp,
                            color = if (!isLogin) selectedTextColor else unselectedTextColor
                        )
                    }
                }
            }

            // Transición animada
            AnimatedContent(targetState = isLogin, label = "FormSwitch") { login ->
                Column {
                    if (!login) {
                        // Campo de nombre solo si es Sign Up
                        CustomTextField(
                            label = "Name",
                            value = name,
                            onValueChange = { name = it },
                            isError = showError,
                            errorMessage = errorMessage
                        )
                    }

                    CustomTextField(
                        label = "Email or Username",
                        value = username,
                        onValueChange = { username = it },
                        isError = showError,
                        errorMessage = errorMessage
                    )

                    CustomTextField(
                        label = "Password",
                        value = password,
                        onValueChange = { password = it },
                        isError = showError,
                        errorMessage = errorMessage
                    )

                    // Botón principal
                    Button(
                        onClick = {
                            // Validación
                            if (username.isEmpty() || password.isEmpty() || (!login && name.isEmpty())) {
                                errorMessage = "All fields are required"
                                showError = true
                            } else if (!isLogin && !android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                                errorMessage = "Please enter a valid email address"
                                showError = true
                            } else if (login) {
                                // Validar si el usuario existe
                                val userExists = dbManager.authenticateUser(username.trim(), password.trim())
                                if (userExists) {
                                    errorMessage = ""
                                    showError = false
                                    onLoginResult(true)
                                } else {
                                    errorMessage = "User not found"
                                    showError = true
                                }
                            } else {
                                // Validar que las contraseñas coinciden (solo Sign Up)
                                if (password.length < 6) {
                                    errorMessage = "Password must be at least 6 characters long"
                                    showError = true
                                } else {
                                    // Llamada para registro
                                    val success = dbManager.registerUser(name.trim(), username.trim(), password.trim())
                                    if (success) {
                                        errorMessage = ""
                                        showError = false
                                        onLoginResult(true)
                                    } else {
                                        errorMessage = "Registration failed"
                                        showError = true
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .width(183.dp)
                            .height(39.dp)
                            .align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF40B93C)),
                        shape = RoundedCornerShape(15.dp)
                    ) {
                        Text(
                            text = if (login) "Log In" else "Sign Up",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Default,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Divider
            Text(
                text = "OR",
                fontSize = 15.sp,
                color = Color(0xFF929292),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Login con redes sociales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.facebook),
                    contentDescription = "Facebook Login",
                    modifier = Modifier
                        .size(45.dp)
                        .padding(end = 16.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google Login",
                    modifier = Modifier
                        .size(45.dp)
                        .padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// Componente para el TextField sin bordes con retroalimentación
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false, // Variable para manejar el estado de error
    errorMessage: String = "" // Mensaje de error
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError, // Indicador de error
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedIndicatorColor = if (isError) Color.Red else Color(0xFFC4C4C4),
            unfocusedIndicatorColor = if (isError) Color.Red else Color(0xFFC4C4C4),
            disabledIndicatorColor = Color.Transparent,
        ),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    )

    // Mensaje de error si existe
    if (isError && errorMessage.isNotEmpty()) {
        Text(
            text = errorMessage,
            color = Color.Red,
            style = TextStyle(fontSize = 12.sp)
        )
    }
}
