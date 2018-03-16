package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class ScoreLayerTest {

    ScoreSong song;
    Staff staff;
    ScoreLayer layer;
    SimpleNote n0, n1, n2;
    SimpleNote n0_m2, n1_m2, n2_m2; // second measure notes
    SimpleRest rest;
    Measure first_measure, second_measure;
    TimeSignatureCommonTime ts;

    @Before
    public void setUp() throws Exception {
        song = new ScoreSong();
        staff = new Pentagram(song, "1", 1);
        staff.setNotationType(NotationType.eModern);
        ts = new TimeSignatureCommonTime(NotationType.eModern);
        first_measure = new Measure(song);
        song.addMeasure(Time.TIME_ZERO, first_measure);
        first_measure.setEndTime(ts.getDuration()); // TODO: 23/9/17 ¿No se debería poner sólo? Hay que verlo y que no choque con los importadores


        staff.addTimeSignature(ts);
        song.addStaff(staff);
        layer = song.addPart().addScoreLayer();
        staff.addLayer(layer);
        n0 = new SimpleNote(Figures.QUARTER, 0, new ScientificPitch(PitchClasses.A, 4));
        staff.addCoreSymbol(n0);
        layer.add(n0);
        n1 = new SimpleNote(Figures.EIGHTH, 0, new ScientificPitch(PitchClasses.B, 4));
        staff.addCoreSymbol(n1);
        layer.add(n1);
        n2 = new SimpleNote(Figures.EIGHTH, 0, new ScientificPitch(PitchClasses.C, 5));
        staff.addCoreSymbol(n2);
        layer.add(n2);

    }

    @Test
    public void createBeaming() throws Exception {

        assertEquals(3, layer.size());

        layer.createBeaming();

        assertEquals(3, layer.size());

        assertNull(n0.getBelongsToBeam());
        assertNotNull(n1.getBelongsToBeam());
        assertNotNull(n2.getBelongsToBeam());
        assertEquals(n1.getBelongsToBeam(), n2.getBelongsToBeam());

        assertEquals(3, layer.getAtomFigures().size());
        assertEquals(3, layer.getAtomPitches().size());
    }

    @Test
    public void getAtomFiguresWithOnsetWithinTest() {

        try {
            second_measure = new Measure(song);
            song.addMeasure(ts.getDuration(), second_measure);
            second_measure.setEndTime(ts.getDuration().add(ts.getDuration()));
            rest = new SimpleRest(Figures.HALF,0); // to complete first_measure
            staff.addCoreSymbol(rest);
            layer.add(rest);

            // Second measure
            n0_m2= new SimpleNote(Figures.QUARTER, 0, new ScientificPitch(PitchClasses.C, 5));
            staff.addCoreSymbol(n0_m2);
            layer.add(n0_m2);
            n1_m2 = new SimpleNote(Figures.EIGHTH, 0, new ScientificPitch(PitchClasses.B, 4));
            staff.addCoreSymbol(n1_m2);
            layer.add(n1_m2);
            n2_m2 = new SimpleNote(Figures.EIGHTH, 0, new ScientificPitch(PitchClasses.C, 5));
            staff.addCoreSymbol(n2_m2);
            layer.add(n2_m2);

            SortedSet<AtomFigure> atoms = layer.getAtomFiguresSortedByTimeWithin(first_measure);
            assertEquals("First measure, first note",n0.getAtomFigure(),atoms.first());
            // TODO: test notes in between
            assertEquals("First measure, last note",rest.getAtomFigure(),atoms.last());

            atoms = layer.getAtomFiguresSortedByTimeWithin(second_measure);
            assertEquals("Second measure, first note",n0_m2.getAtomFigure(),atoms.first());
            // TODO: test notes in between
            assertEquals("Second measure, last note",n2_m2.getAtomFigure(),atoms.last());
        } catch (IM3Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
}