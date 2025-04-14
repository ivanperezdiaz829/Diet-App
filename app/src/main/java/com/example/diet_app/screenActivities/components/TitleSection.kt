package com.example.diet_app.screenActivities.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diet_app.ui.theme.PrimaryGreen
import com.example.diet_app.ui.theme.Typography

@Composable
fun TitleSection(text: String, textGreen: String, description: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = buildAnnotatedString {
                append("$text ")
                withStyle(SpanStyle(color = PrimaryGreen)) {
                    append(textGreen)
                }
            },
            style = Typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = description,
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            color = com.example.diet_app.ui.theme.GrayGreen
        )
    }
}