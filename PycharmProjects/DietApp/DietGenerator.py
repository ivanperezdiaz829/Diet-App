from Plates import *
from SQLsentences import *
from collections import Counter

import sqlite3
import random
import os


def check_repetition(solution, selected_breakfasts, selected_lunches, selected_dinners):
    breakfast_main = solution[0][0]
    main_lunch = solution[1][0]
    side_lunch = solution[1][1]
    main_dinner = solution[2][0]

    diet_length = len(selected_breakfasts) + 1

    if diet_length <= 4:
        if main_lunch in selected_lunches or main_dinner in selected_dinners:
            return False
        if selected_lunches.count(side_lunch) >= 2:
            return False

    else:
        if selected_lunches.count(main_lunch) >= 3 or selected_dinners.count(main_dinner) >= 2:
            return False
        if selected_lunches.count(side_lunch) >= 4:
            return False

    selected_breakfasts.append(breakfast_main)
    selected_lunches.append(main_lunch)
    selected_lunches.append(side_lunch)
    selected_dinners.append(main_dinner)

    return True

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
    elif person_type == 5:
        return [plate for plate in plates if plate.halal == 1]
    return plates

def nutrient_quantity_kcal_split(nutrient_limit, kcal_meal_limit, kcal_total_limit):
    return nutrient_limit * (kcal_meal_limit / kcal_total_limit)

def obtain_breakfast(cursor, selected_breakfasts, carbohydrates, sugar, energy, protein, salt, fat, price, person_type):
    breakfast_mult = 2
    breakfasts_rows = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 1, 1, -1, breakfast_mult)
    breakfasts_convert = [Plate(row, breakfast_mult) for row in breakfasts_rows]
    breakfasts = filter_plates(breakfasts_convert, person_type)
    drink_mult = 1
    drinks_rows = sql_sentences(cursor, 0, sugar, [0, energy[1]], 0, salt, fat, price, 4, 1, -1, drink_mult)
    drinks_convert = [Plate(row, drink_mult) for row in drinks_rows]
    drinks = filter_plates(drinks_convert, person_type)

    valid_combinations = []
    if breakfasts and drinks:
        for breakfast in breakfasts:
            for drink in drinks:
                combination = (breakfast, drink)

                combined_carbs = drink.carbohydrates + breakfast.carbohydrates
                combined_sugar = drink.sugar + breakfast.sugar
                combined_energy = drink.calories + breakfast.calories
                combined_protein = drink.protein + breakfast.protein
                combined_salt = drink.salt + breakfast.salt
                combined_fat = drink.fat + breakfast.fat
                combined_price = drink.price + breakfast.price

                if (combined_carbs >= carbohydrates * 0.8 and
                        combined_sugar <= sugar * 1.2 and
                        energy[0] * 0.8 <= combined_energy <= energy[1] * 1.2 and
                        combined_protein >= protein * 0.8 and
                        combined_salt <= salt * 1.2 and
                        combined_fat <= fat * 1.2 and
                        combined_price <= price * 1.2):

                    valid_combinations.append(combination)

    if valid_combinations:
        return valid_combinations
    return None


def obtain_lunch(cursor, selected_lunches, carbohydrates, sugar, energy, protein, salt, fat, price, person_type):
    main_mult = 2
    mains_rows = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 2, 1, -1, main_mult)
    mains_convert = [Plate(row, main_mult) for row in mains_rows]
    mains = filter_plates(mains_convert, person_type)
    side_mult = 2
    sides_rows = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 3, 1, -1, side_mult)
    sides_convert = [Plate(row, side_mult) for row in sides_rows]
    sides = filter_plates(sides_convert, person_type)
    drink_mult = 1
    drinks_rows = sql_sentences(cursor, 0, sugar, [0, energy[1]], 0, salt, fat, price, 4, 1, -1, drink_mult)
    drinks_convert = [Plate(row, drink_mult) for row in drinks_rows]
    drinks = filter_plates(drinks_convert, person_type)

    valid_combinations = []

    if mains and sides and drinks:
        for main in mains:
            for side in sides:
                for drink in drinks:
                    combination = (main, side, drink)

                    combined_carbs = drink.carbohydrates + side.carbohydrates + main.carbohydrates
                    combined_sugar = drink.sugar + side.sugar + main.sugar
                    combined_energy = drink.calories + side.calories + main.calories
                    combined_protein = drink.protein + side.protein + main.protein
                    combined_salt = drink.salt + side.salt + main.salt
                    combined_fat = drink.fat + side.fat + main.fat
                    combined_price = drink.price + side.price + main.price

                    # Aplicar condiciones de mínimos y máximos
                    if (combined_carbs >= carbohydrates * 0.8 and
                            combined_sugar <= sugar * 1.2 and
                            energy[0] * 0.8 <= combined_energy <= energy[1] * 1.2 and
                            combined_protein >= protein * 0.8 and
                            combined_salt <= salt * 1.2 and
                            combined_fat <= fat * 1.2 and
                            combined_price <= price * 1.2):

                        valid_combinations.append(combination)

    if valid_combinations:
        return valid_combinations
    return None


