from Plates import *
from SQLsentences import *

import sqlite3
import random
import os


def filter_plates(plates, person_type):
    if not all(isinstance(plate, Plate) for plate in plates):
        raise TypeError("All elements in plates must be Plates")
    if person_type == 1:
        return plates
    elif person_type == 2:
        return [plate for plate in plates if plate.vegetarian == 1]
    elif person_type == 3:
        return [plate for plate in plates if plate.vegan == 1]
    elif person_type == 4:
        return [plate for plate in plates if plate.celiac == 1]

    return plates

def percents_generator_food(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, food_time, sub_sentence):

    result = []
    if food_time == 1:
        breakfast_total = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 1, 2, sub_sentence, -1) * 2
        result.append(breakfast_total)
        breakfast1_percent = 1.0
        result.append(breakfast1_percent)

    elif food_time == 2:
        lunch2 = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 2, 2, sub_sentence, -1) * 2
        lunch3 = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 3, 2, sub_sentence, -1) * 2
        lunch_total = lunch2 + lunch3
        result.append(lunch_total)
        lunch2_percent = lunch2 / lunch_total
        result.append(lunch2_percent)
        lunch3_percent = lunch3 / lunch_total
        result.append(lunch3_percent)

    elif food_time == 3:
        dinner_total = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 2, 2, sub_sentence, -1) * 2
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
    print(energy, carbohydrates, protein, fat, sugar, salt, price, person_type)
    breakfasts_rows = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 1, 1, -1, 2)
    breakfasts_convert = [Plate(row) for row in breakfasts_rows]
    breakfasts = filter_plates(breakfasts_convert, person_type)
    drinks_rows = sql_sentences(cursor, [0, carbohydrates[1]], sugar, [0, energy[1]], [0, protein[1]], salt, [0, fat[1]], price, 4, 1, -1, 1)
    drinks_convert = [Plate(row) for row in drinks_rows]
    drinks = filter_plates(drinks_convert, person_type)
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

                if (carbohydrates[0] * 0.8 <= combined_carbs <= carbohydrates[1] * 1.2 and
                        combined_sugar <= sugar * 1.2 and
                        energy[0] * 0.8 <= combined_energy <= energy[1] * 1.2 and
                        protein[0] * 0.8 <= combined_protein <= protein[1] * 1.2 and
                        combined_salt <= salt * 1.2 and
                        fat[0] * 0.8 <= combined_fat <= fat[1] * 1.2 and
                        combined_price <= price * 1.2):

                    valid_combinations.append((breakfast, drink))

    if valid_combinations:
        print(f"\nVALID_BREAKFASTS:")
        for combi in valid_combinations:
            print(combi)
        selected_combination = random.choice(valid_combinations)
        selected_breakfasts.add(selected_combination)
        return selected_combination

    return None


