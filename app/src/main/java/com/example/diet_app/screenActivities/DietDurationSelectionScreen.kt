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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.diet_app.R
import com.example.diet_app.screenActivities.components.BackButtonLeft


@Composable
fun DietDurationScreen(
    navigationVariable: Boolean,
    onNavigateBack: () -> Unit,
    onNextName: (Int) -> Unit,
    onNextDiet: (Int) -> Unit
) {
    var selectedDays by remember { mutableIntStateOf(1) }
    val customGreen = Color(0xFF40B93C)

    BackButtonLeft(onNavigateBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título con jerarquía visual
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Selecciona la ",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                "duración de tu dieta",
                fontSize = 28.sp,
                color = customGreen,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Instrucciones
        Text(
            text = "¿Para cuantos días debería durar la dieta?",
            fontSize = 18.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Selector de días
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Botón de disminuir
            IconButton(
                onClick = { if (selectedDays > 1) selectedDays-- },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.menos),
                    contentDescription = "Decrease days",
                    tint = if (selectedDays > 1) customGreen else Color.LightGray,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Display de días
            Text(
                text = "$selectedDays ${if (selectedDays == 1) "día" else "días"}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .width(120.dp)
                    .padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )

            // Botón de aumentar
            IconButton(
                onClick = { if (selectedDays < 7) selectedDays++ },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.mas),
                    contentDescription = "Increase days",
                    tint = if (selectedDays < 7) customGreen else Color.LightGray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Indicador de rango
        Text(
            text = "(1-7 días)",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Botón de confirmación
        Button(
            onClick = {
                if (navigationVariable) onNextDiet(selectedDays) else onNextName(selectedDays)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = customGreen,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp)
        ) {
            Text(
                text = "Confirm Selection",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
