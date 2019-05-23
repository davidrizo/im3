package es.ua.dlsi.im3.core.score;

import static org.junit.Assert.*;

import es.ua.dlsi.im3.core.score.clefs.*;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.junit.Before;
import org.junit.Test;

import es.ua.dlsi.im3.core.IM3Exception;

/**
 * @author drizo
 */
public class KeySignatureTest {

    @Before
    public void setUp() {
    }

    @Test
    public void testTransposingInstruments() throws IM3Exception {
        KeySignature ks = new KeySignature(NotationType.eModern, new Key(-1, Mode.MAJOR));
        assertEquals(-1, ks.getInstrumentKey().getFifths());
        assertEquals(-1, ks.getInstrumentKey().getFifths());
        ks.setTranspositionInterval(Intervals.FIFTH_PERFECT_ASC.createInterval());
        assertEquals(-1, ks.getInstrumentKey().getFifths());
        assertEquals(0, ks.getConcertPitchKey().getFifths());
    }

    @Test
    public void testAlterationsOfNotes() throws IM3Exception {
        KeySignature ks = new KeySignature(NotationType.eModern, new Key(2, Mode.MAJOR)); // DMajor, F# and C#
        assertNull(ks.getAccidentalOf(DiatonicPitch.D));
        assertNull(ks.getAccidentalOf(DiatonicPitch.E));
        assertEquals(Accidentals.SHARP, ks.getAccidentalOf(DiatonicPitch.F));
        assertNull(ks.getAccidentalOf(DiatonicPitch.G));
        assertNull(ks.getAccidentalOf(DiatonicPitch.A));
        assertNull(ks.getAccidentalOf(DiatonicPitch.B));
        assertEquals(Accidentals.SHARP, ks.getAccidentalOf(DiatonicPitch.C));
    }

