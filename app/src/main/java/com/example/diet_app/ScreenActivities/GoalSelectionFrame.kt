package com.example.app.ui.ScreenActivities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.diet_app.ui.theme.Typography
import com.example.diet_app.ui.theme.BackButtonBackground
import com.example.diet_app.ScreenActivities.Components.*
import com.example.diet_app.ui.theme.DarkGreen
import com.example.diet_app.ui.theme.DarkOverlay
import com.example.diet_app.ui.theme.GrayGreen
import com.example.diet_app.ui.theme.LightGray
import com.example.diet_app.ui.theme.PrimaryGreen

enum class Goal {
    LOSE_WEIGHT,
    GAIN_WEIGHT,
    STAY_HEALTHY
}

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
            .background(Color.White)
    ) {
        StatusBar()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp, bottom = 40.dp)
        ) {
            Header(onNavigateBack, onSkip)

            Spacer(modifier = Modifier.height(120.dp))

            TitleSection()

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
private fun StatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(17.dp, 11.dp)
                    .background(Color.Black)
            )
            Box(
                modifier = Modifier
                    .size(15.dp, 11.dp)
                    .background(Color.Black)
            )
            Icon(
                painter = painterResource(id = android.R.drawable.ic_lock_idle_charging),
                contentDescription = "Battery",
                tint = Color.Black,
                modifier = Modifier.size(24.dp, 11.dp)
            )
        }
    }
}

@Composable
private fun TitleSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = buildAnnotatedString {
                append("What is your ")
                withStyle(SpanStyle(color = PrimaryGreen)) {
                    append("goal?")
                }
            },
            style = Typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 32.sp
        )

        Text(
            text = "We will use this data to give you a better diet type for you",
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = com.example.diet_app.ui.theme.GrayGreen
        )
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
            goal = Goal.LOSE_WEIGHT,
            text = "Lose weight",
            imageUrl = "https://cdn.builder.io/api/v1/image/assets/TEMP/22f57b9e5c369652cc2b4e79fc4c97e853dbdb68",
            isSelected = selectedGoal == Goal.LOSE_WEIGHT,
            onClick = { onGoalSelected(Goal.LOSE_WEIGHT) }
        )

        GoalOption(
            goal = Goal.GAIN_WEIGHT,
            text = "Gain weight",
            imageUrl = "https://cdn.builder.io/api/v1/image/assets/TEMP/a9ad78ccc95ba11fe58f424876d7434ad938c4ea",
            isSelected = selectedGoal == Goal.GAIN_WEIGHT,
            onClick = { onGoalSelected(Goal.GAIN_WEIGHT) }
        )

        GoalOption(
            goal = Goal.STAY_HEALTHY,
            text = "Stay healthy",
            imageUrl = "https://cdn.builder.io/api/v1/image/assets/TEMP/fc1c1eaac78d9b96f7a7c76de2e90baff2fb9fc2",
            isSelected = selectedGoal == Goal.STAY_HEALTHY,
            onClick = { onGoalSelected(Goal.STAY_HEALTHY) }
        )
    }
}

@Composable
private fun GoalOption(
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