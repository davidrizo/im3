package es.ua.dlsi.im3.core.score;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class BeamedGroupTest {
    @Test
    public void getNumBeams() throws Exception {
        PitchClasses [] pitches = new PitchClasses[] {
                PitchClasses.C, PitchClasses.D, PitchClasses.E
        };

        ScoreSong song = new ScoreSong();
        ScorePart part = song.addPart();
        ScoreLayer layer = part.addScoreLayer();
        BeamedGroup eighth = new BeamedGroup(Figures.EIGHTH, false);
        assertEquals(1, eighth.getNumBeams());
        SimpleNote n1 = new SimpleNote(Figures.EIGHTH, 0, new ScientificPitch(pitches[0], 4));
        eighth.addSubatom(n1);

        BeamedGroup sixteen = new BeamedGroup(Figures.SIXTEENTH, false);
        SimpleNote n2 = new SimpleNote(Figures.SIXTEENTH, 0, new ScientificPitch(pitches[1], 4));
        SimpleNote n3 = new SimpleNote(Figures.SIXTEENTH, 0, new ScientificPitch(pitches[2], 4));
        sixteen.addSubatom(n2);
        sixteen.addSubatom(n3);

        eighth.addSubatom(sixteen);

        assertEquals("Total duration", Figures.QUARTER.getDuration(), eighth.getDuration().getExactTime());

        layer.add(eighth);
        ArrayList<AtomPitch> atomPitches = layer.getAtomPitches();
        assertEquals(pitches.length, atomPitches.size());
        for (int i=0; i<pitches.length; i++) {
            assertEquals(pitches[i].getPitchClass(), atomPitches.get(i).getScientificPitch().getPitchClass());
        }

    }
}