package com.example.diet_app.screenActivities

import androidx.compose.foundation.layout.*
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
    userViewModel: UserViewModel,) {

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    BackButtonLeft(onNavigateBack = { navController.popBackStack() })

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

            UsernameText(userViewModel.getUser().name)
            ChangePasswordTitle()
            PasswordRequirementText()
            PasswordField(
                label = "New Password",
                password = newPassword,
                onValueChange = { newPassword = it }
            )
            PasswordField(
                label = "Enter again your new password",
                password = confirmPassword,
                onValueChange = { confirmPassword = it }
            )
            ForgotPasswordLink { }
            ChangePasswordButton { }
        }
    }
}
