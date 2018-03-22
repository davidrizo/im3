package es.ua.dlsi.im3.omr.primus.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.conversions.ScoreToPlayed;
import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.played.PlayedSong;
import es.ua.dlsi.im3.core.played.io.MidiSongExporter;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.io.ScoreSongImporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.Encoder;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticExporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticExporter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Formerly MEI2GraphicsSymbosl
 */
public class PrIMuSGenerator {
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


    public void run(AgnosticVersion agnosticVersion, List<File> files) {
        int i=0;
        int n = files.size();
        for (File file: files) {
            System.out.println("Processing " + i + "/" + n);
            i++;

            //MEISongImporter importer = new MEISongImporter();
            ScoreSongImporter importer = new ScoreSongImporter();
            //ScoreGraphicalDescriptionWriter writer = new ScoreGraphicalDescriptionWriter();
            Encoder encoder = new Encoder(agnosticVersion);
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

                ScoreToPlayed scoreToPlayed = new ScoreToPlayed();
                PlayedSong played = scoreToPlayed.createPlayedSongFromScore(scoreSong);
                MidiSongExporter midiSongExporter = new MidiSongExporter();
                File midiOutput = new File(file.getParent(), FileUtils.getFileNameWithoutExtension(file.getName()) + ".mid");
                midiSongExporter.exportSong(midiOutput, played);
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
        if (args.length != 2) {
            System.err.println("Use: MEI2GraphicsSymbosl {v1, v2} <mei or musicxml files folder (it leaves here the output file with extension .agnostic and .semantic)>");
            return;
        }

        AgnosticVersion agnosticVersion;
        switch (args[0]) {
            case "v1":
                agnosticVersion = AgnosticVersion.v1;
                break;
            case "v2":
                agnosticVersion = AgnosticVersion.v2;
                break;
            default:
                System.err.println("Invalid version, shoud be v1 or v2, and it is '" + args[0] + "'");
                return;
        }

        File inputFolder = new File(args[1]);
        if (!inputFolder.exists()) {
            System.err.println("The folder " + inputFolder.getAbsolutePath() + " does not exist");
            return;
        }
        ArrayList<File> files = new ArrayList<>();
        try {
            String [] extensions = new String [] {"mei", "xml"};
            FileUtils.readFiles(inputFolder, files, extensions, true);
        } catch (IOException e) {
            System.err.println("Error reading files: " + e);
            return;
        }

        new PrIMuSGenerator().run(agnosticVersion, files);
    }
}
