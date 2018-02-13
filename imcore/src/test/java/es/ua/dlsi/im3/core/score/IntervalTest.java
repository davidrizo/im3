package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntervalTest {
    //TODO Mas tests de intervalos

    @Test
    public void transpose() throws IM3Exception {
        Interval transposition = Intervals.FIFTH_PERFECT_DESC.createInterval();
        ScientificPitch from = new ScientificPitch(PitchClasses.B_FLAT, 5);
        ScientificPitch to = transposition.computeScientificPitchFrom(from);
        assertEquals("Transposed pitch class", PitchClasses.E_FLAT.getPitchClass(), to.getPitchClass());
        assertEquals("Transposed octave", 5, to.getOctave());

        from = new ScientificPitch(PitchClasses.B, 5);
        to = transposition.computeScientificPitchFrom(from);
        assertEquals("Transposed pitch class", PitchClasses.E.getPitchClass(), to.getPitchClass());

    }
}