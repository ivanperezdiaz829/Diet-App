package com.example.diet_app.screenActivities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diet_app.R
import com.example.diet_app.screenActivities.components.SettingsOption

// Pantalla de configuración del usuario con botón de logout funcional
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp)) // Ajusta el valor según lo necesites

        Text(
            text = "Settings",
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
        )

        // Imagen de perfil
        Image(
            painter = painterResource(id = R.drawable.male), // Imagen de perfil localizada en res/drawable
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        // Nombre de usuario
        Text(
            text = "@3j1s",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
        )

        // Spacer para agregar espacio entre el nombre de usuario y las opciones
        Spacer(modifier = Modifier.height(30.dp)) // Ajusta el valor según lo necesites

        // Opción: Cambiar contraseña
        SettingsOption(text = "Change your password", R.drawable.security_icon) {
            // Acción al tocar cambiar contraseña
        }

        // Opción: Editar datos
        SettingsOption(text = "Edit your data", R.drawable.data_icon) {
            // Acción al tocar editar datos
        }

        Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo

        // Botón de cerrar sesión
        Button(
            onClick = {
                // Acción al presionar "Log out"
                // Aquí puedes poner la lógica de logout, como limpiar datos, navegar al login, etc.
            },
            colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50)), // Verde
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Log out", color = Color.White, fontSize = 16.sp)
        }
    }
}
