import sqlite3
import random
import os


def sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, food_type, mult, sentence_type, sub_sentence):

    print(carbohydrates, sugar, energy, protein, salt, fat, budget, food_type, mult, sentence_type, sub_sentence)
    if sentence_type == 1:
        sql = ("SELECT * FROM plates WHERE type == ? AND "
               "carbohydrates BETWEEN ? AND ? "
               "AND sugar BETWEEN ? AND ? "
               "AND calories BETWEEN ? AND ? "
               "AND protein BETWEEN ? AND ? "
               "AND sodium BETWEEN ? AND ? "
               "AND fats BETWEEN ? AND ? "
               "AND price <= ?")

        cursor.execute(sql, (food_type,
                             carbohydrates[0], carbohydrates[1],
                             sugar[0], sugar[1],
                             energy[0], energy[1],
                             protein[0], protein[1],
                             salt[0], salt[1],
                             fat[0], fat[1],
                             budget))
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
            cursor.execute(sql, (food_type,))
            return cursor.fetchone()[0]

        return None


def percents_generator_food(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, food_time, sub_sentence):

    result = []
    if food_time == 1:
        breakfast1 = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 1, -1, 2, sub_sentence)
        breakfast4 = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 4, -1, 2, sub_sentence)
        breakfast_total = breakfast1 + breakfast4
        result.append(breakfast_total)
        breakfast1_percent = breakfast1 / breakfast_total
        result.append(breakfast1_percent)
        breakfast4_percent = breakfast4 / breakfast_total
        result.append(breakfast4_percent)
        print(f"PORCENTAJE PRINCIPAL DESAYUNO = {breakfast1_percent}")
        print(f"PORCENTAJE BEBIDA DESAYUNO = {breakfast4_percent}")

    elif food_time == 2:
        lunch2 = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 2, -1, 2, sub_sentence)
        lunch3 = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 3, -1, 2, sub_sentence)
        lunch4 = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 4, -1, 2, sub_sentence)
        lunch5 = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 5, -1, 2, sub_sentence)
        lunch_total = lunch2 + lunch3 + lunch4 + lunch5
        result.append(lunch_total)
        lunch2_percent = lunch2 / lunch_total
        result.append(lunch2_percent)
        lunch3_percent = lunch3 / lunch_total
        result.append(lunch3_percent)
        lunch4_percent = lunch4 / lunch_total
        result.append(lunch4_percent)
        lunch5_percent = lunch5 / lunch_total
        result.append(lunch5_percent)
        print(f"\nPORCENTAJE PRINCIPAL ALMUERZO: {lunch2_percent}")
        print(f"PORCENTAJE SECUNDARIO ALMUERZO: {lunch3_percent}")
        print(f"PORCENTAJE BEBIDA ALMUERZO: {lunch4_percent}")
        print(f"PORCENTAJE POSTRE ALMUERZO: {lunch5_percent}")

    elif food_time == 3:
        dinner2 = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 2, -1, 2, sub_sentence)
        dinner4 = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 4, -1, 2, sub_sentence)
        dinner_total = dinner2 + dinner4
        result.append(dinner_total)
        dinner2_percent = dinner2 / dinner_total
        result.append(dinner2_percent)
        dinner4_percent = dinner4 / dinner_total
        result.append(dinner4_percent)
        print(f"\nPORCENTAJE PRINCIPAL CENA = {dinner2_percent}")
        print(f"PORCENTAJE BEBIDA CENA = {dinner4_percent}")

    return result


def percents_generator_day(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, sub_sentence):

    solution = []
    breakfast_total = percents_generator_food(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 1, sub_sentence)[0]
    lunch_total = percents_generator_food(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 2, sub_sentence)[0]
    dinner_total = percents_generator_food(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 3, sub_sentence)[0]
    total = breakfast_total + lunch_total + dinner_total

    breakfast_percent = breakfast_total / total
    solution.append(breakfast_percent)
    lunch_percent = lunch_total / total
    solution.append(lunch_percent)
    dinner_percent = dinner_total / total
    solution.append(dinner_percent)

    print(f"\nPORCENTAJE DEL DÍA EN DESAYUNO = {breakfast_percent}")
    print(f"PORCENTAJE DEL DÍA EN ALMUERZO = {lunch_percent}")
    print(f"PORCENTAJE DEL DÍA EN CENA: {dinner_percent}")
    return solution


