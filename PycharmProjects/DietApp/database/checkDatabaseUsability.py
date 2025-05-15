import sqlite3

import requests
import json
import random
from pprint import pprint
from configuration.config import DB_PATH

# Configuración (igual que en tu aplicación Flask)
BASE_URL = "http://localhost:8000"
HEADERS = {'Content-Type': 'application/json'}


def print_response(response):
    """Muestra la respuesta de la API de forma legible"""
    print(f"Status Code: {response.status_code}")
    try:
        print("Response Body:")
        print(json.dumps(response.json(), indent=2))
    except ValueError:
        print("No JSON response")



def main():
    """Función principal
    print("INICIANDO PRUEBAS DE LA API".center(50, "="))
    """



if __name__ == '__main__':
    main()
