package com.example.diet_app.screenActivities

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.diet_app.screenActivities.components.BackButton
import com.example.diet_app.screenActivities.components.ListIconScreen
import com.example.diet_app.screenActivities.components.NutritionInfoComponent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diet_app.fetchNutritionalData
import com.example.diet_app.model.GlobalData
import com.example.diet_app.screenActivities.components.ToolBox

@Composable
fun GraphicFrame(
    navController: NavController,
    dietId: String
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val nutritionData = remember { mutableStateOf<Map<String, Float>?>(null) }

    LaunchedEffect(GlobalData.dietJson) {
        fetchNutritionalData(context, GlobalData.dietJson) { data ->
            nutritionData.value = data
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onNavigateBack = { navController.popBackStack() })
            ListIconScreen()
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Gráfica de valores nutricionales",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        nutritionData.value?.let { data ->
            NutritionBarChart(data = data)

            Spacer(modifier = Modifier.height(24.dp))

            data.forEach { (title, value) ->
                NutritionInfoComponent(
                    title = title,
                    content = "Valor: $value",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        } ?: Text("Cargando datos...", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun NutritionBarChart(data: Map<String, Float>) {
    SimpleBarChart(data = data)
}

@Composable
fun SimpleBarChart(data: Map<String, Float>, modifier: Modifier = Modifier) {
    val maxValue = data.values.maxOrNull() ?: 1f
    val barWidth = 40.dp
    val spacing = 16.dp

    val labels = data.keys.toList()
    val values = data.values.toList()

    val barColor = Color(0xFF4CAF50)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val canvasHeight = size.height
            val canvasWidth = size.width
            val barWidthPx = barWidth.toPx()
            val spacingPx = spacing.toPx()

            val totalBars = values.size
            val totalChartWidth = totalBars * barWidthPx + (totalBars - 1) * spacingPx
            val startX = (canvasWidth - totalChartWidth) / 2f

            values.forEachIndexed { index, value ->
                val left = startX + index * (barWidthPx + spacingPx)
                val barHeight = (value / maxValue) * canvasHeight
                val top = canvasHeight - barHeight

                drawRect(
                    color = barColor,
                    topLeft = Offset(left, top),
                    size = Size(barWidthPx, barHeight)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            labels.forEach {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.width(barWidth)
                )
            }
        }
    }
}