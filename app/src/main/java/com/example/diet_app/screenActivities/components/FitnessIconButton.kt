package com.example.diet_app.screenActivities.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.example.diet_app.R

@Composable
fun FitnessIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(92.dp, 100.dp)
            .background(
                color = Color(0xFF05D09F),
                shape = RoundedCornerShape(12.dp)
            )
            .semantics {
                contentDescription = "Fitness Icon Button"
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_fitness), // Asegúrate de tener esta imagen en res/drawable
            contentDescription = "Fitness Icon",
            modifier = Modifier.size(60.dp)
        )
    }
}