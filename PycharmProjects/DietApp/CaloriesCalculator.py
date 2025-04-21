def calculate_basal_metabolic_rate(weight, height, age, gender):
    try:
        w = float(weight)
        h = float(height)
        a = float(age)
    except ValueError:
        return -1
    if gender.lower() == "m":
        return 88.362 + (13.397 * w) + (4.799 * h) - (5.677 * a)
    elif gender.lower() == "f":
        return 447.593 + (9.247 * w) + (3.098 * h) - (4.330 * a)
    else:
        return 0

def calculate_maintenance_calories(weight, height, age, gender, physical_activity_level):
    try:
        w = float(weight)
        h = float(height)
        a = float(age)
        pal = int(physical_activity_level)
    except ValueError:
        return -1
    physical_activity_coefficients = [1.2, 1.375, 1.55, 1.725, 1.9]
    if gender.lower() == "m":
        return physical_activity_coefficients[pal] * (66 + (13.7 * w) + (5 * h) - (6.8 * a))
    elif gender.lower() == "f":
        return physical_activity_coefficients[pal] * (665 + (9.6 * w) + (1.8 * h) - (4.7 * a))
    else:
        return 0

# Prueba de la calculadora de Gasto Energético Basal (BMR)
print(calculate_basal_metabolic_rate(70, 175, 25, "M"))  # Hombre, 70kg, 175cm, 25 años
print(calculate_basal_metabolic_rate(60, 160, 30, "F"))  # Mujer, 60kg, 160cm, 30 años

# Prueba de la calculadora de Calorías de Mantenimiento
print(calculate_maintenance_calories(70, 175, 25, "M", 0))  # Hombre, 70kg, 175cm, 25 años, nivel de actividad 0
print(calculate_maintenance_calories(60, 160, 30, "F", 1))  # Mujer, 60kg, 160cm, 30 años, nivel de actividad 1
