from enum import nonmember

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
    elif person_type == 5:
        return [plate for plate in plates if plate.halal == 1]
    return plates

def nutrient_quantity_kcal_split(nutrient_limit, kcal_meal_limit, kcal_total_limit):
    return nutrient_limit * (kcal_meal_limit / kcal_total_limit)

def obtain_breakfast(cursor, selected_breakfasts, carbohydrates, sugar, energy, protein, salt, fat, price, person_type):
    print()
    print("\n--------- PLATOS DISPOBIBLES DESAYUNO ---------")
    print(energy, carbohydrates, protein, fat, sugar, salt, price, person_type)
    breakfasts_rows = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 1, 1, -1, 2)
    breakfasts = [Plate(row) for row in breakfasts_rows]
    breakfasts = filter_plates(breakfasts, person_type)
    drinks_rows = sql_sentences(cursor, 0, sugar, [0, energy[1]], 0, salt, fat, price, 4, 1, -1, 1)
    drinks = [Plate(row) for row in drinks_rows]
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

                if (combined_carbs >= carbohydrates * 0.8 and
                        combined_sugar <= sugar * 1.2 and
                        energy[0] * 0.8 <= combined_energy <= energy[1] * 1.2 and
                        combined_protein >= protein * 0.8 and
                        combined_salt <= salt * 1.2 and
                        combined_fat <= fat * 1.2 and
                        combined_price <= price * 1.2):
                    valid_combinations.append((breakfast, drink))

    if valid_combinations:
        print(f"\nVALID_BREAKFASTS:")
        for combi in valid_combinations:
            print(combi)
        selected_combination = random.choice(valid_combinations)
        return selected_combination

    return None


def obtain_lunch(cursor, selected_lunches, carbohydrates, sugar, energy, protein, salt, fat, price, person_type):
    print("\n--------- PLATOS DISPONIBLES ALMUERZO ---------")
    print(energy, carbohydrates, protein, fat, sugar, salt, price, person_type)
    print()

    # Consulta directa para platos principales
    mains_rows = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 2, 1, -1, 2)
    mains = [Plate(row) for row in mains_rows]
    mains = filter_plates(mains, person_type)

    # Consulta directa para platos secundarios
    sides_rows = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 3, 1, -1, 2)
    sides = [Plate(row) for row in sides_rows]
    sides = filter_plates(sides, person_type)

    # Consulta directa para bebidas
    drinks_rows = sql_sentences(cursor, 0, sugar, [0, energy[1]], 0, salt, fat, price, 4, 1, -1, 1)
    drinks = [Plate(row) for row in drinks_rows]
    drinks = filter_plates(drinks, person_type)

    print(f'\nPRINCIPAL:')
    for main in mains:
        print(f"main: {main}")
    print("\nSECUNDARIO:")
    for side in sides:
        print(f"side: {side}")
    print(f'\nBEBIDA:')
    for drink in drinks:
        print(f"drink: {drink}")

    valid_combinations = []

    # Validación de combinaciones
    if mains and sides and drinks:
        for main in mains:
            for side in sides:
                for drink in drinks:
                    combination = (main, side, drink)

                    if combination in selected_lunches:
                        continue

                    # Calcular valores combinados
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
                        valid_combinations.append((main, side, drink))

    if valid_combinations:
        print(f"\nVALID_LUNCHES:")
        for combi in valid_combinations:
            print(combi)
        selected_combination = random.choice(valid_combinations)
        return selected_combination

    return None


def obtain_dinner(cursor, selected_dinners, carbohydrates, sugar, energy, protein, salt, fat, price, person_type):
    print()
    print("\n--------- PLATOS DISPONIBLES CENA ---------")
    print(energy, carbohydrates, protein, fat, sugar, salt, price, person_type)


    mains_rows = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 2, 1, -1, 2)
    mains2_rows = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, 3, 1, -1, 2)
    mains_rows.extend(mains2_rows)
    mains = [Plate(row) for row in mains_rows]
    mains = filter_plates(mains, person_type)

    drinks_rows = sql_sentences(cursor, 0, sugar, [0, energy[1]], 0, salt, fat, price, 4, 1, -1, 1)
    drinks = [Plate(row) for row in drinks_rows]
    drinks = filter_plates(drinks, person_type)

    print(f'\nPRINCIPAL:')
    for main in mains:
        print(f"main: {main}")
    print(f'\nBEBIDA:')
    for drink in drinks:
        print(f"drink: {drink}")

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

                if (combined_carbs >= carbohydrates * 0.8 and
                        combined_sugar <= sugar * 1.2 and
                        energy[0] * 0.8 <= combined_energy <= energy[1] * 1.2 and
                        combined_protein >= protein * 0.8 and
                        combined_salt <= salt * 1.2 and
                        combined_fat <= fat * 1.2 and
                        combined_price <= price * 1.2):
                    valid_combinations.append((main, drink))

    if valid_combinations:
        print(f"\nVALID_DINNERS:")
        for combi in valid_combinations:
            print(combi)
        selected_combination = random.choice(valid_combinations)
        return selected_combination

    return None


