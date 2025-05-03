from flask import Flask, request, jsonify, send_file
from Graphs import *
from ObtainTotals import *
import time
import ast
from io import BytesIO
import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
from GenerarDieta2 import *

#start_time = time.time()

app = Flask(__name__)

@app.route('/calculate', methods=['POST'])
def calculate_diet():
    data = request.get_json()

    if "values" not in data:
        return jsonify({"error": "No values provided"}), 400

    try:
        values_str = data["values"]
        print(f"Valor recibido como string: {values_str}")
        values_list = ast.literal_eval(values_str)

        if not isinstance(values_list, list):
            raise ValueError("El valor recibido no es una lista válida.")

        values = []
        for i in values_list:
            print(f"Valor recibido: {i}, Tipo: {type(i)}")
            values.append(float(i))

    except (ValueError, TypeError) as e:
        print(f"Error al convertir: {e}")
        return jsonify({"error": "All values must be numbers"}), 400

    carbohydrates = values[0]
    sugar = values[1]
    energy = [values[2], values[3]]
    protein = values[4]
    salt = values[5]
    fat = values[6]
    price = values[8]
    person_type = int(values[9])
    person_preferences = 1
    total_days = int(values[7])

    try:
        dieta = total_diet_generator(carbohydrates, sugar, energy, protein, salt, fat, price, person_type, person_preferences, total_days)

        if not dieta or len(dieta) < 3:
            return jsonify({"error": "No valid diet found"}), 404

        result = []

        for i in range(total_days):
            response = {
                "breakfast": dieta[i][0][0].name + ", " + dieta[i][0][1].name,
                "lunch": dieta[i][1][0].name + ", " + dieta[i][1][1].name + ", " + dieta[i][1][2].name,
                "dinner": dieta[i][2][0].name + ", " + dieta[i][2][1].name
            }
            result.append(response)


        print(f"Solución enviada: {result}")
        return jsonify(result)

    except Exception as e:
        print(f"Error al generar la dieta: {e}")
        return jsonify({"error": f"Internal server error: {e}"}), 500

@app.route("/barplot", methods=["POST"])
def barplot():
    data = request.get_json(force=True)
    print("DEBUG - JSON recibido:", data)
    if not data or 'dieta' not in data:
        return jsonify({'error': 'Falta el parámetro "dieta"'}), 400

    dieta = data['dieta']

    # Convertimos cada plato en la dieta en un objeto Plate
    diet_total = []
    for food_group in dieta:
        plates_group = []
        for plate_data in food_group:
            # Creamos un objeto Plate para cada plato
            plate = Plate(plate_data, 1)  # Pasamos directamente el diccionario plate_data
            plates_group.append(plate)
        diet_total.append(plates_group)

    # Pasamos la dieta convertida a la función nutritional_values_total
    res = nutritional_values_total(diet_total)

    # Devolvemos los resultados como lista
    valores = {
        "calorias": res[0],
        "carbohidratos": res[1],
        "proteinas": res[2],
        "grasas": res[3],
        "azucares": res[4],
        "sales": res[5],
        "precio": res[6]
    }

    return jsonify(valores)

@app.route("/basal", methods=["POST"])
def calculate_basal_metabolic_rate():
    data = request.get_json()
    weight = data.get("weight")
    height = data.get("height")
    age = data.get("age")
    gender = data.get("gender")

    try:
        w = float(weight)
        h = float(height)
        a = float(age)
    except (ValueError, TypeError):
        return jsonify({"result": -1})

    if gender.lower() == "m":
        result = 88.362 + (13.397 * w) + (4.799 * h) - (5.677 * a)
    elif gender.lower() == "f":
        result = 447.593 + (9.247 * w) + (3.098 * h) - (4.330 * a)
    else:
        result = 0

    return jsonify({"result": result})

@app.route("/maintenance", methods=["POST"])
def calculate_maintenance_calories():
    data = request.get_json()
    weight = data.get("weight")
    height = data.get("height")
    age = data.get("age")
    gender = data.get("gender")
    physical_activity_level = data.get("physical_activity_level")

    try:
        w = float(weight)
        h = float(height)
        a = float(age)
        pal = int(physical_activity_level)
    except (ValueError, TypeError):
        return jsonify({"result": -1})

    physical_activity_coefficients = [1.2, 1.375, 1.55, 1.725, 1.9]

    if 0 <= pal < len(physical_activity_coefficients):
        if gender.lower() == "m":
            result = physical_activity_coefficients[pal] * (66 + (13.7 * w) + (5 * h) - (6.8 * a))
        elif gender.lower() == "f":
            result = physical_activity_coefficients[pal] * (665 + (9.6 * w) + (1.8 * h) - (4.7 * a))
        else:
            result = 0
    else:
        result = 0

    return jsonify({"result": result})

if __name__ == '__main__':
    app.run(debug=True, host="0.0.0.0", port=8000)

