package com.example.diet_app.screenActivities.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun FoodDetailDialog(
    foodViewModel: FoodViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    text = foodViewModel.getFood().name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                FoodVariants(foodViewModel)

                Spacer(modifier = Modifier.height(24.dp))

                val nutritionalData = mapOf(
                    "Proteins" to foodViewModel.getFood().protein,
                    "Fats" to foodViewModel.getFood().fats,
                    "Sugar" to foodViewModel.getFood().sugar,
                    "Salt" to foodViewModel.getFood().salt,
                    "Carbohydrates" to foodViewModel.getFood().carbohydrates,
                    "Calories" to foodViewModel.getFood().calories
                )

                NutritionalInfoGrid(data = nutritionalData)
            }
        }
    }
}