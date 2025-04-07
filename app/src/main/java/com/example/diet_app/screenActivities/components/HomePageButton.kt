package com.example.diet_app.screenActivities.components


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import okhttp3.*
import androidx.compose.foundation.layout.*

@Composable
fun ColoredButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick, // Llama a la función onClick pasada como argumento
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .size(120.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 16.sp)
    }
}