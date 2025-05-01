package com.example.diet_app.screenActivities

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.diet_app.screenActivities.components.OptionGrid
import com.example.diet_app.screenActivities.components.ToolBox
import com.example.diet_app.ui.theme.PrimaryGreen
import com.example.diet_app.ui.theme.Typography
import com.example.diet_app.viewModel.UserViewModel

@Composable
fun HomePageFrame(navController: NavController, userViewModel: UserViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 40.dp),
        contentAlignment = Alignment.Center // Centra el contenido
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                buildAnnotatedString {
                    append("What do you want to ")
                    withStyle(style = SpanStyle(color = PrimaryGreen)) {
                        append("do")
                    }
                    append("?")
                },
                style = Typography.headlineLarge,
                modifier = Modifier.padding(bottom = 40.dp),
                textAlign = TextAlign.Center
            )

            OptionGrid(navController) // Llamamos a OptionGrid sin navegación por ahora
        }
    }
    ToolBox(navController)
}

