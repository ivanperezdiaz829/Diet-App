import sqlite3
import random
import os


def filter_plates(plates, person_type):
    if person_type == 1:  # Persona normal
        return plates
    elif person_type == 2:  # Vegetariano
        return [plate for plate in plates if plate[9] == 1]  # Asumiendo que columna 8 es vegetarian
    elif person_type == 3:  # Vegano
        return [plate for plate in plates if plate[10] == 1]  # Asumiendo que columna 9 es vegan
    elif person_type == 4:  # Diabético
        return [plate for plate in plates if plate[11] == 1]  # Asumiendo que columna 10 es diabetic
    return plates


def sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, meal_type, sentence_type, sub_sentence):

    if sentence_type == 1:
        sql = ("SELECT * FROM plates WHERE type == ? AND "
               "carbohydrates BETWEEN ? AND ? "
               "AND sugar BETWEEN ? AND ? "
               "AND calories BETWEEN ? AND ? "
               "AND protein BETWEEN ? AND ? "
               "AND sodium BETWEEN ? AND ? "
               "AND fats BETWEEN ? AND ? "
               "AND price <= ?")

        cursor.execute(sql, (meal_type,
                             carbohydrates[0], carbohydrates[1],
                             sugar[0], sugar[1],
                             energy[0], energy[1],
                             protein[0], protein[1],
                             salt[0], salt[1],
                             fat[0], fat[1],
                             price))
        return cursor.fetchall()

    if sentence_type == 2:
        columns = {
            1: "carbohydrates",
            2: "sugar",
            3: "calories",
            4: "protein",
            5: "sodium",
            6: "fats",
            7: "price",
        }
        column = columns.get(sub_sentence)

        if column:
            sql = f"SELECT SUM({column}) FROM plates WHERE type = ?"
            cursor.execute(sql, (meal_type,))
            return cursor.fetchone()[0]

        return None


def percents_generator_food(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, food_time, sub_sentence):

    result = []
    if food_time == 1:
        breakfast_total = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 1, 2, sub_sentence) * 2
        result.append(breakfast_total)
        breakfast1_percent = 1.0
        result.append(breakfast1_percent)

    elif food_time == 2:
        lunch2 = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 2, 2, sub_sentence) * 2
        lunch3 = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 3, 2, sub_sentence) * 2
        lunch_total = lunch2 + lunch3
        result.append(lunch_total)
        lunch2_percent = lunch2 / lunch_total
        result.append(lunch2_percent)
        lunch3_percent = lunch3 / lunch_total
        result.append(lunch3_percent)

    elif food_time == 3:
        dinner_total = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 2, 2, sub_sentence) * 2
        result.append(dinner_total)
        dinner2_percent = 1.0
        result.append(dinner2_percent)

    return result


def percents_generator_day(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, sub_sentence):

    solution = []

    breakfast_total = percents_generator_food(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 1, sub_sentence)[0]
    lunch_total = percents_generator_food(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 2, sub_sentence)[0]
    dinner_total = percents_generator_food(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 3, sub_sentence)[0]

    total = breakfast_total + lunch_total + dinner_total

    breakfast_percent = breakfast_total / total
    solution.append(breakfast_percent)
    lunch_percent = lunch_total / total
    solution.append(lunch_percent)
    dinner_percent = dinner_total / total
    solution.append(dinner_percent)

    return solution


