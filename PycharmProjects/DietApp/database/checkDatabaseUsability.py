import sqlite3

import requests
import json
import random
from pprint import pprint
from PycharmProjects.DietApp.configuration.config import DB_PATH


# Configuración (igual que en tu aplicación Flask)
BASE_URL = "http://localhost:8000"
HEADERS = {'Content-Type': 'application/json'}


def print_response(response):
    """Muestra la respuesta de la API de forma legible"""
    print(f"\nStatus Code: {response.status_code}")
    try:
        pprint(response.json())
    except ValueError:
        print(response.text)
    print("-" * 50)


def generate_day_data():
    """Genera datos aleatorios para un día de dieta"""
    return {
        "breakfast_dish": random.randint(1, 100),
        "breakfast_drink": random.randint(1, 100),
        "lunch_main_dish": random.randint(1, 100),
        "lunch_side_dish": random.randint(1, 100),
        "lunch_drink": random.randint(1, 100),
        "dinner_dish": random.randint(1, 100),
        "dinner_drink": random.randint(1, 100)
    }


def generate_user_data():
    """Genera datos aleatorios para un usuario"""
    return {
        "email": f"user{random.randint(1, 10000)}@example.com",
        "password": "securepassword123",
        "physical_activity": random.choice(["SEDENTARY", "LIGHT", "MODERATE", "HIGH", "EXTREME"]),
        "sex": random.choice(["MALE", "FEMALE"]),
        "birthday": "1990-01-01",
        "height": random.uniform(1.50, 2.00),
        "weight": random.uniform(50.0, 100.0)
    }

def print_response(response):
    """Muestra la respuesta de la API de forma legible"""
    print(f"Status Code: {response.status_code}")
    try:
        print("Response Body:")
        print(json.dumps(response.json(), indent=2))
    except ValueError:
        print("No JSON response")

def test_user_authentication():
    """Prueba el endpoint de autenticación de usuario"""
    print("\n" + "=" * 50)
    print(" TESTING USER AUTHENTICATION ".center(50, "="))
    print("=" * 50)

    # Credenciales de prueba (ajusta según tu base de datos)
    TEST_EMAIL = "gloton@hotmail.com"
    TEST_PASSWORD = "Glotoner2*"
    INVALID_PASSWORD = "wrongpass"
    NON_EXISTENT_EMAIL = "noexiste@ejemplo.com"

    # 1. Caso exitoso - Credenciales válidas
    print("\n1. Probando credenciales válidas...")
    data = {
        "email": TEST_EMAIL,
        "password": TEST_PASSWORD
    }
    response = requests.post(
        f"{BASE_URL}/get_user_by_credentials",
        headers=HEADERS,
        data=json.dumps(data)
    )
    print_response(response)

    if response.status_code != 200:
        print("Error en autenticación válida, abortando pruebas...")
        return None

    user_data = response.json()
    print("\nDatos de usuario obtenidos:")
    for key, value in user_data.items():
        print(f"{key}: {value}")

    # 2. Caso error - Contraseña incorrecta
    print("\n2. Probando contraseña incorrecta...")
    data = {
        "email": TEST_EMAIL,
        "password": INVALID_PASSWORD
    }
    response = requests.post(
        f"{BASE_URL}/get_user_by_credentials",
        headers=HEADERS,
        data=json.dumps(data)
    )
    print_response(response)

    # 3. Caso error - Usuario no existe
    print("\n3. Probando email no existente...")
    data = {
        "email": NON_EXISTENT_EMAIL,
        "password": TEST_PASSWORD
    }
    response = requests.post(
        f"{BASE_URL}/get_user_by_credentials",
        headers=HEADERS,
        data=json.dumps(data)
    )
    print_response(response)

    # 4. Caso error - Campos faltantes
    print("\n4. Probando falta de campos requeridos...")
    # Caso sin password
    data = {"email": TEST_EMAIL}
    response = requests.post(
        f"{BASE_URL}/get_user_by_credentials",
        headers=HEADERS,
        data=json.dumps(data)
    )
    print_response(response)

    # Caso sin email
    data = {"password": TEST_PASSWORD}
    response = requests.post(
        f"{BASE_URL}/get_user_by_credentials",
        headers=HEADERS,
        data=json.dumps(data)
    )
    print_response(response)

    # 5. Verificar que la contraseña no se devuelve
    print("\n5. Verificando que la contraseña no está en la respuesta...")
    data = {
        "email": TEST_EMAIL,
        "password": TEST_PASSWORD
    }
    response = requests.post(
        f"{BASE_URL}/get_user_by_credentials",
        headers=HEADERS,
        data=json.dumps(data)
    )
    user_data = response.json()
    assert "password" not in user_data, "ERROR: La contraseña está incluida en la respuesta"
    print("OK: La contraseña no está incluida en la respuesta")

    return user_data.get("id")  # Devuelve el ID del usuario para usar en otras pruebas

