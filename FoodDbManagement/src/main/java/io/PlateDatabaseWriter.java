package io;

import model.Plate;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static java.sql.Types.*;

public class PlateDatabaseWriter implements PlateWriter{
    private final Connection connection;
    private final PreparedStatement insertPlatePreparedStatement;

    public PlateDatabaseWriter(Connection connection) throws SQLException {
        this.connection = connection;
        stopAutoCommit(connection);
        this.createTable();
        insertPlatePreparedStatement = connection.prepareStatement(insertPlateStatement);
    }

    private static void stopAutoCommit(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
    }

    private static final String createTable = """
            CREATE TABLE IF NOT EXISTS plates(
            name TEXT PRIMARY KEY,
            calories INTEGER NOT NULL,
            carbohydrates INTEGER NOT NULL,
            protein INTEGER NOT NULL,
            fats INTEGER NOT NULL,
            sugar INTEGER NOT NULL,
            sodium INTEGER NOT NULL,
            price FLOAT NOT NULL,
            type INT INTEGER NOT NULL,
            vegan INTEGER NOT NULL,
            vegetarian INTEGER NOT NULL,
            celiac INTEGER NOT NULL
            )
            """;

    private static final String insertPlateStatement = """
            INSERT or REPLACE INTO plates (name, calories, carbohydrates, protein, fats, sugar, sodium, price, type, vegan, vegetarian, celiac)
            VALUES (?,?,?,?,?,?,?,ROUND(?, 2),?,?,?,?)
            """;

    private void createTable() throws SQLException {
        connection.createStatement().executeUpdate("DROP TABLE IF EXISTS plates");
        connection.createStatement().execute(createTable);
    }

    public PlateDatabaseWriter(String connection) throws SQLException {
        this(DriverManager.getConnection(connection));
    }

    public static PlateDatabaseWriter open(File file) throws SQLException {
        return new PlateDatabaseWriter("jdbc:sqlite:"+file.getAbsolutePath());
    }

    private record Parameter(int index, Object value, int type) {}

    @Override
    public void write(Plate plate) throws IOException {
        try {
            insertPlatePreparedStatementFor(plate).execute();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    private PreparedStatement insertPlatePreparedStatementFor(Plate plate) throws SQLException {
        insertPlatePreparedStatement.clearParameters();
        parameterOf(plate).forEach(this::define);
        return insertPlatePreparedStatement;
    }

    private void define(Parameter parameter) {
        try {
            if(parameter.value == null) {
                insertPlatePreparedStatement.setNull(parameter.index, parameter.type);
            }else
                insertPlatePreparedStatement.setObject(parameter.index, parameter.value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Parameter> parameterOf(Plate plate) {
        return List.of(
                new Parameter(1, plate.getName(), NVARCHAR),
                new Parameter(2, plate.getCalories(), INTEGER),
                new Parameter(3, plate.getCarbohydrates(), INTEGER),
                new Parameter(4, plate.getProtein(), INTEGER),
                new Parameter(5, plate.getFats(), INTEGER),
                new Parameter(6, plate.getSugar(), INTEGER),
                new Parameter(7, plate.getSodium(), INTEGER),
                new Parameter(8, plate.getPrice(), FLOAT),
                new Parameter(9, plate.getType(), INTEGER),
                new Parameter(10, plate.isVegan() ? 1 : 0, INTEGER),
                new Parameter(11, plate.isVegetarian() ? 1 : 0, INTEGER),
                new Parameter(12, plate.isCeliac() ? 1 : 0, INTEGER)
        );
    }

    @Override
    public void close() throws Exception {
        connection.commit();
        connection.close();
    }
}