def obtain_lunch(cursor, selected_lunches, carbohydrates, sugar, energy, protein, salt, fat, price, person_type):
    print("\n--------- PLATOS DISPONIBLES ALMUERZO ---------")
    print(energy, carbohydrates, protein, fat, sugar, salt, price, person_type)
    print()
    nutritional_percents = []
    for i in range(1, 8):
        nutritional_percents.append(percents_generator_food(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 2, i)[1:])

    mains_rows = sql_sentences(cursor, [nutritional_percents[0][0] * carbohydrates[0], nutritional_percents[0][0] * carbohydrates[1]],
                          nutritional_percents[1][0] * sugar,
                          [nutritional_percents[2][0] * energy[0], nutritional_percents[2][0] * energy[1]],
                          [nutritional_percents[3][0] * protein[0], nutritional_percents[3][0] * protein[1]],
                          nutritional_percents[4][0] * salt,
                          [nutritional_percents[5][0] * fat[0], nutritional_percents[5][0] * fat[1]],
                          nutritional_percents[6][0] * price, 2, 1, -1, 2)
    mains_convert = [Plate(row) for row in mains_rows]
    mains = filter_plates(mains_convert, person_type)

    sides_rows = sql_sentences(cursor, [nutritional_percents[0][1] * carbohydrates[0], nutritional_percents[0][1] * carbohydrates[1]],
                          nutritional_percents[1][1] * sugar,
                          [nutritional_percents[2][1] * energy[0], nutritional_percents[2][1] * energy[1]],
                          [nutritional_percents[3][1] * protein[0], nutritional_percents[3][1] * protein[1]],
                          nutritional_percents[4][1] * salt,
                          [nutritional_percents[5][1] * fat[0], nutritional_percents[5][1] * fat[1]],
                          nutritional_percents[6][1] * price, 3, 1, -1,2 )
    sides_convert = [Plate(row) for row in sides_rows]
    sides = filter_plates(sides_convert, person_type)

    drinks_rows = sql_sentences(cursor, [0, carbohydrates[1]], sugar, [0, energy[1]], [0, protein[1]], salt, [0, fat[1]], price, 4, 1, -1, 1)
    drinks_convert = [Plate(row) for row in drinks_rows]
    drinks = filter_plates(drinks_convert, person_type)

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

                    if (carbohydrates[0] * 0.8 <= combined_carbs <= carbohydrates[1] * 1.2 and
                            combined_sugar <= sugar * 1.2 and
                            energy[0] * 0.8 <= combined_energy <= energy[1] * 1.2 and
                            protein[0] * 0.8 <= combined_protein <= protein[1] * 1.2 and
                            combined_salt <= salt * 1.2 and
                            fat[0] * 0.8 <= combined_fat <= fat[1] * 1.2 and
                            combined_price <= price * 1.2):

                        valid_combinations.append((main, side, drink))

    if valid_combinations:
        print(f"\nVALID_LUNCHES:")
        for combi in valid_combinations:
            print(combi)
        selected_combination = random.choice(valid_combinations)
        selected_lunches.add(selected_combination)
        return selected_combination

    return None


def obtain_dinner(cursor, selected_dinners, carbohydrates, sugar, energy, protein, salt, fat, price, person_type):
    print()
    print("\n--------- PLATOS DISPONIBLES CENA ---------")
    print(energy, carbohydrates, protein, fat, sugar, salt, price, person_type)
    mains_rows = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 2, 1, -1, 2)
    mains2_rows = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 3, 1, -1, 2)
    mains_rows.extend(mains2_rows)
    mains_convert = [Plate(row) for row in mains_rows]
    mains = filter_plates(mains_convert, person_type)
    drinks_rows = sql_sentences(cursor, [0, carbohydrates[1]], sugar, [0, energy[1]], [0, protein[1]], salt, [0, fat[1]], price, 4, 1, -1, 1)
    drinks_convert = [Plate(row) for row in drinks_rows]
    drinks = filter_plates(drinks_convert, person_type)

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

                if (carbohydrates[0] * 0.8 <= combined_carbs <= carbohydrates[1] * 1.2 and
                        combined_sugar <= sugar * 1.2 and
                        energy[0] * 0.8 <= combined_energy <= energy[1] * 1.2 and
                        protein[0] * 0.8 <= combined_protein <= protein[1] * 1.2 and
                        combined_salt <= salt * 1.2 and
                        fat[0] * 0.8 <= combined_fat <= fat[1] * 1.2 and
                        combined_price <= price * 1.2):

                    valid_combinations.append((main, drink))

    if valid_combinations:
        print(f"\nVALID_DINNERS:")
        for combi in valid_combinations:
            print(combi)
        selected_combination = random.choice(valid_combinations)
        selected_dinners.add(selected_combination)
        return selected_combination
    return None


