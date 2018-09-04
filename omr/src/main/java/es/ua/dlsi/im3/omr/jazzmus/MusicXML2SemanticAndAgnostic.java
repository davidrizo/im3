package es.ua.dlsi.im3.omr.jazzmus;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.Encoder;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticExporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticExporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * It takes a MusicXML with system break tags and exports it to a semantic and agnostic file, where
 * each line contains an staff. It leaves the file in the same folder than the input xml file
 * @autor drizo
 */
public class MusicXML2SemanticAndAgnostic {
    public static final void main(String [] args) throws Exception {
        String inputFolder;
        if (args.length > 1) {
            throw new Exception("Missing folder with MusicXML files");
        } else if (args.length == 0){
            inputFolder = "/Users/drizo/Documents/GCLOUDUA/HISPAMUS/repositorios/jazzmus/dataset";
        } else {
            inputFolder = args[0];
        }
        File folder = new File(inputFolder);
        if (!folder.exists()) {
            throw new Exception("Input folder '" + folder.getAbsolutePath() + "' does not exist");
        }

        ArrayList<File> files = new ArrayList<>();
        FileUtils.readFiles(folder, files, "xml", true);

        for (File file: files) {
            System.out.println("Exporting " + file.getAbsoluteFile());
            String fileName = FileUtils.getFileWithoutPathOrExtension(file);
            File semantic = new File(file.getParent(), fileName + ".semantic");
            File agnostic = new File(file.getParent(), fileName + ".agnostic");
            File kern = new File(file.getParent(), fileName + ".kern"); //TODO kern extendido
            new MusicXML2SemanticAndAgnostic().run(file, semantic, agnostic, kern);
        }
    }

    public void run(File file, File semantic, File agnostic, File kern) throws IM3Exception, IOException {
        try {
            MusicXMLImporter importer = new MusicXMLImporter();
            ScoreSong song = importer.importSong(file);

            Encoder encoder = new Encoder(AgnosticVersion.v2, true);
            SemanticExporter semanticExporter = new SemanticExporter();
            AgnosticExporter agnosticExporter = new AgnosticExporter();
            encoder.encode(song);
            semanticExporter.export(encoder.getSemanticEncoding(), semantic);
            agnosticExporter.export(encoder.getAgnosticEncoding(), agnostic);

            KernExporter kernExporter = new KernExporter();
            kernExporter.exportSong(kern, song);
        } catch (Throwable e) {
            System.err.println("Error with file " + file.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
