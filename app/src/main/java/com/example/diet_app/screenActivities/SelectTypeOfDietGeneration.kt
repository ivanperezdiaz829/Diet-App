package com.example.diet_app.screenActivities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diet_app.R
import com.example.diet_app.screenActivities.components.BackButtonLeft
import com.example.diet_app.screenActivities.components.NextButton
import com.example.diet_app.screenActivities.components.TitleSection
import com.example.diet_app.ui.theme.DarkGreen
import com.example.diet_app.ui.theme.DarkOverlay
import com.example.diet_app.ui.theme.GrayGreen
import com.example.diet_app.ui.theme.LightGray
import com.example.diet_app.ui.theme.Typography
import com.example.diet_app.viewModel.UserViewModel

enum class DietGeneratorType {
    USER_DATA,    // Generador basado en datos del usuario
    MANUAL_INPUT  // Generador basado en datos introducidos manualmente
}

@Composable
fun DietGeneratorSelectionScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit,
    onNext: (DietGeneratorType?) -> Unit
) {
    var selectedGenerator by remember { mutableStateOf<DietGeneratorType?>(null) }

    BackButtonLeft(
        onNavigateBack = onNavigateBack
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp, bottom = 40.dp)
        ) {

            Spacer(modifier = Modifier.height(120.dp))

            TitleSection(
                "¿Como quieres",
                "generar tu dieta?",
                description = "Escoge entre una generación basada en tu perfil o una manual"
            )

            Spacer(modifier = Modifier.height(40.dp))

            GeneratorTypeOptions(
                selectedGenerator = selectedGenerator,
                onGeneratorSelected = { selectedGenerator = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                enabled = selectedGenerator != null,
                onClick = {
                    onNext(selectedGenerator)
                }
            )
        }
    }
}

@Composable
private fun GeneratorTypeOptions(
    selectedGenerator: DietGeneratorType?,
    onGeneratorSelected: (DietGeneratorType) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GeneratorTypeOption(
            type = DietGeneratorType.USER_DATA,
            text = "Basada en mis datos",
            imageId = R.drawable.profile, // Cambia por tu icono
            isSelected = selectedGenerator == DietGeneratorType.USER_DATA,
            onClick = { onGeneratorSelected(DietGeneratorType.USER_DATA) }
        )

        GeneratorTypeOption(
            type = DietGeneratorType.MANUAL_INPUT,
            text = "Especificada manualmente",
            imageId = R.drawable.manual, // Cambia por tu icono
            isSelected = selectedGenerator == DietGeneratorType.MANUAL_INPUT,
            onClick = { onGeneratorSelected(DietGeneratorType.MANUAL_INPUT) }
        )
    }
}

@Composable
private fun GeneratorTypeOption(
    type: DietGeneratorType,
    text: String,
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
                modifier = Modifier.size(80.dp),
            )
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = text,
                style = Typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) DarkGreen else GrayGreen
                )
            )
        }
    }
}