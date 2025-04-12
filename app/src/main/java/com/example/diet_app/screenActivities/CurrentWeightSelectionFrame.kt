package com.example.diet_app.screenActivities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diet_app.screenActivities.components.Header
import com.example.diet_app.screenActivities.components.NextButton
import com.example.diet_app.screenActivities.components.TitleSection
import com.example.diet_app.ui.theme.DarkOverlay
import com.example.diet_app.ui.theme.LightGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWeightSelectionScreen(
    onNavigateBack: () -> Unit,
    onSkip: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") } // Edad inicial

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(onNavigateBack, onSkip)

            Spacer(modifier = Modifier.height(120.dp))

            TitleSection("Your ", "current weight")

            Spacer(modifier = Modifier.height(40.dp))

            var currentWeight1 by remember { mutableStateOf("") } // Valor del primer TextField
            var currentWeight2 by remember { mutableStateOf("") } // Valor del segundo TextField
            var lastFocusedField by remember { mutableStateOf("") } // Último campo en el que escribiste
            Row(
                modifier = Modifier
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(157.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (lastFocusedField === "Field1") DarkOverlay else LightGray),
                    contentAlignment = Alignment.Center
                ){
                    // Primer TextField
                    TextField(
                        value = currentWeight1,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) { // Verifica que solo sean números
                                currentWeight1 = newValue // Actualiza el valor escrito
                                lastFocusedField = "Field1" // Guarda cuál fue el último TextField usado
                            }
                        },
                        label = { Text("Your current weight min") },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 64.sp, // Tamaño de texto más grande
                            textAlign = TextAlign.Center,
                            lineHeight = 150.sp // Ajusta la altura de línea según sea necesario
                        ),
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent, // Fondo transparente del TextField
                            focusedIndicatorColor = Color.Transparent, // Elimina la línea del indicador cuando está enfocado
                            unfocusedIndicatorColor = Color.Transparent, // Elimina la línea del indicador cuando no está enfocado
                            cursorColor = Color.Black // Color del cursor
                        ),
                        modifier = Modifier
                            .width(157.dp)
                            .height(180.dp)
                            .background(Color.Transparent)

                    )
                }
                Box(
                    modifier = Modifier
                        .width(157.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (lastFocusedField === "Field2") DarkOverlay else LightGray),
                    contentAlignment = Alignment.Center
                ){

                    TextField(
                        value = currentWeight2,
                        onValueChange = {  newValue ->
                            if (newValue.all { it.isDigit() }) { // Verifica que solo sean números
                                currentWeight2 = newValue // Actualiza el valor escrito
                                lastFocusedField = "Field2" // Guarda cuál fue el último TextField usado
                            }
                        },
                        label = { Text("Your current weight max") },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 64.sp, // Tamaño de texto más grande
                            textAlign = TextAlign.Center,
                            lineHeight = 150.sp // Ajusta la altura de línea según sea necesario
                        ),
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent, // Fondo transparente del TextField
                            focusedIndicatorColor = Color.Transparent, // Elimina la línea del indicador cuando está enfocado
                            unfocusedIndicatorColor = Color.Transparent, // Elimina la línea del indicador cuando no está enfocado
                            cursorColor = Color.Black // Color del cursor
                        ),
                        modifier = Modifier
                            .width(157.dp)
                            .height(180.dp)
                            .background(Color.Transparent)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                enabled = currentWeight1.isNotEmpty() && currentWeight2.isNotEmpty(),
                onClick = { }
            )
        }
    }
}
