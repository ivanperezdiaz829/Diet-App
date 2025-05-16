package com.example.diet_app.screenActivities.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    )
                    FilterChip(
                        selected = filtersState.vegetarian,
                        onClick = { onFiltersChanged(filtersState.copy(vegetarian = !filtersState.vegetarian)) },
                        label = { Text("Vegetariana") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    )
                    FilterChip(
                        selected = filtersState.celiac,
                        onClick = { onFiltersChanged(filtersState.copy(celiac = !filtersState.celiac)) },
                        label = { Text("Celíaca") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),

                    )
                    FilterChip(
                        selected = filtersState.halal,
                        onClick = { onFiltersChanged(filtersState.copy(halal = !filtersState.halal)) },
                        label = { Text("Halal") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Aplicar")
                }
            }
        )
    }
}