    private void checkPositionsOfAccitentals(Clef clef, Key key, PositionInStaff[] expected) throws IM3Exception {
        Pentagram p = new Pentagram(new ScoreSong(), "1", 1);
        clef.setTime(Time.TIME_ZERO);
        p.addElementWithoutLayer(clef);
        KeySignature ks = new KeySignature(NotationType.eModern, key);
        ks.setStaff(p);
        PositionInStaff[] positions = ks.computePositionsOfAccidentals();
        assertEquals("Number of accidentals", expected.length, positions.length);
        for (int i=0; i<expected.length; i++) {
            assertEquals("Key " + key + ", clef " + clef + ", position #" + i, expected[i], positions[i]);
        }
    }
    @Test
    public void testComputePositionsOfAccidentals() throws IM3Exception {
        Key key1 = new Key(4, Mode.MAJOR); // eMajor

        checkPositionsOfAccitentals(new ClefG2(), key1, new PositionInStaff[] {PositionsInStaff.LINE_5, PositionsInStaff.SPACE_3, PositionsInStaff.SPACE_5, PositionsInStaff.LINE_4});
        checkPositionsOfAccitentals(new ClefG2OttavaAlta(), key1, new PositionInStaff[] {PositionsInStaff.LINE_5, PositionsInStaff.SPACE_3, PositionsInStaff.SPACE_5, PositionsInStaff.LINE_4});
        checkPositionsOfAccitentals(new ClefG2OttavaBassa(), key1, new PositionInStaff[] {PositionsInStaff.LINE_5, PositionsInStaff.SPACE_3, PositionsInStaff.SPACE_5, PositionsInStaff.LINE_4});
        checkPositionsOfAccitentals(new ClefF3(), key1, new PositionInStaff[] {PositionsInStaff.LINE_3, PositionsInStaff.SPACE_1, PositionsInStaff.SPACE_3, PositionsInStaff.LINE_2});
        checkPositionsOfAccitentals(new ClefF4(), key1, new PositionInStaff[] {PositionsInStaff.LINE_4, PositionsInStaff.SPACE_2, PositionsInStaff.SPACE_4, PositionsInStaff.LINE_3});
        checkPositionsOfAccitentals(new ClefF4OttavaAlta(), key1, new PositionInStaff[] {PositionsInStaff.LINE_4, PositionsInStaff.SPACE_2, PositionsInStaff.SPACE_4, PositionsInStaff.LINE_3});
        checkPositionsOfAccitentals(new ClefF4OttavaBassa(), key1, new PositionInStaff[] {PositionsInStaff.LINE_4, PositionsInStaff.SPACE_2, PositionsInStaff.SPACE_4, PositionsInStaff.LINE_3});
        checkPositionsOfAccitentals(new ClefC1(), key1, new PositionInStaff[] {PositionsInStaff.SPACE_2, PositionsInStaff.LINE_1, PositionsInStaff.LINE_3, PositionsInStaff.SPACE_1});
        checkPositionsOfAccitentals(new ClefC2(), key1, new PositionInStaff[] {PositionsInStaff.SPACE_3, PositionsInStaff.LINE_2, PositionsInStaff.LINE_4, PositionsInStaff.SPACE_2});
        checkPositionsOfAccitentals(new ClefC3(), key1, new PositionInStaff[] {PositionsInStaff.SPACE_4, PositionsInStaff.LINE_3, PositionsInStaff.LINE_5, PositionsInStaff.SPACE_3});
        checkPositionsOfAccitentals(new ClefC4(), key1, new PositionInStaff[] {PositionsInStaff.LINE_2, PositionsInStaff.LINE_4, PositionsInStaff.SPACE_2, PositionsInStaff.SPACE_4});
        checkPositionsOfAccitentals(new ClefC5(), key1, new PositionInStaff[] {PositionsInStaff.LINE_3, PositionsInStaff.SPACE_1, PositionsInStaff.SPACE_3, PositionsInStaff.LINE_2});

        Key key = new Key(-4, Mode.MAJOR); // AbMajor

        checkPositionsOfAccitentals(new ClefG2(), key, new PositionInStaff[] {PositionsInStaff.LINE_3, PositionsInStaff.SPACE_4, PositionsInStaff.SPACE_2, PositionsInStaff.LINE_4});
        checkPositionsOfAccitentals(new ClefG2OttavaBassa(), key, new PositionInStaff[] {PositionsInStaff.LINE_3, PositionsInStaff.SPACE_4, PositionsInStaff.SPACE_2, PositionsInStaff.LINE_4});
        checkPositionsOfAccitentals(new ClefF3(), key, new PositionInStaff[] {PositionsInStaff.SPACE_4, PositionsInStaff.SPACE_2, PositionsInStaff.LINE_4, PositionsInStaff.LINE_2});
        checkPositionsOfAccitentals(new ClefF4(), key, new PositionInStaff[] {PositionsInStaff.LINE_2, PositionsInStaff.SPACE_3, PositionsInStaff.SPACE_1, PositionsInStaff.LINE_3});
        checkPositionsOfAccitentals(new ClefC1(), key, new PositionInStaff[] {PositionsInStaff.LINE_4, PositionsInStaff.LINE_2, PositionsInStaff.SPACE_3, PositionsInStaff.SPACE_1});
        checkPositionsOfAccitentals(new ClefC2(), key, new PositionInStaff[] {PositionsInStaff.LINE_5, PositionsInStaff.LINE_3, PositionsInStaff.SPACE_4, PositionsInStaff.SPACE_2});
        checkPositionsOfAccitentals(new ClefC3(), key, new PositionInStaff[] {PositionsInStaff.SPACE_2, PositionsInStaff.LINE_4, PositionsInStaff.LINE_2, PositionsInStaff.SPACE_3});
        checkPositionsOfAccitentals(new ClefC4(), key, new PositionInStaff[] {PositionsInStaff.SPACE_3, PositionsInStaff.LINE_5, PositionsInStaff.LINE_3, PositionsInStaff.SPACE_4});
        checkPositionsOfAccitentals(new ClefC5(), key, new PositionInStaff[] {PositionsInStaff.SPACE_4, PositionsInStaff.SPACE_2, PositionsInStaff.LINE_4, PositionsInStaff.LINE_2});
    }


    //TODO Test igual con sharps
}
