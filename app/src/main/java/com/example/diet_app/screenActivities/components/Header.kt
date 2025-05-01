package com.example.diet_app.screenActivities.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.diet_app.ui.theme.BackButtonBackground
import com.example.diet_app.ui.theme.LightGray
import com.example.diet_app.ui.theme.Typography

@Composable
fun Header(onNavigateBack: () -> Unit, onSkip: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(onNavigateBack)
        //SkipButton(onSkip)
    }
}

@Composable
fun BackButton(onNavigateBack: () -> Unit) {
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
}

@Composable
fun SkipButton(onSkip: () -> Unit) {
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

@Composable
fun BackButtonLeft(onNavigateBack: () -> Unit) {
    Box(Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .background(Color.Transparent)
        .padding(vertical = 32.dp)
        .padding(horizontal = 20.dp),
    ) {
        BackButton(onNavigateBack)
    }
}