CREATE TABLE physical_activity_levels(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    description TEXT NOT NULL
);


CREATE TABLE users(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    physical_activity INTEGER NOT NULL,
    sex INTEGER NOT NULL CHECK(sex IN (0, 1)),
    birthday DATE NOT NULL CHECK(birthday < CURRENT_DATE),
    height INTEGER NOT NULL CHECK({MIN_HEIGHT} <= height AND height <= {MAX_HEIGHT}),
    weight INTEGER NOT NULL CHECK({MIN_WEIGHT} <= weight AND weight <= {MAX_WEIGHT}),
    FOREIGN KEY (physical_activity) REFERENCES physical_activity_levels(id) ON DELETE RESTRICT
);

CREATE TABLE plate_type(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    description TEXT NOT NULL
);


CREATE TABLE plates(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    user_id INTEGER DEFAULT NULL,
    calories INTEGER NOT NULL,
    carbohydrates INTEGER NOT NULL,
    proteins INTEGER NOT NULL,
    fats INTEGER NOT NULL,
    sugar INTEGER NOT NULL,
    sodium REAL NOT NULL,
    price REAL NOT NULL,
    type INTEGER NOT NULL,
    vegan INTEGER NOT NULL CHECK(vegan IN(0, 1)),
    vegetarian INTEGER NOT NULL CHECK(vegetarian IN(0, 1)),
    celiac INTEGER NOT NULL CHECK(celiac IN(0, 1)),
    halal INTEGER NOT NULL CHECK(halal IN(0, 1)),
    FOREIGN KEY (type) REFERENCES plate_type(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
