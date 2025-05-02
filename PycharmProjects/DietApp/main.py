from ObtainTotals import *
import time
from GenerarDieta2 import *

start_time = time.time()

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
"""
"""
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
"""

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


end_time = time.time() - start_time
print(f"\nTiempo de ejecución {end_time}")
