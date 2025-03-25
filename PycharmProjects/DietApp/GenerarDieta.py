import sqlite3
import random
import os


def sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, food_type, mult, sentence_type, sub_sentence, sub_type):

    print(carbohydrates, sugar, energy, protein, salt, fat, budget, food_type, mult)

    if sentence_type == 1:
        sql = ("SELECT * FROM plates WHERE type == ? AND "
               "carbohydrates * ? BETWEEN ? AND ? "
               "AND sugar * ? BETWEEN ? AND ? "
               "AND calories * ? BETWEEN ? AND ? "
               "AND protein * ? BETWEEN ? AND ? "
               "AND sodium * ? BETWEEN ? AND ? "
               "AND fats * ? BETWEEN ? AND ? "
               "AND price * ? <= ?")

        cursor.execute(sql, (food_type,
                             mult, carbohydrates[0], carbohydrates[1],
                             mult, sugar[0], sugar[1],
                             mult, energy[0], energy[1],
                             mult, protein[0], protein[1],
                             mult, salt[0], salt[1],
                             mult, fat[0], fat[1],
                             mult, budget))
        return cursor.fetchall()

    elif sentence_type == 2:
        sql = "SELECT SUM(?) FROM plates WHERE type ?"
        nutritional = ""

        if sub_sentence == 1:
            nutritional = "carbohydrates"

        elif sub_sentence == 2:
            nutritional = "sugar"

        elif sub_sentence == 3:
            nutritional = "calories"

        cursor.execute(sql, (nutritional, sub_type))


def obtain_breakfast(cursor, selected_breakfasts, carbohydrates, sugar, energy, protein, salt, fat, budget):

    print("\n----------DESAYUNO--------------")
    valid = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 1, 3)
    print(f'\nSentencia de desayuno: {valid}')

    if valid:
        valid_foods = []
        for food in valid:
            if food not in selected_breakfasts:
                valid_foods.append(food)

        if valid_foods:
            selected = random.choice(valid_foods)
            selected_breakfasts.add(selected)
            return selected

    return None


def obtain_lunch(cursor, selected_lunches, carbohydrates, sugar, energy, protein, salt, fat, budget):

    print("\n--------------ALMUERZO-------------------")
    print("PRINCIPAL:")
    mains = sql_sentences(cursor, [0.4 * carbohydrates[0], 0.4 * carbohydrates[1]], [0.4 * sugar[0], 0.4 * sugar[1]],
                          [0.4 * energy[0], 0.4 * energy[1]], [0.4 * protein[0], 0.4 * protein[1]], [0.4 * salt[0], 0.4 * salt[1]],
                          [0.4 * fat[0], 0.4 * fat[1]], budget, 2, 3)
    print("SECUNDARIO")
    sides = sql_sentences(cursor, [0.4 * carbohydrates[0], 0.4 * carbohydrates[1]], [0.4 * sugar[0], 0.4 * sugar[1]],
                          [0.4 * energy[0], 0.4 * energy[1]], [0.4 * protein[0], 0.4 * protein[1]], [0.4 * salt[0], 0.4 * salt[1]],
                          [0.4 * fat[0], 0.4 * fat[1]], budget, 3, 2)
    print("BEBIDA")
    drinks = sql_sentences(cursor, [0, carbohydrates[1]], [0, sugar[1]], [0, energy[1]], [0, protein[1]], [0, salt[1]], [0, fat[1]], budget, 4, 1)
    print("POSTRE")
    desserts = sql_sentences(cursor, [0.2 * carbohydrates[0], 0.2 * carbohydrates[1]], [0.2 * sugar[0], 0.2 * sugar[1]],
                          [0.2 * energy[0], 0.2 * energy[1]], [0.2 * protein[0], 0.2 * protein[1]], [0.2 * salt[0], 0.2 * salt[1]],
                          [0.2 * fat[0], 0.2 * fat[1]], budget, 5, 1)

    print(f'\ntipo2 almuerzo: {mains}')
    print(f'tipo3 almuerzo: {sides}')
    print(f'tipo4 almuerzo: {drinks}')
    print(f'tipo5 almuerzo: {desserts}')

    valid_combinations = []

    if mains and sides and drinks and desserts:
        for main in mains:
            for side in sides:
                for drink in drinks:
                    for dessert in desserts:
                        combination = (main[1], side[1], drink[1], dessert[1])

                        if combination in selected_lunches:
                            continue

                        combined_carbs = drink[2] + side[2] + main[2] + dessert[2]
                        combined_sugar = drink[5] + side[5] + main[5] + dessert[5]
                        combined_energy = drink[1] + side[1] + main[1] + dessert[1]
                        combined_protein = drink[3] + side[3] + main[3] + dessert[3]
                        combined_salt = drink[6] + side[6] + main[6] + dessert[6]
                        combined_fat = drink[4] + side[4] + main[4] + dessert[4]
                        combined_price = drink[7] + side[7] + main[7] + dessert[7]

                        if (carbohydrates[0] <= combined_carbs <= carbohydrates[1] and
                                sugar[0] <= combined_sugar <= sugar[1] and
                                energy[0] <= combined_energy <= energy[1] and
                                protein[0] <= combined_protein <= protein[1] and
                                salt[0] <= combined_salt <= salt[1] and
                                fat[0] <= combined_fat <= fat[1] and
                                combined_price <= budget):

                            valid_combinations.append((main[0], side[0], drink[0], dessert[0]))

    if valid_combinations:
        selected_combination = random.choice(valid_combinations)
        selected_lunches.add(selected_combination)
        return selected_combination

    return None


