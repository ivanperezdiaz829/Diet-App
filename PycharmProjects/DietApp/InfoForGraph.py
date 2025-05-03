class GraphData:

    def __init__(self, plate_data, mult):
        self.name = plate_data.get('name', '')
        self.calories = plate_data.get('calories', 0) * mult
        self.carbohydrates = plate_data.get('carbohydrates', 0) * mult
        self.protein = plate_data.get('protein', 0) * mult
        self.fat = plate_data.get('fat', 0) * mult
        self.sugar = plate_data.get('sugar', 0) * mult
        self.salt = plate_data.get('salt', 0) * mult
        self.price = plate_data.get('price', 0) * mult
        self.quantity = mult

    def to_dict(self):
        return {
            'name': self.name,
            'calories': self.calories,
            'carbohydrates': self.carbohydrates,
            'protein': self.protein,
            'fat': self.fat,
            'sugar': self.sugar,
            'salt': self.salt,
            'price': self.price,
            'quantity': self.quantity
        }