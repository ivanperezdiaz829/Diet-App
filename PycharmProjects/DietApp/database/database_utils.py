import os

from configuration.config import *
import sqlite3


def load_plates(conn, dataset_path, separator=';'):
    cursor = conn.cursor()
    try:
        with (open(dataset_path, 'r', encoding="utf-8") as file):
            file.readline()
            insert_sentence = """INSERT OR REPLACE INTO 
            plates(name, calories, carbohydrates, proteins, fats, sugar, 
                    sodium, price, type, vegan, vegetarian, celiac, halal)
                     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"""
            for line in file:
                values = line.strip().split(separator)
                cursor.execute(insert_sentence, values)
        conn.commit()
    except Exception as e:
        print(f"Error al cargar los platos del archivo: {e}")
        conn.rollback()
        raise sqlite3.Error(f"Couldn't load plates from dataset {dataset_path}")


def create_database():
    if os.path.exists(DB_PATH):
        raise FileExistsError(f'La base de datos {DB_PATH} ya existe')
    error = False
    conn = None
    try:
        with sqlite3.connect(DB_PATH) as conn:
            cursor = conn.cursor()
            with open(SCHEME_PATH, 'r') as file:
                sql_create_script = (
                    file.read().replace("{MAX_WEIGHT}", str(MAX_WEIGHT)).
                    replace('{MIN_WEIGHT}', str(MIN_WEIGHT)).
                    replace('{MAX_HEIGHT}', str(MAX_HEIGHT)).
                    replace('{MIN_HEIGHT}', str(MIN_HEIGHT))
                )

                cursor.executescript(sql_create_script)

                for script in INSERT_SCRIPTS:
                    print(script)
                    cursor.executescript(script)

            load_plates(conn, DATASET_PATH, ',')
    except sqlite3.Error as e:
        print(f"Error en la base de datos: {e}")
        error = True
    except Exception as e:
        print(f"Error inesperado: {e}")
        error = True
    finally:
        if error:
            conn.close()
            if os.path.exists(DB_PATH):
                os.remove(DB_PATH)
        else:
            print(f'Base de datos creada en {DB_PATH}')
