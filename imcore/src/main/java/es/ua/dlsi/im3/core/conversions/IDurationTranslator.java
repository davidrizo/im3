package es.ua.dlsi.im3.core.conversions;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.List;

public interface IDurationTranslator {
    /**
     * It generates a list of tied figures with dots
     */
    List<FigureAndDots> findRhythmForDuration(long onset, long duration) throws IM3Exception;

}
