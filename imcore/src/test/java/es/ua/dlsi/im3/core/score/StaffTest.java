package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.junit.Test;

import static org.junit.Assert.*;

public class StaffTest {
    @Test
    public void computeLineSpacePitch() throws Exception {
        ClefG2 g2 = new ClefG2();
        ScoreSong song = new ScoreSong();
        Pentagram pentagram = new Pentagram(song, "1", 1);
        assertEquals("E4 in ClefG2", PossitionsInStaff.LINE_1, pentagram.computeLineSpacePitch(g2, DiatonicPitch.E, 4));
        assertEquals("F5 in ClefG2", PossitionsInStaff.LINE_5, pentagram.computeLineSpacePitch(g2, DiatonicPitch.F, 5));
        assertEquals("C5 in ClefG2", PossitionsInStaff.SPACE_3, pentagram.computeLineSpacePitch(g2, DiatonicPitch.C, 5));
    }

}