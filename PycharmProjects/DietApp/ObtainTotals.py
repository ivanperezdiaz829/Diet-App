from DietGenerator import *

def total_diet_generator(carbohydrates, sugar, energy, protein, salt, fat, price, person_type, person_preferences, total_days):

    selected_breakfasts, selected_lunches, selected_dinners, not_valid = set(), set(), set(), []
    total_diet = []
    solution = []

    for i in range(total_days):
        daily_meals = diet_generator(carbohydrates, sugar, energy, protein, salt, fat, price, person_type, person_preferences, selected_breakfasts, selected_lunches, selected_dinners, not_valid)
        total_diet.append(daily_meals)

    """
        for comida in daily_meals:
            if isinstance(comida, (list, tuple)):  # lista o tupla
                solution.extend(comida)
            else:
                solution.append(comida)

    for plate in solution:
        print(f"Tipo: {type(plate)}, Valor: {plate}")

    return [plate.to_dict() for plate in solution]
    """

    return total_diet


def nutritional_values_total(diet_total):

    calories, carbohydrates, protein, fat, sugar, salt, price = 0, 0, 0, 0, 0, 0, 0
    for food_group in diet_total:
        for food in food_group:
            for plate in food:
                calories += plate.calories
                carbohydrates += plate.carbohydrates
                protein += plate.protein
                fat += plate.fat
                sugar += plate.sugar
                salt += plate.salt
                price += plate.price

    return calories, carbohydrates, protein, fat, sugar, salt/1000, price


def nutritional_values_day(diet_day):

    calories, carbohydrates, protein, fat, sugar, salt, price = 0, 0, 0, 0, 0, 0, 0
    for food in diet_day:
        for plate in food:
            calories += plate.calories
            carbohydrates += plate.carbohydrates
            protein += plate.protein
            fat += plate.fat
            sugar += plate.sugar
            salt += plate.salt
            price += plate.price

    return calories, carbohydrates, protein, fat, sugar, salt/1000, price
