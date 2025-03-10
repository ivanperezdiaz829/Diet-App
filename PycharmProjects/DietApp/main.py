import sqlite3


# Conexión con la base de datos
def conectar_db():
    conn = sqlite3.connect('comidas.db')
    cursor = conn.cursor()
    return conn, cursor


# Leer los datos de las comidas desde la base de datos
def leer_comidas(cursor):
    cursor.execute('SELECT nombre, energia, proteina, sal, grasa, costo, comida_tipo FROM comidas')
    comidas = cursor.fetchall()
    return comidas


# Obtener las restricciones del usuario
def obtener_restricciones():
    min_energy = int(input("Introduce la energía mínima (kcal): "))
    min_protein = int(input("Introduce la proteína mínima (g): "))
    max_salt = int(input("Introduce la cantidad máxima de sal (g): "))
    max_fat = int(input("Introduce la cantidad máxima de grasa (g): "))
    budget = int(input("Introduce el presupuesto máximo (moneda): "))
    return min_energy, min_protein, max_salt, max_fat, budget


# Filtrar las comidas por tipo
def filtrar_comidas_por_tipo(comidas):
    desayuno = [comida for comida in comidas if comida[6] == 'desayuno']
    mains = [comida for comida in comidas if comida[6] == 'main']
    sides = [comida for comida in comidas if comida[6] == 'side']
    desserts = [comida for comida in comidas if comida[6] == 'dessert']
    return desayuno, mains, sides, desserts


# Calcular el costo y las características de una combinación de comidas
def get_total_cost_and_features(comidas_seleccionadas):
    total_cost = sum([comida[5] for comida in comidas_seleccionadas])
    total_energy = sum([comida[1] for comida in comidas_seleccionadas])
    total_fat = sum([comida[4] for comida in comidas_seleccionadas])
    total_salt = sum([comida[3] for comida in comidas_seleccionadas])
    total_protein = sum([comida[2] for comida in comidas_seleccionadas])
    return total_cost, total_energy, total_fat, total_salt, total_protein


# Resolver las combinaciones posibles de desayuno, almuerzo y cena
def resolver_dieta(comidas, min_energy, min_protein, max_salt, max_fat, budget):
    desayuno, mains, sides, desserts = filtrar_comidas_por_tipo(comidas)

    best_solution = None
    min_cost = float('inf')  # Empezamos con un costo muy alto

    # Recorremos todas las combinaciones posibles de desayuno, almuerzo y cena
    for d in desayuno:
        for m in mains:
            for s in sides:
                for des in desserts:
                    # Aseguramos que no haya repetición de comidas entre desayuno, almuerzo y cena
                    if len(set([d[0], m[0], s[0], des[0]])) != 4:
                        continue  # Si hay repetición, continuamos con la siguiente combinación

                    # Elegir la cena: puede ser un 'main' o un 'side'
                    for c in mains + sides:
                        if c[0] in [d[0], m[0], s[0], des[0]]:
                            continue  # Aseguramos que no se repita comida

                        # Calcular el costo y las características de la combinación
                        comidas_seleccionadas = [d, m, s, des, c]
                        total_cost, total_energy, total_fat, total_salt, total_protein = get_total_cost_and_features(
                            comidas_seleccionadas)

                        # Verificamos las restricciones
                        if (total_cost <= budget and
                                total_energy >= min_energy and
                                total_protein >= min_protein and
                                total_fat <= max_fat and
                                total_salt <= max_salt):

                            # Si cumple con las restricciones, verificamos si es la mejor solución
                            if total_cost < min_cost:
                                min_cost = total_cost
                                best_solution = {
                                    'desayuno': d[0],
                                    'almuerzo': [m[0], s[0], des[0]],
                                    'cena': c[0],
                                    'cost': total_cost,
                                    'energy': total_energy,
                                    'protein': total_protein,
                                    'fat': total_fat,
                                    'salt': total_salt
                                }

    return best_solution


# Función principal
def main():
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


if __name__ == '__main__':
    main()

