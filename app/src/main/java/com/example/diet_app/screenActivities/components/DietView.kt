package com.example.diet_app.screenActivities.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diet_app.ui.theme.DarkGreen
import com.example.diet_app.ui.theme.DarkOverlay
import com.example.diet_app.ui.theme.Typography
import com.example.diet_app.viewModel.DietViewModel

@Composable
fun DietViewScreen(
    onClick: () -> Unit,
    diet: DietViewModel,
    image: Int,
){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = DarkOverlay
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = diet.getDiet().name,
                style = Typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                )
            )
            Text(
                text = diet.getDiet().duration.toString() + "d",
                style = Typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DarkGreen
                )
            )

            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                modifier = Modifier.size(70.dp),
            )
        }
    }
}