def obtain_dinner(cursor, selected_dinners, carbohydrates, sugar, energy, protein, salt, fat, price, person_type):
    main_mult = 2
    mains_rows = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 2, 1, -1, main_mult)
    mains2_rows = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 3, 1, -1, main_mult)
    mains_rows.extend(mains2_rows)
    mains_convert = [Plate(row, main_mult) for row in mains_rows]
    mains = filter_plates(mains_convert, person_type)
    drink_mult = 1
    drinks_rows = sql_sentences(cursor, 0, sugar, [0, energy[1]], 0, salt, fat, price, 4, 1, -1, drink_mult)
    drinks_convert = [Plate(row, drink_mult) for row in drinks_rows]
    drinks = filter_plates(drinks_convert, person_type)

    valid_combinations = []

    if mains and drinks:
        for main in mains:
            for drink in drinks:
                combination = (main, drink)

                combined_carbs = drink.carbohydrates + main.carbohydrates
                combined_sugar = drink.sugar + main.sugar
                combined_energy = drink.calories + main.calories
                combined_protein = drink.protein + main.protein
                combined_salt = drink.salt + main.salt
                combined_fat = drink.fat + main.fat
                combined_price = drink.price + main.price

                if (combined_carbs >= carbohydrates * 0.8 and
                        combined_sugar <= sugar * 1.2 and
                        energy[0] * 0.8 <= combined_energy <= energy[1] * 1.2 and
                        combined_protein >= protein * 0.8 and
                        combined_salt <= salt * 1.2 and
                        combined_fat <= fat * 1.2 and
                        combined_price <= price * 1.2):

                    valid_combinations.append(combination)

    if valid_combinations:
        return valid_combinations
    return None


def validate_full_diet(diet, carbs_min, sugar_max, kcal_range, protein_min, salt_max, fat_max, price_max):
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

    if not (kcal_range[0] <= total_kcal <= kcal_range[1]):
        return False
    if total_carbs < carbs_min:
        return False
    if total_protein < protein_min:
        return False
    if total_fat > fat_max:
        return False
    if total_sugar > sugar_max:
        return False
    if total_salt > salt_max:
        return False
    if total_price > price_max:
        return False

    return True


def diet_generator(carbohydrates_min, sugar_max, energy_range, protein_min, salt_max, fat_max, price_max, person_type, person_preferences, selected_breakfasts, selected_lunches, selected_dinners, not_valid):
    BASE_DIR = os.path.dirname(os.path.abspath(__file__))
    db_path = os.path.join(BASE_DIR, './database', 'DietApp_Sprint2.db')
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    kcal_breakfast = (energy_range[0] * 0.2, energy_range[1] * 0.5)
    kcal_lunch = (energy_range[0] * 0.2, energy_range[1] * 0.5)
    kcal_dinner = (energy_range[0] * 0.2, energy_range[1] * 0.5)

    carbs_b = nutrient_quantity_kcal_split(carbohydrates_min, kcal_breakfast[0], energy_range[0])
    carbs_l = nutrient_quantity_kcal_split(carbohydrates_min, kcal_lunch[0], energy_range[0])
    carbs_d = nutrient_quantity_kcal_split(carbohydrates_min, kcal_dinner[0], energy_range[0])

    protein_b = nutrient_quantity_kcal_split(protein_min, kcal_breakfast[0], energy_range[0])
    protein_l = nutrient_quantity_kcal_split(protein_min, kcal_lunch[0], energy_range[0])
    protein_d = nutrient_quantity_kcal_split(protein_min, kcal_dinner[0], energy_range[0])

    sugar_b = nutrient_quantity_kcal_split(sugar_max, kcal_breakfast[1], energy_range[1])
    sugar_l = nutrient_quantity_kcal_split(sugar_max, kcal_lunch[1], energy_range[1])
    sugar_d = nutrient_quantity_kcal_split(sugar_max, kcal_dinner[1], energy_range[1])

    fat_b = nutrient_quantity_kcal_split(fat_max, kcal_breakfast[1], energy_range[1])
    fat_l = nutrient_quantity_kcal_split(fat_max, kcal_lunch[1], energy_range[1])
    fat_d = nutrient_quantity_kcal_split(fat_max, kcal_dinner[1], energy_range[1])

    salt_b = nutrient_quantity_kcal_split(salt_max, kcal_breakfast[1], energy_range[1])
    salt_l = nutrient_quantity_kcal_split(salt_max, kcal_lunch[1], energy_range[1])
    salt_d = nutrient_quantity_kcal_split(salt_max, kcal_dinner[1], energy_range[1])

    breakfasts = obtain_breakfast(cursor, selected_breakfasts,
                                 carbs_b, sugar_b, kcal_breakfast,
                                 protein_b, salt_b, fat_b, price_max / 4, person_type)
    lunches = obtain_lunch(cursor, selected_lunches,
                         carbs_l, sugar_l, kcal_lunch,
                         protein_l, salt_l, fat_l, price_max / 2, person_type)
    dinners = obtain_dinner(cursor, selected_dinners,
                           carbs_d, sugar_d, kcal_dinner,
                           protein_d, salt_d, fat_d, price_max / 4, person_type)


    if not breakfasts or not lunches or not dinners:
        conn.close()
        return None

    random.shuffle(breakfasts)
    random.shuffle(lunches)
    random.shuffle(dinners)

    for breakfast in breakfasts:
        for lunch in lunches:
            for dinner in dinners:
                solution = [breakfast, lunch, dinner]
                if validate_full_diet(solution, carbohydrates_min, sugar_max, energy_range, protein_min, salt_max,
                                      fat_max, price_max) and check_repetition(solution, selected_breakfasts, selected_lunches, selected_dinners):
                    conn.close()
                    print("Solución encontrada")
                    print(solution)
                    return solution

    conn.close()
    return None


