package es.ua.dlsi.im3.core.score.scales.jazz;

import es.ua.dlsi.im3.core.score.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class JazzModalScalesTest {

    private void test(JazzModalScale scale, KeysEnum key, PitchClasses [] expected) {
        List<PitchClass> generatedScale = scale.generateOneOctaveScale(key.getKey());
        assertEquals("Number of notes in scale " + scale.getName(), expected.length, generatedScale.size());
        for (int i=0; i<expected.length; i++) {
            assertEquals("Scale " + scale.getName() + ", #" + i + "th note", expected[i].getPitchClass(), generatedScale.get(i));
        }
    }

    @Test
    public void generateTest() {
        test(new Ionian(), KeysEnum.CM, new PitchClasses[] {
                PitchClasses.C, PitchClasses.D, PitchClasses.E, PitchClasses.F,
                PitchClasses.G, PitchClasses.A, PitchClasses.B
        });
        test(new Dorian(), KeysEnum.DM, new PitchClasses[] {
                PitchClasses.D, PitchClasses.E, PitchClasses.F,
                PitchClasses.G, PitchClasses.A, PitchClasses.B, PitchClasses.C,
        });
        test(new Phrygian(), KeysEnum.EM, new PitchClasses[] {
                PitchClasses.E, PitchClasses.F,
                PitchClasses.G, PitchClasses.A, PitchClasses.B, PitchClasses.C, PitchClasses.D
        });
        test(new Lydian(), KeysEnum.FM, new PitchClasses[] {
                PitchClasses.F,
                PitchClasses.G, PitchClasses.A, PitchClasses.B,
                PitchClasses.C, PitchClasses.D, PitchClasses.E
        });
        test(new Mixolydian(), KeysEnum.GM, new PitchClasses[] {
                PitchClasses.G, PitchClasses.A, PitchClasses.B,
                PitchClasses.C, PitchClasses.D, PitchClasses.E, PitchClasses.F
        });
        test(new Aeolian(), KeysEnum.AM, new PitchClasses[] {
                PitchClasses.A, PitchClasses.B,
                PitchClasses.C, PitchClasses.D, PitchClasses.E, PitchClasses.F,
                PitchClasses.G
        });
        test(new Locrian(), KeysEnum.BM, new PitchClasses[] {
                PitchClasses.B, PitchClasses.C, PitchClasses.D, PitchClasses.E, PitchClasses.F,
                PitchClasses.G, PitchClasses.A
        });
    }
}