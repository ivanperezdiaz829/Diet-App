package com.example.diet_app.screenActivities

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diet_app.screenActivities.components.Header
import com.example.diet_app.screenActivities.components.NextButton
import com.example.diet_app.screenActivities.components.TitleSection
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

@Composable
fun HeightSelectionScreen(
    onNavigateBack: () -> Unit,
    onSkip: () -> Unit,
    onNext: () -> Unit
) {
    var height by remember { mutableStateOf("") } // Edad inicial

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

            TitleSection("What is your", "height?")

            Spacer(modifier = Modifier.height(40.dp))

            val heightRange = (0..250).toList() // Rango de alturas
            var sliderPosition by remember { mutableFloatStateOf(150f) } // Posición inicial del slider
            val lazyListState = rememberLazyListState() // Estado del carrusel
            val coroutineScope = rememberCoroutineScope() // Para animar el deslizamiento

            // Sincronizar el carrusel con el slider
            LaunchedEffect(sliderPosition) {
                coroutineScope.launch {
                    lazyListState.animateScrollToItem(sliderPosition.toInt())
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Texto que muestra la posición actual del slider
                Text(
                    text = "Current: ${sliderPosition.toInt()} cm",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Carrusel táctil con tarjetas dinámicas
                LazyRow(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { _, dragAmount ->
                                coroutineScope.launch {
                                    lazyListState.scrollBy(-dragAmount) // Desplaza el carrusel
                                }
                                sliderPosition = lazyListState.firstVisibleItemIndex.toFloat() // Sincroniza el slider con el carrusel
                            }
                        },
                    horizontalArrangement = Arrangement.spacedBy(24.dp), // Espaciado entre tarjetas
                    contentPadding = PaddingValues(horizontal = 48.dp), // Márgenes en los extremos
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(heightRange.size) { index ->
                        val scale = calculateCardScale(index, lazyListState.firstVisibleItemIndex)
                        HeightCard(
                            height = heightRange[index],
                            scale = scale
                        )
                    }
                }

                //Spacer(modifier = Modifier.height(48.dp)) // Separación entre el carrusel y el slider

                // Slider para controlar el carrusel (sin seleccionar dinámicamente)
                Slider(
                    value = sliderPosition,
                    onValueChange = { newValue ->
                        sliderPosition = newValue // Actualiza la posición del slider
                    },
                    valueRange = 0f..250f,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            NextButton(enabled = true, onClick = onNext)
        }
    }
}

@Composable
fun HeightCard(height: Int, scale: Float) {
    Box(
        modifier = Modifier
            .size(
                (110.dp * scale).coerceIn(110.dp, 140.dp), // Escalado dinámico
                (150.dp * scale).coerceIn(150.dp, 180.dp)
            )
            .background(
                color = Color.LightGray,
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$height cm",
            fontSize = (18f * scale).coerceIn(18f, 24f).sp, // Solución aplicada aquí
            color = Color.Black
        )
    }
}

fun calculateCardScale(currentIndex: Int, centerIndex: Int): Float {
    val distance = abs(currentIndex - centerIndex) // Distancia de la tarjeta al centro
    return max(0.8f, 1.2f - 0.2f * distance) // Ajusta el tamaño según la distancia
}