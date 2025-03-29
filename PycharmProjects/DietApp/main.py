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
#carbohydrates, sugar, energy, protein, salt, fat, budget = obtain_restrictions()
carbohydrates = [75, 250]
energy = [1500, 2200]
sugar = [0, 50]
protein = [75, 100]
salt = [0, 5000]
fat = [55, 80]
budget = 2000.0

# Llamar a la función resolver_dieta pasando la conexión a la base de datos
solution = diet_generator(carbohydrates, sugar, energy, protein, salt, fat, budget, 1, set(), set(), set())

# Mostrar la mejor solución
if solution:
    print("\n--- SOLUCION ---")
    print(f'Desayuno: {solution[0]}')
    print(f'Almuerzo: {solution[1]}')
    print(f'Cena: {solution[2]}')

    end_time = time.time()
    print(f'\nEl tiempo de ejecución del programa es: {time.process_time()}')

else:
    print("No se encontró una solución que cumpla con las restricciones.")

