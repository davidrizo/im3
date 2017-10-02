package es.ua.dlsi.im3.core.score;

import es.ua.dlsi.im3.core.score.meters.TimeSignatureCommonTime;
import es.ua.dlsi.im3.core.score.staves.Pentagram;
import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class ScoreLayerTest {
    @Test
    public void createBeaming() throws Exception {
        ScoreSong song = new ScoreSong();
        Staff staff = new Pentagram(song, "1", 1);
        staff.setNotationType(NotationType.eModern);
        TimeSignatureCommonTime ts = new TimeSignatureCommonTime(NotationType.eModern);
        Measure measure = new Measure(song);
        song.addMeasure(Time.TIME_ZERO, measure);
        measure.setEndTime(ts.getDuration()); // TODO: 23/9/17 ¿No se debería poner sólo? Hay que verlo y que no choque con los importadores

        staff.addTimeSignature(ts);
        song.addStaff(staff);
        ScoreLayer layer = song.addPart().addScoreLayer();
        staff.addLayer(layer);
        SimpleNote n0 = new SimpleNote(Figures.QUARTER, 0, new ScientificPitch(PitchClasses.A, 4));
        staff.addCoreSymbol(n0);
        layer.add(n0);
        SimpleNote n1 = new SimpleNote(Figures.EIGHTH, 0, new ScientificPitch(PitchClasses.B, 4));
        staff.addCoreSymbol(n1);
        layer.add(n1);
        SimpleNote n2 = new SimpleNote(Figures.EIGHTH, 0, new ScientificPitch(PitchClasses.C, 5));
        staff.addCoreSymbol(n2);
        layer.add(n2);

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

}