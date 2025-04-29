def sql_sentences(cursor, carbohydrates, sugar, energy, protein, salt, fat, price, meal_type, sentence_type, sub_sentence, mult):

    if sentence_type == 1:
        sql = ("SELECT * FROM plates WHERE type == ? AND "
               "carbohydrates * ? BETWEEN ? AND ? "
               "AND sugar * ? <= ? "
               "AND calories * ? BETWEEN ? AND ? "
               "AND protein * ? BETWEEN ? AND ? "
               "AND sodium * ? <= ? "
               "AND fats * ? BETWEEN ? AND ? "
               "AND price * ? <= ?")

        cursor.execute(sql, (meal_type,
                             mult, carbohydrates[0] / mult, carbohydrates[1] * mult,
                             mult, sugar * mult,
                             mult, energy[0] / mult, energy[1] * mult,
                             mult, protein[0] / mult, protein[1] * mult,
                             mult, salt * mult,
                             mult, fat[0] / mult, fat[1] * mult,
                             mult, price * mult))
        return cursor.fetchall()

    if sentence_type == 2:
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
