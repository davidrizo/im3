package es.ua.dlsi.im3.core.utils;

import es.ua.dlsi.im3.core.score.*;

import java.util.*;

public class SonoritySegmenter {
    /**
     * Build segments using the shortest note of each sonority such that no
     * segment can contain two notes in a given part and it contains at least one note.
     * It divides the multimeasure rests
     */
    public List<Segment> segmentSonorities(ScoreSong song)  {
        List<Segment> segments = new LinkedList<>();
        TreeSet<AtomFigure> ssds = song.getAtomFiguresSortedByTime();

        TreeSet<Time> times = new TreeSet<>(); // all onset or offsets
        for (AtomFigure atomFigure : ssds) {
            if (atomFigure.getAtom() instanceof SimpleMultiMeasureRest) {
                SimpleMultiMeasureRest simpleMultiMeasureRest = (SimpleMultiMeasureRest) atomFigure.getAtom();
                Time dur = simpleMultiMeasureRest.getDuration().divide(simpleMultiMeasureRest.getNumMeasures());
                Time time = simpleMultiMeasureRest.getTime();
                for (int i=0; i<simpleMultiMeasureRest.getNumMeasures(); i++) {
                    times.add(time);
                    time = time.add(dur);
                }
            } else {
                times.add(atomFigure.getTime());
                times.add(atomFigure.getEndTime());
            }
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

    public List<Sonority> buildSonorities(ScoreSong scoreSong) {
        List<Segment> sonoritySegments = segmentSonorities(scoreSong);
        List<Sonority> sonorities = new LinkedList<>();
        for (Segment segment: sonoritySegments) {
            List<AtomPitch> atomPitches = scoreSong.getAtomPitchesWithOnsetWithin(segment);
            SortedSet<ScientificPitch> sonorityPitches = new TreeSet<>();
            for (AtomPitch ap: atomPitches) {
                sonorityPitches.add(ap.getScientificPitch());
            }
            Sonority sonority = new Sonority(segment, sonorityPitches);
            sonorities.add(sonority);
        }
        return sonorities;
    }
}