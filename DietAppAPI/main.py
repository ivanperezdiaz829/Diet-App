from flask import Flask, jsonify, request
import os
import sqlite3
from model.User import User, validate_email
from configuration.config import DB_PATH
from configuration.config import PHYSICAL_ACTIVITY_LEVELS, SEX_VALUES
from database.database_utils import create_database

"""
if not os.path.exists(DB_PATH):
    raise Exception("Database is not available, must be created through create_database.py")

SEX_DICT = {}
for i in range(len(SEX_VALUES)):
    SEX_DICT[SEX_VALUES[i]] = i

app = Flask(__name__)


@app.route('/create_user', methods=['POST'])
def create_user():
    try:
        data = request.get_json()
        user = User.from_dict(data)
        print(user)

        with sqlite3.connect(DB_PATH) as conn:
            cursor = conn.cursor()
            cursor.execute(
            INSERT INTO users(email, password, physical_activity, sex, birthday, height, weight)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            , user.get_values())
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
"""

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
    #app.run(debug=True, port=8000)
    create_database()
