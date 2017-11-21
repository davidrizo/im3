package es.ua.dlsi.im3.core.conversions;

import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.Time;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RhythmUtilsTest {
    @Test
    public void findRhythmForDuration() throws Exception {

        for (int dots=0; dots<2; dots++) {
            for (Figures figure: Figures.getFiguresSortedDesc(NotationType.eModern)) {
                List<FigureAndDots> result = RhythmUtils.findRhythmForDuration(NotationType.eModern, figure.getDurationWithDots(dots));
                assertEquals(1, result.size());
                assertEquals(figure, result.get(0).getFigure());
                assertEquals(dots, result.get(0).getDots());
            }
        }

        // TODO: 16/10/17 Más exhaustivo
        List<FigureAndDots> result = RhythmUtils.findRhythmForDuration(NotationType.eModern, new Time(5));
        assertEquals(2, result.size());
        assertEquals(Figures.WHOLE, result.get(0).getFigure());
        assertEquals(0, result.get(0).getDots());
        assertEquals(Figures.QUARTER, result.get(1).getFigure());
        assertEquals(0, result.get(1).getDots());

        List<FigureAndDots> result2 = RhythmUtils.findRhythmForDuration(NotationType.eModern, new Time(5, 2));
        assertEquals(2, result2.size());
        assertEquals(Figures.HALF, result2.get(0).getFigure());
        assertEquals(0, result2.get(0).getDots());
        assertEquals(Figures.EIGHTH, result2.get(1).getFigure());
        assertEquals(0, result2.get(1).getDots());

    }

}