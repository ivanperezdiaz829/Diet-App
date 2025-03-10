import sqlite3
import random
import os


def obtain_breakfast(cursor, selected, carbohydrates, sugar, energy, protein, salt, fat, budget):
    # Cambia 'plates.db' por 'plates' que es el nombre de la tabla
    sql = ("SELECT * FROM plates WHERE type == 1 AND "
           "carbohydrates BETWEEN ? AND ? "
           "AND sugar BETWEEN ? AND ? "
           "AND calories BETWEEN ? AND ? "
           "AND protein BETWEEN ? AND ? "
           "AND sodium BETWEEN ? AND ? "
           "AND fats BETWEEN ? AND ? "
           "AND price <= ?")
    cursor.execute(sql, (carbohydrates[0], carbohydrates[1],
                         sugar[0], sugar[1],
                         energy[0], energy[1],
                         protein[0], protein[1],
                         salt[0], salt[1],
                         fat[0], fat[1],
                         budget))
    valid = cursor.fetchall()

    if valid:
        valid_foods = []
        for food in valid:
            if food not in selected:
                valid_foods.append(food)

        if valid_foods:
            selected = random.choice(valid_foods)  # Asegúrate de elegir de 'valid_foods'
            return selected
        else:
            return None
    else:
        return None


# Calcular el costo y las características de una combinación de comidas
def get_total_cost_and_features(comidas_seleccionadas):
    total_cost = sum([comida[5] for comida in comidas_seleccionadas])
    total_energy = sum([comida[1] for comida in comidas_seleccionadas])
    total_fat = sum([comida[4] for comida in comidas_seleccionadas])
    total_salt = sum([comida[3] for comida in comidas_seleccionadas])
    total_protein = sum([comida[2] for comida in comidas_seleccionadas])
    return total_cost, total_energy, total_fat, total_salt, total_protein


def resolver_dieta(carbohydrates, sugar, energy, protein, salt, fat, budget):
    db_path = os.path.join('../../FoodDbManagement', 'plates.db')

    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    selected = []
    solution = []

    breakfast = obtain_breakfast(cursor, selected,
                                 [2 * carbohydrates[0] / 6, 2 * carbohydrates[1] / 6],
                                 [2 * sugar[0] / 6, 2 * sugar[1] / 6],
                                 [2 * energy[0] / 6, 2 * energy[1] / 6],
                                 [2 * protein[0] / 6, 2 * protein[1] / 6],
                                 [2 * salt[0] / 6, 2 * salt[1] / 6],
                                 [2 * fat[0] / 6, 2 * fat[1] / 6],
                                 budget / 3)

    if breakfast is None:
        return None

    selected.append(breakfast)

    solution.append(breakfast[0])

    return solution
