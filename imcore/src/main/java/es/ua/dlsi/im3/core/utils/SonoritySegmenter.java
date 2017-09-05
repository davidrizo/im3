package es.ua.dlsi.im3.core.utils;

import es.ua.dlsi.im3.core.score.AtomFigure;
import es.ua.dlsi.im3.core.score.ScoreSong;
import es.ua.dlsi.im3.core.score.Segment;
import es.ua.dlsi.im3.core.score.Time;

import java.util.ArrayList;
import java.util.TreeSet;

public class SonoritySegmenter {
    /**
     * Build segments using the shortest note of each sonority such that no
     * segment can contain two notes in a given part and it contains at least one note
     *
     */
    public ArrayList<Segment> segmentSonorities(ScoreSong song)  {
        ArrayList<Segment> segments = new ArrayList<>();
        TreeSet<AtomFigure> ssds = song.getAtomFiguresSortedByTime();

        TreeSet<Time> times = new TreeSet<>(); // all onset or offsets
        for (AtomFigure atomFigure : ssds) {
            times.add(atomFigure.getTime());
            times.add(atomFigure.getEndTime());
        }

        Time previousTime = null;
        for (Time time : times) {
            if (previousTime != null) {
                Segment segment = new Segment(previousTime, time);
                segments.add(segment);
            }

            previousTime = time;
        }
        return segments;
    }

}