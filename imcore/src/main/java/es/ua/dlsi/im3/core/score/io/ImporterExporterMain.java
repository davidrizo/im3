package es.ua.dlsi.im3.core.score.io;

import es.ua.dlsi.im3.core.io.ExportException;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongImporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;

import java.io.File;

/**
 * It converts from format to format
 * Created by drizo on 24/7/17.
 */
public class ImporterExporterMain {
    public static void main(String [] args) throws ImportException, ExportException {
        if (args.length != 4) {
            System.err.println("Use: es.ua.dlsi.im3.core.score.io.ImporterExporterMain <type> <fromfile> <type> <tofile>\n"+
            " where <type> is [musicxml,mei]");
            return;
        }

        ScoreSong from = readScoreSong(args[0], args[1]);
        writeScoreSong(from, args[2], args[3]);
        System.out.println("Exported");
    }

    private static void writeScoreSong(ScoreSong from, String type, String filename) throws ImportException, ExportException {
        if (type.equals("mei")) {
            MEISongExporter exporter = new MEISongExporter();
            exporter.exportSong(new File(filename), from);
        } else {
            throw new ImportException("Export type " + type + " no available");
        }
    }

    private static ScoreSong readScoreSong(String type, String filename) throws ImportException {
        ScoreSong result;
        if (type.equals("musicxml")) {
            MusicXMLImporter importer = new MusicXMLImporter();
            result = importer.importSong(new File(filename));
            return result;
        } else if (type.equals("mei")) {
            MEISongImporter importer = new MEISongImporter();
            result = importer.importSong(new File(filename));
            return result;
        } else {
            throw new ImportException("Import type " + type + " no available");
        }
    }

}
