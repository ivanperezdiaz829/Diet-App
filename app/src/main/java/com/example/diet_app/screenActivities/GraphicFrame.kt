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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.diet_app.loadBarplotImage
import com.example.diet_app.screenActivities.components.BackButton
import com.example.diet_app.screenActivities.components.ListIconScreen
import com.example.diet_app.screenActivities.components.NutritionInfoComponent

@Composable
fun CustomScreen(
    dataFromApi: List<Pair<String, Float>>,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Fila para BackButton y ListIconScreen
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(onNavigateBack = onNavigateBack)

                // Este irá totalmente a la derecha
                ListIconScreen()
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gráfica
            Text(
                text = "Gráfica de ejemplo",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            BarplotImage(
                dietJson = """{"data": ${dataFromApi.map { mapOf("name" to it.first, "value" to it.second) }}}""",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de CustomBox
            dataFromApi.forEach { (title, value) ->
                NutritionInfoComponent(
                    title = title,
                    content = "Valor: $value",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}


@Composable
fun BarplotImage(dietJson: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            ImageView(ctx).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                loadBarplotImage(ctx, dietJson)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CustomScreenPreview() {
    val mockData = listOf(
        "Energía Solar" to 78.5f,
        "Eólica" to 65.2f,
        "Hidroeléctrica" to 50.0f
    )

    MaterialTheme {
        CustomScreen(
            dataFromApi = mockData,
            onNavigateBack = { /* Acción simulada */ }
        )
    }
}