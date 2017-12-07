package es.ua.dlsi.im3.omr.primus.conversions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ScoreGraphicalDescriptionWriter {
    private static final char TAB = '\t';

    public void write(File outputFile, List tokens) throws IOException {
        FileWriter fileWriter = new FileWriter(outputFile);

        for (Object graphicalToken: tokens) {
            fileWriter.write(graphicalToken.toString());
            fileWriter.write(TAB);
        }

        fileWriter.close();

    }
}
