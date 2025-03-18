import sqlite3
import random
import os


def dynamic_values(cursor):
    sql = ("SELECT SUM(sugar) FROM plates WHERE type == 1 ")
    cursor.execute(sql)
    sugar_breakfast = cursor.fetchall()
    # sugar_lunch = sugar_main * 2 + sugar_side + sugar_water
    print(f'Suma de azucares desayuno: {sugar_breakfast[0][0]}')
    return 1


def obtain_breakfast(cursor, selected, carbohydrates, sugar, energy, protein, salt, fat, budget):
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


def obtain_lunch(cursor, selected_combinations, carbohydrates, sugar, energy, protein, salt, fat, budget):
    print(energy, carbohydrates, protein, fat, sugar, salt, budget)

    # Consulta para obtener platos de tipo 2, 3, 4 y 5
    sql = ("SELECT * FROM plates WHERE type == 2 AND "
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
                         salt[0], salt[1], fat[0], fat[1], budget))
    type2_foods = cursor.fetchall()

    sql = ("SELECT * FROM plates WHERE type == 5 AND "
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
                         salt[0], salt[1], fat[0], fat[1], budget))
    type5_foods = cursor.fetchall()

    sql = ("SELECT * FROM plates WHERE type IN (3, 4, 5) AND "
           "carbohydrates <= ? "
           "AND sugar <= ? "
           "AND calories <= ? "
           "AND protein <= ? "
           "AND sodium <= ? "
           "AND fats <= ? "
           "AND price <= ?")
    cursor.execute(sql, (carbohydrates[1], sugar[1], energy[1], protein[1], salt[1], fat[1], budget))
    type_3_4_5_foods = cursor.fetchall()

    print(f'Comidas tipo2: {type2_foods}')
    type3_foods = [food for food in type_3_4_5_foods if food[8] == 3]
    print(f'Comidas tipo3: {type3_foods}')
    type4_foods = [food for food in type_3_4_5_foods if food[8] == 4]
    print(f'Comidas tipo4: {type4_foods}')
    type5_foods = [food for food in type_3_4_5_foods if food[8] == 5]
    print(f'Comidas tipo5: {type5_foods}')

    valid_combinations = []

    if type2_foods and type3_foods and type4_foods and type5_foods:
        for food2 in type2_foods:
            for food3 in type3_foods:
                for food4 in type4_foods:
                    for food5 in type5_foods:
                        combination = (food2[1], food3[1], food4[1], food5[1])

                        # Evitar repetir la misma combinación
                        if combination in selected_combinations:
                            continue

                        combined_carbs = (food2[2] + food3[2] + food4[2] + food5[2]) * 3
                        combined_sugar = (food2[5] + food3[5] + food4[5] + food5[5]) * 3
                        combined_energy = (food2[1] + food3[1] + food4[1] + food5[1]) * 3
                        combined_protein = (food2[3] + food3[3] + food4[3] + food5[3]) * 3
                        combined_salt = (food2[6] + food3[6] + food4[6] + food5[6]) * 3
                        combined_fat = (food2[4] + food3[4] + food4[4] + food5[4]) * 3
                        combined_price = (food2[7] + food3[7] + food4[7] + food5[7]) * 3

                        # Comprobar que la combinación de los cuatro platos cumple los límites
                        if (carbohydrates[0] <= combined_carbs <= carbohydrates[1] and
                                sugar[0] <= combined_sugar <= sugar[1] and
                                energy[0] <= combined_energy <= energy[1] and
                                protein[0] <= combined_protein <= protein[1] and
                                salt[0] <= combined_salt <= salt[1] and
                                fat[0] <= combined_fat <= fat[1] and
                                combined_price <= budget):
                            # Agregar combinación válida a la lista
                            valid_combinations.append((food2[0], food3[0], food4[0], food5[0]))

    print(valid_combinations)
    if valid_combinations:
        selected_combination = random.choice(valid_combinations)
        selected_combinations.add(selected_combination)
        return selected_combination

    return None, None, None, None


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
    selected_combinations = set()
    solution = []

    mults = [(2.5, 5, 2.5), (2, 6, 2), (3, 4, 3)]

    dynamic_values(cursor)

    lunch = obtain_lunch(cursor, selected_combinations,
                                 [mults[person_type - 1][1] * carbohydrates[0] / 10, mults[person_type - 1][1] * carbohydrates[1] / 10],
                                 [mults[person_type - 1][1] * sugar[0] / 10, mults[person_type - 1][1] * sugar[1] / 10],
                                 [mults[person_type - 1][1] * energy[0] / 10, mults[person_type - 1][1] * energy[1] / 10],
                                 [mults[person_type - 1][1] * protein[0] / 10, mults[person_type - 1][1] * protein[1] / 10],
                                 [mults[person_type - 1][1] * salt[0] / 10, mults[person_type - 1][1] * salt[1] / 10],
                                 [mults[person_type - 1][1] * fat[0] / 10, mults[person_type - 1][1] * fat[1] / 10],
                                 budget / 3)

    breakfast = obtain_breakfast(cursor, selected,
                                 [mults[person_type - 1][0] * carbohydrates[0] / 10, mults[person_type - 1][0] * carbohydrates[1] / 10],
                                 [mults[person_type - 1][0] * sugar[0] / 10, mults[person_type - 1][0] * sugar[1] / 10],
                                 [mults[person_type - 1][0] * energy[0] / 10, mults[person_type - 1][0] * energy[1] / 10],
                                 [mults[person_type - 1][0] * protein[0] / 10, mults[person_type - 1][0] * protein[1] / 10],
                                 [mults[person_type - 1][0] * salt[0] / 10, mults[person_type - 1][0] * salt[1] / 10],
                                 [mults[person_type - 1][0] * fat[0] / 10, mults[person_type - 1][0] * fat[1] / 10],
                                 budget / 3)

    if breakfast is None or lunch is None:
        return None

    solution.append(breakfast[0])
    solution.append(lunch)

    return solution