def obtain_breakfast(cursor, selected_breakfasts, carbohydrates, sugar, energy, protein, salt, fat, price, person_type):
    print()
    print("\n--------- PLATOS DISPOBIBLES DESAYUNO ---------")
    breakfasts = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 1, 1, -1)
    breakfasts = filter_plates(breakfasts, person_type)
    drinks = sql_sentences(cursor, [0, carbohydrates[1]], [0, sugar[1]], [0, energy[1]], [0, protein[1]], salt, [0, fat[1]], price, 4, 1, -1)
    drinks = filter_plates(drinks, person_type)
    print(f'\nPRINCIPAL:')
    for i in range(len(breakfasts)):
        print(f"breakfast: {breakfasts[i]}")
    print(f'\nBEBIDA:')
    for i in range(len(drinks)):
        print(f"drink: {drinks[i]}")

    valid_combinations = []
    if breakfasts and drinks:
        for breakfast in breakfasts:
            for drink in drinks:
                combination = (breakfast[0], drink[0])

                if combination in selected_breakfasts:
                    continue

                combined_carbs = drink[2] + breakfast[2]
                combined_sugar = drink[5] + breakfast[5]
                combined_energy = drink[1] + breakfast[1]
                combined_protein = drink[3] + breakfast[3]
                combined_salt = drink[6] + breakfast[6]
                combined_fat = drink[4] + breakfast[4]
                combined_price = drink[7] + breakfast[7]

                if (carbohydrates[0] <= combined_carbs <= carbohydrates[1] and
                        sugar[0] <= combined_sugar <= sugar[1] and
                        energy[0] <= combined_energy <= energy[1] and
                        protein[0] <= combined_protein <= protein[1] and
                        salt[0] <= combined_salt <= salt[1] and
                        fat[0] <= combined_fat <= fat[1] and
                        combined_price <= price):

                    valid_combinations.append((breakfast, drink))

    if valid_combinations:
        selected_combination = random.choice(valid_combinations)
        return selected_combination

    return None


def obtain_lunch(cursor, selected_lunches, carbohydrates, sugar, energy, protein, salt, fat, price, person_type):
    print()
    print("\n--------- PLATOS DISPONIBLES ALMUERZO ---------")
    nutritional_percents = []
    for i in range(1, 8):
        nutritional_percents.append(percents_generator_food(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 2, i)[1:])

    mains = sql_sentences(cursor, [nutritional_percents[0][0] * carbohydrates[0], nutritional_percents[0][0] * carbohydrates[1]],
                          [0 * sugar[0], nutritional_percents[1][0] * sugar[1]],
                          [nutritional_percents[2][0] * energy[0], nutritional_percents[2][0] * energy[1]],
                          [nutritional_percents[3][0] * protein[0], nutritional_percents[3][0] * protein[1]],
                          [0 * salt[0], nutritional_percents[4][0] * salt[1]],
                          [nutritional_percents[5][0] * fat[0], nutritional_percents[5][0] * fat[1]],
                          nutritional_percents[6][0] * price, 2, 1, -1)
    mains = filter_plates(mains, person_type)

    sides = sql_sentences(cursor, [nutritional_percents[0][1] * carbohydrates[0], nutritional_percents[0][1] * carbohydrates[1]],
                          [0 * sugar[0], nutritional_percents[1][1] * sugar[1]],
                          [nutritional_percents[2][1] * energy[0], nutritional_percents[2][1] * energy[1]],
                          [nutritional_percents[3][1] * protein[0], nutritional_percents[3][1] * protein[1]],
                          [0 * salt[0], nutritional_percents[4][1] * salt[1]],
                          [nutritional_percents[5][1] * fat[0], nutritional_percents[5][1] * fat[1]],
                          nutritional_percents[6][1] * price, 3, 1, -1)
    sides = filter_plates(sides, person_type)
    sides = filter_plates(sides, person_type)

    drinks = sql_sentences(cursor, [0, carbohydrates[1]], [0, sugar[1]], [0, energy[1]], [0, protein[1]], salt, [0, fat[1]], price, 4, 1, -1)
    drinks = filter_plates(drinks, person_type)

    print(f'\nPRINCIPAL:')
    for i in range(len(mains)):
        print(f"main: {mains[i]}")
    print("\nSECUNDARIO:")
    for i in range(len(sides)):
        print(f"side: {sides[i]}")
    print(f'\nBEBIDA:')
    for i in range(len(drinks)):
        print(f"drink: {drinks[i]}")


    """
    print("\nPOSTRE:")
    desserts = sql_sentences(cursor, [nutritional_percents[0][2] * carbohydrates[0], nutritional_percents[0][2] * carbohydrates[1]],
                          [0 * sugar[0], nutritional_percents[1][2] * sugar[1]],
                          [nutritional_percents[2][2] * energy[0], nutritional_percents[2][2] * energy[1]],
                          [nutritional_percents[3][2] * protein[0], nutritional_percents[3][2] * protein[1]],
                          [0 * salt[0], nutritional_percents[4][2] * salt[1]],
                          [nutritional_percents[5][2] * fat[0], nutritional_percents[5][2] * fat[1]],
                          nutritional_percents[6][2] * price, 5, 1, 1)
    for i in range(len(desserts)):
        print(f"dessert: {desserts[i]}")
    """

    valid_combinations = []

    if mains and sides and drinks:
        for main in mains:
            for side in sides:
                for drink in drinks:
                        combination = (main[0], side[0], drink[0])

                        if combination in selected_lunches:
                            continue

                        combined_carbs = drink[2] + side[2] + main[2]
                        combined_sugar = drink[5] + side[5] + main[5]
                        combined_energy = drink[1] + side[1] + main[1]
                        combined_protein = drink[3] + side[3] + main[3]
                        combined_salt = drink[6] + side[6] + main[6]
                        combined_fat = drink[4] + side[4] + main[4]
                        combined_price = drink[7] + side[7] + main[7]

                        if (carbohydrates[0] <= combined_carbs <= carbohydrates[1] and
                                sugar[0] <= combined_sugar <= sugar[1] and
                                energy[0] <= combined_energy <= energy[1] and
                                protein[0] <= combined_protein <= protein[1] and
                                salt[0] <= combined_salt <= salt[1] and
                                fat[0] <= combined_fat <= fat[1] and
                                combined_price <= price):

                            valid_combinations.append((main, side, drink))

    if valid_combinations:
        selected_combination = random.choice(valid_combinations)
        return selected_combination

    return None