def validate_full_diet(diet, carbs_min, sugar_max, kcal_range, protein_min, salt_max, fat_max, price_max):
    total_carbs = total_sugar = total_kcal = total_protein = total_salt = total_fat = total_price = 0

    # Calcular los totales de las macros y el precio
    for meal in diet:
        for plate in meal:
            total_carbs += plate.carbohydrates
            total_sugar += plate.sugar
            total_kcal += plate.calories
            total_protein += plate.protein
            total_salt += plate.salt
            total_fat += plate.fat
            total_price += plate.price

    # Validaciones de límites
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

    print("\n--------- DIETA VÁLIDA ---------")
    print(f"Carbohidratos totales: {total_carbs} (Mínimo Requerido: {carbs_min})")
    print(f"Azúcar total: {total_sugar} (Máximo Permitido: {sugar_max})")
    print(f"Calorías totales: {total_kcal} (Rango: {kcal_range[0]} - {kcal_range[1]})")
    print(f"Proteínas totales: {total_protein} (Mínimo Requerido: {protein_min})")
    print(f"Sal total: {total_salt} (Máximo Permitido: {salt_max})")
    print(f"Grasas totales: {total_fat} (Máximo Permitido: {fat_max})")
    print(f"Precio total: {total_price} (Máximo Permitido: {price_max})")

    return True



def diet_generator(carbohydrates_min, sugar_max, energy_range, protein_min, salt_max, fat_max, price_max,
                   person_type, person_preferences, selected_breakfasts, selected_lunches, selected_dinners, not_valid):
    db_path = os.path.join('../../FoodDbManagement', 'DietApp.db')
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    solution = []

    print(f"\nValores de entrada resolver_dieta: {carbohydrates_min, sugar_max, energy_range, protein_min, salt_max, fat_max, price_max}")

    kcal_breakfast = (energy_range[0] * 0.20, energy_range[1] * 0.20)
    kcal_lunch = (energy_range[0] * 0.50, energy_range[1] * 0.50)
    kcal_dinner = (energy_range[0] * 0.30, energy_range[1] * 0.30)

    carbs_b = nutrient_quantity_kcal_split(carbohydrates_min, kcal_breakfast[0], energy_range[0])
    carbs_l = nutrient_quantity_kcal_split(carbohydrates_min, kcal_lunch[0], energy_range[0])
    carbs_d = nutrient_quantity_kcal_split(carbohydrates_min, kcal_dinner[0], energy_range[0])

    protein_b = nutrient_quantity_kcal_split(protein_min, kcal_breakfast[0], energy_range[0])
    protein_l = nutrient_quantity_kcal_split(protein_min, kcal_lunch[0], energy_range[0])
    protein_d = nutrient_quantity_kcal_split(protein_min, kcal_dinner[0], energy_range[0])

    # Macronutrientes con máximo → solo se calcula máximo por comida
    sugar_b = nutrient_quantity_kcal_split(sugar_max, kcal_breakfast[1], energy_range[1])
    sugar_l = nutrient_quantity_kcal_split(sugar_max, kcal_lunch[1], energy_range[1])
    sugar_d = nutrient_quantity_kcal_split(sugar_max, kcal_dinner[1], energy_range[1])

    fat_b = nutrient_quantity_kcal_split(fat_max, kcal_breakfast[1], energy_range[1])
    fat_l = nutrient_quantity_kcal_split(fat_max, kcal_lunch[1], energy_range[1])
    fat_d = nutrient_quantity_kcal_split(fat_max, kcal_dinner[1], energy_range[1])

    salt_b = nutrient_quantity_kcal_split(salt_max, kcal_breakfast[1], energy_range[1])
    salt_l = nutrient_quantity_kcal_split(salt_max, kcal_lunch[1], energy_range[1])
    salt_d = nutrient_quantity_kcal_split(salt_max, kcal_dinner[1], energy_range[1])

    breakfast = obtain_breakfast(cursor, selected_breakfasts,
                                 carbs_b, sugar_b, kcal_breakfast,
                                 protein_b, salt_b, fat_b, price_max / 4, person_type)

    lunch = obtain_lunch(cursor, selected_lunches,
                         carbs_l, sugar_l, kcal_lunch,
                         protein_l, salt_l, fat_l, price_max / 2, person_type)

    dinner = obtain_dinner(cursor, selected_dinners,
                           carbs_d, sugar_d, kcal_dinner,
                           protein_d, salt_d, fat_d, price_max / 4, person_type)

    if breakfast is None or lunch is None or dinner is None:
        conn.close()
        return None

    solution.extend([breakfast, lunch, dinner])

    if not validate_full_diet(solution, carbohydrates_min, sugar_max, energy_range, protein_min, salt_max, fat_max, price_max):
        print("La dieta generada no cumple con los requisitos totales.")
        if solution in not_valid:
            return None
        not_valid.append(solution)
        return diet_generator(carbohydrates_min, sugar_max, energy_range, protein_min, salt_max, fat_max, price_max,
                              person_type, person_preferences, selected_breakfasts, selected_lunches, selected_dinners, not_valid)

    conn.close()
    return solution

