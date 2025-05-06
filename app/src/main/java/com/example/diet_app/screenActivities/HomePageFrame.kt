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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.diet_app.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp

@Composable
fun HomePageFrame(navController: NavController, userViewModel: UserViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Texto principal
            Text(
                buildAnnotatedString {
                    append("¿Quieres ver tus dietas ")
                    withStyle(style = SpanStyle(color = PrimaryGreen)) {
                        append("programadas")
                    }
                    append("?")
                },
                style = Typography.headlineLarge,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )

            // Espaciador entre el texto y el grid
            Spacer(modifier = Modifier.height(32.dp))

            // Grid de opciones
            OptionGrid(navController)

            // Espaciador entre el grid y las filas de info
            Spacer(modifier = Modifier.height(32.dp))

            // Sección explicativa con íconos
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                InfoRow(R.drawable.home_symbol, "Aquí puedes ver tu programación.")
                InfoRow(R.drawable.meals_symbol, "Crea y revisa tus planes nutricionales.")
                InfoRow(R.drawable.book_symbol, "Añade tus comidas favoritas si no las tenemos.")
                InfoRow(R.drawable.user_symbol, "Consulta tu perfil y modifica lo que necesites.")
            }
        }
    }

    ToolBox(navController)
}


@Composable
fun InfoRow(iconRes: Int, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp) // Padding interno por fila
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = description,
            style = Typography.bodyMedium,
            fontSize = 14.sp
        )
    }
}

