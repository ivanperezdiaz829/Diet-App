from flask import Flask, request, jsonify, send_file
from Graphs import *
from ObtainTotals import *
import ast
from InfoForGraph import *
import os
from werkzeug.security import generate_password_hash, check_password_hash
import sqlite3
import re
from model.User import User, validate_email
from configuration.config import DB_PATH, PHYSICAL_ACTIVITY_LEVELS, SEX_VALUES
from model.DietPlanDay import DietPlanDay
from model.DietPlanComplete import DietPlanComplete

# Inicialización única de la aplicación Flask
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
            boolean_fields = ['vegan', 'vegetarian', 'celiac', 'halal']
            for field in boolean_fields:
                plate_data[field] = bool(plate_data[field])

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

@app.route('/create_diet_plan', methods=['POST'])
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

@app.route('/calculate', methods=['POST'])
def calculate_diet():
    try:
        data = request.get_json()

        if "values" not in data:
            return jsonify({"error": "No values provided"}), 400

        values_str = data["values"]
        values_list = ast.literal_eval(values_str)

        if not isinstance(values_list, list) or len(values_list) < 10:
            return jsonify({"error": "Lista de valores inválida"}), 400

        values = [float(i) for i in values_list]

        dieta = total_diet_generator(
            carbohydrates=values[0],
            sugar=values[1],
            energy=[values[2], values[3]],
            protein=values[4],
            salt=values[5],
            fat=values[6],
            price=values[8],
            person_type=int(values[9]),
            person_preferences=1,
            total_days=int(values[7])
        )

        if not dieta or len(dieta) < int(values[7]):
            return jsonify({"error": "No valid diet found"}), 404

        result = []
        for day in dieta:
            result.append({
                "breakfast_dish": day[0][0].plate_id,
                "breakfast_drink": day[0][1].plate_id,
                "lunch_main_dish": day[1][0].plate_id,
                "lunch_side_dish": day[1][1].plate_id,
                "lunch_drink": day[1][2].plate_id,
                "dinner_dish": day[2][0].plate_id,
                "dinner_drink": day[2][1].plate_id
            })

        return jsonify(result)

    except (ValueError, TypeError) as e:
        return jsonify({"error": f"Datos inválidos: {str(e)}"}), 400
    except Exception as e:
        return jsonify({"error": f"Error interno: {str(e)}"}), 500


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
# Punto de entrada principal
# --------------------------

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=8000)
