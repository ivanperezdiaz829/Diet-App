class Plate:

    def __init__(self, row, mult):
        # Si los valores son None, los reemplazamos con 0 (o algún valor predeterminado adecuado)
        self.name = row["name"]
        self.calories = (row["calories"] if row["calories"] is not None else 0) * mult
        self.carbohydrates = (row["carbohydrates"] if row["carbohydrates"] is not None else 0) * mult
        self.protein = (row["protein"] if row["protein"] is not None else 0) * mult
        self.fat = (row["fat"] if row["fat"] is not None else 0) * mult
        self.sugar = (row["sugar"] if row["sugar"] is not None else 0) * mult
        self.salt = (row["salt"] if row["salt"] is not None else 0) * mult
        self.price = (row["price"] if row["price"] is not None else 0) * mult
        self.food_type = row["food_type"] if row.get("food_type") is not None else ""
        self.vegan = row["vegan"] if row.get("vegan") is not None else False
        self.vegetarian = row["vegetarian"] if row.get("vegetarian") is not None else False
        self.celiac = row["celiac"] if row.get("celiac") is not None else False
        self.halal = row["halal"] if row.get("halal") is not None else False

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
