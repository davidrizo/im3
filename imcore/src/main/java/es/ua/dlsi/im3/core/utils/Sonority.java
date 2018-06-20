package es.ua.dlsi.im3.core.utils;

import es.ua.dlsi.im3.core.score.ScientificPitch;
import es.ua.dlsi.im3.core.score.Segment;
import es.ua.dlsi.im3.core.score.Time;

import java.util.SortedSet;

/**
 * @autor drizo
 */
public class Sonority {
    Segment segment;
    SortedSet<ScientificPitch> scientificPitches;

    public Sonority(Segment segment, SortedSet<ScientificPitch> scientificPitches) {
        this.segment = segment;
        this.scientificPitches = scientificPitches;
    }

    public Segment getSegment() {
        return segment;
    }

    public SortedSet<ScientificPitch> getScientificPitches() {
        return scientificPitches;
    }



}
