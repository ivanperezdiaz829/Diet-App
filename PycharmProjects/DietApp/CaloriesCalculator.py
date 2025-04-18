def calculate_basal_metabolic_rate(weight, height, age, gender):
    try:
        w = float(weight)
        h = float(height)
        a = float(age)
    except ValueError:
        return "Peso, altura o edad no válidos"

    if gender.lower() == "m":
        bmr = 88.362 + (13.397 * w) + (4.799 * h) - (5.677 * a)
        return f"Gasto Energético Basal: {bmr:.2f} kcal/día"
    elif gender.lower() == "f":
        bmr = 447.593 + (9.247 * w) + (3.098 * h) - (4.330 * a)
        return f"Gasto Energético Basal: {bmr:.2f} kcal/día"
    else:
        return "Género no válido"


def calculate_maintenance_calories(weight, height, age, gender, physical_activity_level):
    try:
        w = float(weight)
        h = float(height)
        a = float(age)
        pal = float(physical_activity_level)
    except ValueError:
        return "Peso, altura, edad o nivel de actividad física no válidos"

    physical_activity_coefficients = [1.2, 1.375, 1.55, 1.725, 1.9]

    if gender.lower() == "m":
        mc = physical_activity_coefficients[int(pal)] * (66 + (13.7 * w) + (5 * h) - (6.8 * a))
        return f"Calorías de mantenimiento: {mc:.2f} kcal/día"
    elif gender.lower() == "f":
        mc = physical_activity_coefficients[int(pal)] * (665 + (9.6 * w) + (1.8 * h) - (4.7 * a))
        return f"Calorías de mantenimiento: {mc:.2f} kcal/día"
    else:
        return "Género no válido"

# Prueba de la calculadora de Gasto Energético Basal (BMR)
print(calculate_basal_metabolic_rate(70, 175, 25, "M"))  # Hombre, 70kg, 175cm, 25 años
print(calculate_basal_metabolic_rate(60, 160, 30, "F"))  # Mujer, 60kg, 160cm, 30 años

# Prueba de la calculadora de Calorías de Mantenimiento
print(calculate_maintenance_calories(70, 175, 25, "M", 0))  # Hombre, 70kg, 175cm, 25 años, nivel de actividad 0
print(calculate_maintenance_calories(60, 160, 30, "F", 1))  # Mujer, 60kg, 160cm, 30 años, nivel de actividad 1
