package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * It tests the insertion and deletion of elements in the beginning, in the middle, from a system beginning to the next
 */
public class SystemsTest {
    ScoreSong song;
    Staff staff;
    ScorePart part;
    ScoreLayer layer;
    private SystemBeginning sb0;
    private SystemBeginning sb1;
    private SystemBeginning sb2;

    // for testing purposes, all notes of each system are in one octave
    @Before
    public void setup() throws IM3Exception {
        song = new ScoreSong();
        staff = new Pentagram(song, "1", 1);
        staff.setNotationType(NotationType.eModern);
        part = song.addPart();
        layer = part.addScoreLayer();
        staff.addLayer(layer);
        TimeSignatureCommonTime ts = new TimeSignatureCommonTime(NotationType.eModern);

        // ----------------- initial creation -------
        // system ------------
        sb0 = new SystemBeginning(Time.TIME_ZERO, true);
        part.addSystemBeginning(sb0);

        // first measure
        Measure measure0 = new Measure(song, 1);
        song.addMeasure(Time.TIME_ZERO, measure0);
        layer.add(new SimpleNote(Figures.QUARTER, 0, new ScientificPitch(PitchClasses.C, 1)));
        layer.add(new SimpleNote(Figures.QUARTER, 0, new ScientificPitch(PitchClasses.D, 1)));
        layer.add(new SimpleNote(Figures.QUARTER, 0, new ScientificPitch(PitchClasses.E, 1)));
        layer.add(new SimpleNote(Figures.QUARTER, 0, new ScientificPitch(PitchClasses.F, 1)));
        measure0.setEndTime(layer.getDuration());

        // second measure
        Measure measure1 = new Measure(song, 2);
        song.addMeasure(layer.getDuration(), measure1);
        layer.add(new SimpleNote(Figures.WHOLE, 0, new ScientificPitch(PitchClasses.G, 1)));
        measure1.setEndTime(layer.getDuration());

        // system ------------
        sb1 = new SystemBeginning(layer.getDuration(), true);
        part.addSystemBeginning(sb1);

        // third measure
        Measure measure2 = new Measure(song, 1);
        song.addMeasure(layer.getDuration(), measure2);
        layer.add(new SimpleNote(Figures.HALF, 1, new ScientificPitch(PitchClasses.C, 2)));
        layer.add(new SimpleNote(Figures.QUARTER, 0, new ScientificPitch(PitchClasses.D, 2)));
        measure2.setEndTime(layer.getDuration());

        // fourth measure
        Measure measure3 = new Measure(song, 3);
        song.addMeasure(layer.getDuration(), measure3);
        layer.add(new SimpleNote(Figures.QUARTER, 0, new ScientificPitch(PitchClasses.E, 2)));
        measure3.setEndTime(layer.getDuration());

        // system ------------
        sb2 = new SystemBeginning(layer.getDuration(), true);
        part.addSystemBeginning(sb2);

        // fourth measure
        Measure measure4 = new Measure(song, 4);
        song.addMeasure(layer.getDuration(), measure4);
        layer.add(new SimpleNote(Figures.HALF, 0, new ScientificPitch(PitchClasses.C, 3)));
        layer.add(new SimpleNote(Figures.HALF, 0, new ScientificPitch(PitchClasses.D, 3)));
        measure4.setEndTime(layer.getDuration());

        // fifth measure
        Measure measure5 = new Measure(song, 5);
        song.addMeasure(layer.getDuration(), measure5);
        layer.add(new SimpleNote(Figures.HALF, 0, new ScientificPitch(PitchClasses.E, 3)));
        layer.add(new SimpleNote(Figures.HALF, 0, new ScientificPitch(PitchClasses.F, 3)));
        measure5.setEndTime(layer.getDuration());

        assertEquals("Initial atoms #", 12, layer.getAtoms().size());
        assertEquals("Initial system beginnings", 3, part.getPageSystemBeginnings().getSystemBeginnings().size());
        checkAllElementsContiguousTimes();

    }