"""
def obtain_restrictions():
    carbohydrates, sugar, energy, protein, salt, fat = [], [], [], [], [], []

    # Promedio -> [281.25, 406.25]
    for i in range(2):
        if i == 0:
            carbohydrates.append(float(input("Introduce el mínimo de carbohidratos (g): ")))
        else:
            carbohydrates.append(float(input("Introduce el máximo de carbohidratos (g): ")))

    # Promedio -> [31.5, 62.5]
    for i in range(2):
        if i == 0:
            sugar.append(float(input("Introduce el mínimo de azucar (g): ")))
        else:
            sugar.append(float(input("Introduce el máximo de azucar (g): ")))

    # Promedio -> [1800, 3000]
    for i in range(2):
        if i == 0:
            energy.append(float(input("Introduce el mínimo de calorías (kcal): ")))
        else:
            energy.append(float(input("Introduce el máximo de calorías (kcal): ")))

    # Promedio -> [62.5, 218.75]
    for i in range(2):
        if i == 0:
            protein.append(float(input("Introduce el mínimo de proteína (g): ")))
        else:
            protein.append(float(input("Introduce el máximo de proteína (g): ")))

    # Promedio -> [0, 5]
    for i in range(2):
        if i == 0:
            salt.append(float(input("Introduce el mínimo de sal (g): ")))
        else:
            salt.append(float(input("Introduce el máximo de sal (g): ")))

    # Promedio -> [55.56, 97.22]
    for i in range(2):
        if i == 0:
            fat.append(float(input("Introduce el mínimo de grasa (g): ")))
        else:
            fat.append(float(input("Introduce el máximo de grasa (g): ")))

    budget = float(input("Introduce el presupuesto máximo (euros): "))

    return carbohydrates, sugar, energy, protein, salt, fat, budget


carbohydrates_min = 250
energy_min = 1800
energy_max = 2200
sugar_max = 50
protein_min = 50
salt_max = 5000.0
fat_max = 90
budget = 50
solution = total_diet_generator(
    carbohydrates_min,
    sugar_max,
    [energy_min, energy_max],
    protein_min,
    salt_max,
    fat_max,
    budget,
    1, 1, 2
)
print("*")
for day in solution:
    print(day)


carbohydrates_min = 200
energy_min = 1600
energy_max = 1800
sugar_max = 40
protein_min = 40
salt_max = 4000.0
fat_max = 80
solution = total_diet_generator(
    carbohydrates_min,
    sugar_max,
    [energy_min, energy_max],
    protein_min,
    salt_max,
    fat_max,
    budget,
    2, 1, 3
)

print("**")
for day in solution:
    print(day)
carbohydrates_min = 250
energy_min = 1800
energy_max = 2200
sugar_max = 50
protein_min = 50
salt_max = 5000.0
fat_max = 90
budget = 30
solution = total_diet_generator(
    carbohydrates_min,
    sugar_max,
    [energy_min, energy_max],
    protein_min,
    salt_max,
    fat_max,
    budget,
    3, 1, 4
)
print("***")
for day in solution:
    print(day)
carbohydrates_min = 250
energy_min = 1800
energy_max = 2200
sugar_max = 50
protein_min = 50
salt_max = 5000.0
fat_max = 90
budget = 50
solution = total_diet_generator(
    carbohydrates_min,
    sugar_max,
    [energy_min, energy_max],
    protein_min,
    salt_max,
    fat_max,
    budget,
    4, 1, 5
)
print("****")
for day in solution:
    print(day)
carbohydrates_min = 250
energy_min = 2600
energy_max = 2900
sugar_max = 80
protein_min = 100
salt_max = 5000.0
fat_max = 90
budget = 50
solution = total_diet_generator(
    carbohydrates_min,
    sugar_max,
    [energy_min, energy_max],
    protein_min,
    salt_max,
    fat_max,
    budget,
    5, 1, 2
)

print("*****")
for day in solution:
    print(day)
mch_requirements = calculate_nutritional_requirements(60, 183, 21, "m", 2, "gain")
erc_requirements = calculate_nutritional_requirements(70, 179, 24, "m", 3, "lose")
ggn_requirements = calculate_nutritional_requirements(55, 170, 20, "m", 1, "maintain")


print(mch_requirements)
print(erc_requirements)
print(ggn_requirements)

solution = total_diet_generator(
    mch_requirements[0],
    mch_requirements[1],
    mch_requirements[2],
    mch_requirements[3],
    mch_requirements[4],
    mch_requirements[5],
    30,
    4,
    1,
    2
)

print("******")
for day in solution:
    print(day)
solution = total_diet_generator(
    erc_requirements[0],
    erc_requirements[1],
    erc_requirements[2],
    erc_requirements[3],
    erc_requirements[4],
    erc_requirements[5],
    30,
    3,
    1,
    2
)

print("*******")
for day in solution:
    print(day)
solution = total_diet_generator(
    ggn_requirements[0],
    ggn_requirements[1],
    ggn_requirements[2],
    ggn_requirements[3],
    ggn_requirements[4],
    ggn_requirements[5],
    30,
    2,
    1,
    4
)

print("********")
for day in solution:
    print(day)


end_time = time.time() - start_time
print(f"\nTiempo de ejecución {end_time}")

"""
