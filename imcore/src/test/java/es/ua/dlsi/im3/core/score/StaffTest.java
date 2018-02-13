package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.clefs.*;
import es.ua.dlsi.im3.core.score.io.musicxml.MusicXMLImporter;
import es.ua.dlsi.im3.core.score.layout.coresymbols.components.Accidental;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class StaffTest {
    void test(String message, Staff staff, Clef clef, PositionInStaff positionInStaff, DiatonicPitch dp, int octave) throws IM3Exception {
        PositionInStaff computedPositionInStaff = staff.computePositionInStaff(clef, dp, octave);
        assertEquals(message + ", computed position in staff", positionInStaff, computedPositionInStaff);

        // the reverse operation
        ScientificPitch computedScientificPitch = staff.computeScientificPitch(clef, positionInStaff);
        assertEquals(message + ", computed diatonic pitch", dp, computedScientificPitch.getPitchClass().getNoteName());
        assertEquals(message + ", computed octave", octave, computedScientificPitch.getOctave());
    }
    @Test
    public void computeLineSpacePitch() throws Exception {
        ClefG2 g2 = new ClefG2();
        ScoreSong song = new ScoreSong();
        Pentagram pentagram = new Pentagram(song, "1", 1);
        // TODO: 5/10/17 más tests, incluidos ledger lines
        test("C5 in ClefG2", pentagram, g2, PositionsInStaff.SPACE_3, DiatonicPitch.C, 5);
        test("E4 in ClefG2", pentagram, g2, PositionsInStaff.LINE_1, DiatonicPitch.E, 4);
        test("F5 in ClefG2", pentagram, g2, PositionsInStaff.LINE_5, DiatonicPitch.F, 5);

        test("ClefG2OctAlta, G", pentagram, new ClefG2OttavaAlta(), PositionsInStaff.LINE_2, DiatonicPitch.G, 5);
        test("ClefG2OctBassa, G", pentagram, new ClefG2OttavaBassa(), PositionsInStaff.LINE_2, DiatonicPitch.G, 3);
        test("ClefG2QuindAlta, G", pentagram, new ClefG2QuindicesimaAlta(), PositionsInStaff.LINE_2, DiatonicPitch.G, 6);
        test("ClefG2QuindBassa, G", pentagram, new ClefG2QuindicesimaBassa(), PositionsInStaff.LINE_2, DiatonicPitch.G, 2);
        test("ClefF2, F", pentagram, new ClefF2(), PositionsInStaff.LINE_2, DiatonicPitch.F, 3);
        test("ClefF3, F", pentagram, new ClefF3(), PositionsInStaff.LINE_3, DiatonicPitch.F, 3);
        test("ClefF4, F", pentagram, new ClefF4(), PositionsInStaff.LINE_4, DiatonicPitch.F, 3);
        test("ClefF4OctAlta, F", pentagram, new ClefF4OttavaAlta(), PositionsInStaff.LINE_4, DiatonicPitch.F, 4);
        test("ClefF4OctBassa, F", pentagram, new ClefF4OttavaBassa(), PositionsInStaff.LINE_4, DiatonicPitch.F, 2);
        test("ClefF4QuindBassa, F", pentagram, new ClefF4QuindicesimaBassa(), PositionsInStaff.LINE_4, DiatonicPitch.F, 1);
        test("ClefF4QuindAlta, F", pentagram, new ClefF4QuindicesimaAlta(), PositionsInStaff.LINE_4, DiatonicPitch.F, 5);
        test("ClefF5, F", pentagram, new ClefF5(), PositionsInStaff.LINE_5, DiatonicPitch.F, 3);
        test("ClefC1, C", pentagram, new ClefC1(), PositionsInStaff.LINE_1, DiatonicPitch.C, 4);
        test("ClefC2, C", pentagram, new ClefC2(), PositionsInStaff.LINE_2, DiatonicPitch.C, 4);
        test("ClefC3, C", pentagram, new ClefC3(), PositionsInStaff.LINE_3, DiatonicPitch.C, 4);
        test("ClefC4, C", pentagram, new ClefC4(), PositionsInStaff.LINE_4, DiatonicPitch.C, 4);
        test("ClefC5, C", pentagram, new ClefC5(), PositionsInStaff.LINE_5, DiatonicPitch.C, 4);

    }

    @Test
    public void testAccidentalsToShow() throws IM3Exception {
        MusicXMLImporter importer = new MusicXMLImporter();
        ScoreSong scoreSong = importer.importSong(TestFileUtils.getFile("/testdata/core/score/io/naturals.xml"));
        assertEquals(1, scoreSong.getStaves().size());
        Staff staff = scoreSong.getStaves().get(0);
        HashMap<AtomPitch, Accidentals> accidentalsToShow = staff.createNoteAccidentalsToShow();
        Accidentals [] expectedShownAccidentals = new Accidentals[] {
                null, Accidentals.NATURAL, Accidentals.SHARP, Accidentals.NATURAL, null, null
        };
        List<AtomPitch> atomPitches = scoreSong.getStaves().get(0).getAtomPitches();
        assertEquals(6, atomPitches.size());
        for (int i=0; i<6; i++) {
            Accidentals accidentalToShow = accidentalsToShow.get(atomPitches.get(i));
            assertEquals("Atom pitch #" + i, expectedShownAccidentals[i], accidentalToShow);
            //System.out.println(atomPitches.get(i) + " -> " + accidentalToShow);
        }
    }

    @Test
    public void testAccidentalsToShow2() throws IM3Exception {
        MusicXMLImporter importer = new MusicXMLImporter();
        ScoreSong song = importer.importSong(TestFileUtils.getFile("/testdata/core/score/io/naturals2.xml"));

        assertEquals(1, song.getStaves().size());
        assertEquals("Key signature with 4 flats", -4, song.getUniqueKeyWithOnset(Time.TIME_ZERO).getFifths());
        Staff staff = song.getStaves().get(0);
        List<AtomPitch> atomPitches = song.getStaves().get(0).getAtomPitches();
        assertEquals(8, atomPitches.size());

        HashMap<AtomPitch, Accidentals> accidentalsToShow = staff.createNoteAccidentalsToShow();
        Accidentals [] expectedShownAccidentals = new Accidentals[] {
                null, Accidentals.SHARP, Accidentals.DOUBLE_SHARP, Accidentals.DOUBLE_FLAT, Accidentals.FLAT, Accidentals.NATURAL, null, null
        };
        for (int i=0; i<expectedShownAccidentals.length; i++) {
            Accidentals accidentalToShow = accidentalsToShow.get(atomPitches.get(i));
            assertEquals("Atom pitch #" + i, expectedShownAccidentals[i], accidentalToShow);
            //System.out.println(atomPitches.get(i) + " -> " + accidentalToShow);
        }
    }
}