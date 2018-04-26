package es.ua.dlsi.im3.omr.conversions;

import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticSymbolType;
import es.ua.dlsi.im3.omr.encoding.agnostic.agnosticsymbols.*;
import es.ua.dlsi.im3.omr.encoding.enums.ClefNote;
import es.ua.dlsi.im3.omr.encoding.enums.MeterSigns;

import java.io.*;

/**
 * It converts the old graphical symbols training set built using bimodal information (image + strokes) (ISMIR 2017)
 * to a new format adapted to the new agnostic symbols naming
 *
 * @autor drizo
 */
public class Calco2AgnosticSymbolsTraining {
    public void convertTrainingSetFile(File input, File output) throws IOException, ImportException {
        Calco2Agnostic calco2Agnostic = new Calco2Agnostic();
        PrintStream printStream = new PrintStream(output);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(input));
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line=br.readLine())!=null) {
            System.out.println(line);
            String [] components = line.split(":");
            if (components.length != 3) {
                throw new IOException("Invalid line, must have 3 components and it has just " + components.length);
            }
            String label = components[0];
            String strokes = components[1];
            String grayscalePixels = components[2];

            AgnosticSymbolType symbol = calco2Agnostic.convert(label);

            printStream.print(symbol.toAgnosticString());
            printStream.print(':');
            printStream.print(strokes);
            printStream.print(':');
            printStream.println(grayscalePixels);
        }
        isr.close();
        printStream.close();
        System.out.println("FINISHED!!");

    }

    public static final void main(String [] args) throws IOException, ImportException {
        // Used once, this is why it is not parametrized
        File input = new File("/Users/drizo/Documents/investigacion/hispamus/muret/proyectos_ejemplo_muret/mensural/data.train");
        File output = new File("/Users/drizo/Documents/GCLOUDUA/HISPAMUS/trainingsets/manuscript_mensural_ismir2017.train");
        new Calco2AgnosticSymbolsTraining().convertTrainingSetFile(input, output);
    }
}