def obtain_dinner(cursor, selected_dinners, carbohydrates, sugar, energy, protein, salt, fat, price, person_type):
    print()
    print("\n--------- PLATOS DISPONIBLES CENA ---------")
    mains = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 2, 1, -1)
    mains = filter_plates(mains, person_type)
    drinks = sql_sentences(cursor, [0, carbohydrates[1]], [0, sugar[1]], [0, energy[1]], [0, protein[1]], salt, [0, fat[1]], price, 4, 1, -1)
    drinks = filter_plates(drinks, person_type)

    print(f'\nPRINCIPAL:')
    for i in range(len(mains)):
        print(f"main: {mains[i]}")
    print(f'\nBEBIDA:')
    for i in range(len(drinks)):
        print(f"drink: {drinks[i]}")

    valid_combinations = []

    if mains and drinks:
        for main in mains:
            for drink in drinks:
                combination = (main[0], drink[0])

                if combination in selected_dinners:
                    continue

                combined_carbs = drink[2] + main[2]
                combined_sugar = drink[5] + main[5]
                combined_energy = drink[1] + main[1]
                combined_protein = drink[3] + main[3]
                combined_salt = drink[6] + main[6]
                combined_fat = drink[4] + main[4]
                combined_price = drink[7] + main[7]

                if (carbohydrates[0] <= combined_carbs <= carbohydrates[1] and
                        sugar[0] <= combined_sugar <= sugar[1] and
                        energy[0] <= combined_energy <= energy[1] and
                        protein[0] <= combined_protein <= protein[1] and
                        salt[0] <= combined_salt <= salt[1] and
                        fat[0] <= combined_fat <= fat[1] and
                        combined_price <= price):

                    valid_combinations.append((main, drink))
    if valid_combinations:
        selected_combination = random.choice(valid_combinations)
        return selected_combination
    return None


def get_nutritional_mults(nutrient, factors):
    return


def percents_person_type(person_type):
    factors = {
        1: {  # Tipo por defecto
            "breakfast": {
                "carbohydrates": [0.20, 0.35],
                "sugar": [0, 0.55],
                "energy": [0.15, 0.30],
                "protein": [0.075, 0.30],
                "salt": [0, 0.25],
                "fat": [0.10, 0.45],
                "price_divisor": 4
            },
            "lunch": {
                "carbohydrates": [0.45, 0.60],
                "sugar": [0, 0.40],
                "energy": [0.40, 0.60],
                "protein": [0.3525, 0.55],
                "salt": [0, 0.60],
                "fat": [0.40, 0.60],
                "price_divisor": 2
            },
            "dinner": {
                "carbohydrates": [0.15, 0.25],
                "sugar": [0, 0.25],
                "energy": [0.25, 0.30],
                "protein": [0.30, 0.35],
                "salt": [0, 0.35],
                "fat": [0.30, 0.35],
                "price_divisor": 4
            }
        },
        2: {  # Persona activa (ejemplo)
            "breakfast": {
                "carbohydrates": [0.25, 0.40],
                "sugar": [0, 0.50],
                "energy": [0.20, 0.35],
                "protein": [0.10, 0.35],
                "salt": [0, 0.20],
                "fat": [0.15, 0.50],
                "price_divisor": 4
            },
            "lunch": {
                "carbohydrates": [0.50, 0.65],
                "sugar": [0, 0.35],
                "energy": [0.45, 0.65],
                "protein": [0.40, 0.60],
                "salt": [0, 0.55],
                "fat": [0.45, 0.65],
                "price_divisor": 2
            },
            "dinner": {
                "carbohydrates": [0.20, 0.30],
                "sugar": [0, 0.20],
                "energy": [0.30, 0.35],
                "protein": [0.35, 0.40],
                "salt": [0, 0.30],
                "fat": [0.35, 0.40],
                "price_divisor": 4
            }
        }
    }
    return factors.get(person_type)


