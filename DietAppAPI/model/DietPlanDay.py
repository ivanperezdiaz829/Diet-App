class DietPlanDay:
    def __init__(self,
                 breakfast_dish: int,
                 breakfast_drink: int,
                 lunch_main_dish: int,
                 lunch_side_dish: int,
                 lunch_drink: int,
                 dinner_dish: int,
                 dinner_drink: int,
                 ):

        self.breakfast_dish = self._validate_meal_id(breakfast_dish)
        self.breakfast_drink = self._validate_meal_id(breakfast_drink)
        self.lunch_main_dish = self._validate_meal_id(lunch_main_dish)
        self.lunch_side_dish = self._validate_meal_id(lunch_side_dish)
        self.lunch_drink = self._validate_meal_id(lunch_drink)
        self.dinner_dish = self._validate_meal_id(dinner_dish)
        self.dinner_drink = self._validate_meal_id(dinner_drink)

    def _validate_meal_id(self, meal_id: int) -> int:
        if not isinstance(meal_id, int):
            raise ValueError("El ID del meal debe ser un número entero")

        if meal_id <= 0:
            raise ValueError("El ID del meal debe ser un número positivo")
        return meal_id

    def to_tuple(self):
        """Returns all meal IDs as a tuple in the order they were defined."""
        return (
            self.breakfast_dish,
            self.breakfast_drink,
            self.lunch_main_dish,
            self.lunch_side_dish,
            self.lunch_drink,
            self.dinner_dish,
            self.dinner_drink
        )