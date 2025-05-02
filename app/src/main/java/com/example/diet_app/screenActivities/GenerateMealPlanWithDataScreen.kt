package com.example.diet_app.screenActivities

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diet_app.screenActivities.components.BackButtonLeft
import com.example.diet_app.viewModel.UserViewModel

@Composable
fun GenerateMealPlanScreen(
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit,
    onNext: (Boolean) -> Unit
) {

    BackButtonLeft(onNavigateBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Generate a new",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                "meal plan",
                fontSize = 28.sp,
                color = Color(0xFF40B93C),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "This is the data we will use to generate your new diet.",
            fontSize = 18.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = "Do you want to proceed or update it?",
            fontSize = 18.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserDataItem(label = "Goal:", value = userViewModel.getUser().goal.toString())
            UserDataItem(label = "Sex:", value = userViewModel.getUser().sex.toString())
            UserDataItem(label = "Age:", value = "${userViewModel.getUser().age} years")
            UserDataItem(label = "Height:", value = "${userViewModel.getUser().height} cm")
            UserDataItem(label = "Current weight:", value = "${userViewModel.getUser().currentWeight} kg")
        }

        Spacer(modifier = Modifier.height(48.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(
                text = "Proceed",
                onClick = {
                    onNext(true)
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            CustomButton(
                text = "Update",
                onClick = {
                    onNext(false)
                },
                modifier = Modifier.fillMaxWidth(0.8f),
                isSecondary = true
            )
        }
    }
}

@Composable
private fun UserDataItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            color = Color.DarkGray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSecondary: Boolean = false
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSecondary) Color.White else Color(0xFF40B93C),
            contentColor = if (isSecondary) Color(0xFF40B93C) else Color.White
        ),
        modifier = modifier.height(50.dp),
        border = if (isSecondary) ButtonDefaults.outlinedButtonBorder else null
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}