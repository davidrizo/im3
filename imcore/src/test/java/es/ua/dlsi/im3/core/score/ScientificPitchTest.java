package es.ua.dlsi.im3.core.score;

import org.junit.Test;

import static org.junit.Assert.*;

public class ScientificPitchTest {


    @Test
    public void computeMidiPitch() {
        assertEquals("C4", 60, new ScientificPitch(PitchClasses.C, 4).computeMidiPitch(), 60);
        assertEquals("A4", 69, new ScientificPitch(PitchClasses.A, 4).computeMidiPitch(), 60);
    }

    @Test
    public void computeFrequency() {
        assertEquals("A4", 440, new ScientificPitch(PitchClasses.A, 4).computeFrequency(), 0.0001);
        assertEquals("A5", 880, new ScientificPitch(PitchClasses.A, 5).computeFrequency(), 0.0001);
        assertEquals("C4", 261.6, new ScientificPitch(PitchClasses.C, 4).computeFrequency(), 0.1);
    }

    @Test
    public void parse() {
        assertEquals("A4", new ScientificPitch(PitchClasses.A, 4), ScientificPitch.parse("A4"));
        assertEquals("C#3", new ScientificPitch(PitchClasses.C_SHARP, 3), ScientificPitch.parse("C#3"));
        assertEquals("Bb5", new ScientificPitch(PitchClasses.B_FLAT, 5), ScientificPitch.parse("Bb5"));
        assertEquals("Gx2", new ScientificPitch(PitchClasses.G_DSHARP, 2), ScientificPitch.parse("Gx2"));
        assertEquals("Cbb1", new ScientificPitch(PitchClasses.C_DFLAT, 1), ScientificPitch.parse("Cbb1"));
    }
}
