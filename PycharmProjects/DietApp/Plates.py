class Plate:

    def __init__(self, row):
        self.name = row[0]
        self.calories = row[1]
        self.carbohydrates = row[2]
        self.protein = row[3]
        self.fat = row[4]
        self.sugar = row[5]
        self.salt = row[6]
        self.price = row[7]
        self.food_type = row[8]
        self.vegan = row[9]
        self.vegetarian = row[10]
        self.celiac = row[11]

    def __repr__(self):
        return self.name

    def __str__(self):
        return f"{self.name}, {self.calories}, {self.carbohydrates}, {self.protein}, {self.fat}, {self.sugar}, {self.salt}, {self.price}, {self.food_type}, {self.vegan}, {self.vegetarian}, {self.celiac}"

    def __eq__(self, other):
        if not isinstance(other, Plate):
            return NotImplemented
        return self.to_dict() == other.to_dict()

    def __hash__(self):
        return hash((
            self.name, self.calories, self.carbohydrates, self.protein,
            self.fat, self.sugar, self.salt, self.price, self.food_type,
            self.vegan, self.vegetarian, self.celiac))

    def to_dict(self):
        return {
            "name": self.name,
            "calories": self.calories,
            "carbohydrates": self.carbohydrates,
            "protein": self.protein,
            "fat": self.fat,
            "sugar": self.sugar,
            "salt": self.salt,
            "price": self.price,
            "food_type": self.food_type,
            "vegan": self.vegan,
            "vegetarian": self.vegetarian,
            "celiac": self.celiac
        }

    @property
    def is_vegan(self):
        return self.vegan

    @property
    def is_vegetarian(self):
        return self.vegetarian

    @property
    def is_celiac(self):
        return self.celiac
