import sqlite3


def sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, meal_type, sentence_type, sub_sentence):

    if sentence_type == 1:
        sql = ("SELECT * FROM plates WHERE type == ? AND "
               "carbohydrates BETWEEN ? AND ? "
               "AND sugar BETWEEN ? AND ? "
               "AND calories BETWEEN ? AND ? "
               "AND protein BETWEEN ? AND ? "
               "AND sodium BETWEEN ? AND ? "
               "AND fats BETWEEN ? AND ? "
               "AND price <= ?")

        cursor.execute(sql, (meal_type,
                             carbohydrates[0], carbohydrates[1],
                             sugar[0], sugar[1],
                             energy[0], energy[1],
                             protein[0], protein[1],
                             salt[0], salt[1],
                             fat[0], fat[1],
                             price))
        return cursor.fetchall()

    elif sentence_type == 2:
        columns = {
            1: "carbohydrates",
            2: "sugar",
            3: "calories",
            4: "protein",
            5: "sodium",
            6: "fats",
            7: "price",
        }
        column = columns.get(sub_sentence)

        if column:
            sql = f"SELECT SUM({column}) FROM plates WHERE type = ?"
            cursor.execute(sql, (meal_type,))
            return cursor.fetchone()[0]

    return None
