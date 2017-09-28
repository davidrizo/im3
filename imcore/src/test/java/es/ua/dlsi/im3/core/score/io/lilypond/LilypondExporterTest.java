package es.ua.dlsi.im3.core.score.io.lilypond;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class LilypondExporterTest {
    //TODO Just check it does not crash
    @Test
    public void exportSong() throws Exception {
        MusicXMLImporter importer = new MusicXMLImporter();
        File file = TestFileUtils.getFile("/testdata/core/score/io/simple1.xml");
        ScoreSong song = importer.importSong(file);
        LilypondExporter exporter = new LilypondExporter();
        File lyFile = TestFileUtils.createTempFile("simple.ly");
        exporter.exportSong(lyFile, song);
    }

}