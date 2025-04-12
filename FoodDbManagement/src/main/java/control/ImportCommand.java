package control;

import io.*;
import model.Plate;
import ui.ImportDialog;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class ImportCommand implements Command{
    private final ImportDialog dialog;

    public ImportCommand(ImportDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void execute() {
        try(PlateWriter writer = PlateDatabaseWriter.open(new File("DietApp.db"));
                PlateReader reader = new PlateFileReader(new PlateCsvDeserializer(), dialog.get())) {
            doExecute(reader, writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doExecute(PlateReader reader, PlateWriter writer) throws IOException {
        while(true) {
            Plate plate = reader.read();
            if(plate == null) break;
            writer.write(plate);
        }
    }
}
