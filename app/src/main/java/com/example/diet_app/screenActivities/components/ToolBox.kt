package com.example.diet_app.screenActivities.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diet_app.R
import com.example.diet_app.model.Screen


@Composable
fun ToolBox(
    navController: NavController,
) {

    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val currentScreen = when (currentRoute) {
        Screen.Home.route -> "Home"
        Screen.Meals.route -> "Meals"
        Screen.FoodList.route -> "Activities"
        Screen.Settings.route -> "Settings"
        else -> ""
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
)   {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(bottom = 10.dp)
                .background(Color.Transparent)
                .drawBehind {
                    val offsetY = -10.dp.toPx()
                    drawLine(
                        color = Color.Black,
                        start = Offset(0f, offsetY),
                        end = Offset(size.width, offsetY),
                        strokeWidth = 2.dp.toPx()
                    )
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
                ToolBoxItem(
                    painterResource(id = R.drawable.home_symbol),
                    screen = "Home",
                    currentScreen,
                    onClick = {navController.navigateSingleInStack(Screen.Home.route)}
                    // onScreenSelected,
                )
                ToolBoxItem(
                    painterResource(id = R.drawable.meals_symbol),
                    screen = "Meals",
                    currentScreen,
                    onClick = {navController.navigateSingleInStack(Screen.Meals.route)}
                    // onScreenSelected,
                )
                ToolBoxItem(
                    painterResource(id = R.drawable.book_symbol),
                    screen = "Activities",
                    currentScreen,
                    onClick = {navController.navigateSingleInStack(Screen.FoodList.route)}
                    // onScreenSelected,
                )
                ToolBoxItem(
                    painterResource(id = R.drawable.user_symbol),
                    screen = "Settings",
                    currentScreen,
                    onClick = {navController.navigateSingleInStack(Screen.Settings.route)}
                    // onScreenSelected,
                )
            }
        }

}

@Composable
fun ToolBoxItem(
    painter: Painter,
    screen: String,
    currentScreen: String,
    onClick: () -> Unit,
) {
    val isSelected = screen == currentScreen
    val color = if (isSelected) Color.Black else Color.Gray
    //val colorFilter = if (isSelected) ColorFilter.tint(Color.Black) else ColorFilter.tint(Color.Gray)

    Icon(
        painter = painter,
        contentDescription = screen,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .size(40.dp)
            .clickable(onClick = onClick),
        tint = color
    )
}

/**
 * Navega a una pantalla con gestión estricta del stack:
 * 1. Elimina todas las pantallas anteriores (si las hay)
 * 2. No permite navegar a la pantalla actual
 * 3. Mantiene siempre exactamente 1 instancia en el stack
 */
fun NavController.navigateSingleInStack(route: String) {
    val currentRoute = currentBackStackEntry?.destination?.route

    when {
        // Caso 1: No hay pantalla actual (navegación inicial)
        currentRoute == null -> {
            navigate(route)
        }

        // Caso 2: La ruta destino es diferente a la actual
        currentRoute != route -> {
            navigate(route) {
                // Elimina todas las pantallas excepto la actual antes de navegar
                popUpTo(currentRoute) { inclusive = true }
                // Evita múltiples instancias
                launchSingleTop = true
                // Restaura el estado si ya existe
                restoreState = true
            }
        }

        // Caso 3: Ya está en la pantalla destino (no hacer nada)
        else -> return
    }
}

