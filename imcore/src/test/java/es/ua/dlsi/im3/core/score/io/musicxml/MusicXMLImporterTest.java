package es.ua.dlsi.im3.core.score.io.musicxml;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.ScoreSong;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by drizo on 24/7/17.
 */
public class MusicXMLImporterTest {
    @Test
    public void importSong3() throws Exception {
        File f3 = TestFileUtils.getFile("/testdata/core/score/io/3.xml");
        MusicXMLImporter importer = new MusicXMLImporter();
        ScoreSong song = importer.importSong(f3);
        assertEquals(2, song.getStaves().size());
        assertEquals(1, song.getStaves().get(0).getClefs().size());
        assertEquals(1, song.getStaves().get(1).getClefs().size());
    }

    @Test
    public void importChordSequence() throws Exception {
        File f3 = TestFileUtils.getFile("/testdata/core/score/io/chord_sequence.xml");
        MusicXMLImporter importer = new MusicXMLImporter();
        ScoreSong song = importer.importSong(f3);
        assertEquals(1, song.getStaves().size());
        assertEquals(4, song.getStaves().get(0).getAtoms().size());

        assertEquals(1, song.getStaves().get(0).getAtoms().get(0).getAtomPitches().size());
        assertEquals(2, song.getStaves().get(0).getAtoms().get(1).getAtomPitches().size());
        assertEquals(2, song.getStaves().get(0).getAtoms().get(2).getAtomPitches().size());
        assertEquals(2, song.getStaves().get(0).getAtoms().get(3).getAtomPitches().size());
    }

}