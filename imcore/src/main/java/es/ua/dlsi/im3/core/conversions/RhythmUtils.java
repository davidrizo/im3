package es.ua.dlsi.im3.core.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.NotationType;
import es.ua.dlsi.im3.core.score.Time;
import org.apache.commons.lang3.math.Fraction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by drizo on 13/6/17.
 */
public class RhythmUtils {
    /**
     *
     * @param notationType
     * @param duration
     * @return A list of figures equivalent to the given duration
     * @throws IM3Exception
     */
    public static List<FigureAndDots> findRhythmForDuration(NotationType notationType, Time duration) throws IM3Exception {
        ArrayList<FigureAndDots> result = new ArrayList<>();

        int i=0;
        Figures [] figures = Figures.getFiguresSortedDesc(notationType);
        while (!duration.isZero() && i<figures.length) {
            Figures figure = figures[i];
            for (int dts = 2; dts >= 0; dts--) {
                Time durWithDots = figure.getDurationWithDots(dts);
                int comparison = durWithDots.compareTo(duration);
                if (comparison <= 0) {
                    result.add(new FigureAndDots(figure, dts));
                    duration = duration.substract(durWithDots);
                    break;
                }
            }
            i++;
        }
        if (!duration.isZero()) {
            throw new IM3Exception("Cannot locate a valid figure with dots for duration " + duration);
        }
        return result;
    }
}