def obtain_dinner(cursor, selected_dinners, carbohydrates, sugar, energy, protein, salt, fat, budget):

    print("\n---------CENA----------")
    print("PRINCIPAL")
    mains = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 2, 3)
    print("SECUNDARIO")
    drinks = sql_sentences(cursor, [0, carbohydrates[1]], [0, sugar[1]], [0, energy[1]], [0, protein[1]], [0, salt[1]], [0, fat[1]], budget, 4, 1)

    print(f'\ntipo2 cena: {mains}')
    print(f'tipo4 cena: {drinks}')

    valid_combinations = []

    if mains and drinks:
        for main in mains:
            for drink in drinks:
                combination = (main[1], drink[1])

                if combination in selected_dinners:
                    continue

                combined_carbs = main[2] + drink[2]
                combined_sugar = main[5] + drink[2]
                combined_energy = main[1] + drink[1]
                combined_protein = main[3] + drink[3]
                combined_salt = main[6] + drink[6]
                combined_fat = main[4] + drink[4]
                combined_price = main[7] + drink[7]

                if (carbohydrates[0] <= combined_carbs <= carbohydrates[1] and
                        sugar[0] <= combined_sugar <= sugar[1] and
                        energy[0] <= combined_energy <= energy[1] and
                        protein[0] <= combined_protein <= protein[1] and
                        salt[0] <= combined_salt <= salt[1] and
                        fat[0] <= combined_fat <= fat[1] and
                        combined_price <= budget):

                    valid_combinations.append((main[0], drink[0]))

    print(valid_combinations)
    if valid_combinations:
        selected_dinner = random.choice(valid_combinations)
        selected_dinners.add(selected_dinner)
        return selected_dinner

    return None


# Calcular el costo y las características de una combinación de comidas
def get_total_cost_and_features(comidas_seleccionadas):
    total_cost = sum([comida[5] for comida in comidas_seleccionadas])
    total_energy = sum([comida[1] for comida in comidas_seleccionadas])
    total_fat = sum([comida[4] for comida in comidas_seleccionadas])
    total_salt = sum([comida[3] for comida in comidas_seleccionadas])
    total_protein = sum([comida[2] for comida in comidas_seleccionadas])
    return total_cost, total_energy, total_fat, total_salt, total_protein


