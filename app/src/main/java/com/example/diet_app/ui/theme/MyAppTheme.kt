package com.example.diet_app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6200EE), // Color morado para elementos primarios
            onPrimary = Color.White,    // Texto en elementos primarios
            secondary = Color(0xFF03DAC6), // Color aqua para elementos secundarios
            background = Color(0xFFF5F5F5), // Fondo claro
            surface = Color.White      // Superficie (botones, tarjetas, etc.)
        ),
        typography = Typography(), // Tipografía predeterminada
        content = content
    )
}