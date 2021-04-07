package es.ua.dlsi.im3.omr.language;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.Encoder;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticEncoding;
import es.ua.dlsi.im3.omr.language.modern.GraphicalModernSymbolsAutomaton;
import org.apache.commons.math3.fraction.BigFraction;

import java.io.*;
import java.util.ArrayList;

/**
 * It reads MEI files from PRIMUS, converts to a sequence of graphical symbols and checks that the GraphicaSymbolsAutomaton is able to accept the input.
 * It outputs thorugh the standard output the files with probability 0, and through error output the ones that the MEI importer cannot read
 * @author drizo
 */
public class LanguageChecker {
    public static final void main(String [] args) {
        if (args.length != 2) {
            System.err.println("Use: LanguageChecker <mei files folder> <output folder>");
            return;
        }

        File inputFolder = new File(args[0]);
        if (!inputFolder.exists()) {
            System.err.println("The folder " + inputFolder.getAbsolutePath() + " does not exist");
            return;
        }
        ArrayList<File> files = new ArrayList<>();
        try {
            FileUtils.readFiles(inputFolder, files, "mei", true);
        } catch (IOException e) {
            System.err.println("Error reading files: " + e);
            return;
        }

        File outputFolder = new File(args[1]);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
            return;
        }

        LanguageChecker checker = new LanguageChecker();
        try {
            checker.run(files, outputFolder);
        } catch (Exception e) {
            System.err.println("Error checking: " + e.getMessage());
            return;
        }
    }

    private void run(ArrayList<File> files, File outputFolder) throws IOException {
        //MEI2GraphicSymbols converter = new MEI2GraphicSymbols();

        PrintStream errors = new PrintStream(new FileOutputStream(new File(outputFolder, "errors.txt")));
        PrintStream ok = new PrintStream(new FileOutputStream(new File(outputFolder, "ok.txt")));
        PrintStream notAccepted = new PrintStream(new FileOutputStream(new File(outputFolder, "notaccepted.txt")));

        int n=files.size();
        int i=1;
        int nok=0;
        int nko=0;
        int nerrorsImport = 0;
        for (File file: files) {
            System.out.println("Processing " + i + "/" + n);
            i++;

            MEISongImporter importer = new MEISongImporter();
            try {
                GraphicalModernSymbolsAutomaton automaton = new GraphicalModernSymbolsAutomaton();

                ScoreSong scoreSong = importer.importSong(file);
                Encoder encoder = new Encoder(false, false, false);
                encoder.encode(scoreSong);
                AgnosticEncoding agnosticEncoding = encoder.getAgnosticEncoding();
                //List<GraphicalToken> graphicalTokenList = export.convert(scoreSong).getTokens();

                BigFraction p = automaton.probabilityOf(agnosticEncoding.getSymbols(), false).getProbability();
                if (p.getNumeratorAsLong() == 0) {
                    notAccepted.println(file.getAbsolutePath() + "\t" + agnosticEncoding.getSymbols());
                    nko++;
                } else {
                    ok.println(p + "\t" + file.getAbsolutePath() + "\t" + agnosticEncoding.getSymbols());
                    nok++;
                }
            } catch (Throwable e) {
                errors.println(file.getAbsolutePath());
                e.printStackTrace(errors);
                errors.println("\n------\n");
                nerrorsImport++;
            }
        }

        errors.close();
        ok.close();
        notAccepted.close();

        System.out.println("-------------------------");
        System.out.println("Total: " + n);
        System.out.println("Accepted: " + nok);
        System.out.println("No accepted: " + nko);
        System.out.println("Errors importing: " + nerrorsImport);
        System.out.println("-------------------------");

    }
}
