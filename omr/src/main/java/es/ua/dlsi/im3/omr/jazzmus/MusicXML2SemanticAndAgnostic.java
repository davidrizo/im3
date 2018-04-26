package es.ua.dlsi.im3.omr.jazzmus;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.utils.FileUtils;
import es.ua.dlsi.im3.omr.encoding.Encoder;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticExporter;
import es.ua.dlsi.im3.omr.encoding.agnostic.AgnosticVersion;
import es.ua.dlsi.im3.omr.encoding.semantic.SemanticExporter;

import java.io.File;
import java.io.IOException;

/**
 * It takes a MusicXML with system break tags and exports it to a semantic and agnostic file, where
 * each line contains an staff. It leaves the file in the same folder than the input xml file
 * @autor drizo
 */
public class MusicXML2SemanticAndAgnostic {
    public static final void main(String [] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("Missing MusicXML file name");
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            throw new Exception("Input file '" + file.getAbsolutePath() + "' does not exist");
        }

        String fileName = FileUtils.getFileWithoutPathOrExtension(file);
        File semantic = new File(file.getParent(), fileName + ".semantic");
        File agnostic = new File(file.getParent(), fileName + ".agnostic");

        new MusicXML2SemanticAndAgnostic().run(file, semantic, agnostic);
    }

    public void run(File file, File semantic, File agnostic) throws IM3Exception, IOException {
        MusicXMLImporter importer = new MusicXMLImporter();
        ScoreSong song = importer.importSong(file);

        Encoder encoder = new Encoder(AgnosticVersion.v2);
        SemanticExporter semanticExporter = new SemanticExporter();
        AgnosticExporter agnosticExporter = new AgnosticExporter();
        encoder.encode(song);
        semanticExporter.export(encoder.getSemanticEncoding(), semantic);
        agnosticExporter.export(encoder.getAgnosticEncoding(), agnostic);
    }
}
