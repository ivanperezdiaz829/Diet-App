from Plates import *
from SQLsentences import *
import sqlite3
import random
import os


def filter_plates(plates, person_type):
    if person_type == 1:
        return plates
    elif person_type == 2:
        return [plate for plate in plates if plate.vegetarian == 1]
    elif person_type == 3:
        return [plate for plate in plates if plate.vegan == 1]
    elif person_type == 4:
        return [plate for plate in plates if plate.celiac == 1]
    return plates


def sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, meal_type, sentence_type, sub_sentence):

    if sentence_type == 1:
        sql = ("SELECT * FROM plates WHERE type == ? AND "
               "carbohydrates BETWEEN ? AND ? "
               "AND sugar <= ? "
               "AND calories BETWEEN ? AND ? "
               "AND protein BETWEEN ? AND ? "
               "AND sodium <= ? "
               "AND fats BETWEEN ? AND ? "
               "AND price <= ?")

        cursor.execute(sql, (meal_type,
                             carbohydrates[0], carbohydrates[1],
                             sugar,
                             energy[0], energy[1],
                             protein[0], protein[1],
                             salt,
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
    breakfasts_rows = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 1, 1, -1)
    breakfasts_rows = filter_plates(breakfasts_rows, person_type)
    breakfasts = [Plate(row) for row in breakfasts_rows]
    drinks_rows = sql_sentences(cursor, [0, carbohydrates[1]], sugar, [0, energy[1]], [0, protein[1]], salt, [0, fat[1]], price, 4, 1, -1)
    drinks_rows = filter_plates(drinks_rows, person_type)
    drinks = [Plate(row) for row in drinks_rows]
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
                combination = (breakfast, drink)

                if combination in selected_breakfasts:
                    continue

                combined_carbs = drink.carbohydrates + breakfast.carbohydrates
                combined_sugar = drink.sugar + breakfast.sugar
                combined_energy = drink.calories + breakfast.calories
                combined_protein = drink.protein + breakfast.protein
                combined_salt = drink.salt + breakfast.salt
                combined_fat = drink.fat + breakfast.fat
                combined_price = drink.price + breakfast.price

                if (carbohydrates[0] <= combined_carbs <= carbohydrates[1] and
                        combined_sugar <= sugar and
                        energy[0] <= combined_energy <= energy[1] and
                        protein[0] <= combined_protein <= protein[1] and
                        combined_salt <= salt and
                        fat[0] <= combined_fat <= fat[1] and
                        combined_price <= price):

                    valid_combinations.append((breakfast, drink))

    if valid_combinations:
        print(valid_combinations)
        selected_combination = random.choice(valid_combinations)
        return selected_combination

    return None


def obtain_lunch(cursor, selected_lunches, carbohydrates, sugar, energy, protein, salt, fat, price, person_type):
    print()
    print("\n--------- PLATOS DISPONIBLES ALMUERZO ---------")
    nutritional_percents = []
    for i in range(1, 8):
        nutritional_percents.append(percents_generator_food(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 2, i)[1:])

    mains_rows = sql_sentences(cursor, [nutritional_percents[0][0] * carbohydrates[0], nutritional_percents[0][0] * carbohydrates[1]],
                          nutritional_percents[1][0] * sugar,
                          [nutritional_percents[2][0] * energy[0], nutritional_percents[2][0] * energy[1]],
                          [nutritional_percents[3][0] * protein[0], nutritional_percents[3][0] * protein[1]],
                          nutritional_percents[4][0] * salt,
                          [nutritional_percents[5][0] * fat[0], nutritional_percents[5][0] * fat[1]],
                          nutritional_percents[6][0] * price, 2, 1, -1)
    mains_rows = filter_plates(mains_rows, person_type)
    mains = [Plate(row) for row in mains_rows]

    sides_rows = sql_sentences(cursor, [nutritional_percents[0][1] * carbohydrates[0], nutritional_percents[0][1] * carbohydrates[1]],
                          nutritional_percents[1][1] * sugar,
                          [nutritional_percents[2][1] * energy[0], nutritional_percents[2][1] * energy[1]],
                          [nutritional_percents[3][1] * protein[0], nutritional_percents[3][1] * protein[1]],
                          nutritional_percents[4][1] * salt,
                          [nutritional_percents[5][1] * fat[0], nutritional_percents[5][1] * fat[1]],
                          nutritional_percents[6][1] * price, 3, 1, -1)
    sides_rows = filter_plates(sides_rows, person_type)
    sides = [Plate(row) for row in sides_rows]

    drinks_rows = sql_sentences(cursor, [0, carbohydrates[1]], sugar, [0, energy[1]], [0, protein[1]], salt, [0, fat[1]], price, 4, 1, -1)
    drinks_rows = filter_plates(drinks_rows, person_type)
    drinks = [Plate(row) for row in drinks_rows]

    print(f'\nPRINCIPAL:')
    for i in range(len(mains)):
        print(f"main: {mains[i]}")
    print("\nSECUNDARIO:")
    for i in range(len(sides)):
        print(f"side: {sides[i]}")
    print(f'\nBEBIDA:')
    for i in range(len(drinks)):
        print(f"drink: {drinks[i]}")

    valid_combinations = []

    if mains and sides and drinks:
        for main in mains:
            for side in sides:
                for drink in drinks:
                    combination = (main, side, drink)

                    if combination in selected_lunches:
                        continue

                    combined_carbs = drink.carbohydrates + side.carbohydrates + main.carbohydrates
                    combined_sugar = drink.sugar + side.sugar + main.sugar
                    combined_energy = drink.calories + side.calories + main.calories
                    combined_protein = drink.protein + side.protein + main.protein
                    combined_salt = drink.salt + side.salt + main.salt
                    combined_fat = drink.fat + side.fat + main.fat
                    combined_price = drink.price + side.price + main.price

                    if (carbohydrates[0] <= combined_carbs <= carbohydrates[1] and
                            combined_sugar <= sugar and
                            energy[0] <= combined_energy <= energy[1] and
                            protein[0] <= combined_protein <= protein[1] and
                            combined_salt <= salt and
                            fat[0] <= combined_fat <= fat[1] and
                            combined_price <= price):

                        valid_combinations.append((main, side, drink))

    if valid_combinations:
        print(valid_combinations)
        selected_combination = random.choice(valid_combinations)
        return selected_combination

    return None


def obtain_dinner(cursor, selected_dinners, carbohydrates, sugar, energy, protein, salt, fat, price, person_type):
    print()
    print("\n--------- PLATOS DISPONIBLES CENA ---------")
    mains_rows = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 2, 1, -1)
    mains_rows = filter_plates(mains_rows, person_type)
    mains = [Plate(row) for row in mains_rows]

    drinks_rows = sql_sentences(cursor, [0, carbohydrates[1]], sugar, [0, energy[1]], [0, protein[1]], salt, [0, fat[1]], price, 4, 1, -1)
    drinks_rows = filter_plates(drinks_rows, person_type)
    drinks = [Plate(row) for row in drinks_rows]

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
                combination = (main, drink)

                if combination in selected_dinners:
                    continue

                combined_carbs = drink.carbohydrates + main.carbohydrates
                combined_sugar = drink.sugar + main.sugar
                combined_energy = drink.calories + main.calories
                combined_protein = drink.protein + main.protein
                combined_salt = drink.salt + main.salt
                combined_fat = drink.fat + main.fat
                combined_price = drink.price + main.price

                if (carbohydrates[0] <= combined_carbs <= carbohydrates[1] and
                        combined_sugar <= sugar and
                        energy[0] <= combined_energy <= energy[1] and
                        protein[0] <= combined_protein <= protein[1] and
                        combined_salt <= salt and
                        fat[0] <= combined_fat <= fat[1] and
                        combined_price <= price):

                    valid_combinations.append((main, drink))

    if valid_combinations:
        print(valid_combinations)
        selected_combination = random.choice(valid_combinations)
        return selected_combination
    return None


