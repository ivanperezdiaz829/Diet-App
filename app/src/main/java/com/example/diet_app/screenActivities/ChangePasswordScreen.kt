package com.example.diet_app.screenActivities

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diet_app.screenActivities.components.BackButtonLeft
import com.example.diet_app.viewModel.UserViewModel
import com.example.ui.components.*

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit,
    onNext: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Estados para el diálogo de error
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Función mejorada de validación
    fun validatePassword(): Boolean {
        return when {
            newPassword.isEmpty() || confirmPassword.isEmpty() -> {
                errorMessage = "Por favor, completa ambos campos"
                false
            }
            newPassword.length < 8 -> {
                errorMessage = "La contraseña debe tener al menos 8 caracteres"
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
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TitleSection()
            }

            UsernameText(userViewModel.getUser().email)
            ChangePasswordTitle()
            PasswordRequirementText()
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
            ForgotPasswordLink { }

            ChangePasswordButton {
                if (validatePassword()) {
                    onNext() // Solo navega si la validación es exitosa
                } else {
                    showErrorDialog = true // Muestra el error
                }
            }
        }
    }

    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error en la contraseña") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(
                    onClick = { showErrorDialog = false }
                ) {
                    Text("Entendido")
                }
            }
        )
    }
}
