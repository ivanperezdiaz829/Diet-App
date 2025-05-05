package com.example.diet_app.model

enum class Goal {
    PERDER_PESO,
    MANTENERSE,
    GANAR_PESO
}

fun getGoalInt(goal: Goal): Int {
    return when (goal) {
        Goal.PERDER_PESO -> 0
        Goal.MANTENERSE -> 1
        Goal.GANAR_PESO -> 2
    }
}