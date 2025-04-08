from GenerarDieta import *
import time


start_time = time.time()


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


# Obtener las restricciones de dieta
# carbohydrates, sugar, energy, protein, salt, fat, budget = obtain_restrictions()
carbohydrates = [200, 827.13]
energy = [1200, 2800]
sugar = 187.23
protein = [0, 253.27]
salt = 5000.0    # 5g
fat = [0, 87]
budget = 50

# Llamar a la función resolver_dieta pasando la conexión a la base de datos

# print("Dieta Estándar")
solution = diet_generator(carbohydrates, sugar, energy, protein, salt, fat, budget, 1, 1, set(), set(), set())
print_solution(solution)
end_time = time.time() - start_time
print(f"\nTiempo de ejecución {end_time}")

"""
print("Dieta Vegetariana")
solution_vegetarian = diet_generator(carbohydrates, sugar, energy, protein, salt, fat, budget, 3, set(), set(), set())
print_solution(solution_vegetarian)

print("Dieta Veganana")
solution_vegan = diet_generator(carbohydrates, sugar, energy, protein, salt, fat, budget, 2, set(), set(), set())
print_solution(solution_vegan)

print("Dieta Glucémica")
solution_glucemic = diet_generator(carbohydrates, sugar, energy, protein, salt, fat, budget, 4, set(), set(), set())
print_solution(solution_glucemic)
"""