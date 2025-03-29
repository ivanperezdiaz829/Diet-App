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
    carbohydrates = [80, 300]

    # Promedio -> [31.5, 62.5]
    for i in range(2):
        if i == 0:
            sugar.append(float(input("Introduce el mínimo de azucar (g): ")))
        else:
            sugar.append(float(input("Introduce el máximo de azucar (g): ")))
    sugar = [80, 300]

    # Promedio -> [1800, 3000]
    for i in range(2):
        if i == 0:
            energy.append(float(input("Introduce el mínimo de calorías (kcal): ")))
        else:
            energy.append(float(input("Introduce el máximo de calorías (kcal): ")))
    energy = [1500, 3000]

    # Promedio -> [62.5, 218.75]
    for i in range(2):
        if i == 0:
            protein.append(float(input("Introduce el mínimo de proteína (g): ")))
        else:
            protein.append(float(input("Introduce el máximo de proteína (g): ")))
    protein = [50, 218.75]

    # Promedio -> [0, 5]
    for i in range(2):
        if i == 0:
            salt.append(float(input("Introduce el mínimo de sal (g): ")))
        else:
            salt.append(float(input("Introduce el máximo de sal (g): ")))
    salt = [0, 10000]

    # Promedio -> [55.56, 97.22]
    for i in range(2):
        if i == 0:
            fat.append(float(input("Introduce el mínimo de grasa (g): ")))
        else:
            fat.append(float(input("Introduce el máximo de grasa (g): ")))
    fat = [0, 100]

    budget = float(input("Introduce el presupuesto máximo (euros): "))
    budget = 2000.0

    """
    print(f'\nPresupuesto máximo (euros): {budget}')
    print(f'Intervalo de carbohidratos: {carbohydrates}')
    print(f'Intervalo de azúcar: {sugar}')
    print(f'Intervalo de calorías: {energy}')
    print(f'Intervalo de proteína: {protein}')
    print(f'Intervalo de sal: {salt}')
    print(f'Intervalo de grasa: {fat}')
    """
    return carbohydrates, sugar, energy, protein, salt, fat, budget


# Obtener las restricciones de dieta
carbohydrates, sugar, energy, protein, salt, fat, budget = obtain_restrictions()

# Llamar a la función resolver_dieta pasando la conexión a la base de datos
solution = diet_generator(carbohydrates, sugar, energy, protein, salt, fat, budget, 1, set(), set(), set())

# Mostrar la mejor solución
if solution:
    print("\n--- SOLUCION ---")
    print(f'\nDesayuno: {solution[0]}')
    print(f'Almuerzo: {solution[1]}')
    print(f'Cena: {solution[2]}')

    end_time = time.time()
    print(f'\nEl tiempo de ejecución del programa es: {time.process_time()}')
    """
    print(f"Almuerzo: {', '.join(best_solution['almuerzo'])}")
    print(f"Cena: {best_solution['cena']}")
    print(f"Costo total: {best_solution['cost']}")
    print(f"Calorías total: {best_solution['energy']} kcal")
    print(f"Azucar total: {best_solution['sugar']} g")
    print(f"Carbohidratos total: {best_solution['carbohydrates']} g")
    print(f"Proteína total: {best_solution['protein']} g")
    print(f"Grasa total: {best_solution['fat']} g")
    print(f"Sal total: {best_solution['salt']} g")
    """
else:
    print("No se encontró una solución que cumpla con las restricciones.")