def validate_full_diet(diet, carbs_range, sugar_max, kcal_range, protein_range, salt_max, fat_range, price_max):
    total_carbs = total_sugar = total_kcal = total_protein = total_salt = total_fat = total_price = 0

    for meal in diet:
        for plate in meal:
            total_carbs += plate.carbohydrates
            total_sugar += plate.sugar
            total_kcal += plate.calories
            total_protein += plate.protein
            total_salt += plate.salt
            total_fat += plate.fat
            total_price += plate.price

    return (
            carbs_range[0] * 0.8 <= total_carbs <= carbs_range[1] * 1.2 and
            kcal_range[0] * 0.8 <= total_kcal <= kcal_range[1] * 1.2 and
            protein_range[0] * 0.8 <= total_protein <= protein_range[1] * 1.2 and
            fat_range[0] * 0.8 <= total_fat <= fat_range[1] * 1.2 and
            total_sugar <= sugar_max and
            total_salt <= salt_max and
            total_price <= price_max
    )


def diet_generator(carbohydrates, sugar, energy, protein, salt, fat, price, person_type, person_preferences, selected_breakfasts, selected_lunches, selected_dinners, not_valid):
    BASE_DIR = os.path.dirname(os.path.abspath(__file__))
    db_path = os.path.join(BASE_DIR, '../../FoodDbManagement', 'DietApp.db')
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    solution = []

    print(f"\nValores de entrada resolver_dieta: {carbohydrates, sugar, energy, protein, salt, fat, price}")

    # Distribución calórica por comida (en kcal)
    kcal_breakfast = (energy[0] * 0.20, energy[1] * 0.20)
    kcal_lunch = (energy[0] * 0.50, energy[1] * 0.50)
    kcal_dinner = (energy[0] * 0.30, energy[1] * 0.30)

    # Función para obtener el rango de gramos de un nutriente para una comida según kcal
    def gram_split(nutrient_range, kcal_meal_range, kcal_total_range):
        if kcal_total_range[0] == 0 or kcal_total_range[1] == 0:
            return None
        return [nutrient_range[0] * (kcal_meal_range[0] / kcal_total_range[0]),
                nutrient_range[1] * (kcal_meal_range[1] / kcal_total_range[1])]

    # Reparto de macronutrientes por comida
    carbs_breakfast = gram_split(carbohydrates, kcal_breakfast, energy)
    carbs_lunch = gram_split(carbohydrates, kcal_lunch, energy)
    carbs_dinner = gram_split(carbohydrates, kcal_dinner, energy)

    protein_breakfast = gram_split(protein, kcal_breakfast, energy)
    protein_lunch = gram_split(protein, kcal_lunch, energy)
    protein_dinner = gram_split(protein, kcal_dinner, energy)

    fat_breakfast = gram_split(fat, kcal_breakfast, energy)
    fat_lunch = gram_split(fat, kcal_lunch, energy)
    fat_dinner = gram_split(fat, kcal_dinner, energy)

    # Distribución de azúcar y sal (fija o proporcional)
    sugar_limit = sugar
    salt_limit = salt

    # --- Obtener comidas ---
    breakfast = obtain_breakfast(cursor, selected_breakfasts,
                                 carbs_breakfast,
                                 sugar_limit * 0.5,
                                 kcal_breakfast,
                                 protein_breakfast,
                                 salt_limit * 0.25,
                                 fat_breakfast,
                                 price / 4, person_type)

    lunch = obtain_lunch(cursor, selected_lunches,
                         carbs_lunch,
                         sugar_limit * 0.35,
                         kcal_lunch,
                         protein_lunch,
                         salt_limit * 0.5,
                         fat_lunch,
                         price / 2, person_type)

    dinner = obtain_dinner(cursor, selected_dinners,
                           carbs_dinner,
                           sugar_limit * 0.15,
                           kcal_dinner,
                           protein_dinner,
                           salt_limit * 0.25,
                           fat_dinner,
                           price / 4, person_type)

    if breakfast is None or lunch is None or dinner is None:
        conn.close()
        return None

    solution.extend([breakfast, lunch, dinner])

    if not validate_full_diet(solution, carbohydrates, sugar, energy, protein, salt, fat, price):
        print("La dieta generada no cumple con los requisitos totales.")
        if solution in not_valid:
            return None
        not_valid.append(solution)
        return diet_generator(carbohydrates, sugar, energy, protein, salt, fat, price, person_type, person_preferences, selected_breakfasts, selected_lunches, selected_dinners, not_valid)

    conn.close()
    return solution