def obtener_valores(cursor):
    print("-------- DATOS DESAYUNO --------")
    sql = "SELECT SUM(carbohydrates) FROM plates WHERE type IN(1, 4)"
    cursor.execute(sql)
    carbsDes = cursor.fetchone()[0]
    print(f'carbos: {carbsDes}')
    sql = "SELECT SUM(sugar) FROM plates WHERE type IN(1, 4)"
    cursor.execute(sql)
    sugarDes = cursor.fetchone()[0]
    print(f'azucar: {sugarDes}')
    sql = "SELECT SUM(calories) FROM plates WHERE type IN(1, 4)"
    cursor.execute(sql)
    calorDes = cursor.fetchone()[0]
    print(f'calorias: {calorDes}')
    sql = "SELECT SUM(protein) FROM plates WHERE type IN(1, 4)"
    cursor.execute(sql)
    proteDes = cursor.fetchone()[0]
    print(f'proteinas: {proteDes}')
    sql = "SELECT SUM(sodium) FROM plates WHERE type IN(1, 4)"
    cursor.execute(sql)
    salesDes = cursor.fetchone()[0]
    print(f'sales: {salesDes}')
    sql = "SELECT SUM(fats) FROM plates WHERE type IN(1, 4)"
    cursor.execute(sql)
    fatsDes = cursor.fetchone()[0]
    print(f'grasas: {fatsDes}')
    sql = "SELECT SUM(price) FROM plates WHERE type IN(1, 4)"
    cursor.execute(sql)
    priceDes = cursor.fetchone()[0]
    print(f'precio: {priceDes}')

    print("\n-------- DATOS ALMUERZO --------")
    sql = "SELECT SUM(carbohydrates) FROM plates WHERE type IN(2, 3, 4, 5)"
    cursor.execute(sql)
    carbsAlmuerzo = cursor.fetchone()[0]
    print(f'carbos: {carbsAlmuerzo}')
    sql = "SELECT SUM(sugar) FROM plates WHERE type IN(2, 3, 4, 5)"
    cursor.execute(sql)
    sugarAlmuerzo = cursor.fetchone()[0]
    print(f'azucar: {sugarAlmuerzo}')
    sql = "SELECT SUM(calories) FROM plates WHERE type IN(2, 3, 4, 5)"
    cursor.execute(sql)
    calorAlmuerzo = cursor.fetchone()[0]
    print(f'calorias: {calorAlmuerzo}')
    sql = "SELECT SUM(protein) FROM plates WHERE type IN(2, 3, 4, 5)"
    cursor.execute(sql)
    proteAlmuerzo = cursor.fetchone()[0]
    print(f'proteinas: {proteAlmuerzo}')
    sql = "SELECT SUM(sodium) FROM plates WHERE type IN(2, 3, 4, 5)"
    cursor.execute(sql)
    salesAlmuerzo = cursor.fetchone()[0]
    print(f'sales: {salesAlmuerzo}')
    sql = "SELECT SUM(fats) FROM plates WHERE type IN(2, 3, 4, 5)"
    cursor.execute(sql)
    fatsAlmuerzo = cursor.fetchone()[0]
    print(f'grasas: {fatsAlmuerzo}')
    sql = "SELECT SUM(price) FROM plates WHERE type IN(2, 3, 4, 5)"
    cursor.execute(sql)
    priceAlmuerzo = cursor.fetchone()[0]
    print(f'precio: {priceAlmuerzo}')

    print("\n-------- DATOS CENA --------")
    sql = "SELECT SUM(carbohydrates) FROM plates WHERE type IN(2, 4)"
    cursor.execute(sql)
    carbsCena = cursor.fetchone()[0]
    print(f'carbos: {carbsCena}')
    sql = "SELECT SUM(sugar) FROM plates WHERE type IN(2, 4)"
    cursor.execute(sql)
    sugarCena = cursor.fetchone()[0]
    print(f'azucar: {sugarCena}')
    sql = "SELECT SUM(calories) FROM plates WHERE type IN(2, 4)"
    cursor.execute(sql)
    calorCena = cursor.fetchone()[0]
    print(f'calorias: {calorCena}')
    sql = "SELECT SUM(protein) FROM plates WHERE type IN(2, 4)"
    cursor.execute(sql)
    proteCena = cursor.fetchone()[0]
    print(f'proteinas: {proteCena}')
    sql = "SELECT SUM(sodium) FROM plates WHERE type IN(2, 4)"
    cursor.execute(sql)
    salesCena = cursor.fetchone()[0]
    print(f'sales: {salesCena}')
    sql = "SELECT SUM(fats) FROM plates WHERE type IN(2, 4)"
    cursor.execute(sql)
    fatsCena = cursor.fetchone()[0]
    print(f'grasas: {fatsCena}')
    sql = "SELECT SUM(price) FROM plates WHERE type IN(2, 4)"
    cursor.execute(sql)
    priceCena = cursor.fetchone()[0]
    print(f'precio: {priceCena}')

    print("\n-------- DATOS TOTALES --------")
    carbsTotal = carbsDes + carbsAlmuerzo + carbsCena
    print(f'carbos: {carbsTotal}')
    sugarTotal = sugarDes + sugarAlmuerzo + sugarCena
    print(f'azucar: {sugarTotal}')
    calorTotal = calorDes + calorAlmuerzo + calorCena
    print(f'calorias: {calorTotal}')
    proteTotal = proteDes + proteAlmuerzo + proteCena
    print(f'proteinas: {proteTotal}')
    salesTotal = salesDes + salesAlmuerzo + salesCena
    print(f'sales: {salesTotal}')
    fatsTotal = fatsDes + fatsAlmuerzo + fatsCena
    print(f'grasas: {fatsTotal}')
    priceTotal = priceDes + priceAlmuerzo + priceCena
    print(f'precio: {priceTotal}')

    print("\n-------- PLATOS PRINCIPALES --------")
    sql = "SELECT SUM(carbohydrates) FROM plates WHERE type == 2"
    cursor.execute(sql)
    print(f'carbos: {cursor.fetchone()[0]}')
    sql = "SELECT SUM(sugar) FROM plates WHERE type == 2"
    cursor.execute(sql)
    print(f'azucar: {cursor.fetchone()[0]}')
    sql = "SELECT SUM(calories) FROM plates WHERE type == 2"
    cursor.execute(sql)
    print(f'calorias: {cursor.fetchone()[0]}')
    sql = "SELECT SUM(protein) FROM plates WHERE type == 2"
    cursor.execute(sql)
    print(f'proteinas: {cursor.fetchone()[0]}')
    sql = "SELECT SUM(sodium) FROM plates WHERE type IN(2, 4)"
    cursor.execute(sql)
    salesCena = cursor.fetchone()[0]
    print(f'sales: {salesCena}')
    sql = "SELECT SUM(fats) FROM plates WHERE type IN(2, 4)"
    cursor.execute(sql)
    fatsCena = cursor.fetchone()[0]
    print(f'grasas: {fatsCena}')
    sql = "SELECT SUM(price) FROM plates WHERE type IN(2, 4)"
    cursor.execute(sql)
    priceCena = cursor.fetchone()[0]
    print(f'precio: {priceCena}')

    return 1


