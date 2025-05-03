
from flask import Flask, jsonify, request
import os
import sqlite3

from model.User import User, validate_email
from configuration.config import DB_PATH
from configuration.config import PHYSICAL_ACTIVITY_LEVELS, SEX_VALUES
from database.database_utils import create_database
from model.DietPlanDay import DietPlanDay
from model.DietPlanComplete import DietPlanComplete



if not os.path.exists(DB_PATH):
    raise Exception("Database is not available, must be created through create_database.py")

SEX_DICT = {}
for i in range(len(SEX_VALUES)):
    SEX_DICT[SEX_VALUES[i]] = i

app = Flask(__name__)


@app.route('/create_diet_plan', methods=['POST'])
def create_diet_plan():
    try:
        data = request.get_json()

        # Validar y crear los días primero
        days_data = []
        day_ids = []

        with sqlite3.connect(DB_PATH) as conn:
            cursor = conn.cursor()

            # Procesar cada día (day1 a day7)
            for i in range(1, 8):
                day_key = f'day{i}'
                if day_key in data:
                    try:
                        # Validar y crear el objeto DietPlanDay
                        day_obj = DietPlanDay(
                            breakfast_dish=data[day_key]['breakfast_dish'],
                            breakfast_drink=data[day_key]['breakfast_drink'],
                            lunch_main_dish=data[day_key]['lunch_main_dish'],
                            lunch_side_dish=data[day_key]['lunch_side_dish'],
                            lunch_drink=data[day_key]['lunch_drink'],
                            dinner_dish=data[day_key]['dinner_dish'],
                            dinner_drink=data[day_key]['dinner_drink']
                        )

                        # Insertar en la base de datos
                        cursor.execute("""
                            INSERT INTO diet_plan_day (
                                breakfast_dish, breakfast_drink,
                                lunch_main_dish, lunch_side_dish, lunch_drink,
                                dinner_dish, dinner_drink
                            ) VALUES (?, ?, ?, ?, ?, ?, ?)
                        """, (
                            day_obj.breakfast_dish,
                            day_obj.breakfast_drink,
                            day_obj.lunch_main_dish,
                            day_obj.lunch_side_dish,
                            day_obj.lunch_drink,
                            day_obj.dinner_dish,
                            day_obj.dinner_drink
                        ))
                        day_ids.append(cursor.lastrowid)
                    except ValueError as e:
                        return jsonify({"error": f"Error en {day_key}: {str(e)}"}), 400
                else:
                    day_ids.append(None)

            # Validar y crear el plan de dieta completo
            try:
                plan = DietPlanComplete(
                    name=data['name'],
                    user_id=data['user_id'],
                    duration=data['duration'],
                    diet_type_id=data['diet_type_id'],
                    day1=day_ids[0],
                    day2=day_ids[1],
                    day3=day_ids[2],
                    day4=day_ids[3],
                    day5=day_ids[4],
                    day6=day_ids[5],
                    day7=day_ids[6]
                )

                # Insertar el plan completo
                cursor.execute("""
                    INSERT INTO diet_plans_complete (
                        name, user_id, duration, diet_type_id,
                        day1, day2, day3, day4, day5, day6, day7
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, (
                    plan.name,
                    plan.user_id,
                    plan.duration,
                    plan.diet_type_id,
                    plan.day1,
                    plan.day2,
                    plan.day3,
                    plan.day4,
                    plan.day5,
                    plan.day6,
                    plan.day7
                ))

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

            # Obtener el plan completo
            cursor.execute("SELECT * FROM diet_plans_complete WHERE id = ?", (plan_id,))
            plan_row = cursor.fetchone()

            if not plan_row:
                return jsonify({"error": "Plan no encontrado"}), 404

            # Obtener los días asociados
            days = {}
            for i in range(1, 8):
                day_id = plan_row[f'day{i}']
                if day_id:
                    cursor.execute("SELECT * FROM diet_plan_day WHERE id = ?", (day_id,))
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

            # Verificar que el plan existe
            cursor.execute("SELECT * FROM diet_plans_complete WHERE id = ?", (plan_id,))
            plan = cursor.fetchone()

            if not plan:
                return jsonify({"error": "Plan no encontrado"}), 404

            # Actualizar campos del plan si están presentes
            if 'name' in data or 'user_id' in data or 'duration' in data or 'diet_type_id' in data:
                try:
                    # Crear objeto temporal para validación
                    temp_plan = DietPlanComplete(
                        name=data.get('name', plan['name']),
                        user_id=data.get('user_id', plan['user_id']),
                        duration=data.get('duration', plan['duration']),
                        diet_type_id=data.get('diet_type_id', plan['diet_type_id']),
                        day1=plan['day1'],  # Los días no se actualizan aquí
                        day2=plan['day2'],
                        day3=plan['day3'],
                        day4=plan['day4'],
                        day5=plan['day5'],
                        day6=plan['day6'],
                        day7=plan['day7']
                    )

                    # Actualizar en BD
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

            # Actualizar días si están en el payload
            for day_num in range(1, 8):
                day_key = f'day{day_num}'
                if day_key in data:
                    day_id = plan[day_key]
                    if not day_id:
                        continue  # No se puede actualizar un día que no existe

                    try:
                        # Obtener día actual
                        cursor.execute("SELECT * FROM diet_plan_day WHERE id = ?", (day_id,))
                        current_day = cursor.fetchone()

                        if not current_day:
                            return jsonify({"error": f"{day_key} no encontrado"}), 404

                        # Crear objeto con valores actualizados
                        updated_day = DietPlanDay(
                            breakfast_dish=data[day_key].get('breakfast_dish', current_day['breakfast_dish']),
                            breakfast_drink=data[day_key].get('breakfast_drink', current_day['breakfast_drink']),
                            lunch_main_dish=data[day_key].get('lunch_main_dish', current_day['lunch_main_dish']),
                            lunch_side_dish=data[day_key].get('lunch_side_dish', current_day['lunch_side_dish']),
                            lunch_drink=data[day_key].get('lunch_drink', current_day['lunch_drink']),
                            dinner_dish=data[day_key].get('dinner_dish', current_day['dinner_dish']),
                            dinner_drink=data[day_key].get('dinner_drink', current_day['dinner_drink'])
                        )

                        # Actualizar en BD
                        cursor.execute("""
                            UPDATE diet_plan_day
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

            # 1. Primero obtener el plan para validar su existencia
            cursor.execute("SELECT * FROM diet_plans_complete WHERE id = ?", (plan_id,))
            plan_row = cursor.fetchone()

            if not plan_row:
                return jsonify({"error": "El plan con el ID proporcionado no existe"}), 404

            # 2. Convertir a objeto DietPlanComplete para validación (opcional)
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

            # 3. Recoger todos los IDs de días asociados (no nulos)
            day_ids = [plan_row[f'day{i}'] for i in range(1, 8) if plan_row[f'day{i}'] is not None]

            # 4. Eliminar el plan principal
            cursor.execute("DELETE FROM diet_plans_complete WHERE id = ?", (plan_id,))

            # 5. Eliminar los días asociados (si existen)
            if day_ids:
                # Usar parámetros dinámicos para la consulta IN
                placeholders = ','.join(['?'] * len(day_ids))
                cursor.execute(f"DELETE FROM diet_plan_day WHERE id IN ({placeholders})", day_ids)

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


@app.route('/create_user', methods=['POST'])
def create_user():
    try:
        data = request.get_json()
        user = User.from_dict(data)
        print(user)

        with sqlite3.connect(DB_PATH) as conn:
            cursor = conn.cursor()
            cursor.execute("""
            INSERT INTO users(email, password, physical_activity, sex, birthday, height, weight)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """, user.get_values())
            conn.commit()
        return jsonify({"message": "Usuario creado exitosamente",
                        "user": user.to_dict()}), 201
    except (TypeError, ValueError) as e:
        return jsonify({"error": str(e)}), 400
    except sqlite3.DatabaseError as e:
        return jsonify({"error": str(e)}), 500


@app.route('/get_user/<string:email>', methods=['GET'])
def get_user(email):
    try:
        user = select_user(email)
        if not user:
            return jsonify({"error": "no existe un usuario con el email proporcionado"}), 404
        return jsonify({"message": "Usuario encontrado",
                        "user": user.to_dict()})
    except (ValueError, TypeError) as e:
        return jsonify({"error": str(e)}), 400
    except sqlite3.DatabaseError as e:
        return jsonify({'error:', str(e)}), 500


@app.route('/delete_user/<string:email>', methods=['DELETE'])
def delete_user(email):
    try:
        user = select_user(email)
        if not user:
            return jsonify({"error": "El usuario con el email proporcionado no existe"}), 404
        with sqlite3.connect(DB_PATH) as conn:
            cursor = conn.cursor()
            cursor.execute("DELETE FROM users WHERE email=?", (email,))
            conn.commit()
        return jsonify({"message": "Usuario eliminado exitosamente",
                        "user": user.to_dict()})
    except (TypeError, ValueError) as e:
        return jsonify({"error": str(e)}), 400
    except sqlite3.DatabaseError as e:
        return jsonify({'error', str(e)}), 500


@app.route('/update_user/<string:email>', methods=['PATCH'])
def update_user(email):
    try:
        user = select_user(email)
        if not user:
            return jsonify({'error': f'El usuario con email {email} no existe'}), 400

        data = request.get_json()

        # Solo actualizamos los campos que estén presentes
        fields = []
        values = []

        for field in data.keys():
            if field not in User.get_fields():
                raise ValueError(f"Data contained a non contemplated field: {field}\n"
                                 f'expected fields: {User.get_fields()}')
            fields.append(str(field) + ' = ?')
            values.append(data[field])

        if fields:
            # Construir la sentencia UPDATE dinámicamente
            sql = f"UPDATE users SET {', '.join(fields)} WHERE email = ?"
            values.append(email)

            with sqlite3.connect(DB_PATH) as conn:
                cursor = conn.cursor()
                cursor.execute(sql, tuple(values))
                conn.commit()
                user = select_user(email)
                if user:
                    return jsonify({"message": "Usuario actualizado exitosamente",
                                    "user": user.to_dict()})
                else:
                    return jsonify({"error": "El usuario con el id proporcionado no existe"}), 404
        else:
            return jsonify({"message": "No se proporcionaron datos para actualizar"}), 400

    except (ValueError, TypeError) as e:
        return jsonify({"error": str(e)}), 400
    except sqlite3.DatabaseError as e:
        return jsonify({'error': str(e)}), 500

def select_user(email: str):
    # Returns a User if exists in database. Otherwise, returns None or throws Exception
    if type(email) != str:
        raise TypeError(f"Expected a string, got: {email}")

    sql = """SELECT * FROM users WHERE email = ?"""
    with sqlite3.connect(DB_PATH) as conn:
        cursor = conn.cursor()
        cursor.execute(sql, (email,))
        data = cursor.fetchone()
        if data:
            return User.of(data[1:])
        else:
            return None


if __name__ == "__main__":
    app.run(debug=True, port=8000)

