package com.example.diet_app

// Archivo: NavigationRoutes.kt
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Details : Screen("details/{itemId}") {
        fun createRoute(itemId: Int) = "details/$itemId"
    }
    object Goal : Screen("goal")
}