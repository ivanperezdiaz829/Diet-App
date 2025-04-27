package com.example.diet_app.model

// Archivo: NavigationRoutes.kt
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object Register : Screen("register")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Details : Screen("details/{itemId}") {
        fun createRoute(itemId: Int) = "details/$itemId"
    }
    object Goal : Screen("goal")
    object Sex : Screen("sex")
    object Age : Screen("age")
    object Height : Screen("height")
}