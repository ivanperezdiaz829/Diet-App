package com.example.diet_app.screenActivities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diet_app.ui.theme.BackButtonBackground
import com.example.diet_app.ui.theme.Typography
import com.example.diet_app.viewModel.FoodViewModel

// Pantalla de configuración del usuario con botón de logout funcional
@Composable
fun FoodDetailScreen(
    foodViewModel: FoodViewModel,
    onNavigateBack: () -> Unit)
{

    Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
                .clickable(onClick = onNavigateBack),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "<",
                style = Typography.titleLarge.copy(
                    color = DarkGray,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(vertical = 20.dp, horizontal = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(30.dp)) // Ajusta el valor según lo necesites

        Text(
            text = foodViewModel.getFood().name,
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
        )

        Spacer(modifier = Modifier.height(12.dp)) // Ajusta el valor según lo necesites

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            FoodVariants(foodViewModel)

            Spacer(modifier = Modifier.height(30.dp))

            val nutritionalData = mapOf(
                "Proteinas" to foodViewModel.getFood().protein,
                "Grasas" to foodViewModel.getFood().fats,
                "Azúcares" to foodViewModel.getFood().sugar,
                "Sal" to foodViewModel.getFood().salt,
                "Carbohidratos" to foodViewModel.getFood().carbohydrates,
                "Calorias" to foodViewModel.getFood().calories
            )

            NutritionalInfoGrid(data = nutritionalData)

        }
    }
}

@Composable
fun FoodVariants(foodViewModel: FoodViewModel){

    Text(
        text = "Variantes alimenticias",
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        color = DarkGray,
        modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (variant in foodViewModel.getFood().foodVariants) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth() // Asegura que el ancho se ajuste al contenido
                    .wrapContentHeight() // Asegura que la altura se ajuste al contenido
                    .clickable(onClick = { /* Acción al hacer clic */ }),
                shape = RoundedCornerShape(25.dp),
                color = BackButtonBackground
            ) {
                Box(
                    modifier = Modifier.padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = variant.toString(),
                        style = Typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun NutritionalInfoGrid(data: Map<String, Double>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Título
        Text(
            text = "Cualidades",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkGray,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp)
        )

        // Contenido (excluyendo "Calories" para manejarlo por separado)
        data.filterKeys { it != "Calories" }.forEach { (key, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = key,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$value g",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Línea divisoria
            Divider(
                color = Color.Gray.copy(alpha = 0.5f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Manejo especial para "Calories"
        data["Calories"]?.let { calories ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Calories",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$calories kcal",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            // Línea divisoria
            Divider(
                color = Color.Gray.copy(alpha = 0.5f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
