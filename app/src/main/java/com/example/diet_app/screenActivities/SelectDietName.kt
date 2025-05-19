package com.example.diet_app.screenActivities

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.font.FontWeight
import com.example.diet_app.screenActivities.components.BackButtonLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton

@Composable
fun DietNameScreen(
    onNavigateBack: () -> Unit,
    onNext: (String) -> Unit
) {
    var dietName by remember { mutableStateOf("") }
    var isValid by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

    val customGreen = Color(0xFF40B93C)

    BackButtonLeft(onNavigateBack)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text("¿Confirmar nombre?")
            },
            text = {
                Text("¿Estás seguro de que deseas generar la dieta con el nombre \"$dietName\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onNext(dietName)
                    }
                ) {
                    Text("Sí", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Ingresa el ",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                "nombre de tu dieta",
                fontSize = 28.sp,
                color = customGreen,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = dietName,
            onValueChange = {
                dietName = it
                isValid = dietName.length in 4..20
            },
            label = { Text("Nombre de la dieta") },
            isError = !isValid,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        if (!isValid) {
            Text(
                text = "El nombre debe tener entre 4 y 20 caracteres.",
                color = Color.Red,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                if (isValid) showDialog = true
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = customGreen,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            enabled = isValid
        ) {
            Text(
                text = "Confirmar Nombre",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}