def resolver_dieta(carbohydrates, sugar, energy, protein, salt, fat, budget, person_type, selected_breakfasts, selected_lunches, selected_dinners):
    db_path = os.path.join('../../FoodDbManagement', 'plates.db')

    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    solution = []

    print("\n")
    obtener_valores(cursor)
    print("\n")

    print("\n CARBOHIDRATOS - AZUCARES - CALORIAS - PROTEINAS - SALES - GRASAS - PRECIO")
    print(f"\nValores de entrada resolver_dieta: {carbohydrates, sugar, energy, protein, salt, fat, budget}")



    breakfast = obtain_breakfast(cursor, selected_breakfasts,
                                 [carbohydrates[0] * 0.55, carbohydrates[1] * 0.55],
                                 [sugar[0] * 0.45, sugar[1] * 0.45],
                                 [energy[0] * 0.275, energy[1] * 0.275],
                                 [protein[0] * 0.175, protein[1] * 0.175],
                                 [salt[0] * 0.225, salt[1] * 0.225],
                                 [fat[0] * 0.225, fat[1] * 0.225],
                                 budget * 0.33)

    lunch = obtain_lunch(cursor, selected_lunches,
                                 [carbohydrates[0] * 0.375, carbohydrates[1] * 0.375],
                                 [sugar[0] * 0.325, 99999.0],
                                 [energy[0] * 0.425, energy[1] * 0.425],
                                 [protein[0] * 0.325, protein[1] * 0.325],
                                 [salt[0] * 0.45, salt[1] * 0.45],
                                 [fat[0] * 0.275, fat[1] * 0.275],
                                 budget * 0.34)

    dinner = obtain_dinner(cursor, selected_dinners,
                                 [carbohydrates[0] * 0.175, carbohydrates[1] * 0.175],
                                 [sugar[0] * 0.175, sugar[1] * 0.175],
                                 [energy[0] * 0.275, energy[1] * 0.275],
                                 [protein[0] * 0.325, protein[1] * 0.325],
                                 [salt[0] * 0.275, salt[1] * 0.275],
                                 [fat[0] * 0.375, fat[1] * 0.375],
                                 budget * 0.33)

    if breakfast is None or lunch is None or dinner is None:
        return None

    solution.append(breakfast[0])
    solution.append(lunch)
    solution.append(dinner)

    return solution
