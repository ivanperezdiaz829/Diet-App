package com.example.diet_app.ScreenActivities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diet_app.ScreenActivities.Components.Header
import com.example.diet_app.ScreenActivities.Components.NextButton
import com.example.diet_app.ScreenActivities.Components.TitleSection
import com.example.diet_app.ui.theme.DarkGreen
import com.example.diet_app.ui.theme.LightGray
import com.example.diet_app.R
import com.example.diet_app.ui.theme.DarkOverlay

@Composable
fun AgeSelectionScreen(
    onNavigateBack: () -> Unit,
    onSkip: () -> Unit,
    onNext: (Sex) -> Unit
) {
    var selectedSex by remember { mutableStateOf<Int?>(null) }

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

            /*
            SexOptions(
                selectedSex = selectedSex,
                onSexSelected = { selectedSex = it }
            )*/

            Spacer(modifier = Modifier.weight(1f))


            Column(
                modifier = Modifier
                    .width(335.dp)
                    .padding(vertical = 7.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Number Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(105.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkOverlay),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "23",
                        style = TextStyle(
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.44).sp,
                            color = DarkGreen
                        )
                    )
                }

                // Date Selection Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(LightGray)
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Date Components
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DateComponent(text = "February", isSeparator = false)
                        DateComponent(text = "/", isSeparator = true)
                        DateComponent(text = "20", isSeparator = false)
                        DateComponent(text = "/", isSeparator = true)
                        DateComponent(text = "1999", isSeparator = false)
                    }

                    // Calendar Icon
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_calendar),
                        contentDescription = "Calendar",
                        tint = DarkGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            /*
            NextButton(
                enabled = selectedSex != null,
                onClick = { selectedSex?.let { onNext(it) } }
            )
            */
        }
    }
}

@Composable
private fun DateComponent(
    text: String,
    isSeparator: Boolean
) {
    Text(
        text = text
    )
}