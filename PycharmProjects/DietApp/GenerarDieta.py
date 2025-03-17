import sqlite3
import random
import os


def obtain_breakfast(cursor, selected, carbohydrates, sugar, energy, protein, salt, fat, budget):
    # Cambia 'plates.db' por 'plates' que es el nombre de la tabla
    print(energy, carbohydrates, protein, fat, sugar, salt, budget)
    sql = ("SELECT * FROM plates WHERE type == 1 AND "
           "carbohydrates * 3 BETWEEN ? AND ? "
           "AND sugar * 3 BETWEEN ? AND ? "
           "AND calories * 3 BETWEEN ? AND ? "
           "AND protein * 3 BETWEEN ? AND ? "
           "AND sodium * 3 BETWEEN ? AND ? "
           "AND fats * 3 BETWEEN ? AND ? "
           "AND price * 3 <= ?")
    cursor.execute(sql, (carbohydrates[0], carbohydrates[1],
                         sugar[0], sugar[1],
                         energy[0], energy[1],
                         protein[0], protein[1],
                         salt[0], salt[1],
                         fat[0], fat[1],
                         budget))
    valid = cursor.fetchall()
    print(valid)

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


def resolver_dieta(carbohydrates, sugar, energy, protein, salt, fat, budget, person_type):
    db_path = os.path.join('../../FoodDbManagement', 'plates.db')

    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    selected = []
    solution = []

    mults = [(2.5, 5, 2.5), (2, 6, 2), (3, 4, 3)]

    breakfast = obtain_breakfast(cursor, selected,
                                 [mults[person_type - 1][0] * carbohydrates[0] / 10, mults[person_type - 1][0] * carbohydrates[1] / 10],
                                 [mults[person_type - 1][0] * sugar[0] / 10, mults[person_type - 1][0] * sugar[1] / 10],
                                 [mults[person_type - 1][0] * energy[0] / 10, mults[person_type - 1][0] * energy[1] / 10],
                                 [mults[person_type - 1][0] * protein[0] / 10, mults[person_type - 1][0] * protein[1] / 10],
                                 [mults[person_type - 1][0] * salt[0] / 10, mults[person_type - 1][0] * salt[1] / 10],
                                 [mults[person_type - 1][0] * fat[0] / 10, mults[person_type - 1][0] * fat[1] / 10],
                                 budget / 3)

    if breakfast is None:
        return None

    selected.append(breakfast)
    solution.append(breakfast[0])

    return solution
