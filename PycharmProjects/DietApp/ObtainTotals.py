from GenerarDieta import *


def total_diet_generator(carbohydrates, sugar, energy, protein, salt, fat, price, person_type, person_preferences, total_days):

    selected_breakfasts, selected_lunches, selected_dinners, not_valid = set(), set(), set(), []
    total_diet = []
    solution = []
    for i in range(total_days):
        total_diet.append(diet_generator(carbohydrates, sugar, energy, protein, salt, fat, price, person_type, person_preferences, selected_breakfasts, selected_lunches, selected_dinners, not_valid))

    print("\n")
    for i in range(len(total_diet)):
        print(f"\nDía {i+1}:")
        for comida in total_diet[i]:
            print(comida)
            solution.append(comida)

    return solution


def nutritional_values_day(diet_day):

    calories, carbohydrates, protein, fat, sugar, salt, price = 0, 0, 0, 0, 0, 0, 0
    for food_group in diet_day:
        for food in food_group:
            calories += food.calories
            carbohydrates += food.carbohydrates
            protein += food.protein
            fat += food.fat
            sugar += food.sugar
            salt += food.salt
            price += food.price

    return calories, carbohydrates, protein, fat, sugar, salt/1000, price


def total_nutritional_values(diet):
    total_calories, total_carbohydrates, total_protein, total_fat, total_sugar, total_salt, total_price  = 0, 0, 0, 0, 0, 0, 0

    for day in diet:
        calories, carbohydrates, protein, fat, sugar, salt, price = nutritional_values_day(day)
        total_calories += calories
        total_carbohydrates += carbohydrates
        total_protein += protein
        total_fat += fat
        total_sugar += sugar
        total_salt += salt
        total_price += price

    return total_calories, total_carbohydrates, total_protein, total_fat, total_sugar, total_salt, total_price
