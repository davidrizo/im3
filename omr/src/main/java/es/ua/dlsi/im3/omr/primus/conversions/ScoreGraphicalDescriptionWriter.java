package es.ua.dlsi.im3.omr.primus.conversions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ScoreGraphicalDescriptionWriter {
    private static final char TAB = '\t';

    public void write(File outputFile, ScoreGraphicalDescription scoreGraphicalDescription) throws IOException {
        FileWriter fileWriter = new FileWriter(outputFile);

        for (GraphicalToken graphicalToken: scoreGraphicalDescription.getTokens()) {
            fileWriter.write(graphicalToken.toString());
            fileWriter.write(TAB);
        }

        fileWriter.close();

    }
}
