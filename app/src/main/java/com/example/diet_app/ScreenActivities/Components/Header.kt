package com.example.diet_app.ScreenActivities.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diet_app.ui.theme.BackButtonBackground
import com.example.diet_app.ui.theme.LightGray
import com.example.diet_app.ui.theme.Typography

@Composable
public fun Header(onNavigateBack: () -> Unit, onSkip: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(BackButtonBackground)
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

        Surface(
            modifier = Modifier
                .width(100.dp)
                .height(60.dp)
                .clickable(onClick = onSkip),
            shape = RoundedCornerShape(25.dp),
            color = LightGray
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Skip",
                    style = Typography.labelMedium
                )
            }
        }
    }
}