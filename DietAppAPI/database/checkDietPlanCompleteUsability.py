import requests
import json
import random
from pprint import pprint

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


def test_diet_plan_endpoints():
    """Prueba los endpoints de planes de dieta"""
    print("\n" + "=" * 50)
    print(" TESTING DIET PLAN ENDPOINTS ".center(50, "="))
    print("=" * 50)

    # 1. Crear un plan de dieta
    print("\n1. Creando plan de dieta...")
    data = {
        "name": "Plan Mediterráneo Test",
        "user_id": 2,
        "duration": 3,
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


def test_user_endpoints():
    """Prueba los endpoints de usuarios"""
    print("\n" + "=" * 50)
    print(" TESTING USER ENDPOINTS ".center(50, "="))
    print("=" * 50)

    # 1. Crear un usuario
    print("\n1. Creando usuario...")
    user_data = generate_user_data()
    response = requests.post(f"{BASE_URL}/create_user", headers=HEADERS, data=json.dumps(user_data))
    print_response(response)

    if response.status_code != 201:
        print("Error creando usuario, abortando pruebas...")
        return None

    email = user_data["email"]

    # 2. Obtener el usuario creado
    print("\n2. Obteniendo usuario creado...")
    response = requests.get(f"{BASE_URL}/get_user/{email}")
    print_response(response)

    # 3. Actualizar el usuario
    print("\n3. Actualizando usuario...")
    update_data = {
        "physical_activity": "HIGH",
        "weight": 85.5
    }
    response = requests.patch(f"{BASE_URL}/update_user/{email}", headers=HEADERS, data=json.dumps(update_data))
    print_response(response)

    # 4. Verificar cambios
    print("\n4. Verificando cambios...")
    response = requests.get(f"{BASE_URL}/get_user/{email}")
    print_response(response)

    # 5. Eliminar el usuario
    print("\n5. Eliminando usuario...")
    response = requests.delete(f"{BASE_URL}/delete_user/{email}")
    print_response(response)

    # 6. Verificar que ya no existe
    print("\n6. Verificando eliminación...")
    response = requests.get(f"{BASE_URL}/get_user/{email}")
    print_response(response)

    return email


def test_error_cases():
    """Prueba casos de error"""
    print("\n" + "=" * 50)
    print(" TESTING ERROR CASES ".center(50, "="))
    print("=" * 50)

    # 1. Crear plan con datos inválidos
    print("\n1. Crear plan con datos inválidos...")
    invalid_data = {
        "name": "P",  # Nombre demasiado corto
        "user_id": -1,  # ID negativo
        "duration": 10,  # Duración inválida
        "diet_type_id": 1,
        "day1": generate_day_data()
    }
    response = requests.post(f"{BASE_URL}/create_diet_plan", headers=HEADERS, data=json.dumps(invalid_data))
    print_response(response)

    # 2. Obtener plan que no existe
    print("\n2. Obtener plan que no existe...")
    response = requests.get(f"{BASE_URL}/get_diet_plan/999999")
    print_response(response)

    # 3. Crear usuario con datos inválidos
    print("\n3. Crear usuario con datos inválidos...")
    invalid_user = {
        "email": "notanemail",
        "password": "short",
        "physical_activity": "INVALID",
        "sex": "OTHER",
        "birthday": "3000-01-01",  # Fecha futura
        "height": -1.70,  # Altura negativa
        "weight": -70.0  # Peso negativo
    }
    response = requests.post(f"{BASE_URL}/create_user", headers=HEADERS, data=json.dumps(invalid_user))
    print_response(response)


def main():
    """Función principal"""
    print("INICIANDO PRUEBAS DE LA API".center(50, "="))

    # Ejecutar pruebas
    test_diet_plan_endpoints()

    print("\n" + "PRUEBAS COMPLETADAS".center(50, "="))


if __name__ == '__main__':
    main()