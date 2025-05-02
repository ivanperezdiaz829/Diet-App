def calculate_nutritional_requirements(weight, height, age, gender, activity_level, goal):
    try:
        w = float(weight)
        h = float(height)
        a = int(age)
        if not (40 <= w <= 200):
            return {"error": "Peso debe estar entre 40-200 kg"}
        if not (140 <= h <= 220):
            return {"error": "Altura debe estar entre 140-220 cm"}
        if not (18 <= a <= 100):
            return {"error": "Edad debe estar entre 18-100 años"}
        if gender.lower() not in ['m', 'f']:
            return {"error": "Género debe ser 'm' o 'f'"}
        if activity_level not in [0, 1, 2, 3, 4]:
            return {"error": "Nivel de actividad debe ser 0-4"}
        if goal not in ['lose', 'maintain', 'gain']:
            return {"error": "Objetivo no válido"}
    except ValueError:
        return {"error": "Datos numéricos inválidos"}

    if gender == 'm':
        maintenance = (10 * w + 6.25 * h - 5 * a + 5) * [1.2, 1.375, 1.55, 1.725, 1.9][activity_level]
    else:
        maintenance = (10 * w + 6.25 * h - 5 * a - 161) * [1.2, 1.375, 1.55, 1.725, 1.9][activity_level]

    if goal == 'lose':
        target_calories = max(maintenance - 500, maintenance * 0.85)  # Más flexible
        protein_min = 1.6 * w  # Reducido de 1.8
    elif goal == 'gain':
        target_calories = min(maintenance + 500, maintenance * 1.15)  # Más flexible
        protein_min = 1.4 * w  # Reducido de 1.6
    else:
        target_calories = maintenance
        protein_min = 1.2 * w  # Reducido de 1.4

    # Macronutrientes con rangos más flexibles
    fat_max = (0.4 * target_calories) / 9  # Aumentado de 0.35
    carbs_min = (target_calories - (protein_min * 4) - (fat_max * 9)) / 4  # Más flexible

    # Límites superiores más flexibles
    sugar_max = (target_calories * 0.1) / 4  # Aumentado de 5% a 10% y mayor conversión
    sodium_max = 6000 if a <= 50 else 3000  # Aumentado

    # Rango de calorías más amplio
    calorie_range = (round(target_calories * 0.85), round(target_calories * 1.15))  # 15% en lugar de 20%

    return [
        round(carbs_min),
        round(sugar_max, 1),
        calorie_range,
        round(protein_min),
        sodium_max,
        round(fat_max)
    ]