package com.example.diet_app.model

// Archivo: NavigationRoutes.kt
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object Register : Screen("register")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Goal : Screen("goal")
    object Sex : Screen("sex")
    object Age : Screen("age")
    object Height : Screen("height")
    object CurrentWeight: Screen("currentWeight")
    object TargetWeight : Screen("targetWeight")
    object Welcome : Screen("welcome")
    object Meals : Screen("meals")
    object MealPlan : Screen("mealPlan")
    object Password: Screen("password")
    object PhsysicalData: Screen("physicalData")
    object FoodList: Screen("foodList")
    object AddFood: Screen("addFood")
    object NewFoodType: Screen("newFoodType")
    object NewFoodSummary: Screen("newFoodSummary")

    object FoodDetail: Screen("foodDetail/{foodId}") {
        // Función auxiliar para crear la ruta con el ID
        fun createRoute(foodId: Int) = "foodDetail/$foodId"
    }

    object GenerateMealPlanWithData: Screen("generateMealPlanWithData")
    object TypeOfDietSelection: Screen("typeOfDietSelection")
    object DietDurationSelection: Screen("dietDurationSelection")

    object DietInterface : Screen("dietInterface/{dietId}") {
        fun createRoute(dietId: String) = "dietInterface/$dietId"
    }

    object GraphicFrame : Screen("graphicFrame/{dietId}") {
        fun createRoute(dietId: String) = "graphicFrame/$dietId"
    }

    object SelectTypeOfDietGeneration: Screen("selectTypeOfDietGeneration")
    object GenerateMealPlanWithInputs: Screen("generateMealPlanWithInputs")
    object Calendar: Screen("calendar")
    object ChosenDiet: Screen("chosenDiet")
    object DietNameSelection: Screen("dietNameSelection")
}