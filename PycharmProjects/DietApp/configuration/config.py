import os

CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(CURRENT_DIR, os.pardir))

DB_PATH = os.path.join(PROJECT_ROOT, 'database', 'DietApp_Sprint1.db')
SCHEME_PATH = os.path.join(PROJECT_ROOT, 'database', 'DbScheme.sql')
DATASET_PATH = os.path.join(PROJECT_ROOT, 'database', 'plates.csv')

MAX_HEIGHT = 250
MIN_HEIGHT = 100

MAX_WEIGHT = 350
MIN_WEIGHT = 35


def to_sql_value_list(values):
    rows = ['(\'' + item + '\')' for item in values]
    return ', '.join(rows) + ';'


PHYSICAL_ACTIVITY_LEVELS = ['LOSE_WEIGHT', 'GAIN_WEIGHT', 'STAY_HEALTHY']
SEX_VALUES = ['FEMALE', 'MALE']
PLATE_TYPES = ['Light Meal', 'Drink', 'Main Dish', 'Side Dish', 'Dessert']

USER_FIELDS = ['email', 'password', 'physical_activity', 'sex', 'birthday', 'height', 'weight']

PLATE_TYPE_INSERT_SCRIPT = """INSERT INTO plate_type(description) VALUES """ + to_sql_value_list(PLATE_TYPES)
PHYSICAL_ACTIVITY_INSERT_SCRIPT = ("""INSERT INTO physical_activity_levels(description) VALUES""" +
                                   to_sql_value_list(PHYSICAL_ACTIVITY_LEVELS))

INSERT_SCRIPTS = [PLATE_TYPE_INSERT_SCRIPT, PHYSICAL_ACTIVITY_INSERT_SCRIPT]
