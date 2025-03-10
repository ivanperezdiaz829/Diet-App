import sqlite3
from GenerarDieta import *

# Conectar a la base de datos
conn, cursor = conectar_db()

# Leer las comidas de la base de datos
comidas = leer_comidas(cursor)

# Obtener restricciones del usuario
min_energy, min_protein, max_salt, max_fat, budget = obtener_restricciones()

# Resolver las combinaciones
best_solution = resolver_dieta(comidas, min_energy, min_protein, max_salt, max_fat, budget)

# Mostrar la mejor solución
if best_solution:
    print(f"Desayuno: {best_solution['desayuno']}")
    print(f"Almuerzo: {', '.join(best_solution['almuerzo'])}")
    print(f"Cena: {best_solution['cena']}")
    print(f"Costo total: {best_solution['cost']}")
    print(f"Energía total: {best_solution['energy']} kcal")
    print(f"Proteína total: {best_solution['protein']} g")
    print(f"Grasa total: {best_solution['fat']} g")
    print(f"Sal total: {best_solution['salt']} g")
else:
    print("No se encontró una solución que cumpla con las restricciones.")

# Cerrar la conexión a la base de datos
conn.close()
