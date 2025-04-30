package com.example.diet_app.screenActivities.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diet_app.R

@Composable
fun ListIconScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.TopEnd
    ) {
        Icon(
            painter = painterResource(id = R.drawable.optionsbars),
            contentDescription = "List Icon",
            modifier = Modifier.size(width = 52.dp, height = 51.dp),
            tint = Color.Black
        )
    }
}
@Preview
@Composable
fun PreviewListIconScreen() {
    ListIconScreen()
}
