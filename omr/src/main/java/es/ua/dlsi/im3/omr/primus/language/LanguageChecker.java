package es.ua.dlsi.im3.omr.primus.language;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.language.GraphicalSymbolsAutomaton;
import es.ua.dlsi.im3.omr.primus.conversions.MEI2GraphicSymbols;
import es.ua.dlsi.im3.omr.primus.conversions.Token;
import org.apache.commons.math3.fraction.Fraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * It reads MEI files from PRIMUS, converts to a sequence of graphical symbols and checks that the GraphicaSymbolsAutomaton is able to accept the input.
 * It outputs thorugh the standard output the files with probability 0, and through error output the ones that the MEI importer cannot read
 * @author drizo
 */
public class LanguageChecker {
    public static final void main(String [] args) {
        if (args.length != 1) {
            System.err.println("Use: LanguageChecker <mei files folder>");
            return;
        }

        File folder = new File(args[0]);
        if (!folder.exists()) {
            System.err.println("The folder " + folder.getAbsolutePath() + " does not exist");
            return;
        }
        ArrayList<File> files = new ArrayList<>();
        try {
            FileUtils.readFiles(folder, files, "mei", true);
        } catch (IOException e) {
            System.err.println("Error reading files: " + e);
            return;
        }

        LanguageChecker checker = new LanguageChecker();
        try {
            checker.run(files);
        } catch (IM3Exception e) {
            System.err.println("Error checking: " + e.getMessage());
            return;
        }
    }

    private void run(ArrayList<File> files) throws IM3Exception {
        GraphicalSymbolsAutomaton automaton = new GraphicalSymbolsAutomaton();
        MEI2GraphicSymbols converter = new MEI2GraphicSymbols();

        System.out.println("------Files with 0 probability -------");
        System.err.println("----- Cannot read the following files ------");

        for (File file: files) {
            MEISongImporter importer = new MEISongImporter();
            try {
                ScoreSong scoreSong = importer.importSong(file);
                List<Token> tokenList = converter.convert(scoreSong);
                Fraction p = automaton.probabilityOfTokens(tokenList);
                if (p.getNumerator() == 0) {
                    System.out.println(file.getAbsolutePath() + "\t" + tokenList);
                } else {
                    System.out.println(p);
                }
            } catch (ImportException e) {
                System.err.println(file.getAbsolutePath());
                e.printStackTrace(System.err);
                System.err.println("\n------\n");
            }
        }



    }
}
