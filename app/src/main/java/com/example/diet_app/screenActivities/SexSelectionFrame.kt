package com.example.diet_app.screenActivities

import Sex
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
import com.example.diet_app.R
import com.example.diet_app.screenActivities.components.Header
import com.example.diet_app.screenActivities.components.NextButton
import com.example.diet_app.screenActivities.components.TitleSection
import com.example.diet_app.ui.theme.DarkGreen
import com.example.diet_app.ui.theme.DarkOverlay
import com.example.diet_app.ui.theme.GrayGreen
import com.example.diet_app.ui.theme.LightGray
import com.example.diet_app.ui.theme.Typography

enum class Sex {
    MALE,
    FEMALE,
}

@Composable
fun SexSelectionScreen(
    onNavigateBack: () -> Unit,
    onSkip: () -> Unit,
    onNext: (Sex) -> Unit
) {
    var selectedSex by remember { mutableStateOf<Sex?>(null) }

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

            TitleSection("What is your", "sex?")

            Spacer(modifier = Modifier.height(40.dp))

            SexOptions(
                selectedSex = selectedSex,
                onSexSelected = { selectedSex = it }
            )

            Spacer(modifier = Modifier.weight(1f))

            NextButton(
                enabled = selectedSex != null,
                onClick = { selectedSex?.let { onNext(it) } }
            )
        }
    }
}

@Composable
private fun SexOptions(
    selectedSex: Sex?,
    onSexSelected: (Sex) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SexOption(
            sex = Sex.FEMALE,
            text = "Female",
            imageId = R.drawable.female,
            isSelected = selectedSex == Sex.FEMALE,
            onClick = { onSexSelected(Sex.FEMALE) }
        )

        SexOption(
            sex = Sex.MALE,
            text = "Male",
            imageId = R.drawable.male,
            isSelected = selectedSex == Sex.MALE,
            onClick = { onSexSelected(Sex.MALE) }
        )
    }
}

@Composable
private fun SexOption(
    sex: Sex,
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