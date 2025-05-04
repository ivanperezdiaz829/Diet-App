package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleSection() {
    Text(
        text = "Ajustes",
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
fun UsernameText(username: String) {
    Text(
        text = username,
        color = Color.Gray,
        fontSize = 22.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun ChangePasswordTitle() {
    Text(
        text = "Cambiar contraseña",
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun PasswordRequirementText() {
    Text(
        text = "Tu contraseña debe tener al menos 8 caracteres.",
        color = Color.Gray,
        fontSize = 20.sp,
        modifier = Modifier.padding(bottom = 24.dp)
    )
}

@Composable
fun PasswordField(label: String, password: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = password,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 18.sp) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    )
}

@Composable
fun ForgotPasswordLink(onClick: () -> Unit) {
    Text(
        text = "¿Olvidaste la contraseña?",
        color = Color.Blue,
        fontSize = 18.sp,
        modifier = Modifier
            .padding(vertical = 12.dp)
            .clickable(onClick = onClick)
    )
}

@Composable
fun ChangePasswordButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50))
    ) {
        Text("Cambiar contraseña", color = Color.White, fontSize = 20.sp)
    }
}
