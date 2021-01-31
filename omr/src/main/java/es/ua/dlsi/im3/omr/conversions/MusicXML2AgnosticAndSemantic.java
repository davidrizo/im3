package es.ua.dlsi.im3.omr.conversions;

import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.Encoder;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticExporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticExporter;

import java.io.File;
import java.util.ArrayList;

/**
 * It takes a MusicXML with system break tags and exports it to a semantic and agnostic file, where
 * each line contains an staff. It leaves the file in the same folder than the input xml file.
 *
 * When notation type is mensural, it converts the semantic codes to mensural.
 * @autor drizo
 */
public class MusicXML2AgnosticAndSemantic {

    public void convertFolder(String inputFolder, NotationType notationType) throws Exception {
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
            new MusicXML2AgnosticAndSemantic().run(file, semantic, agnostic, kern, notationType);
        }
    }

    public void run(File file, File semantic, File agnostic, File kern, NotationType notationType)  {
        try {
            MusicXMLImporter importer = new MusicXMLImporter();
            ScoreSong song = importer.importSong(file);

            if (notationType == NotationType.eMensural) {
                convertToMensural(song);
            }

            Encoder encoder = new Encoder(AgnosticVersion.v2, true, true, true);
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

    private void convertToMensural(ScoreSong song) {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
