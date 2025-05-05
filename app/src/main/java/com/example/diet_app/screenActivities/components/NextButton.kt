package com.example.diet_app.screenActivities.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diet_app.ui.theme.PrimaryGreen
import com.example.diet_app.ui.theme.Typography

@Composable
fun NextButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .width(178.dp)
                .height(37.dp)
                .clickable(enabled = enabled, onClick = onClick),
            shape = RoundedCornerShape(50.dp),
            color = if (enabled) PrimaryGreen else PrimaryGreen.copy(alpha = 0.5f)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Continuar",
                    style = Typography.bodyLarge.copy(
                        color = Color.White
                    )
                )
            }
        }
    }
}