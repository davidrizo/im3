package es.ua.dlsi.im3.core.score.io.mei;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.SimpleMultiMeasureRest;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by drizo on 24/7/17.
 */
public class MEIImporterTest {
    @Test
    public void importSong3() throws Exception {
        File f3 = TestFileUtils.getFile("/testdata/core/score/io/mei/000051802-1_1_1-nokey.mei");
        MEISongImporter importer = new MEISongImporter();
        ScoreSong song = importer.importSong(f3);
        assertEquals(1, song.getStaves().size());
        assertEquals(1, song.getStaves().get(0).getClefs().size());
        assertEquals(3, song.getMeaureCount());
        assertEquals(0, song.getStaves().get(0).getKeySignatures().size());
    }


    @Test
    public void importIncipitsRISM() throws Exception {
        File f3 = TestFileUtils.getFile("/testdata/core/score/io/mei/000051650-1_1_1.mei");
        MEISongImporter importer = new MEISongImporter();
        ScoreSong song = importer.importSong(f3);
        assertEquals(1, song.getStaves().size());

        MEISongExporter exporter = new MEISongExporter();
        exporter.exportSong(new File("/tmp/zzz.mei"), song);
    }

    //TOO Import Export Tests
    @Test
    public void importMultiMeasureRest() throws Exception {
        File f3 = TestFileUtils.getFile("/testdata/core/score/io/mei/000051650-1_1_2.mei");
        MEISongImporter importer = new MEISongImporter();
        ScoreSong song = importer.importSong(f3);
        assertEquals(1, song.getStaves().size());
        assertEquals(14, song.getMeaureCount());
        assertTrue(song.getStaves().get(0).getAtoms().get(0) instanceof SimpleMultiMeasureRest);
    }

}