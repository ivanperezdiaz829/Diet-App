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
    onNext: (Double) -> Unit
) {
    var currentWeight by remember { mutableStateOf("") }

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

            TitleSection("Tu ", "peso actual", "Usaremos estos datos para darte una mejor dieta")

            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .width(157.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LightGray),
                contentAlignment = Alignment.Center
            ) {
                TextField(
                    value = currentWeight,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            currentWeight = newValue
                        }
                    },
                    label = { Text("Tu peso actual") },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 64.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 150.sp
                    ),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                    ),
                    modifier = Modifier
                        .width(157.dp)
                        .height(180.dp)
                        .background(Color.Transparent)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                enabled = currentWeight.isNotEmpty() && currentWeight.toInt() in 30..300,
                onClick = { onNext(currentWeight.toDouble()) }
            )
        }
    }
}