def test_diet_plan_endpoints():
    """Prueba los endpoints de planes de dieta"""
    print("\n" + "=" * 50)
    print(" TESTING DIET PLAN ENDPOINTS ".center(50, "="))
    print("=" * 50)

    # 1. Crear un plan de dieta
    print("\n1. Creando plan de dieta...")
    data = {
        "name": "Dieta 1",
        "user_id": 2,
        "duration": 2,
        "diet_type_id": 1,
        "day1": generate_day_data(),
        "day2": generate_day_data()
    }
    response = requests.post(f"{BASE_URL}/create_diet_plan", headers=HEADERS, data=json.dumps(data))
    print_response(response)

    if response.status_code != 201:
        print("Error creando plan, abortando pruebas...")
        return None

    plan_id = response.json()["plan_id"]

    # 2. Obtener el plan creado
    print("\n2. Obteniendo plan creado...")
    response = requests.get(f"{BASE_URL}/get_diet_plan/{plan_id}")
    print_response(response)
    """
    # 3. Actualizar el plan
    print("\n3. Actualizando plan...")
    update_data = {
        "name": "Plan Mediterráneo Modificado",
        "day1": {
            "breakfast_dish": 50,
            "dinner_drink": 51
        },
        "day3": generate_day_data()  # Añadir un nuevo día
    }
    response = requests.patch(f"{BASE_URL}/update_diet_plan/{plan_id}", headers=HEADERS, data=json.dumps(update_data))
    print_response(response)
    """
    # 4. Verificar cambios
    print("\n4. Verificando cambios...")
    response = requests.get(f"{BASE_URL}/get_diet_plan/{plan_id}")
    print_response(response)

    # 5. Eliminar el plan
    print("\n5. Eliminando plan...")
    response = requests.delete(f"{BASE_URL}/delete_diet_plan/{plan_id}")
    print_response(response)

    # 6. Verificar que ya no existe
    print("\n6. Verificando eliminación...")
    response = requests.get(f"{BASE_URL}/get_diet_plan/{plan_id}")
    print_response(response)

    return plan_id

def test_get_diet_plan_days_by_complete(plan_id):
    """Prueba obtener todos los diet_plan_day a partir de un diet_plans_complete"""
    print("\n" + "=" * 50)
    print(f" TEST: DÍAS DE PLAN COMPLETO ID {plan_id} ".center(50, "="))
    print("=" * 50)

    response = requests.get(f"{BASE_URL}/get_diet_plan_days_by_complete/{plan_id}")
    print_response(response)

    if response.status_code == 200:
        days = response.json()
        print(f"✔ Se recuperaron {len(days)} día(s):")
        for d in days:
            pprint(d)
    elif response.status_code == 404:
        print("❌ Plan completo no encontrado.")
    else:
        print("❌ Error inesperado.")


def test_get_existing_diet_plans_for_user_7():
    """Prueba obtener todos los diet plans del usuario con ID 7"""
    print("\n" + "=" * 50)
    print(" TEST: OBTENER PLANES DE USUARIO 7 ".center(50, "="))
    print("=" * 50)

    # 1. Obtener todos los planes del usuario 7
    print("\n1. Consultando planes del usuario con ID 7...")
    response = requests.get(f"{BASE_URL}/get_diet_plans_by_user/7")
    print_response(response)

    assert response.status_code == 200, "❌ Error al obtener planes por user_id"

    plans = response.json()

    if plans:
        print(f"\n✔ Se encontraron {len(plans)} plan(es) para el usuario 7.")
        for plan in plans:
            print(f"- Plan ID: {plan['id']}, Nombre: {plan['name']}")
    else:
        print("\n⚠ No se encontraron planes para el usuario 7.")

