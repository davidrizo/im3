package es.ua.dlsi.im3.cmme;

import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.score.*;
import es.ua.dlsi.im3.core.score.clefs.ClefC1;
import es.ua.dlsi.im3.core.score.clefs.ClefC3;
import es.ua.dlsi.im3.core.score.clefs.ClefC4;
import es.ua.dlsi.im3.core.score.clefs.ClefF3;
import es.ua.dlsi.im3.core.score.io.kern.KernExporter;
import es.ua.dlsi.im3.core.score.io.mei.MEISongExporter;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCutTime;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class CMMESongImporterTest {
    @Test
    public void importSong() throws Exception {
        CMMESongImporter importer = new CMMESongImporter();
        File file = TestFileUtils.getFile("/testdata/cmme/missa-mort-et-fortune_01kyrie.cmme.xml");
        ScoreSong scoreSong = importer.importSong(file);
        assertEquals("Composer", "Jachet Berchem", scoreSong.getComposer());

        assertEquals("Staves", 4, scoreSong.getStaves().size());

        assertEquals("Staff 1", "Cantus", scoreSong.getStaves().get(0).getName());
        assertEquals("Part 1", "Cantus", scoreSong.getParts().get(0).getName());
        assertTrue("Cantus first clef", scoreSong.getStaves().get(0).getClefAtTime(Time.TIME_ZERO) instanceof ClefC1);

        assertEquals("Staff 2", "Altus", scoreSong.getStaves().get(1).getName());
        assertEquals("Part 2", "Altus", scoreSong.getParts().get(1).getName());
        assertTrue("Altus first clef", scoreSong.getStaves().get(1).getClefAtTime(Time.TIME_ZERO) instanceof ClefC3);

        assertEquals("Staff 3", "Tenor", scoreSong.getStaves().get(2).getName());
        assertEquals("Part 3", "Tenor", scoreSong.getParts().get(2).getName());
        assertTrue("Tenor first clef", scoreSong.getStaves().get(2).getClefAtTime(Time.TIME_ZERO) instanceof ClefC4);

        assertEquals("Staff 4", "Bassus", scoreSong.getStaves().get(3).getName());
        assertEquals("Part 4", "Bassus", scoreSong.getParts().get(3).getName());
        assertTrue("Bassus first clef", scoreSong.getStaves().get(3).getClefAtTime(Time.TIME_ZERO) instanceof ClefF3);

        assertTrue("First meter sign", scoreSong.getUniqueMeterWithOnset(Time.TIME_ZERO) instanceof TimeSignatureCutTime);

        List<Atom> cantusAtoms = scoreSong.getParts().get(0).getAtomsSortedByTime();
        assertTrue("First event in cantus", cantusAtoms.get(0) instanceof SimpleRest);
        assertEquals("Duration of first cantus event", new Time(4, 1), cantusAtoms.get(0).getDuration());

        assertTrue("Second event in cantus", cantusAtoms.get(1) instanceof SimpleNote);
        assertEquals("Duration of second cantus event", new Time(2, 1), cantusAtoms.get(1).getDuration());
        assertEquals("Pitch of second cantus event", new ScientificPitch(PitchClasses.A, 4), ((SimpleNote)cantusAtoms.get(1)).getPitch());

        assertTrue("11th event in cantus", cantusAtoms.get(10) instanceof SimpleNote);
        assertEquals("Duration of second cantus event", new Time(3, 2), cantusAtoms.get(10).getDuration());
        assertEquals("Pitch of second cantus event", new ScientificPitch(PitchClasses.C, 4), ((SimpleNote)cantusAtoms.get(10)).getPitch());
        assertEquals("Dots of 11th event in cantus", 1, ((SimpleNote) cantusAtoms.get(10)).getAtomFigure().getDots());


        MEISongExporter meiSongExporter = new MEISongExporter();
        meiSongExporter.exportSong(new File("/tmp/cmme.mei"), scoreSong);

        KernExporter kernExporter = new KernExporter();
        kernExporter.exportSong(new File("/tmp/cmme.krn"), scoreSong);
    }

}