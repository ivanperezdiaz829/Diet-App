package com.example.diet_app.screenActivities

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diet_app.screenActivities.components.BackButtonLeft
import com.example.diet_app.updateUserPassword
import com.example.diet_app.viewModel.UserViewModel
import com.example.ui.components.*

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

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

    BackButtonLeft(onNavigateBack = onNavigateBack)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            TitleSection()
            UsernameText(userViewModel.getUser().email)
            ChangePasswordTitle()
            PasswordRequirementText()

            PasswordField(
                label = "Contraseña actual",
                password = currentPassword,
                onValueChange = { currentPassword = it }
            )
            PasswordField(
                label = "Nueva contraseña",
                password = newPassword,
                onValueChange = { newPassword = it }
            )
            PasswordField(
                label = "Confirmación de su nueva contraseña",
                password = confirmPassword,
                onValueChange = { confirmPassword = it }
            )

            ChangePasswordButton {
                if (validatePassword()) {
                    updateUserPassword(
                        id = userViewModel.getUser().id,
                        currentPassword = currentPassword,
                        newPassword = newPassword,
                        context = context,
                        onResult = {
                            onNext() // Solo navega si la API responde con éxito
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