def test_user_crud_operations():
    """Prueba completa de CRUD para usuarios"""
    print("\n" + "=" * 50)
    print(" TESTING USER CRUD OPERATIONS ".center(50, "="))
    print("=" * 50)
    # Datos de prueba ajustados a 0/1 para sexo
    user1_data = {
        "email": "gloton@hotmail.com",
        "password": "Glotoner2*",
        "physical_activity": 1,
        "sex": 1,  # 0 = femenino
        "birthday": "1990-01-01",
        "height": 165,
        "weight": 60,
        "goal": 1
    }

    user2_data = {
        "email": "gloton@gmail.com",
        "password": "Gloton!4",
        "physical_activity": 2,
        "sex": 1,  # 1 = masculino
        "birthday": "1985-05-15",
        "height": 180,
        "weight": 75,
        "goal": 2
    }

    # 1. Crear usuarios
    print("\n1. Creando usuarios de prueba...")

    # Usuario 1 (mujer)
    print("\nCreando Usuario 1 (sexo = 0 - femenino)...")
    response = requests.post(f"{BASE_URL}/create_user", json=user1_data)
    print_response(response)
    if response.status_code != 201:
        print("Error creando usuario 1, abortando prueba...")
        return
    user1_id = response.json()["user_id"]

    # Usuario 2 (hombre)
    print("\nCreando Usuario 2 (sexo = 1 - masculino)...")
    response = requests.post(f"{BASE_URL}/create_user", json=user2_data)
    print_response(response)
    if response.status_code != 201:
        print("Error creando usuario 2, abortando prueba...")
        return
    user2_id = response.json()["user_id"]

    # 2. Consultar usuario 1
    print("\n2. Obteniendo datos del usuario 1...")
    response = requests.get(f"{BASE_URL}/get_user/{user1_id}")
    print_response(response)
    if response.status_code == 200:
        print("Datos del usuario 1:")
        print(json.dumps(response.json(), indent=2))

    # 3. Actualizar parámetros físicos del usuario 1
    print("\n3. Actualizando datos físicos del usuario 1...")
    physical_update = {
        "height": 180,
        "weight": 75,
        "physical_activity": 4
    }
    response = requests.patch(
        f"{BASE_URL}/update_user_physical/{user1_id}",
        json=physical_update
    )
    print_response(response)
    if response.status_code == 200:
        print("Verificando actualización...")
        response = requests.get(f"{BASE_URL}/get_user/{user1_id}")
        updated_data = response.json()
        print(f"Nueva altura: {updated_data['height']} cm")
        print(f"Nuevo peso: {updated_data['weight']} kg")
        print(f"Nueva actividad: {updated_data['physical_activity']}")

    # 4. Actualizar contraseña del usuario 2
    print("\n4. Actualizando contraseña del usuario 2...")
    password_update = {
        "current_password": user2_data["password"],
        "new_password": "NuevaContraseña789!"
    }
    response = requests.patch(
        f"{BASE_URL}/update_user_password/{user2_id}",
        json=password_update
    )
    print_response(response)

    # 5. Eliminar usuario 2
    print("\n5. Eliminando usuario 2...")
    response = requests.delete(f"{BASE_URL}/delete_user/{user2_id}")
    print_response(response)
    if response.status_code == 200:
        print("Verificando eliminación...")
        response = requests.get(f"{BASE_URL}/get_user/{user2_id}")
        print_response(response)
        if response.status_code == 404:
            print("Usuario eliminado correctamente ✓")

    print("\n" + "=" * 50)
    print(" PRUEBAS COMPLETADAS ".center(50, "="))
    print("=" * 50)

def test_plate_obtention():
    """Prueba los endpoints de obtención de platos"""
    print("\n" + "=" * 50)
    print(" TESTING PLATE OBENTION ENDPOINTS ".center(50, "="))
    print("=" * 50)

    # 1. Crear un plato de prueba (si no existe un endpoint para crear, usar uno existente)
    print("\n1. Preparando prueba usando plato existente...")
    test_plate_id = 1  # Cambiar por un ID existente en tu base de datos

    # 2. Obtener el plato
    print("\n2. Obteniendo plato...")
    response = requests.get(f"{BASE_URL}/get_plate/{test_plate_id}")
    print_response(response)

    if response.status_code != 200:
        print("Error obteniendo plato, abortando pruebas...")
        return None

    plate_data = response.json()
    print("\nDatos del plato obtenido:")
    print(json.dumps(plate_data, indent=2))

    # 3. Verificar estructura de respuesta
    print("\n3. Verificando estructura de respuesta...")
    required_fields = ['id', 'name', 'calories', 'proteins', 'fats', 'type']
    for field in required_fields:
        if field not in plate_data.get('plate', {}):
            print(f"¡Error! Falta campo requerido: {field}")
        else:
            print(f"Campo {field} presente ✓")

    # 4. Obtener un plato que no existe
    print("\n4. Probando con plato inexistente...")
    invalid_plate_id = 9999
    response = requests.get(f"{BASE_URL}/get_plate/{invalid_plate_id}")
    print_response(response)

    if response.status_code == 404:
        print("Comportamiento correcto para plato no encontrado ✓")
    else:
        print("¡Error! Debería retornar 404 para plato no existente")

    # 5. Verificar manejo de errores
    print("\n5. Probando ID inválido...")
    response = requests.get(f"{BASE_URL}/get_plate/abc")  # ID no numérico
    print_response(response)

    if response.status_code == 400:
        print("Comportamiento correcto para ID inválido ✓")
    else:
        print("¡Error! Debería retornar 400 para ID inválido")

    return test_plate_id


def main():
    """Función principal"""
    print("INICIANDO PRUEBAS DE LA API".center(50, "="))

    url = "http://localhost:8000/create_plate"  # Añade la ruta específica
    data = {
        "name": "Ensalada César de César",
        "user_id": 1,
        "calories": 350,
        "carbohydrates": 20,
        "proteins": 15,
        "fats": 10,
        "sugar": 5,
        "sodium": 200,
        "price": 8,
        "type": 1,
        "vegan": 0,
        "vegetarian": 1,
        "celiac": 1,
        "halal": 0
    }

    response = requests.post(url, json=data)

    # Imprimir la respuesta
    print("Status Code:", response.status_code)
    try:
        print("Response Body:", response.json())
    except ValueError:
        print("Response Body: No JSON content", response.text)

    print("\n" + "PRUEBAS COMPLETADAS".center(50, "="))


if __name__ == '__main__':
    main()