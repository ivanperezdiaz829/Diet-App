class Plate:

    def __init__(self, row, mult):
        # Si los valores son None, los reemplazamos con 0 (o algún valor predeterminado adecuado)
        self.name = row[1]
        self.calories = row[3] * mult
        self.carbohydrates = row[4] * mult
        self.protein = row[5] * mult
        self.fat = row[6] * mult
        self.sugar = row[7] * mult
        self.salt = row[8] * mult
        self.price = row[9] * mult
        self.food_type = row[10]
        self.vegan = row[11]
        self.vegetarian = row[12]
        self.celiac = row[13]
        self.halal = row[14]

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

    def str(self):
        return f"{self.name}, {self.calories}, {self.carbohydrates}, {self.protein}, {self.fat}, {self.sugar}, {self.salt}, {self.price}, {self.food_type}, {self.vegan}, {self.vegetarian}, {self.celiac}, {self.halal}"

    def eq(self, other):
        if not isinstance(other, Plate):
            return NotImplemented
        return self.to_dict() == other.to_dict()

    def hash(self):
        return hash(self.name)

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
            "celiac": self.celiac,
            "halal": self.halal
        }
