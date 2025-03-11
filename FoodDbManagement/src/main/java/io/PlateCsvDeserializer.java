package io;

import model.Plate;

public class PlateCsvDeserializer implements PlateDeserializer {

    @Override
    public Plate deserialize(String line) {
        return deserialize(line.split(","));
    }

    private Plate deserialize(String[] fields) {
        return new Plate(
                fields[0],
                toInt(fields[1]),
                toInt(fields[2]),
                toInt(fields[3]),
                toInt(fields[4]),
                toInt(fields[5]),
                toInt(fields[6]),
                toFloat(fields[7]),
                toInt(fields[8]),
                toBoolean(fields[9]),
                toBoolean(fields[10]),
                toBoolean(fields[11])
        );
    }

    private boolean toBoolean(String field) {
        return Integer.parseInt(field) == 1;
    }

    private float toFloat(String field) {
        return !(field.equals("\\N")) ? Float.parseFloat(field) : (float) -1.;
    }

    private int toInt(String field) {
        return !(field.equals("\\N")) ? Integer.parseInt(field) : -1;
    }
}
