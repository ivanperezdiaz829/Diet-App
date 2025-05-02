import os
from configuration.config import DB_PATH
from database_utils import create_database

if __name__ == "__main__":
    try:
        if not os.path.exists(DB_PATH):
            print(f"Base de datos no encontrada en {DB_PATH}. Creando la base de datos...")
            create_database()
        else:
            print(f"La base de datos ya existe en {DB_PATH}.")
    except Exception as e:
        print(f"Ocurrió un error: {e}")
