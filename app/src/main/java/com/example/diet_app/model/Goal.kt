package com.example.diet_app.model

enum class Goal {
    LOSE_WEIGHT,
    GAIN_WEIGHT,
    STAY_HEALTHY
}

fun getGoalInt(goal: Goal): Int {
    return when (goal) {
        Goal.LOSE_WEIGHT -> 0
        Goal.GAIN_WEIGHT -> 1
        Goal.STAY_HEALTHY -> 2
    }
}