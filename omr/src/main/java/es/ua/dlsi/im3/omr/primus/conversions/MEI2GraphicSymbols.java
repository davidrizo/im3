package es.ua.dlsi.im3.omr.primus.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.Encoder;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticExporter;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticExporter;
import es.ua.dlsi.im3.omr.model.Constants;
import es.ua.dlsi.im3.omr.model.pojo.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MEI2GraphicSymbols {
    /**
     * @deprecated
     * @param inputFile MEI File
     * @param outputFile
     */
    public void convert(File inputFile, File outputFile) throws ImportException, ExportException {
        MEISongImporter importer = new MEISongImporter();
        ScoreSong scoreSong = importer.importSong(inputFile);

        //FileWriter fw = null;
        try {
            //fw = new FileWriter(outputFile);
            //BufferedWriter bw = new BufferedWriter(fw);
            Encoder encoder = new Encoder();
            encoder.encode(scoreSong);
            SemanticExporter exporter = new SemanticExporter();
            exporter.export(encoder.getSemanticEncoding(), outputFile);
            /*List<GraphicalToken> graphicalTokens = export.convert(scoreSong).getTokens();
            for (int i = 0; i< graphicalTokens.size(); i++) {
                if (i>0) {
                    bw.write(Constants.SEMANTIC_TOKEN_SEPARATOR);
                }
                bw.write(graphicalTokens.get(i).toString());
            }
            bw.close();*/
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
            //ScoreGraphicalDescriptionWriter writer = new ScoreGraphicalDescriptionWriter();
            Encoder encoder = new Encoder();
            AgnosticExporter agnosticExporter = new AgnosticExporter();
            SemanticExporter semanticExporter = new SemanticExporter();
            try {
                ScoreSong scoreSong = importer.importSong(file);
                encoder.encode(scoreSong);

                File outputFile = new File(file.getParent(), FileUtils.getFileNameWithoutExtension(file.getName()) + ".agnostic");
                agnosticExporter.export(encoder.getAgnosticEncoding());
                //writer.write(outputFile, graficalDescription);

                File outputFileSemantic = new File(file.getParent(), FileUtils.getFileNameWithoutExtension(file.getName()) + ".semantic");
                semanticExporter.export(encoder.getSemanticEncoding(), outputFileSemantic);
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
