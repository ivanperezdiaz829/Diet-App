package com.example.diet_app.screenActivities

import android.content.Context
import android.util.Patterns
import android.widget.Toast
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diet_app.R
import com.example.diet_app.authenticateUser
import com.example.diet_app.model.Goal
import com.example.diet_app.viewModel.UserViewModel


@Composable
fun LoginScreen(
    applicationContext: Context,
    userViewModel: UserViewModel,
    onLoginSuccess: (UserViewModel) -> Unit,
    onRegisterSuccess: (UserViewModel) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF797979))
            .padding(top = 8.dp, start = 8.dp, end = 8.dp),
        contentAlignment = Alignment.Center
    ) {

        LoginCard(
            userViewModel = userViewModel,
            onLoginResult = { isRegistration ->
                if (isRegistration) {
                    onRegisterSuccess(
                        userViewModel
                    )
                } else {
                    onLoginSuccess(
                        userViewModel
                    )
                }
            },
            applicationContext = applicationContext
        )
    }
}

@Composable
private fun LoginCard(
    userViewModel: UserViewModel,
    onLoginResult: (Boolean) -> Unit,
    applicationContext: Context
) {
    var isLogin by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

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

            // Log In / Sign Up buttons
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

                // Log In button
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
                            text = "Iniciar sesión",
                            fontSize = 15.sp,
                            color = if (isLogin) selectedTextColor else unselectedTextColor
                        )
                    }
                }

                // Sign Up button
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
                            text = "Registrarse",
                            fontSize = 15.sp,
                            color = if (!isLogin) selectedTextColor else unselectedTextColor
                        )
                    }
                }
            }

            // Form content
            AnimatedContent(targetState = isLogin, label = "FormSwitch") { login ->
                Column {

                    CustomTextField(
                        label = "Correo electrónico",
                        value = username,
                        onValueChange = { username = it },
                        isError = showError,
                        errorMessage = errorMessage
                    )

                    CustomTextField(
                        label = "Contraseña",
                        value = password,
                        onValueChange = { password = it },
                        isError = showError,
                        errorMessage = errorMessage
                    )

                    Button(
                        onClick = {

                            if (!isLogin && !Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                                errorMessage = "Please enter a valid email address"
                                showError = true
                            } else if (!isLogin && password.length < 8) {
                                errorMessage = "Password must be at least 8 characters long"
                                showError = true
                            } else {

                                errorMessage = ""
                                showError = false

                                // Update ViewModel with user data
                                if (isLogin) {
                                    // For login, we might just verify credentials later
                                    authenticateUser(
                                        email = username,
                                        password = password,
                                        context = applicationContext,
                                        userViewModel = userViewModel
                                    ) { result ->
                                        when {
                                            result.isSuccess -> {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Usuario autenticado\n" +
                                                            "Bienvenido ${userViewModel.getUser().email}!",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                onLoginResult(!login)
                                            }
                                            result.isFailure -> {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Fallo de autenticación",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }

                                } else {
                                    // For registration, set the initial user data
                                    userViewModel.updateUser(
                                        name = name,
                                        email = username,
                                        password = password
                                    )
                                    onLoginResult(!login)
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
                            text = if (login) "Iniciar sesión" else "Registrarse",
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Default,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            focusedIndicatorColor = if (isError) Color.Red else Color(0xFFC4C4C4),
            unfocusedIndicatorColor = if (isError) Color.Red else Color(0xFFC4C4C4),
            disabledIndicatorColor = Color.Transparent,
        ),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    )

    if (isError && errorMessage.isNotEmpty()) {
        Text(
            text = errorMessage,
            color = Color.Red,
            style = TextStyle(fontSize = 12.sp)
        )
    }
}