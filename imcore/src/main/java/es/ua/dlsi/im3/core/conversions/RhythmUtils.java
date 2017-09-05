package es.ua.dlsi.im3.core.conversions;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.Figures;
import es.ua.dlsi.im3.core.score.NotationType;
import org.apache.commons.lang3.math.Fraction;

/**
 * Created by drizo on 13/6/17.
 */
public class RhythmUtils {
    static public class FigureAndDots {
        Figures figure;
        int dots;

        public FigureAndDots(Figures figure, int dots) {
            this.figure = figure;
            this.dots = dots;
        }

        public Figures getFigure() {
            return figure;
        }

        public int getDots() {
            return dots;
        }
    }
    public static FigureAndDots findRhythmForDuration(NotationType notationType, Fraction duration) throws IM3Exception {
        for (Figures f : Figures.values()) {
            if (f != Figures.MAX_FIGURE) {
                for (int dts = 0; dts < 5; dts++) {
                    if (f.getDurationWithDots(dts).equals(duration)) {
                        return new FigureAndDots(f, dts);
                    }
                }
            }
        }
        throw new IM3Exception("Cannot locate a valid figure with dots for duration " + duration);
    }
}
