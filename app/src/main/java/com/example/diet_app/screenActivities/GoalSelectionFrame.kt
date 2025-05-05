package com.example.diet_app.screenActivities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.diet_app.model.Goal
import com.example.diet_app.screenActivities.components.Header
import com.example.diet_app.screenActivities.components.NextButton
import com.example.diet_app.screenActivities.components.TitleSection
import com.example.diet_app.ui.theme.Typography
import com.example.diet_app.ui.theme.DarkGreen
import com.example.diet_app.ui.theme.DarkOverlay
import com.example.diet_app.ui.theme.GrayGreen
import com.example.diet_app.ui.theme.LightGray

@Composable
fun GoalSelectionScreen(
    onNavigateBack: () -> Unit,
    onSkip: () -> Unit,
    onNext: (Goal) -> Unit
) {
    var selectedGoal by remember { mutableStateOf<Goal?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp, bottom = 40.dp)
        ) {
            Header(onNavigateBack, onSkip)

            Spacer(modifier = Modifier.height(120.dp))

            TitleSection("¿Cual es tu", "objetivo?", "Usaremos estos datos para darte una mejor dieta")

            Spacer(modifier = Modifier.height(40.dp))

            GoalOptions(
                selectedGoal = selectedGoal,
                onGoalSelected = { selectedGoal = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                enabled = selectedGoal != null,
                onClick = { selectedGoal?.let { onNext(it) } }
            )
        }
    }
}

@Composable
private fun GoalOptions(
    selectedGoal: Goal?,
    onGoalSelected: (Goal) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        GoalOption(
            goal = Goal.PERDER_PESO,
            text = "Perder peso",
            imageUrl = "https://cdn.builder.io/api/v1/image/assets/TEMP/22f57b9e5c369652cc2b4e79fc4c97e853dbdb68",
            isSelected = selectedGoal == Goal.PERDER_PESO,
            onClick = { onGoalSelected(Goal.PERDER_PESO) }
        )

        GoalOption(
            goal = Goal.GANAR_PESO,
            text = "Ganar peso",
            imageUrl = "https://cdn.builder.io/api/v1/image/assets/TEMP/a9ad78ccc95ba11fe58f424876d7434ad938c4ea",
            isSelected = selectedGoal == Goal.GANAR_PESO,
            onClick = { onGoalSelected(Goal.GANAR_PESO) }
        )

        GoalOption(
            goal = Goal.MANTENERSE,
            text = "Mantenerse saludable",
            imageUrl = "https://cdn.builder.io/api/v1/image/assets/TEMP/fc1c1eaac78d9b96f7a7c76de2e90baff2fb9fc2",
            isSelected = selectedGoal == Goal.MANTENERSE,
            onClick = { onGoalSelected(Goal.MANTENERSE) }
        )
    }
}

@Composable
fun GoalOption(
    goal: Goal,
    text: String,
    imageUrl: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) DarkOverlay else LightGray
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = Typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) DarkGreen else GrayGreen
                )
            )

            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier.size(70.dp),
                contentScale = ContentScale.Crop // Cambia según el efecto deseado
            )
        }
    }
}