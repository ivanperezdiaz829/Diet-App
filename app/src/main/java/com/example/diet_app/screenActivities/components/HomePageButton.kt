package com.example.diet_app.screenActivities.components


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.unit.sp

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