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
    private AgnosticSymbolType convert(String from) throws ImportException {
        switch (from) {
            case "barline":
                return new VerticalLine();
            case "beam":
                return new Unknown();
            case "brevis":
                return new Note(NoteFigures.breve);
            case "brevis_rest":
                return new Rest(RestFigures.breve);
            case "c_clef":
                return new Clef(ClefNote.C);
            case "coloured_brevis":
                return new Note(NoteFigures.breveBlack);
            case "coloured_minima":
                return new Note(NoteFigures.quarter);
            case "coloured_semibrevis":
                return new Note(NoteFigures.wholeBlack);
            case "coloured_semiminima":
                return new Note(NoteFigures.eighth);
            case "common_time":
                return new MeterSign(MeterSigns.C);
            case "custos":
                return new Custos();
            case "cut_time":
                return new MeterSign(MeterSigns.C);
            case "dot":
                return new Dot();
            case "double_barline":
                return new Unknown();
            case "f_clef_1":
                return new Clef(ClefNote.F);
            case "f_clef_2":
                return new Clef(ClefNote.Fpetrucci);
            case "fermata":
                return new Fermata();
            case "flat":
                return new Accidental(Accidentals.flat);
            case "g_clef":
                return new Clef(ClefNote.G);
            case "ligature":
                return new Unknown();
            case "longa":
                return new Note(NoteFigures.longa);
            case "longa_rest":
                return new Rest(RestFigures.longa);
            case "minima":
                return new Note(NoteFigures.half);
            case "minima_rest":
                return new Note(NoteFigures.half);
            case "proportio_maior":
                return new MeterSign(MeterSigns.CZ); // it was incorrect
            case "proportio_minor":
                return new MeterSign(MeterSigns.CcutZ); // it was incorrect
            case "semibrevis":
                return new Note(NoteFigures.whole);
            case "semibrevis_rest":
                return new Rest(RestFigures.whole);
            case "semiminima":
                return new Note(NoteFigures.eighthVoid);
            case "semiminima_rest":
                return new Rest(RestFigures.seminima);
            case "sharp":
                return new Accidental(Accidentals.sharp);
            case "undefined":
                return new Unknown();
            default:
                throw new ImportException("Unsupported '" + from + "'");
        }
    }

    public void convertTrainingSetFile(File input, File output) throws IOException, ImportException {
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

            AgnosticSymbolType symbol = convert(label);

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
