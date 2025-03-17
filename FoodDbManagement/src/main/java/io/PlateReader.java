package io;

import model.Plate;

import java.io.IOException;

public interface PlateReader extends AutoCloseable{
    Plate read() throws IOException;
}
