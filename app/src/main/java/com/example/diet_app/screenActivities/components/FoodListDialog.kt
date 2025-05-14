package com.example.diet_app.screenActivities.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.diet_app.screenActivities.FoodVariants
import com.example.diet_app.screenActivities.NutritionalInfoGrid
import com.example.diet_app.ui.theme.Typography
import com.example.diet_app.viewModel.FoodViewModel

@Composable
fun FoodListDialog(
    foodViewModels: MutableList<FoodViewModel>,
    onDismiss: () -> Unit
) {

    var selectedFood by remember { mutableStateOf<FoodViewModel?>(null) }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header con botón de cerrar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onDismiss)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "×",
                            style = Typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                }
                // Contenido del diálogo (copiado de tu FoodDetailScreen original)
                Text(
                    text = "Escoge la comida que quieras añadir",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()) // Hace la columna scrollable
                        .padding(horizontal = 16.dp),

                    verticalArrangement = Arrangement.spacedBy(12.dp), // Espacio entre comidas
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    //BackButtonLeft(onNavigateBack = { navController.popBackStack() })

                    Spacer(modifier = Modifier.padding(20.dp))

                    if (foodViewModels.isEmpty()) {
                        Text("No hay comidas añadidas")
                    } else {
                        for (food in foodViewModels) {
                            FoodViewScreen(
                                onClick = { selectedFood = food }, // Pasa el ViewModel al hacer clic
                                food = food
                            )
                        }
                    }

                }
            }
        }
    }
}