def diet_generator(carbohydrates, sugar, energy, protein, salt, fat, price, special_diet, person_type, selected_breakfasts, selected_lunches, selected_dinners):
    db_path = os.path.join('../../FoodDbManagement', 'DietApp.db')

    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    solution = []

    print(f"\nValores de entrada resolver_dieta: {carbohydrates, sugar, energy, protein, salt, fat, price}")

    breakfast = obtain_breakfast(cursor, selected_breakfasts,
                                 [carbohydrates[0] * 0.20, carbohydrates[1] * 0.35],
                                 [sugar[0] * 0, sugar[1] * 0.55],
                                 [energy[0] * 0.15, energy[1] * 0.30],
                                 [protein[0] * 0.075, protein[1] * 0.30],
                                 [salt[0] * 0, salt[1] * 0.25],
                                 [fat[0] * 0.10, fat[1] * 0.45],
                                 price / 4, special_diet)

    lunch = obtain_lunch(cursor, selected_lunches,
                                 [carbohydrates[0] * 0.45, carbohydrates[1] * 0.60],
                                 [sugar[0] * 0, sugar[1] * 0.40],
                                 [energy[0] * 0.40, energy[1] * 0.60],
                                 [protein[0] * 0.3525, protein[1] * 0.55],
                                 [salt[0] * 0, salt[1] * 0.60],
                                 [fat[0] * 0.40, fat[1] * 0.60],
                                 price / 2, special_diet)

    dinner = obtain_dinner(cursor, selected_dinners,
                                 [carbohydrates[0] * 0.15, carbohydrates[1] * 0.25],
                                 [sugar[0] * 0, sugar[1] * 0.25],
                                 [energy[0] * 0.25, energy[1] * 0.30],
                                 [protein[0] * 0.30, protein[1] * 0.35],
                                 [salt[0] * 0, salt[1] * 0.35],
                                 [fat[0] * 0.30, fat[1] * 0.35],
                                 price / 4, special_diet)

    if breakfast is None or lunch is None or dinner is None:
        return None

    solution.append(breakfast)
    solution.append(lunch)
    solution.append(dinner)

    conn.close()
    return solution


def print_solution(solution):
    if solution:
        print("\n--- SOLUCIÓN ---")
        # Nombres de los atributos nutricionales y el precio
        requirements = ["name", "energy", "carbohydrates", "sugar", "protein", "fat", "salt", "price"]
        print(f"\nDesayuno: {solution[0][0][0]}, {solution[0][1][0]}")
        print(f"Almuerzo: {solution[1][0][0]}, {solution[1][1][0]}, {solution[1][2][0]}")
        print(f"Cena: {solution[2][0][0]}, {solution[2][1][0]}")
        
        # Inicializa una lista con ceros para acumular las cualidades de la dieta (carbohidratos, azúcar, etc.)
        diet_qualities = [0] * 8  # Excluye "name", que es texto

        print("\n--------- VALORES NUTRICIONALES ---------")
        for meal in solution:
            for plate in meal:
                print("\n")
                # Imprime los valores de cada plato y acumula los datos numéricos
                for i in range(0, 8):
                    print(f"{requirements[i]}: {plate[i]}")
                    if i > 0:  # Evita sumar el nombre del plato
                        diet_qualities[i - 1] += plate[i]

        # Imprime el total acumulado de las cualidades de la dieta
        print()
        print("\n--------- TOTALES DE LA DIETA ---------\n")
        for i in range(1, 8):
            print(f"{requirements[i]} total: {diet_qualities[i - 1]}")
        
        return 0
    else:
        print("No se encontró una solución válida.")
        return -1