    private void insertRestsForOneMeasure(Time time) throws IM3Exception {
        ArrayList<ITimedElementInStaff> rests = new ArrayList<>();
        for (int i=0; i<8; i++) {
            rests.add(new SimpleRest(Figures.EIGHTH, 0));
        }
        layer.insertAt(time, rests);
    }


    private void checkAllElementsContiguousTimes() {
        Time t = Time.TIME_ZERO;
        int i=0;
        for (ITimedElementInStaff timedElementInStaff: staff.getCoreSymbolsOrdered()) {
            assertEquals("Element #" + i + " " + timedElementInStaff, t, timedElementInStaff.getTime());
            if (timedElementInStaff instanceof Atom) {
                t = ((Atom) timedElementInStaff).getEndTime();
            }
            i++;
        }
    }

    private void checkNoNoteOfOctave(int octave) {
        for (Atom atom: layer.getAtoms()) {
            assertNotEquals("Octave of atom " + atom, octave, atom.getAtomPitches().get(0).getScientificPitch().getOctave());
        }
    }

    private void assertJustRestsBetween(Time from, Time to) {
        Segment segment = new Segment(from, to);
        for (Atom atom: layer.getAtoms()) {
            if (segment.contains(atom.getTime())) {
                assertTrue("Element should be rest: " + atom, atom instanceof SimpleRest);
            } else {
                assertTrue("Element should be note: " + atom, atom instanceof SimpleNote);
            }
        }
    }


    @Test
    public void removeFirstSystem() throws IM3Exception {
        // ----- now remove the last system
        layer.remove(sb0.getTime(), sb1.getTime());

        assertEquals("Atoms in layer after removing first system", 7, layer.getAtoms().size());
        assertEquals("Atoms in staff after removing first system", 7, layer.getAtoms().size());

        //TODO que desaparezca el sb - se deben recalcular
        checkNoNoteOfOctave(1);
        checkAllElementsContiguousTimes();

        insertRestsForOneMeasure(sb0.getTime());
        assertEquals("Atoms after removing first system and adding rests", 7+8, layer.getAtoms().size());
        assertJustRestsBetween(sb0.getTime(), sb0.getTime().add(Figures.WHOLE.getDuration()));
        checkAllElementsContiguousTimes();
    }

    @Test
    public void removeSecondSystem() throws IM3Exception {
        // ----- now remove the last system
        layer.remove(sb1.getTime(), sb2.getTime());

        assertEquals("Atoms in layer after removing first system", 9, layer.getAtoms().size());
        assertEquals("Atoms in staff after removing first system", 9, staff.getAtoms().size());
        //TODO que desaparezca el sb
        checkNoNoteOfOctave(2);
        checkAllElementsContiguousTimes();

        insertRestsForOneMeasure(sb1.getTime());
        assertEquals("Atoms after removing second system and adding rests", 9+8, layer.getAtoms().size());
        assertJustRestsBetween(sb1.getTime(), sb1.getTime().add(Figures.WHOLE.getDuration()));
        checkAllElementsContiguousTimes();
    }



    @Test
    public void removeLastSystem() throws IM3Exception {
        // ----- now remove the last system
        layer.remove(sb2.getTime(), Time.TIME_MAX);

        assertEquals("Atoms in layer after removing last system", 8, layer.getAtoms().size());
        assertEquals("Atoms in staff after removing last system", 8, staff.getAtoms().size());
        //TODO que desaparezca el sb
        checkNoNoteOfOctave(3);
        checkAllElementsContiguousTimes();

        insertRestsForOneMeasure(sb2.getTime());
        assertEquals("Atoms after removing last system and adding rests", 8+8, layer.getAtoms().size());
        assertJustRestsBetween(sb2.getTime(), sb2.getTime().add(Figures.WHOLE.getDuration()));
        checkAllElementsContiguousTimes();
    }


    //TODO test quitando clefs, .... - que se quiten / muevan los SB ...
    //TODO Probar ahora quitando de staves y quitando de parts secciones completas

}
