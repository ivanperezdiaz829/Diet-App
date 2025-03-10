from GenerarDieta import *

def obtain_restrictions():
    carbohydrates, sugar, energy, protein, salt, fat = [], [], [], [], [], []

    for i in range(2):
        if i == 0:
            carbohydrates.append(float(input("Introduce el mínimo de carbohidratos (g): ")))
        else:
            carbohydrates.append(float(input("Introduce el máximo de carbohidratos (g): ")))

    for i in range(2):
        if i == 0:
            sugar.append(float(input("Introduce el mínimo de azucar (g): ")))
        else:
            sugar.append(float(input("Introduce el máximo de azucar (g): ")))

    for i in range(2):
        if i == 0:
            energy.append(float(input("Introduce el mínimo de calorías (kcal): ")))
        else:
            energy.append(float(input("Introduce el máximo de calorías (kcal): ")))

    for i in range(2):
        if i == 0:
            protein.append(float(input("Introduce el mínimo de proteína (g): ")))
        else:
            protein.append(float(input("Introduce el máximo de proteína (g): ")))

    for i in range(2):
        if i == 0:
            salt.append(float(input("Introduce el mínimo de sal (g): ")))
        else:
            salt.append(float(input("Introduce el máximo de sal (g): ")))

    for i in range(2):
        if i == 0:
            fat.append(float(input("Introduce el mínimo de grasa (g): ")))
        else:
            fat.append(float(input("Introduce el máximo de grasa (g): ")))

    budget = float(input("Introduce el presupuesto máximo (euros): "))

    print(f'\nPresupuesto máximo (euros): {budget}')
    print(f'Intervalo de carbohidratos: {carbohydrates}')
    print(f'Intervalo de azúcar: {sugar}')
    print(f'Intervalo de calorías: {energy}')
    print(f'Intervalo de proteína: {protein}')
    print(f'Intervalo de sal: {salt}')
    print(f'Intervalo de grasa: {fat}')

    return carbohydrates, sugar, energy, protein, salt, fat, budget

# Conectar a la base de datos
conn, cursor = conectar_db()

# Obtener las restricciones de dieta
carbohydrates, sugar, energy, protein, salt, fat, budget = obtain_restrictions()

# Llamar a la función resolver_dieta pasando la conexión a la base de datos
best_solution = resolver_dieta(carbohydrates, sugar, energy, protein, salt, fat, budget)

# Mostrar la mejor solución
if best_solution:
    print(f"Desayuno: {best_solution['desayuno']}")
    print(f"Almuerzo: {', '.join(best_solution['almuerzo'])}")
    print(f"Cena: {best_solution['cena']}")
    print(f"Costo total: {best_solution['cost']}")
    print(f"Calorías total: {best_solution['energy']} kcal")
    print(f"Azucar total: {best_solution['sugar']} g")
    print(f"Carbohidratos total: {best_solution['carbohydrates']} g")
    print(f"Proteína total: {best_solution['protein']} g")
    print(f"Grasa total: {best_solution['fat']} g")
    print(f"Sal total: {best_solution['salt']} g")
else:
    print("No se encontró una solución que cumpla con las restricciones.")

# Cerrar la conexión a la base de datos
conn.close()
