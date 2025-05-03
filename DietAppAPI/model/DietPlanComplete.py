import re
from typing import Optional

class DietPlanComplete:
    def __init__(self,
                 name: str,
                 user_id: int,
                 duration: int,
                 diet_type_id: int,
                 day1: int,
                 day2: Optional[int] = None,
                 day3: Optional[int] = None,
                 day4: Optional[int] = None,
                 day5: Optional[int] = None,
                 day6: Optional[int] = None,
                 day7: Optional[int] = None,
                 ):
        self.name = self.validate_name(name)
        self.user_id = self.validate_user_id(user_id)
        self.duration = self.validate_duration(duration)
        self.diet_type_id = self.validate_diet_type(diet_type_id)
        self.day1 = self.validate_day1(day1)
        self.day2 = self.validate_optional_day(day2)
        self.day3 = self.validate_optional_day(day3)
        self.day4 = self.validate_optional_day(day4)
        self.day5 = self.validate_optional_day(day5)
        self.day6 = self.validate_optional_day(day6)
        self.day7 = self.validate_optional_day(day7)

    def validate_name(self, name: str) -> str:
        if not isinstance(name, str):
            raise ValueError("El nombre debe ser una cadena de texto")

        name = name.strip()

        if len(name) < 3 or len(name) > 20:
            raise ValueError("El nombre debe tener entre 3 y 20 caracteres")

        if not re.match(r'^[a-zA-Z0-9\s\-\'áéíóúÁÉÍÓÚñÑ]+$', name):
            raise ValueError("El nombre contiene caracteres no permitidos")

        return name

    def validate_user_id(self, user_id: int) -> int:
        if not isinstance(user_id, int):
            raise ValueError("El ID de usuario debe ser un número entero")

        if user_id <= 0:
            raise ValueError("El ID de usuario debe ser un número positivo")

        return user_id

    def validate_duration(self, duration: int) -> int:
        if not isinstance(duration, int):
            raise ValueError("La duración debe ser un número entero")

        if duration < 1 or duration > 7:
            raise ValueError("La duración debe estar entre 1 y 7 días")

        return duration

    def validate_day1(self, day: int) -> int:
        if not isinstance(day, int):
            raise ValueError("El ID del día 1 debe ser un número entero")

        if day <= 0:
            raise ValueError("El ID del día 1 debe ser un número positivo")

        return day

    def validate_optional_day(self, day: Optional[int]) -> Optional[int]:
        if day is None:
            return None

        if not isinstance(day, int):
            raise ValueError("El ID del día debe ser un número entero o nulo")

        if day <= 0:
            raise ValueError("Si se proporciona un ID de día, debe ser un número positivo")

        return day

    def validate_diet_type(self, diet_type_id):
        if not isinstance(diet_type_id, int):
            raise ValueError("El ID del tipo de dieta debe ser entero")

        if diet_type_id <= 0:
            raise ValueError("El ID del tipo de dieta debe estar entre 1 y 5")