def diet_generator(carbohydrates, sugar, energy, protein, salt, fat, price, person_type, person_preferences, selected_breakfasts, selected_lunches, selected_dinners):
    db_path = os.path.join('../../FoodDbManagement', 'DietApp.db')

    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    solution = []

    print(f"\nValores de entrada resolver_dieta: {carbohydrates, sugar, energy, protein, salt, fat, price}")

    breakfast = obtain_breakfast(cursor, selected_breakfasts,
                                 [carbohydrates[0] * 0.20, carbohydrates[1] * 0.30],
                                 sugar * 0.55,
                                 [energy[0] * 0.175, energy[1] * 0.30],
                                 [protein[0] * 0.075, protein[1] * 0.30],
                                 salt * 0.25,
                                 [fat[0] * 0.10, fat[1] * 0.45],
                                 price / 4, person_type)

    lunch = obtain_lunch(cursor, selected_lunches,
                                 [carbohydrates[0] * 0.40, carbohydrates[1] * 0.65],
                                 sugar * 0.45,
                                 [energy[0] * 0.325, energy[1] * 0.65],
                                 [protein[0] * 0.3025, protein[1] * 0.60],
                                 salt * 0.60,
                                 [fat[0] * 0.35, fat[1] * 0.60],
                                 price / 2, person_type)

    dinner = obtain_dinner(cursor, selected_dinners,
                                 [carbohydrates[0] * 0.20, carbohydrates[1] * 0.25],
                                 sugar * 0.20,
                                 [energy[0] * 0.275, energy[1] * 0.30],
                                 [protein[0] * 0.25, protein[1] * 0.30],
                                 salt * 0.35,
                                 [fat[0] * 0.30, fat[1] * 0.35],
                                 price / 4, person_type)

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
        print(f"\nDesayuno: {solution[0][0]} y {solution[0][1]}")
        print(f"Almuerzo: {solution[1][0]}, {solution[1][1]} y {solution[1][2]}")
        print(f"Cena: {solution[2][0]} y {solution[2][1]}")

        """
        # Inicializa una lista con ceros para acumular las cualidades de la dieta (carbohidratos, azúcar, etc.)
        diet_qualities = [0] * 8  # Excluye "name", que es texto

        print("\n--------- VALORES NUTRICIONALES ---------\n")

        print(f"DESAYUNO:\nMAIN: \nName: {solution[0][0].name} \nCarbohydrates: {solution[0][0].carbohydrates}")

        # Imprime el total acumulado de las cualidades de la dieta
        print()
        print("\n--------- TOTALES DE LA DIETA ---------\n")
        for i in range(1, 8):
            print(f"{requirements[i]} total: {diet_qualities[i - 1]}")
        """
        return 0
    else:
        print("No se encontró una solución válida.")
        return -1
