package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.score.clefs.ClefF4;
import es.ua.dlsi.im3.core.score.clefs.ClefG2;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClefTest {
    @Test
    public void getBottomLineDiatonicPitchAndOctave() throws Exception {
        ClefG2 g2 = new ClefG2();
        assertEquals("Bottom line pitch of G2", NoteNames.E, g2.getBottomLineDiatonicPitch());
        assertEquals("Bottom line octave of G2", 4, g2.getBottomLineOctave());

        ClefF4 f4 = new ClefF4();
        assertEquals("Bottom line pitch of F4", NoteNames.G, f4.getBottomLineDiatonicPitch());
        assertEquals("Bottom line octave of F4", 2, f4.getBottomLineOctave());

    }


}