def obtain_breakfast(cursor, selected_breakfasts, carbohydrates, sugar, energy, protein, salt, fat, budget):

    print("\n----------DESAYUNO--------------")
    valid = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 1, 2, 1, -1)
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
    nutritional_percents = []
    for i in range(1, 8):
        nutritional_percents.append(percents_generator_food(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 2, i)[1:])
    print(nutritional_percents)
    print("\nCARBOHIDRATOS - AZUCARES - CALORIAS - PROTEINAS - SALES - GRASAS - PRECIO")
    print("\nPRINCIPAL:")
    mains = sql_sentences(cursor, [nutritional_percents[0][0] * carbohydrates[0], nutritional_percents[0][0] * carbohydrates[1]],
                          [nutritional_percents[1][0] * sugar[0], nutritional_percents[1][0] * sugar[1]],
                          [nutritional_percents[2][0] * energy[0], nutritional_percents[2][0] * energy[1]],
                          [nutritional_percents[3][0] * protein[0], nutritional_percents[3][0] * protein[1]],
                          [nutritional_percents[4][0] * salt[0], nutritional_percents[4][0] * salt[1]],
                          [nutritional_percents[5][0] * fat[0], nutritional_percents[5][0] * fat[1]],
                          nutritional_percents[6][0] * budget, 2, 2, 1, -1)
    print(f'tipo2 almuerzo: {mains}')

    print("\nSECUNDARIO:")
    sides = sql_sentences(cursor, [nutritional_percents[0][1] * carbohydrates[0], nutritional_percents[0][1] * carbohydrates[1]],
                          [nutritional_percents[1][1] * sugar[0], nutritional_percents[1][1] * sugar[1]],
                          [nutritional_percents[2][1] * energy[0], nutritional_percents[2][1] * energy[1]],
                          [nutritional_percents[3][1] * protein[0], nutritional_percents[3][1] * protein[1]],
                          [nutritional_percents[4][1] * salt[0], nutritional_percents[4][1] * salt[1]],
                          [nutritional_percents[5][1] * fat[0], nutritional_percents[5][1] * fat[1]],
                          nutritional_percents[6][1] * budget, 2, 2, 1, -1)
    print(f'tipo3 almuerzo: {sides}')

    print("\nBEBIDA:")
    drinks = sql_sentences(cursor, [nutritional_percents[0][2] * carbohydrates[0], nutritional_percents[0][2] * carbohydrates[1]],
                          [nutritional_percents[1][2] * sugar[0], nutritional_percents[1][2] * sugar[1]],
                          [nutritional_percents[2][2] * energy[0], nutritional_percents[2][2] * energy[1]],
                          [nutritional_percents[3][2] * protein[0], nutritional_percents[3][2] * protein[1]],
                          [nutritional_percents[4][2] * salt[0], nutritional_percents[4][2] * salt[1]],
                          [nutritional_percents[5][2] * fat[0], nutritional_percents[5][2] * fat[1]],
                          nutritional_percents[6][2] * budget, 2, 1, 1, -1)
    print(f'tipo4 almuerzo: {drinks}')

    print("\nPOSTRE:")
    desserts = sql_sentences(cursor, [nutritional_percents[0][3] * carbohydrates[0], nutritional_percents[0][3] * carbohydrates[1]],
                          [nutritional_percents[1][3] * sugar[0], nutritional_percents[1][3] * sugar[1]],
                          [nutritional_percents[2][3] * energy[0], nutritional_percents[2][3] * energy[1]],
                          [nutritional_percents[3][3] * protein[0], nutritional_percents[3][3] * protein[1]],
                          [nutritional_percents[4][3] * salt[0], nutritional_percents[4][3] * salt[1]],
                          [nutritional_percents[5][3] * fat[0], nutritional_percents[5][3] * fat[1]],
                          nutritional_percents[6][3] * budget, 2, 1, 1, -1)
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
    mains = sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 2, 3, 1, -1)
    print("SECUNDARIO")
    drinks = sql_sentences(cursor, [0, carbohydrates[1]], [0, sugar[1]], [0, energy[1]], [0, protein[1]], [0, salt[1]], [0, fat[1]], budget, 4, 1, 1, -1)

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


def diet_generator(carbohydrates, sugar, energy, protein, salt, fat, budget, person_type, selected_breakfasts, selected_lunches, selected_dinners):
    db_path = os.path.join('../../FoodDbManagement', 'plates.db')

    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()
    solution = []

    print("\n")
    obtener_valores(cursor)
    print("\n")

    print("\n")
    a = percents_generator_food(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 1, 1)
    print(a)
    print("\n")

    print("\nGENERADOR DÍA")
    a = percents_generator_day(cursor, carbohydrates, sugar, energy, protein, salt, fat, budget, 1)
    print(a)
    print("\n")

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
