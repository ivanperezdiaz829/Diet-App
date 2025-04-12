package io;

import model.Plate;

import java.io.*;

public class PlateFileReader implements PlateReader{
    private final BufferedReader reader;
    private final PlateDeserializer deserializer;

    public PlateFileReader(PlateDeserializer deserializer, File file) throws IOException {
        this.deserializer = deserializer;
        this.reader = getBufferedReaderFrom(file);
        skipHeader();
    }

    private void skipHeader() throws IOException {
        reader.readLine();
    }

    private BufferedReader getBufferedReaderFrom(File file) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(getFileInputStreamFrom(file)));
    }

    private FileInputStream getFileInputStreamFrom(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    @Override
    public Plate read() throws IOException {
        return deserialize(reader.readLine());
    }

    private Plate deserialize(String line) {
        return line != null ? deserializer.deserialize(line) : null;
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }
}
