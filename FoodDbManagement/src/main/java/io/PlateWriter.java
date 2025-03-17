package io;

import model.Plate;

import java.io.IOException;

public interface PlateWriter extends AutoCloseable{
    void write(Plate plate) throws IOException;
}
