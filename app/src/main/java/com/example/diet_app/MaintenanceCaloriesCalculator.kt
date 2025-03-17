enum class Sex {
    MALE, FEMALE
}

enum class PhysicalActivityLevel(val multiplier: Double) {
    SEDENTARY(1.2),
    LIGHTLY_ACTIVE(1.375),
    MODERATELY_ACTIVE(1.55),
    VERY_ACTIVE(1.725),
    EXTRA_ACTIVE(1.9)
}

object MaintenanceCalorieCalculator {
    fun getMaintenanceCalories(
            sex: Sex,
            age: Int,
            height: Int,
            weight: Float,
            physicalActivityLevel: PhysicalActivityLevel
    ): Int {
        val maintenanceCalories = when (sex) {
            Sex.MALE -> 66 + (13.7 * weight) + (5 * height) - (6.8 * age)
            Sex.FEMALE -> 655 + (9.6 * weight) + (1.8 * height) - (4.7 * age)
        }
        return (physicalActivityLevel.multiplier * maintenanceCalories).toInt()
    }
}

fun main() {
    val scanner = Scanner(System.`in`)

    println("Introduce tu sexo (MALE/FEMALE):")
    val sex = Sex.valueOf(scanner.next().uppercase())

    println("Introduce tu edad:")
    val age = scanner.nextInt()

    println("Introduce tu altura en cm:")
    val height = scanner.nextInt()

    println("Introduce tu peso en kg:")
    val weight = scanner.nextFloat()

    println("Elige tu nivel de actividad física:")
    PhysicalActivityLevel.values().forEachIndexed { index, level ->
        println("$index - ${level.name}")
    }
    val activityLevel = PhysicalActivityLevel.values()[scanner.nextInt()]

    val maintenanceCalories = MaintenanceCalorieCalculator.getMaintenanceCalories(sex, age, height, weight, activityLevel)
    println("Tus calorías de mantenimiento son: $maintenanceCalories kcal")
}