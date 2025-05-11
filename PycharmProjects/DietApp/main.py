from flask import Flask, request, jsonify, send_file

from Graphs import *
from ObtainTotals import *
import ast
from InfoForGraph import *
import os
from werkzeug.security import generate_password_hash, check_password_hash
import sqlite3
import re

from GenerarDieta2 import calculate_nutritional_requirements
from model.User import User, validate_email
from configuration.config import DB_PATH, PHYSICAL_ACTIVITY_LEVELS, SEX_VALUES
from model.DietPlanDay import DietPlanDay
from model.DietPlanComplete import DietPlanComplete
from datetime import date
import json

app = Flask(__name__)

# Configuración inicial
if not os.path.exists(DB_PATH):
    raise Exception("Database is not available, must be created through create_database.py")

SEX_DICT = {value: i for i, value in enumerate(SEX_VALUES)}


@app.route('/get_plate/<string:plate_id>', methods=['GET'])  # Cambiamos a string para capturar cualquier valor
def get_plate(plate_id):
    try:
        # Primero validamos que el plate_id sea numérico
        try:
            plate_id_int = int(plate_id)
        except ValueError:
            return jsonify({"error": "El ID debe ser un número entero válido"}), 400

        # Ahora continuamos con la lógica original
        with sqlite3.connect(DB_PATH) as conn:
            conn.row_factory = sqlite3.Row
            cursor = conn.cursor()

            # Obtener el plato principal
            cursor.execute("SELECT * FROM plates WHERE id = ?", (plate_id_int,))
            plate_row = cursor.fetchone()

            if not plate_row:
                return jsonify({"error": "Plato no encontrado"}), 404

            # Obtener información adicional del tipo de plato
            type_info = {}
            if plate_row['type']:
                cursor.execute("SELECT * FROM plate_type WHERE id = ?", (plate_row['type'],))
                type_row = cursor.fetchone()
                if type_row:
                    type_info = dict(type_row)

            # Convertir a diccionario y ajustar campos booleanos
            plate_data = dict(plate_row)

            return jsonify({
                "plate": plate_data,
                "type_info": type_info if type_info else None
            }), 200

    except sqlite3.Error as e:
        return jsonify({"error": f"Error de base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500


# --------------------------
# Endpoints de Usuarios
# --------------------------
@app.route('/create_user', methods=['POST'])
def create_user():
    try:
        data = request.get_json()

        # Validar campos requeridos
        required_fields = ['email', 'password', 'physical_activity', 'sex',
                           'birthday', 'height', 'weight', 'goal']
        for field in required_fields:
            if field not in data:
                return jsonify({"error": f"Campo requerido faltante: {field}"}), 400

        # Validaciones adicionales
        if not re.match(r"[^@]+@[^@]+\.[^@]+", data['email']):
            return jsonify({"error": "Formato de email inválido"}), 400

        if len(data['password']) < 8:
            return jsonify({"error": "La contraseña debe tener al menos 8 caracteres"}), 400

        # Hashear contraseña
        hashed_password = generate_password_hash(data['password'])

        with sqlite3.connect(DB_PATH) as conn:
            cursor = conn.cursor()
            cursor.execute("""
                INSERT INTO users (
                    email, password, physical_activity, sex, 
                    birthday, height, weight, goal
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """, (
                data['email'],
                hashed_password,
                data['physical_activity'],
                data['sex'],
                data['birthday'],
                data['height'],
                data['weight'],
                data['goal']
            ))
            user_id = cursor.lastrowid
            conn.commit()

            return jsonify({
                "message": "Usuario creado exitosamente",
                "user_id": user_id
            }), 201

    except sqlite3.IntegrityError as e:
        if "UNIQUE constraint failed" in str(e):
            return jsonify({"error": "El email ya está registrado"}), 409
        return jsonify({"error": f"Error de integridad: {str(e)}"}), 400
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

@app.route('/get_user_by_credentials', methods=['POST'])
def get_user_by_credentials():
    try:
        data = request.get_json()

        # Validación de campos requeridos
        if 'email' not in data or 'password' not in data:
            return jsonify({"error": "Email y contraseña son requeridos"}), 400

        email = data['email']
        password = data['password']

        # Validar formato del email
        if not re.match(r"[^@]+@[^@]+\.[^@]+", email):
            return jsonify({"error": "Formato de email inválido"}), 400

        with sqlite3.connect(DB_PATH) as conn:
            conn.row_factory = sqlite3.Row  # Permite acceder por nombres de columna
            cursor = conn.cursor()
            cursor.execute("SELECT * FROM users WHERE email = ?", (email,))
            user = cursor.fetchone()

            if not user:
                return jsonify({"error": "Usuario no encontrado"}), 404

            if not check_password_hash(user["password"], password):
                return jsonify({"error": "Contraseña incorrecta"}), 401

            # Construir respuesta excluyendo la contraseña
            user_data = {key: user[key] for key in user.keys() if key != "password"}

            return jsonify(user_data), 200

    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

@app.route('/get_user/<int:user_id>', methods=['GET'])
def get_user(user_id):
    try:
        with sqlite3.connect(DB_PATH) as conn:
            conn.row_factory = sqlite3.Row
            cursor = conn.cursor()

            cursor.execute("""
                SELECT id, email, physical_activity, sex, 
                       birthday, height, weight, goal
                FROM users WHERE id = ?
            """, (user_id,))

            user = cursor.fetchone()
            if not user:
                return jsonify({"error": "Usuario no encontrado"}), 404

            return jsonify(dict(user)), 200

    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500


@app.route('/update_user_physical/<int:user_id>', methods=['PATCH'])
def update_user_physical(user_id):
    try:
        data = request.get_json()

        # Campos actualizables
        updatable_fields = ['physical_activity', 'sex', 'birthday', 'height', 'weight', 'goal']
        update_data = {k: data[k] for k in updatable_fields if k in data}

        if not update_data:
            return jsonify({"error": "No se proporcionaron datos para actualizar"}), 400

        with sqlite3.connect(DB_PATH) as conn:
            cursor = conn.cursor()

            # Verificar que el usuario existe
            cursor.execute("SELECT 1 FROM users WHERE id = ?", (user_id,))
            if not cursor.fetchone():
                return jsonify({"error": "Usuario no encontrado"}), 404

            # Construir query dinámica
            set_clause = ", ".join([f"{field} = ?" for field in update_data.keys()])
            query = f"UPDATE users SET {set_clause} WHERE id = ?"

            cursor.execute(query, (*update_data.values(), user_id))
            conn.commit()

            return jsonify({"message": "Datos físicos actualizados exitosamente"}), 200

    except sqlite3.Error as e:
        return jsonify({"error": f"Error de base de datos: {str(e)}"}), 400
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500


@app.route('/update_user_password/<int:user_id>', methods=['PATCH'])
def update_user_password(user_id):
    try:
        data = request.get_json()

        # Validar campos requeridos
        if 'current_password' not in data or 'new_password' not in data:
            return jsonify({"error": "Se requieren current_password y new_password"}), 400

        # Validar longitud de nueva contraseña
        if len(data['new_password']) < 8:
            return jsonify({"error": "La nueva contraseña debe tener al menos 8 caracteres"}), 400

        with sqlite3.connect(DB_PATH) as conn:
            conn.row_factory = sqlite3.Row  # Para acceso por nombre de columna
            cursor = conn.cursor()

            # Obtener contraseña actual
            cursor.execute("SELECT password FROM users WHERE id = ?", (user_id,))
            result = cursor.fetchone()

            if not result:
                return jsonify({"error": "Usuario no encontrado"}), 404

            # Verificar contraseña actual (acceso corregido)
            stored_password = result['password']  # Acceso por nombre de columna
            if not check_password_hash(stored_password, data['current_password']):
                return jsonify({"error": "Contraseña actual incorrecta"}), 401

            # Actualizar contraseña
            new_hashed = generate_password_hash(data['new_password'])
            cursor.execute("UPDATE users SET password = ? WHERE id = ?", (new_hashed, user_id))
            conn.commit()

            return jsonify({"message": "Contraseña actualizada exitosamente"}), 200

    except sqlite3.Error as e:
        return jsonify({"error": f"Error de base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

@app.route('/delete_user/<int:user_id>', methods=['DELETE'])
def delete_user(user_id):
    try:
        with sqlite3.connect(DB_PATH) as conn:
            cursor = conn.cursor()

            # Verificar existencia
            cursor.execute("SELECT 1 FROM users WHERE id = ?", (user_id,))
            if not cursor.fetchone():
                return jsonify({"error": "Usuario no encontrado"}), 404

            cursor.execute("DELETE FROM users WHERE id = ?", (user_id,))
            conn.commit()

            return jsonify({"message": "Usuario eliminado exitosamente"}), 200

    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500
# --------------------------
# Endpoints de Planes de Dieta
# --------------------------

def create_diet_plan():
    try:
        data = request.get_json()

        # Validate required fields
        required_fields = ['name', 'user_id', 'duration', 'diet_type_id']
        for field in required_fields:
            if field not in data:
                return jsonify({"error": f"Campo requerido faltante: {field}"}), 400

        # Validate duration is between 1-7
        duration = data['duration']
        if not 1 <= duration <= 7:
            return jsonify({"error": "Duración debe estar entre 1 y 7 días"}), 400

        # Validate that required days exist
        for day_num in range(1, duration + 1):
            day_key = f'day{day_num}'
            if day_key not in data:
                return jsonify({
                    "error": f"Se requiere día {day_num} para duración {duration}"
                }), 400

        day_ids = []

        with sqlite3.connect(DB_PATH) as conn:
            cursor = conn.cursor()

            # Process each day up to the duration
            for day_num in range(1, 8):
                day_key = f'day{day_num}'
                if day_key in data and day_num <= duration:
                    try:
                        day_obj = DietPlanDay(**data[day_key])
                        cursor.execute("""
                            INSERT INTO diet_plans_day (
                                breakfast_dish, breakfast_drink,
                                lunch_main_dish, lunch_side_dish, lunch_drink,
                                dinner_dish, dinner_drink
                            ) VALUES (?, ?, ?, ?, ?, ?, ?)
                        """, day_obj.to_tuple())
                        day_ids.append(cursor.lastrowid)
                    except ValueError as e:
                        return jsonify({"error": f"Error en {day_key}: {str(e)}"}), 400
                else:
                    day_ids.append(None)

            # Verify we have the required days
            for day_num in range(1, duration + 1):
                if day_ids[day_num - 1] is None:
                    return jsonify({
                        "error": f"El día {day_num} no puede ser NULL para duración {duration}"
                    }), 400

            try:
                plan = DietPlanComplete(
                    name=data['name'],
                    user_id=data['user_id'],
                    duration=duration,
                    diet_type_id=data['diet_type_id'],
                    day1=day_ids[0],
                    day2=day_ids[1],
                    day3=day_ids[2],
                    day4=day_ids[3],
                    day5=day_ids[4],
                    day6=day_ids[5],
                    day7=day_ids[6]
                )

                cursor.execute("""
                    INSERT INTO diet_plans_complete (
                        name, user_id, duration, diet_type_id,
                        day1, day2, day3, day4, day5, day6, day7
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, plan.to_tuple())

                plan_id = cursor.lastrowid
                conn.commit()

                return jsonify({
                    "message": "Plan de dieta creado exitosamente",
                    "plan_id": plan_id
                }), 201

            except ValueError as e:
                conn.rollback()
                return jsonify({"error": str(e)}), 400

    except sqlite3.Error as e:
        return jsonify({"error": f"Error de base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

@app.route('/get_diet_plans_by_user/<int:user_id>', methods=['GET'])
def get_diet_plans_by_user(user_id):
    try:
        with sqlite3.connect(DB_PATH) as conn:
            cursor = conn.cursor()
            cursor.execute("""
                SELECT id, name, user_id, duration, diet_type_id,
                       day1, day2, day3, day4, day5, day6, day7
                FROM diet_plans_complete
                WHERE user_id = ?
            """, (user_id,))
            rows = cursor.fetchall()

            if not rows:
                return jsonify([]), 200  # Lista vacía si no hay planes

            plans = []
            for row in rows:
                plan = {
                    "id": row[0],
                    "name": row[1],
                    "user_id": row[2],
                    "duration": row[3],
                    "diet_type_id": row[4],
                    "day1": row[5],
                    "day2": row[6],
                    "day3": row[7],
                    "day4": row[8],
                    "day5": row[9],
                    "day6": row[10],
                    "day7": row[11],
                }
                plans.append(plan)

            return jsonify(plans), 200

    except sqlite3.Error as e:
        return jsonify({"error": f"Error de base de datos: {str(e)}"}), 500


@app.route('/get_diet_plan/<int:plan_id>', methods=['GET'])
def get_diet_plan(plan_id):
    try:
        with sqlite3.connect(DB_PATH) as conn:
            conn.row_factory = sqlite3.Row
            cursor = conn.cursor()

            cursor.execute("SELECT * FROM diet_plans_complete WHERE id = ?", (plan_id,))
            plan_row = cursor.fetchone()

            if not plan_row:
                return jsonify({"error": "Plan no encontrado"}), 404

            days = {}
            for i in range(1, 8):
                day_id = plan_row[f'day{i}']
                if day_id:
                    cursor.execute("SELECT * FROM diet_plans_day WHERE id = ?", (day_id,))
                    day_row = cursor.fetchone()
                    if day_row:
                        days[f'day{i}'] = dict(day_row)

            return jsonify({
                "plan": dict(plan_row),
                "days": days
            }), 200

    except sqlite3.Error as e:
        return jsonify({"error": f"Error de base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

@app.route('/get_diet_plan_days_by_complete/<int:complete_plan_id>', methods=['GET'])
def get_diet_plan_days_by_complete(complete_plan_id):
    try:
        with sqlite3.connect(DB_PATH) as conn:
            cursor = conn.cursor()

            # Obtener los IDs de día desde diet_plans_complete
            cursor.execute("""
                SELECT day1, day2, day3, day4, day5, day6, day7
                FROM diet_plans_complete
                WHERE id = ?
            """, (complete_plan_id,))
            row = cursor.fetchone()

            if not row:
                return jsonify({"error": "Plan de dieta completo no encontrado"}), 404

            day_ids = [day_id for day_id in row if day_id is not None]

            if not day_ids:
                return jsonify([]), 200  # No hay días definidos

            # Obtener los datos de cada día
            placeholders = ','.join(['?'] * len(day_ids))
            cursor.execute(f"""
                SELECT id, breakfast_dish, breakfast_drink,
                       lunch_main_dish, lunch_side_dish, lunch_drink,
                       dinner_dish, dinner_drink
                FROM diet_plans_day
                WHERE id IN ({placeholders})
            """, day_ids)
            day_rows = cursor.fetchall()

            days = []
            for r in day_rows:
                day = {
                    "id": r[0],
                    "breakfast_dish": r[1],
                    "breakfast_drink": r[2],
                    "lunch_main_dish": r[3],
                    "lunch_side_dish": r[4],
                    "lunch_drink": r[5],
                    "dinner_dish": r[6],
                    "dinner_drink": r[7],
                }
                days.append(day)

            return jsonify(days), 200

    except sqlite3.Error as e:
        return jsonify({"error": f"Error de base de datos: {str(e)}"}), 500

@app.route('/update_diet_plan/<int:plan_id>', methods=['PATCH'])
def update_diet_plan(plan_id):
    try:
        data = request.get_json()

        with sqlite3.connect(DB_PATH) as conn:
            cursor = conn.cursor()
            cursor.execute("SELECT * FROM diet_plans_complete WHERE id = ?", (plan_id,))
            plan = cursor.fetchone()

            if not plan:
                return jsonify({"error": "Plan no encontrado"}), 404

            if 'name' in data or 'user_id' in data or 'duration' in data or 'diet_type_id' in data:
                try:
                    temp_plan = DietPlanComplete(
                        name=data.get('name', plan['name']),
                        user_id=data.get('user_id', plan['user_id']),
                        duration=data.get('duration', plan['duration']),
                        diet_type_id=data.get('diet_type_id', plan['diet_type_id']),
                        day1=plan['day1'],
                        day2=plan['day2'],
                        day3=plan['day3'],
                        day4=plan['day4'],
                        day5=plan['day5'],
                        day6=plan['day6'],
                        day7=plan['day7']
                    )

                    cursor.execute("""
                        UPDATE diet_plans_complete 
                        SET name = ?, user_id = ?, duration = ?, diet_type_id = ?
                        WHERE id = ?
                    """, (
                        temp_plan.name,
                        temp_plan.user_id,
                        temp_plan.duration,
                        temp_plan.diet_type_id,
                        plan_id
                    ))

                except ValueError as e:
                    return jsonify({"error": str(e)}), 400

            for day_num in range(1, 8):
                day_key = f'day{day_num}'
                if day_key in data:
                    day_id = plan[day_key]
                    if not day_id:
                        continue

                    try:
                        cursor.execute("SELECT * FROM diet_plans_day WHERE id = ?", (day_id,))
                        current_day = cursor.fetchone()

                        if not current_day:
                            return jsonify({"error": f"{day_key} no encontrado"}), 404

                        updated_day = DietPlanDay(
                            breakfast_dish=data[day_key].get('breakfast_dish', current_day['breakfast_dish']),
                            breakfast_drink=data[day_key].get('breakfast_drink', current_day['breakfast_drink']),
                            lunch_main_dish=data[day_key].get('lunch_main_dish', current_day['lunch_main_dish']),
                            lunch_side_dish=data[day_key].get('lunch_side_dish', current_day['lunch_side_dish']),
                            lunch_drink=data[day_key].get('lunch_drink', current_day['lunch_drink']),
                            dinner_dish=data[day_key].get('dinner_dish', current_day['dinner_dish']),
                            dinner_drink=data[day_key].get('dinner_drink', current_day['dinner_drink'])
                        )

                        cursor.execute("""
                            UPDATE diet_plans_day
                            SET breakfast_dish = ?,
                                breakfast_drink = ?,
                                lunch_main_dish = ?,
                                lunch_side_dish = ?,
                                lunch_drink = ?,
                                dinner_dish = ?,
                                dinner_drink = ?
                            WHERE id = ?
                        """, (
                            updated_day.breakfast_dish,
                            updated_day.breakfast_drink,
                            updated_day.lunch_main_dish,
                            updated_day.lunch_side_dish,
                            updated_day.lunch_drink,
                            updated_day.dinner_dish,
                            updated_day.dinner_drink,
                            day_id
                        ))

                    except ValueError as e:
                        conn.rollback()
                        return jsonify({"error": f"Error en {day_key}: {str(e)}"}), 400

            conn.commit()
            return jsonify({"message": "Plan actualizado exitosamente"}), 200

    except sqlite3.Error as e:
        conn.rollback()
        return jsonify({"error": f"Error de base de datos: {str(e)}"}), 500
    except Exception as e:
        conn.rollback()
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500


@app.route('/delete_diet_plan/<int:plan_id>', methods=['DELETE'])
def delete_diet_plan(plan_id):
    try:
        with sqlite3.connect(DB_PATH) as conn:
            conn.row_factory = sqlite3.Row
            cursor = conn.cursor()

            cursor.execute("SELECT * FROM diet_plans_complete WHERE id = ?", (plan_id,))
            plan_row = cursor.fetchone()

            if not plan_row:
                return jsonify({"error": "El plan con el ID proporcionado no existe"}), 404

            try:
                plan = DietPlanComplete(
                    name=plan_row['name'],
                    user_id=plan_row['user_id'],
                    duration=plan_row['duration'],
                    diet_type_id=plan_row['diet_type_id'],
                    day1=plan_row['day1'],
                    day2=plan_row['day2'],
                    day3=plan_row['day3'],
                    day4=plan_row['day4'],
                    day5=plan_row['day5'],
                    day6=plan_row['day6'],
                    day7=plan_row['day7']
                )
            except ValueError as e:
                return jsonify({"error": f"Datos inválidos en el plan: {str(e)}"}), 500

            day_ids = [plan_row[f'day{i}'] for i in range(1, 8) if plan_row[f'day{i}'] is not None]

            cursor.execute("DELETE FROM diet_plans_complete WHERE id = ?", (plan_id,))

            if day_ids:
                placeholders = ','.join(['?'] * len(day_ids))
                cursor.execute(f"DELETE FROM diet_plans_day WHERE id IN ({placeholders})", day_ids)

            conn.commit()

            return jsonify({
                "message": "Plan de dieta y días asociados eliminados exitosamente",
                "plan_id": plan_id,
                "deleted_days": day_ids
            }), 200

    except sqlite3.Error as e:
        conn.rollback()
        return jsonify({"error": f"Error de base de datos: {str(e)}"}), 500
    except Exception as e:
        conn.rollback()
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500


# --------------------------
# Endpoints de Cálculos Nutricionales
# --------------------------
@app.route("/barplot", methods=["POST"])
def barplot():
    data = request.get_json(force=True)
    if not data or 'dieta' not in data:
        return jsonify({'error': 'Falta el parámetro "dieta"'}), 400

    dieta = data['dieta']
    diet_total = []

    for food_group in dieta:
        plates_group = []
        for plate_data in food_group:
            plate = GraphData(plate_data, 1)
            plates_group.append(plate)
        diet_total.append(plates_group)

    res = nutritional_values_total(diet_total)

    valores = {
        "calorias": res[0],
        "carbohidratos": res[1],
        "proteinas": res[2],
        "grasas": res[3],
        "azucares": res[4],
        "sales": res[5],
        "precio": res[6]
    }

    return jsonify(valores)


@app.route("/basal", methods=["POST"])
def calculate_basal_metabolic_rate():
    data = request.get_json()
    weight = data.get("weight")
    height = data.get("height")
    age = data.get("age")
    gender = data.get("gender")

    try:
        w = float(weight)
        h = float(height)
        a = float(age)
    except (ValueError, TypeError):
        return jsonify({"result": -1})

    if gender.lower() == "m":
        result = 88.362 + (13.397 * w) + (4.799 * h) - (5.677 * a)
    elif gender.lower() == "f":
        result = 447.593 + (9.247 * w) + (3.098 * h) - (4.330 * a)
    else:
        result = 0

    return jsonify({"result": result})

# --------------------------
# Funciones auxiliares
# --------------------------

def select_user(email: str):
    if not isinstance(email, str):
        raise TypeError(f"Se esperaba un string, se recibió: {email}")

    sql = """SELECT * FROM users WHERE email = ?"""
    with sqlite3.connect(DB_PATH) as conn:
        cursor = conn.cursor()
        cursor.execute(sql, (email,))
        data = cursor.fetchone()
        if data:
            return User.of(data[1:])
        return None


# --------------------------
# Obtención de toda dieta asociada a un usuario con todos los datos
# --------------------------
@app.route('/get_all_diets_of_user_complete_information/<int:user_id>', methods=['GET'])
def get_all_diets_of_user_complete_information(user_id):
    try:
        with sqlite3.connect(DB_PATH) as conn:
            conn.row_factory = sqlite3.Row
            cursor = conn.cursor()

            # Obtener el usuario
            cursor.execute("SELECT * FROM users WHERE id = ?", (user_id,))
            user = cursor.fetchone()

            print(user)

            if not user:
                return jsonify({"error": "usuario no encontrado"}), 404

            # Imprimir los datos del usuario
            print("Usuario obtenido:")
            print(dict(user))  # Imprime los datos del usuario

            # Obtener los planes de dieta completos
            cursor.execute("SELECT * FROM diet_plans_complete WHERE user_id = ?", (user_id,))
            diet_plans_complete = cursor.fetchall()

            if not diet_plans_complete:
                return jsonify({
                    "user": dict(user),
                    "diet_plans_complete": [],
                    "days_values": []  # Añadimos la lista con los detalles de los días
                }), 201
            # Lista para almacenar los valores de los días de todos los planes de dieta
            all_days = []

            # Recorrer los planes de dieta y extraer los valores de cada día
            for diet_plan_complete in diet_plans_complete:
                # Lista para almacenar los valores de los días para este plan de dieta
                days_details = []
                days = [
                    diet_plan_complete['day1'],
                    diet_plan_complete['day2'],
                    diet_plan_complete['day3'],
                    diet_plan_complete['day4'],
                    diet_plan_complete['day5'],
                    diet_plan_complete['day6'],
                    diet_plan_complete['day7']
                ]

                # Recorrer los días y obtener los detalles de cada uno
                for day in days:
                    if day is not None:  # Verificamos si el día tiene un valor
                        # Obtener los detalles del día desde la tabla diet_plans_day
                        cursor.execute("SELECT * FROM diet_plans_day WHERE id = ?", (day,))
                        diet_plan_day = cursor.fetchone()  # Usamos fetchone ya que esperamos un solo resultado

                        if diet_plan_day:
                            # Lista para almacenar los campos relacionados con el plato
                            plate_fields = []
                            fields = [
                                'breakfast_dish',
                                'breakfast_drink',
                                'lunch_main_dish',
                                'lunch_side_dish',
                                'lunch_drink',
                                'dinner_dish',
                                'dinner_drink'
                            ]

                            # Para cada campo, consultamos la tabla plates usando su id
                            for field in fields:
                                plate_id = diet_plan_day[field]
                                if plate_id is not None:
                                    cursor.execute("SELECT * FROM plates WHERE id = ?", (plate_id,))
                                    plate = cursor.fetchone()
                                    if plate:
                                        plate_fields.append(dict(plate))  # Almacenamos el diccionario de la placa
                                    else:
                                        plate_fields.append(None)  # Si no encontramos la placa, agregamos None
                                else:
                                    plate_fields.append(None)  # Si el campo es None, agregamos None

                            # Añadimos los detalles del día a la lista de días
                            days_details.append({
                                "day_id": day,
                                "plates": plate_fields
                            })
                        else:
                            # Si no encontramos detalles del día, añadir None
                            days_details.append(None)
                    else:
                        # Si el día no está definido (es None), añadir None a la lista de días
                        days_details.append(None)

                # Añadimos los detalles de los días para este plan de dieta a la lista general
                all_days.append({
                    "diet_plan_id": diet_plan_complete['id'],
                    "diet_plan_name": diet_plan_complete['name'],
                    "days_details": days_details
                })

                # Imprimir los detalles de los días
                print(f"Plan de dieta '{diet_plan_complete['name']}' días: {days_details}")

            # Responder con los datos del usuario y los días de los planes de dieta
            return jsonify({
                "user": dict(user),
                "diet_plans_complete": [dict(diet_plan) for diet_plan in diet_plans_complete],
                "days_values": all_days  # Añadimos la lista con los detalles de los días
            }), 200

    except sqlite3.Error as e:
        return jsonify({"error": f"Error de base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

# --------------------------
# Obtención de todas las comidas creadas por un usuario
# --------------------------
@app.route('/get_all_user_plates/<int:user_id>', methods=['GET'])
def get_all_user_plates(user_id):  # Cambié el nombre de la función para que sea coherente con el endpoint
    try:
        with sqlite3.connect(DB_PATH) as conn:
            conn.row_factory = sqlite3.Row
            cursor = conn.cursor()

            # Obtener el usuario
            cursor.execute("SELECT * FROM users WHERE id = ?", (user_id,))
            user = cursor.fetchone()

            if not user:
                return jsonify({"error": "usuario no encontrado"}), 404

            # Obtener los platos del usuario
            cursor.execute("SELECT * FROM plates WHERE user_id = ?", (user_id,))
            plates = cursor.fetchall()

            if not plates:
                return jsonify({"error": "el usuario no tiene platos creados"}), 405

            # Convertir cada fila de platos en un diccionario
            plate_list = [dict(plate) for plate in plates]

            # Devolver los datos serializados
            return jsonify({
                "user": dict(user),
                "plates": plate_list
            }), 200

    except sqlite3.Error as e:
        return jsonify({"error": f"Error de base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

# Endpoint to get all plates where user_id is NULL
@app.route('/get_all_plates_where_user_id_is_null', methods=['GET'])
def get_all_plates_where_user_id_is_null():
    try:
        with sqlite3.connect(DB_PATH) as conn:
            conn.row_factory = sqlite3.Row
            cursor = conn.cursor()

            # Query plates where user_id is NULL
            cursor.execute("SELECT * FROM plates WHERE user_id IS NULL")
            plates = cursor.fetchall()

            if not plates:
                return jsonify({"message": "No plates found with user_id NULL"}), 200

            # Convert plates to list of dictionaries
            plate_list = [dict(plate) for plate in plates]

            return jsonify({
                "plates": plate_list
            }), 200

    except sqlite3.Error as e:
        return jsonify({"error": f"Database error: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Unexpected error: {str(e)}"}), 500

@app.route('/get_all_plates_where_user_id_is_either_users_or_null/<int:user_id>', methods=['GET'])
def get_all_plates_where_user_id_is_either_users_or_null(user_id):
    try:
        with sqlite3.connect(DB_PATH) as conn:
            conn.row_factory = sqlite3.Row
            cursor = conn.cursor()

            cursor.execute("SELECT * FROM plates WHERE user_id = ? OR user_id IS NULL", (user_id,))
            plates = cursor.fetchall()

            # Always return a plates array, empty if no plates found
            plate_list = [dict(plate) for plate in plates]

            return jsonify({
                "user_id": user_id,
                "plates": plate_list
            }), 200

    except sqlite3.Error as e:
        return jsonify({"error": f"Database error: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Unexpected error: {str(e)}"}), 500


@app.route('/calculate_diet_with_inputs', methods=['POST'])
def calculate_diet_with_inputs():
    try:
        # Log incoming request
        print("[INFO] Request received at /calculate_diet_with_inputs")
        data = request.get_json()
        print(f"[DEBUG] Raw JSON data: {data}")

        # Check for the 'values' key
        if "values" not in data:
            print("[ERROR] No 'values' key found in JSON")
            return jsonify({"error": "No values provided"}), 400

        # Extract and parse the values list
        values_list = json.loads(data["values"]) if isinstance(data["values"], str) else data["values"]
        print(f"[DEBUG] Parsed values list: {values_list}")

        # Validate the expected length (13 items)
        if not isinstance(values_list, list) or len(values_list) != 12:
            print(f"[ERROR] Invalid values list structure: {values_list}")
            return jsonify({"error": "Lista de valores inválida"}), 400

        try:
            # Generate the diet plan
            dieta = total_diet_generator(
                carbohydrates=float(values_list[0]),
                sugar=float(values_list[1]),
                energy=[float(values_list[2]), float(values_list[3])],
                protein=float(values_list[4]),
                salt=float(values_list[5]),
                fat=float(values_list[6]),
                price=float(values_list[8]),
                person_type=int(values_list[9]),
                person_preferences=1,
                total_days=int(values_list[7])
            )
            print(f"[DEBUG] Diet generated: {dieta}")
        except Exception as e:
            print(f"[ERROR] Error generating diet: {e}")
            return jsonify({"error": f"Error generating diet: {e}"}), 500

        # Validate the generated diet
        if not dieta or len(dieta) < int(values_list[7]):
            print(f"[ERROR] Insufficient diet length: {dieta}")
            return jsonify({"error": "No valid diet found"}), 404

        # Prepare diet data for database insertion
        try:
            diet_data = {
                "name": values_list[10],
                "user_id": int(values_list[11]),
                "duration": int(values_list[7]),
                "diet_type_id": int(values_list[9])
            }

            for day_num, day in enumerate(dieta, start=1):
                diet_data[f"day{day_num}"] = {
                    "breakfast_dish": day[0][0].plate_id,
                    "breakfast_drink": day[0][1].plate_id,
                    "lunch_main_dish": day[1][0].plate_id,
                    "lunch_side_dish": day[1][1].plate_id,
                    "lunch_drink": day[1][2].plate_id,
                    "dinner_dish": day[2][0].plate_id,
                    "dinner_drink": day[2][1].plate_id
                }
            print(f"[DEBUG] Diet data prepared for DB: {diet_data}")

            response, status_code = create_diet_plan_internal(diet_data)
            print(f"[DEBUG] Response from create_diet_plan_internal: {response}")
            if status_code != 201:
                print(f"[ERROR] Failed to save diet to DB, status code: {status_code}")
                return jsonify(response), status_code

            result = [diet_data[f"day{i + 1}"] for i in range(int(values_list[7]))]
            print(f"[DEBUG] Final API response: {result}")
            return jsonify(result), 200
        except Exception as e:
            print(f"[ERROR] Error preparing DB data or final response: {e}")
            return jsonify({"error": f"Error preparing diet data: {e}"}), 500

    except (ValueError, TypeError) as e:
        print(f"[ERROR] General data error: {e}")
        return jsonify({"error": f"Datos inválidos: {e}"}), 400
    except Exception as e:
        print(f"[ERROR] Internal server error: {e}")
        return jsonify({"error": f"Error interno: {e}"}), 500


def create_diet_plan_internal(data):
    try:
        # Validate required fields
        required_fields = ['name', 'user_id', 'duration', 'diet_type_id']
        for field in required_fields:
            if field not in data:
                return jsonify({"error": f"Campo requerido faltante: {field}"}), 400

        # Validate duration is between 1-7
        duration = data['duration']
        if not 1 <= duration <= 7:
            return jsonify({"error": "Duración debe estar entre 1 y 7 días"}), 400

        # Validate that required days exist
        for day_num in range(1, duration + 1):
            day_key = f'day{day_num}'
            if day_key not in data:
                return jsonify({"error": f"Se requiere día {day_num} para duración {duration}"}), 400

        day_ids = []

        with sqlite3.connect(DB_PATH) as conn:
            cursor = conn.cursor()

            # Process each day up to the duration
            for day_num in range(1, 8):
                day_key = f'day{day_num}'
                if day_key in data and day_num <= duration:
                    try:
                        day_obj = DietPlanDay(**data[day_key])
                        cursor.execute("""
                            INSERT INTO diet_plans_day (
                                breakfast_dish, breakfast_drink,
                                lunch_main_dish, lunch_side_dish, lunch_drink,
                                dinner_dish, dinner_drink
                            ) VALUES (?, ?, ?, ?, ?, ?, ?)
                        """, day_obj.to_tuple())
                        day_ids.append(cursor.lastrowid)
                    except ValueError as e:
                        return jsonify({"error": f"Error en {day_key}: {str(e)}"}), 400
                else:
                    day_ids.append(None)

            # Create the complete diet plan
            plan = DietPlanComplete(
                name=data['name'],
                user_id=int(data['user_id']),
                duration=duration,
                diet_type_id=int(data['diet_type_id']),
                day1=day_ids[0],
                day2=day_ids[1],
                day3=day_ids[2],
                day4=day_ids[3],
                day5=day_ids[4],
                day6=day_ids[5],
                day7=day_ids[6]
            )

            cursor.execute("""
                INSERT INTO diet_plans_complete (
                    name, user_id, duration, diet_type_id,
                    day1, day2, day3, day4, day5, day6, day7
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """, plan.to_tuple())

            plan_id = cursor.lastrowid
            conn.commit()

            return jsonify({"message": "Plan de dieta creado exitosamente", "plan_id": plan_id}), 201

    except sqlite3.Error as e:
        return jsonify({"error": f"Error de base de datos: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500

@app.route('/calculate_diet_with_user_data', methods=['POST'])
def calculate_diet_with_user_data():
    try:
        print("[INFO] Request received at /calculate_diet_with_user_data")
        data = request.get_json()
        print(f"[DEBUG] Raw JSON data: {data}")

        # Convert 'requirements' to a list of integers
        requirements_str = data.get("requirements", "[]")
        try:
            # Parse the JSON string if necessary
            if isinstance(requirements_str, str):
                requirements_list = json.loads(requirements_str)
            else:
                requirements_list = requirements_str

            # Extract numerical requirements (user_id, diet_type_id, duration)
            requirements = list(map(int, requirements_list[:3]))

            # Extract the plan name, default to "prueba" if not provided
            plan_name = requirements_list[3] if len(requirements_list) > 3 else "prueba"
            print(f"[DEBUG] Extracted requirements: {requirements}, Plan name: {plan_name}")

        except (json.JSONDecodeError, ValueError, TypeError) as e:
            return jsonify({"error": f"Invalid 'requirements' format: {e}"}), 400
        print(f"[DEBUG] Extracted and converted requirements: {requirements}")

        # Fetch user data
        user_response, status_code = get_user(requirements[0])
        if status_code != 200:
            return jsonify(user_response), status_code

        user_dict = dict(user_response.get_json())
        print(f"[DEBUG] User data as dict: {user_dict}")

        sexs_list = ['f', 'm']
        try:
            nutritional_requirements = calculate_nutritional_requirements(
                weight=user_dict.get('weight'),
                height=user_dict.get('height'),
                age=(date.today().year - int(user_dict.get('birthday').split('-')[0])),
                gender=sexs_list[int(user_dict.get('sex'))],
                activity_level=user_dict.get('physical_activity'),
                goal=['lose', 'maintain', 'gain'][int(user_dict.get('goal')) - 1]
            )
            print(f"[DEBUG] Nutritional requirements: {nutritional_requirements}")

            # Genera la dieta usando los requerimientos calculados
            if not isinstance(nutritional_requirements, list) or len(nutritional_requirements) != 6:
                print(f"[ERROR] Requerimientos nutricionales inválidos: {nutritional_requirements}")
                return jsonify({"error": "Error en los requerimientos nutricionales"}), 400

            dieta = total_diet_generator(
                carbohydrates=float(nutritional_requirements[0]),
                sugar=float(nutritional_requirements[1]),
                energy=[float(nutritional_requirements[2][0]), float(nutritional_requirements[2][1])],
                protein=float(nutritional_requirements[3]),
                salt=float(nutritional_requirements[4]),
                fat=float(nutritional_requirements[5]),
                price=float(100),
                person_type=int(requirements[1]),
                person_preferences=1,
                total_days=int(requirements[2])
            )
            print(f"[DEBUG] Dieta generada: {dieta}")

            duration = int(requirements[2])
            if not 1 <= duration <= 7:
                return jsonify({"error": "Duración debe estar entre 1 y 7 días"}), 400

            # Validar que la dieta generada tiene la cantidad correcta de días
            if len(dieta) != duration:
                return jsonify({"error": "La dieta generada no coincide con la duración especificada"}), 400

            # Guardar los días de dieta
            with sqlite3.connect(DB_PATH) as conn:
                cursor = conn.cursor()
                day_ids = []
                for day in dieta:
                    cursor.execute("""
                        INSERT INTO diet_plans_day (
                            breakfast_dish, breakfast_drink,
                            lunch_main_dish, lunch_side_dish, lunch_drink,
                            dinner_dish, dinner_drink
                        ) VALUES (?, ?, ?, ?, ?, ?, ?)
                    """, (day[0][0].plate_id, day[0][1].plate_id, day[1][0].plate_id, day[1][1].plate_id, day[1][2].plate_id, day[2][0].plate_id, day[2][1].plate_id))
                    day_ids.append(cursor.lastrowid)
                # Crear el plan de dieta completo
                plan = DietPlanComplete(
                    name=plan_name,
                    user_id=int(requirements[0]),
                    duration=duration,
                    diet_type_id=int(requirements[1]),
                    day1=day_ids[0] if duration > 0 else None,
                    day2=day_ids[1] if duration > 1 else None,
                    day3=day_ids[2] if duration > 2 else None,
                    day4=day_ids[3] if duration > 3 else None,
                    day5=day_ids[4] if duration > 4 else None,
                    day6=day_ids[5] if duration > 5 else None,
                    day7=day_ids[6] if duration > 6 else None
                )
                cursor.execute("""
                    INSERT INTO diet_plans_complete (
                        name, user_id, duration, diet_type_id,
                        day1, day2, day3, day4, day5, day6, day7
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, plan.to_tuple())
                conn.commit()

                print(f"[INFO] Plan de dieta creado exitosamente con ID: {cursor.lastrowid}")
                return jsonify({"message": "Plan de dieta creado exitosamente", "plan_id": cursor.lastrowid}), 201

        except sqlite3.Error as e:
            print(f"[ERROR] Error de base de datos: {str(e)}")
            return jsonify({"error": f"Error de base de datos: {str(e)}"}), 500

    except Exception as e:
        print(f"[ERROR] Error inesperado: {str(e)}")
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500


@app.route('/create_plate', methods=['POST'])  # Eliminamos el parámetro de la ruta
def create_plate():
    try:
        print("[INFO] Request received at /create_plate")
        data = request.get_json()
        print(f"[DEBUG] Raw JSON data: {data}")

        try:
            # Guardar los días de dieta
            with sqlite3.connect(DB_PATH) as conn:
                cursor = conn.cursor()
                cursor.execute("""
                    INSERT INTO plates (
                        name, user_id,
                        calories, carbohydrates, proteins,
                        fats, sugar, sodium, price,
                        type, vegan, vegetarian,
                        celiac, halal
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, [
                    data["name"], data["user_id"],
                    data["calories"], data["carbohydrates"], data["proteins"],
                    data["fats"], data["sugar"], data["sodium"], data["price"],
                    data["type"], data["vegan"], data["vegetarian"],
                    data["celiac"], data["halal"]
                ])

                print(f"[INFO] Plato creado exitosamente con ID: {cursor.lastrowid}")
                return jsonify({"message": "Plato creado exitosamente", "plate_id": cursor.lastrowid}), 201

        except sqlite3.Error as e:
            print(f"[ERROR] Error de base de datos: {str(e)}")
            return jsonify({"error": f"Error de base de datos: {str(e)}"}), 500

    except Exception as e:
        print(f"[ERROR] Error inesperado: {str(e)}")
        return jsonify({"error": f"Error inesperado: {str(e)}"}), 500


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=8000)