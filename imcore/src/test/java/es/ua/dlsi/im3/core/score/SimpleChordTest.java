package es.ua.dlsi.im3.core.score;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SimpleChordTest {
    ScoreLayer layer;
    SimpleChord chord;
    SimpleNote n1, n2, n3;

    @Before
    public void setup() {
        chord = new SimpleChord(Figures.QUARTER,0,ScientificPitch.C4,new ScientificPitch(PitchClasses.E,4), new ScientificPitch(PitchClasses.G,4));
        layer = new ScoreLayer(new ScorePart(new ScoreSong(),1),1,null);
        chord.setLayer(layer);
        n1 = new SimpleNote(Figures.QUARTER,0,ScientificPitch.C4);
        n1.setLayer(layer);
        n2 = new SimpleNote(Figures.QUARTER,0,new ScientificPitch(PitchClasses.E,4));
        n2.setLayer(layer);
        n3 = new SimpleNote(Figures.QUARTER,0,new ScientificPitch(PitchClasses.G,4));
        n3.setLayer(layer);
    }

    @Test
    public void testGetNotes() {
        List<SimpleNote> notes = chord.getNotes();
        assertEquals("1st chord note (C4)",notes.get(0),n1);
        assertEquals("2nd chord note (E4)",notes.get(1),n2);
        assertEquals("3rd chord note (G4)",notes.get(1),n3);
        assertEquals("1st note layer",notes.get(0).getLayer(),n1.getLayer());
        assertEquals("2nd note layer",notes.get(1).getLayer(),n2.getLayer());
        assertEquals("3rd note layer",notes.get(2).getLayer(),n3.getLayer());
    }
}
