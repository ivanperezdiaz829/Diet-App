import re
from datetime import date, datetime
from DietAppAPI.configuration.config import *

class User:

    def __init__(self, email: str, password: str, physical_activity: str,
                 sex: str, birthday: str, height: int, weight: int):
        self.email = validate_email(email)
        self.password = validate_password(password)
        self.physical_activity = validate_physical_activity(physical_activity)
        self.sex = validate_sex(sex)
        self.birthday = validate_birthday(birthday)
        self.height = validate_height(height)
        self.weight = validate_weight(weight)

    @classmethod
    def get_fields(cls):
        return USER_FIELDS

    @classmethod
    def of(cls, data):
        if not isinstance(data, tuple) and not isinstance(data, list):
            raise TypeError(f"A tuple was expected, instead: {data}")

        if len(data) != len(User.get_fields()):
            raise TypeError(f"Incorrect length of params provided: {len(data)}")

        return User(data[0], data[1], data[2], data[3], data[4], data[5], data[6])

    @classmethod
    def from_dict(cls, dictionary):
        if not isinstance(dictionary, dict):
            raise TypeError(f"Expected a dictionary with keys {USER_FIELDS}")
        if set(dictionary.keys()) != set(USER_FIELDS):
            raise ValueError(f"Dictionary is expected to contain fields: {USER_FIELDS}\n"
                             f'got: {list(dictionary.keys())}')
        values = []
        for field in USER_FIELDS:
            values.append(dictionary[field])
        return User.of(values)

    def to_dict(self):
        return {
            'email': self.email,
            'password': self.password,
            'physical activity': self.physical_activity,
            'sex': self.sex,
            'birthday': self.birthday.isoformat(),
            'height': self.height,
            'weight': self.weight
        }

    def get_values(self):
        return [self.email, self.password, self.physical_activity, self.sex, self.birthday.isoformat(),
                self.height, self.weight]

    def __str__(self):
        return str(self.to_dict())


def validate_email(email):
    if type(email) is not str:
        raise TypeError(f"Expected a string to be tested, received: {type(email)}")

    result = re.match(r'^(?!.*[._-]{2})[\w.-]{4,}@(gmail|hotmail)\.[a-z]{2,3}$', email)
    if not result:
        raise ValueError("Email field is supposed to be at least 8 characters long, contain"
                         f" one cap and a special character. Instead received: {email}")
    return email


def validate_sex(sex):
    if not isinstance(sex, int):
        raise TypeError(f"Expected a string as sex value, received: {type(sex)}")
    if sex not in SEX_VALUES:
        raise ValueError("Sex value provided is not contemplated in our database\n"
                         f"Values: {SEX_VALUES}\n"
                         f"Given value: {sex}"
                         )
    return SEX_VALUES.index(sex)


def validate_password(password: str):
    if type(password) is not str:
        raise TypeError(f"Expected a string, got: {type(password)}")
    if len(password) < 8:
        raise ValueError("La contraseña debe tener al menos 8 caracteres")
    if not re.search(r'[A-Z]', password):
        raise ValueError("La contraseña debe contener al menos una letra mayúscula")
    if not re.search(r'[a-z]', password):
        raise ValueError("La contraseña debe contener al menos una letra minúscula")
    if not re.search(r'\d', password):
        raise ValueError("La contraseña debe contener al menos un número")
    if not re.search(r'[\W_]', password):
        raise ValueError("La contraseña debe contener al menos un carácter especial")

    return password


def validate_physical_activity(physical_activity):
    if not isinstance(physical_activity, str):
        raise TypeError(f"Physical activity field is supposed to be a string, got: {type(physical_activity)}")
    if physical_activity in PHYSICAL_ACTIVITY_LEVELS:
        return physical_activity
    else:
        raise ValueError(f"Physical activity level provided is not contemplated in database'"
                         f" values: {PHYSICAL_ACTIVITY_LEVELS}\n"
                         f"Got: {physical_activity}")


def validate_birthday(birthday):
    if not isinstance(birthday, str):
        raise TypeError(f"Birthday field should be a string in format %Y-%m-%d, got: {type(birthday)}")
    try:
        return datetime.strptime(birthday, '%Y-%m-%d').date()
    except ValueError as e:
        raise ValueError(f"{e}" + f" value: {birthday}")


def validate_height(height):
    if not isinstance(height, int):
        raise TypeError(f"Height field should be an int, got: {type(height)}")
    if not MIN_HEIGHT <= height <= MAX_HEIGHT:
        raise ValueError(f"Height values must be between {MIN_HEIGHT} and {MAX_HEIGHT}, value: {height}")
    return height


def validate_weight(weight):
    if not isinstance(weight, int):
        raise TypeError(f"Weight field should be an int, got: {type(weight)}")
    if not MIN_WEIGHT <= weight <= MAX_WEIGHT:
        raise ValueError(f"Weight values must be between {MIN_WEIGHT} and {MAX_WEIGHT}, value: {weight}")
    return weight
