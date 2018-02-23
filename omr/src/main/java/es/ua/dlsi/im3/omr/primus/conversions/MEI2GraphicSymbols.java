package es.ua.dlsi.im3.omr.primus.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.io.AgnosticExporter;
import es.ua.dlsi.im3.omr.model.Constants;
import es.ua.dlsi.im3.omr.model.pojo.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MEI2GraphicSymbols {
    private final AgnosticExporter export = new AgnosticExporter();

    /**
     *
     * @param inputFile MEI File
     * @param outputFile
     */
    public void convert(File inputFile, File outputFile) throws ImportException, ExportException {
        MEISongImporter importer = new MEISongImporter();
        ScoreSong scoreSong = importer.importSong(inputFile);

        FileWriter fw = null;
        try {
            fw = new FileWriter(outputFile);
            BufferedWriter bw = new BufferedWriter(fw);
            List<GraphicalToken> graphicalTokens = export.convert(scoreSong).getTokens();
            for (int i = 0; i< graphicalTokens.size(); i++) {
                if (i>0) {
                    bw.write(Constants.SEMANTIC_TOKEN_SEPARATOR);
                }
                bw.write(graphicalTokens.get(i).toString());
            }
            bw.close();
        } catch (IOException | IM3Exception e) {
            throw new ExportException(e);
        }
    }


    public void run(List<File> files) {
        int i=0;
        int n = files.size();
        for (File file: files) {
            System.out.println("Processing " + i + "/" + n);
            i++;

            MEISongImporter importer = new MEISongImporter();
            ScoreGraphicalDescriptionWriter writer = new ScoreGraphicalDescriptionWriter();
            AgnosticExporter export = new AgnosticExporter();
            try {
                ScoreSong scoreSong = importer.importSong(file);
                ScoreGraphicalDescription graficalDescription = export.convert(scoreSong);
                File outputFile = new File(file.getParent(), FileUtils.getFileNameWithoutExtension(file.getName()) + ".agnostic");
                writer.write(outputFile, graficalDescription.getTokens());

                File outputFileSemantic = new File(file.getParent(), FileUtils.getFileNameWithoutExtension(file.getName()) + ".semantic");
                writer.write(outputFileSemantic, graficalDescription.getSemanticTokens());

            } catch (Exception e) {
                System.err.print("---------------------------------------------------------------");
                System.err.print("Error processing " + file.getAbsolutePath());
                e.printStackTrace(System.err);
            }
        }
    }

    /**
     * @param args
     */
    public static final void main(String [] args) {
        if (args.length != 1) {
            System.err.println("Use: MEI2GraphicSymbols <mei files folder (it leaves here the output file with extension .agnostic and .semantic)>");
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

        new MEI2GraphicSymbols().run(files);
    }
}
