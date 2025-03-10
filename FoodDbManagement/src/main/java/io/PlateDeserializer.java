package io;

import model.Plate;

public interface PlateDeserializer {
    Plate deserialize(String line);
}
