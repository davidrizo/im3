package es.ua.dlsi.im3.core.score;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BeamedGroupTest {
    PitchClasses [] pitches = new PitchClasses[] {
            PitchClasses.C, PitchClasses.D, PitchClasses.E
    };

    /*@Test
    public void testNestedBeams() throws Exception {
        ScoreSong song = new ScoreSong();
        ScorePart part = song.addPart();
        ScoreLayer layer = part.addScoreLayer();
        BeamedGroup eighth = new BeamedGroup(false);
        SimpleNote n1 = new SimpleNote(Figures.EIGHTH, 0, new ScientificPitch(pitches[0], 4));
        eighth.addSubatom(n1);

        BeamedGroup sixteen = new BeamedGroup(false);
        SimpleNote n2 = new SimpleNote(Figures.SIXTEENTH, 0, new ScientificPitch(pitches[1], 4));
        SimpleNote n3 = new SimpleNote(Figures.SIXTEENTH, 0, new ScientificPitch(pitches[2], 4));
        sixteen.addSubatom(n2);
        sixteen.addSubatom(n3);

        eighth.addSubatom(sixteen);

        layer.add(eighth);

        check(layer, eighth);
    }*/

    private void check(ScoreLayer layer, BeamedGroup eighth) {

        assertEquals("Total duration", Figures.QUARTER.getDuration(), eighth.getDuration());

        ArrayList<AtomPitch> atomPitches = layer.getAtomPitches();
        assertEquals(pitches.length, atomPitches.size());
        for (int i=0; i<pitches.length; i++) {
            assertEquals(pitches[i].getPitchClass(), atomPitches.get(i).getScientificPitch().getPitchClass());
        }

        List<AtomFigure> atomFigures = layer.getAtomFigures();
        assertEquals(3, atomFigures.size());
        assertEquals(Figures.EIGHTH, atomFigures.get(0).getFigure());
        assertEquals(Figures.SIXTEENTH, atomFigures.get(1).getFigure());
        assertEquals(Figures.SIXTEENTH, atomFigures.get(2).getFigure());

        assertEquals("First", Time.TIME_ZERO, atomFigures.get(0).getTime());
        assertEquals("Second", Figures.EIGHTH.getDuration(), atomFigures.get(1).getTime());
        assertEquals("Third", Figures.EIGHTH.getDuration().add(Figures.SIXTEENTH.getDuration()), atomFigures.get(2).getTime());

        assertEquals(3, atomPitches.size());
        assertEquals("First", Time.TIME_ZERO, atomPitches.get(0).getTime());
        assertEquals("Second", Figures.EIGHTH.getDuration(), atomPitches.get(1).getTime());
        assertEquals("Third", Figures.EIGHTH.getDuration().add(Figures.SIXTEENTH.getDuration()), atomPitches.get(2).getTime());

    }

    /**
     * The same as testNestedBeams but not nested
     * @throws Exception
     */
    @Test
    public void testNonNestedBeams() throws Exception {
        ScoreSong song = new ScoreSong();
        ScorePart part = song.addPart();
        ScoreLayer layer = part.addScoreLayer();
        BeamedGroup beam = new BeamedGroup(false);
        SimpleNote n1 = new SimpleNote(Figures.EIGHTH, 0, new ScientificPitch(pitches[0], 4));
        beam.addSubatom(n1);

        SimpleNote n2 = new SimpleNote(Figures.SIXTEENTH, 0, new ScientificPitch(pitches[1], 4));
        SimpleNote n3 = new SimpleNote(Figures.SIXTEENTH, 0, new ScientificPitch(pitches[2], 4));
        beam.addSubatom(n2);
        beam.addSubatom(n3);

        layer.add(beam);

        check(layer, beam);

    }
}