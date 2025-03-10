import sqlite3


# Leer los datos de las comidas desde la base de datos
def breakfast(conn, cursor, carbohydrates, sugar, energy, protein, salt, fat, budget):
    sql = ("SELECT * FROM comidas WHERE tipo = 'desayuno' AND "
           "carbohydrates BETWEEN ? AND ? "
           "AND sugar BETWEEN ? AND ? "
           "AND energy BETWEEN ? AND ? "
           "AND protein BETWEEN ? AND ? AND "
           "salt BETWEEN ? AND ? "
           "AND fat BETWEEN ? AND ? "
           "AND price <= ?")
    cursor.execute(sql, (carbohydrates[0], carbohydrates[1],
                         sugar[0], sugar[1],
                         energy[0], energy[1],
                         protein[0], protein[1],
                         salt[0], salt[1],
                         fat[0], fat[1],
                         budget))



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
def resolver_dieta(carbohydrates, sugar, energy, protein, salt, fat, budget):

    conn = sqlite3.connect('comidas.db')
    cursor = conn.cursor()

    breakfast(conn, cursor,
              [2 * carbohydrates[0] / 6, 2 * carbohydrates[1] / 6],
              [2 * sugar[0] / 6, 2 * sugar[1] / 6],
              [2 * energy[0] / 6, 2 * energy[1] / 6],
              [2 * protein[0] / 6, 2 * protein[1] / 6],
              [2 * salt[0] / 6, 2 * salt[1] / 6],
              [2 * fat[0] / 6, 2 * fat[1] / 6],
              budget / 3)

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
