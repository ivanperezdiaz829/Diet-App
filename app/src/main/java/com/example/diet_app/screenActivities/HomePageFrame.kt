package com.example.diet_app.screenActivities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.diet_app.R
import com.example.diet_app.screenActivities.components.Header
import com.example.diet_app.screenActivities.components.NextButton
import com.example.diet_app.screenActivities.components.TitleSection
import com.example.diet_app.ui.theme.*

enum class HomePageOption {
    PROFILE, DIET, HISTORY, SETTINGS
}

@Composable
fun HomePageFrame(
    onNavigateBack: () -> Unit,
    onSkip: () -> Unit,
    onNext: (HomePageOption) -> Unit
) {
    var selectedOption by remember { mutableStateOf<HomePageOption?>(null) }

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

            Spacer(modifier = Modifier.height(40.dp))

            TitleSection("Choose your", "destination")

            Spacer(modifier = Modifier.height(40.dp))

            OptionGrid(
                selected = selectedOption,
                onSelected = { selectedOption = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                enabled = selectedOption != null,
                onClick = { selectedOption?.let { onNext(it) } }
            )
        }
    }
}

@Composable
private fun OptionGrid(
    selected: HomePageOption?,
    onSelected: (HomePageOption) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            HomePageOptionCard(
                option = HomePageOption.PROFILE,
                label = "Perfil",
                imageId = R.drawable.img,
                isSelected = selected == HomePageOption.PROFILE,
                onClick = { onSelected(HomePageOption.PROFILE) }
            )

            HomePageOptionCard(
                option = HomePageOption.DIET,
                label = "Dieta",
                imageId = R.drawable.img,
                isSelected = selected == HomePageOption.DIET,
                onClick = { onSelected(HomePageOption.DIET) }
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            HomePageOptionCard(
                option = HomePageOption.HISTORY,
                label = "Historial",
                imageId = R.drawable.img,
                isSelected = selected == HomePageOption.HISTORY,
                onClick = { onSelected(HomePageOption.HISTORY) }
            )

            HomePageOptionCard(
                option = HomePageOption.SETTINGS,
                label = "Configuración",
                imageId = R.drawable.img,
                isSelected = selected == HomePageOption.SETTINGS,
                onClick = { onSelected(HomePageOption.SETTINGS) }
            )
        }
    }
}

@Composable
private fun HomePageOptionCard(
    option: HomePageOption,
    label: String,
    imageId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(157.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .background(if (isSelected) DarkOverlay else LightGray),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = label,
                style = Typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) DarkGreen else GrayGreen
                )
            )
        }
    }
}
