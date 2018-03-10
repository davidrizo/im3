package es.ua.dlsi.im3.omr.primus.realbook;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Time;

/**
 * It generates several staff incipits with fixed number of measures from musicxml files into MEI format
 * @autor drizo
 */
public class GenerateTwoStaffIncipits {
    private final int measures;
    private final int staves;

    /**
     * Number of measures
     * @param measures
     */
    public GenerateTwoStaffIncipits(int staves, int measures) {
        this.staves = staves;
        this.measures = measures;
    }

    public void generateTwoStaffIncipit(ScoreSong scoreSong) throws IM3Exception {
        if (scoreSong.getNumMeasures() < measures) {
            throw new IM3Exception("Not enough measures, required " + measures + " and found " + scoreSong.getNumMeasures());
        }

        if (scoreSong.getParts().size() > 1) {
            throw new IM3Exception("Just 1 part scores are supported, and there are " + scoreSong.getParts().size());
        }

        if (scoreSong.getStaves().size() > 1) {
            throw new IM3Exception("Just 1 staff scores are supported, and there are " + scoreSong.getStaves().size());
        }

        // remove copies just the elements inside the given measures
        Time fromTime = Time.TIME_ZERO;
        Time toTime = scoreSong.getMeasuresSortedAsArray().get(measures).getTime();

        ScoreSong incipit = new ScoreSong();



        int systemBreakEveryXMeasures = measures / staves;


    }
}
