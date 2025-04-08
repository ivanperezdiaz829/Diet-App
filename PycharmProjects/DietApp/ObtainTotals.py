from GenerarDieta import *
from Plates import *


def total_diet_generator(carbohydrates, sugar, energy, protein, salt, fat, price, person_type, person_preferences, total_days):

    selected_breakfasts, selected_lunches, selected_dinners = set(), set(), set()
    total_diet = []
    solution = []
    for i in range(total_days):
        total_diet.append(diet_generator(carbohydrates, sugar, energy, protein, salt, fat, price, person_type, person_preferences, selected_breakfasts, selected_lunches, selected_dinners))

    print("\n")
    for i in range(len(total_diet)):
        print(f"\nDía {i+1}:")
        for comida in total_diet[i]:
            print(comida)
            solution.append(comida)

    return solution
