package com.example.diet_app.screenActivities

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diet_app.screenActivities.components.BackButton
import com.example.diet_app.screenActivities.components.BackButtonLeft
import com.example.diet_app.updateUserPassword
import com.example.diet_app.viewModel.UserViewModel
import com.example.ui.components.*

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit,
    onNext: (currentPassword: String, newPassword: String) -> Unit
) {
    val context = LocalContext.current

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    fun validatePassword(): Boolean {
        return when {
            currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() -> {
                errorMessage = "Por favor, completa todos los campos"
                false
            }
            newPassword.length < 8 -> {
                errorMessage = "La nueva contraseña debe tener al menos 8 caracteres"
                false
            }
            newPassword != confirmPassword -> {
                errorMessage = "Las contraseñas no coinciden"
                false
            }
            else -> true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            BackButton(onNavigateBack = onNavigateBack)

            TitleSection()
            UsernameText(userViewModel.getUser().email)
            ChangePasswordTitle()
            PasswordRequirementText()

            PasswordField(
                label = "Contraseña actual",
                password = currentPassword,
                onValueChange = { currentPassword = it },
                visualTransformation = PasswordVisualTransformation()
            )
            PasswordField(
                label = "Nueva contraseña",
                password = newPassword,
                onValueChange = { newPassword = it },
                visualTransformation = PasswordVisualTransformation()
            )
            PasswordField(
                label = "Confirmación de su nueva contraseña",
                password = confirmPassword,
                onValueChange = { confirmPassword = it },
                visualTransformation = PasswordVisualTransformation()
            )

            ChangePasswordButton {
                if (validatePassword()) {
                    updateUserPassword(
                        id = userViewModel.getUser().id,
                        currentPassword = currentPassword,
                        newPassword = newPassword,
                        context = context,
                        onResult = {
                            onNext(currentPassword, newPassword)
                        },
                        onError = {
                            errorMessage = it
                            showErrorDialog = true
                        }
                    )
                } else {
                    showErrorDialog = true
                }
            }
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
fun PasswordField(
    label: String,
    password: String,
    onValueChange: (String) -> Unit,
    visualTransformation: VisualTransformation = PasswordVisualTransformation(),
    modifier: Modifier = Modifier
) {
    TextField(
        value = password,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        visualTransformation = visualTransformation,
        modifier = modifier.fillMaxWidth(),
        // Otros parámetros que necesites
    )
}