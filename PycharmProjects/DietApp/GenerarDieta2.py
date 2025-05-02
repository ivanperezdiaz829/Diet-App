from CaloriesCalculator import calculate_maintenance_calories

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

    # Cálculo de calorías
    if gender == 'm':
        maintenance = (10 * w + 6.25 * h - 5 * a + 5) * [1.2, 1.375, 1.55, 1.725, 1.9][activity_level]
    else:
        maintenance = (10 * w + 6.25 * h - 5 * a - 161) * [1.2, 1.375, 1.55, 1.725, 1.9][activity_level]

    # Ajuste por objetivo
    if goal == 'lose':
        target = max(maintenance - 500, maintenance * 0.8)
        protein_range = (1.6 * w, 2.2 * w)
    elif goal == 'gain':
        target = min(maintenance + 500, maintenance * 1.2)
        protein_range = (1.4 * w, 2.0 * w)
    else:
        target = maintenance
        protein_range = (1.2 * w, 1.8 * w)

    # Macronutrientes
    fat_lower = (0.20 * target) / 9
    fat_upper = (0.35 * target) / 9
    carbs_lower = (target - (protein_range[1] * 4) - (fat_upper * 9)) / 4
    carbs_upper = (target - (protein_range[0] * 4) - (fat_lower * 9)) / 4

    # Límites superiores
    sugar_limit = (target * 0.05) / 4
    sodium_limit = 2300 if a <= 50 else 1500

    # Devolver lista en el orden requerido
    return [
        (round(carbs_lower), round(carbs_upper)),         # carbohydrates (min, max)
        round(sugar_limit, 1),                            # sugar
        (round(target * 0.8), round(target * 1.2)),       # energy (min, max)
        (round(protein_range[0], 1), round(protein_range[1], 1)),  # protein (min, max)
        sodium_limit,                                     # salt (sodium)
        (round(fat_lower, 1), round(fat_upper, 1))        # fat (min, max)
    ]


carbohydrates, sugar, energy, protein, salt, fat = calculate_nutritional_requirements(
    weight=40,
    height=150,
    age=18,
    gender='f',
    activity_level=1,
    goal='gain'
)