from flask import Flask, request, jsonify, send_file
from Graphs import *
from ObtainTotals import *
import time
import ast
from io import BytesIO
import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd

start_time = time.time()

"""
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

    carbohydrates = [values[0], values[1]]
    sugar = values[2]
    energy = [values[3], values[4]]
    protein = [values[5], values[6]]
    salt = values[7]
    fat = [values[8], values[9]]
    price = values[10]
    person_type = 1
    person_preferences = 1
    total_days = 3

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
    data = request.get_json()
    if not data or 'dieta' not in data:
        return jsonify({'error': 'Falta el parámetro "dieta"'}), 400

    dieta = data['dieta']
    res = nutritional_values_day(dieta)

    df = pd.DataFrame({
        'Valores Nutricionales': ["Carbohidratos", "Proteina", "Grasas", "Azúcares", "Sales", "Precio"],
        'Cantidades': [res[1], res[2], res[3], res[4], res[5], res[6]],
    })

    plt.figure(figsize=(6, 4))
    colores = sns.color_palette("blend:#b2e2b2,#40B93C", n_colors=len(df))
    ax = sns.barplot(data=df, x='Valores Nutricionales', y='Cantidades', hue="Valores Nutricionales",
                     palette=colores, width=0.6, legend=False)
    ax.set_xlabel("")
    ax.set_ylabel(" Cantidades (gr.)")
    for patch in ax.patches:
        patch.set_edgecolor('black')
        patch.set_linewidth(1)

    plt.title("Datos dieta de " + str(res[0]) + " calorías")
    plt.xticks(fontsize=9)
    plt.tight_layout()

    img = BytesIO()
    plt.savefig(img, format='png')
    plt.close()
    img.seek(0)
    return send_file(img, mimetype='image/png')

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
""""
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
    1, 1, 1
)
print("*****************************************")
print(solution)

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
    2, 1, 4
)
print("*****************************************")
print(solution)

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
    4, 1, 4
)
print("*****************************************")
print(solution)
"""
end_time = time.time() - start_time
print(f"\nTiempo de ejecución {end_time}")
