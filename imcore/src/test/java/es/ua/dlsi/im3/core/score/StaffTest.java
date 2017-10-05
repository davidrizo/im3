package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.junit.Test;

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
        test("C5 in ClefG2", pentagram, g2, PossitionsInStaff.SPACE_3, DiatonicPitch.C, 5);
        test("E4 in ClefG2", pentagram, g2, PossitionsInStaff.LINE_1, DiatonicPitch.E, 4);
        test("F5 in ClefG2", pentagram, g2, PossitionsInStaff.LINE_5, DiatonicPitch.F, 5);
    }

}