package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.TestFileUtils;
import es.ua.dlsi.im3.core.io.ImportException;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
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