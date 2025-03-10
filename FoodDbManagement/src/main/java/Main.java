import control.ImportCommand;
import ui.ImportDialog;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        new ImportCommand(importDialog()).execute();
    }

    private static ImportDialog importDialog() {
        return () -> new File("food_data.csv");
    }
}
