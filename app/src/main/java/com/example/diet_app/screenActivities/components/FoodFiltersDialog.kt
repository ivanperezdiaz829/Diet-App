package com.example.diet_app.screenActivities.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Estado de los filtros
data class FoodFiltersState(
    val vegan: Boolean = false,
    val vegetarian: Boolean = false,
    val celiac: Boolean = false,
    val halal: Boolean = false
) {
    fun hasActiveFilters(): Boolean = vegan || vegetarian || celiac || halal
}

@Composable
fun FoodFiltersDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    filtersState: FoodFiltersState,
    onFiltersChanged: (FoodFiltersState) -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Filtrar por tipo de comida") },
            text = {
                Column {
                    FilterChip(
                        selected = filtersState.vegan,
                        onClick = { onFiltersChanged(filtersState.copy(vegan = !filtersState.vegan)) },
                        label = { Text("Vegano") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF40B93C),
                            selectedLabelColor = Color.White,
                            containerColor = Color.LightGray,
                            labelColor = Color.Black
                        )
                    )
                    FilterChip(
                        selected = filtersState.vegetarian,
                        onClick = { onFiltersChanged(filtersState.copy(vegetarian = !filtersState.vegetarian)) },
                        label = { Text("Vegetariana") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF40B93C),
                            selectedLabelColor = Color.White,
                            containerColor = Color.LightGray,
                            labelColor = Color.Black
                        )
                    )
                    FilterChip(
                        selected = filtersState.celiac,
                        onClick = { onFiltersChanged(filtersState.copy(celiac = !filtersState.celiac)) },
                        label = { Text("Celíaca") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF40B93C),
                            selectedLabelColor = Color.White,
                            containerColor = Color.LightGray,
                            labelColor = Color.Black
                        )
                    )
                    FilterChip(
                        selected = filtersState.halal,
                        onClick = { onFiltersChanged(filtersState.copy(halal = !filtersState.halal)) },
                        label = { Text("Halal") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF40B93C),
                            selectedLabelColor = Color.White,
                            containerColor = Color.LightGray,
                            labelColor = Color.Black
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                                contentColor = Color(0xFF40B93C)
                            )
                ) {
                    Text("Aplicar")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFoodFiltersDialog() {
    MaterialTheme {
        FoodFiltersDialog(
            showDialog = true,
            onDismiss = {},
            filtersState = FoodFiltersState(
                vegan = true,
                vegetarian = false,
                celiac = true,
                halal = false
            ),
            onFiltersChanged = {}
        )
    }
}
